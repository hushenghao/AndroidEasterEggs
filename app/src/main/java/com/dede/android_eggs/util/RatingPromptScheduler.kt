package com.dede.android_eggs.util

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.dede.android_eggs.preferences.AppSettings
import com.dede.android_eggs.util.RatingPromptScheduler.Companion.FIRST_ASK_AGE_DAYS
import com.dede.android_eggs.util.RatingPromptScheduler.Companion.FIRST_ASK_SESSIONS
import com.dede.android_eggs.util.RatingPromptScheduler.Companion.MAX_ASK_INTERVAL_DAYS
import com.dede.android_eggs.util.RatingPromptScheduler.Companion.REPEAT_ASK_DAYS
import com.dede.android_eggs.util.RatingPromptScheduler.Companion.REPEAT_ASK_SESSIONS
import com.dede.android_eggs.util.RatingPromptScheduler.Companion.SESSION_GAP_MILLIS
import kotlin.math.min

/**
 * Decides when the app prompts for a rating, independent of the Play review API.
 *
 * Counts real foreground sessions — a visit counts only after the app has spent
 * [SESSION_GAP_MILLIS] in the background, so activity recreation (rotation,
 * theme change, …) does not inflate the count — and applies the ask schedule
 * over [com.dede.android_eggs.preferences.AppSettings.launchReviewCount] (sessions since the last ask) and
 * [com.dede.android_eggs.preferences.AppSettings.lastRatingPromptTime] (epoch millis of the last ask, `0` while
 * the app has never asked):
 *
 * - first ask: [FIRST_ASK_SESSIONS] sessions and [FIRST_ASK_AGE_DAYS] days after the install
 * - later asks: [REPEAT_ASK_SESSIONS] sessions and [REPEAT_ASK_DAYS] days since the
 *   previous ask, or [MAX_ASK_INTERVAL_DAYS] days at the latest
 *
 * One instance per activity: create it, add it to the activity lifecycle, and
 * run the `markRequested` callback passed to [onShouldAsk] once the prompt has
 * actually gone out, so a failed attempt stays eligible.
 */
class RatingPromptScheduler(
    private val activity: Activity,
    private val onShouldAsk: (markRequested: () -> Unit) -> Unit,
) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_STOP -> lastBackgroundTime = System.currentTimeMillis()
            Lifecycle.Event.ON_START -> if (isNewSession()) onSession()
            else -> Unit
        }
    }

    private fun isNewSession(): Boolean {
        return System.currentTimeMillis() - lastBackgroundTime >= SESSION_GAP_MILLIS
    }

    private fun onSession() {
        val now = System.currentTimeMillis()
        val count = AppSettings.launchReviewCount.get(activity)
        val lastAsk = AppSettings.lastRatingPromptTime.get(activity)
        if (lastAsk == 0L && count > FIRST_ASK_SESSIONS) {
            // Legacy counter values count activity creations and the old schedule
            // (4th, then every 10th launch) has already asked at least once.
            // Restart on this schedule instead of asking right after the update.
            markRequested()
            return
        }
        // Cap at the ask threshold while the app has never asked: a larger value
        // would look like the legacy counter checked above.
        val session = if (lastAsk == 0L) min(count + 1, FIRST_ASK_SESSIONS) else count + 1
        AppSettings.launchReviewCount.set(activity, session)
        if (!shouldAsk(now, lastAsk, session)) {
            return
        }
        // One frame later, so the prompt cannot stack on top of the startup UI.
        activity.window.decorView.post { onShouldAsk { markRequested() } }
    }

    private fun shouldAsk(now: Long, lastAsk: Long, sessions: Int): Boolean {
        if (lastAsk == 0L) {
            val installAge = now - firstInstallTime()
            return sessions >= FIRST_ASK_SESSIONS && installAge >= FIRST_ASK_AGE_DAYS * DAY_MILLIS
        }
        val sinceLastAsk = now - lastAsk
        return sinceLastAsk >= REPEAT_ASK_DAYS * DAY_MILLIS &&
            (sessions >= REPEAT_ASK_SESSIONS || sinceLastAsk >= MAX_ASK_INTERVAL_DAYS * DAY_MILLIS)
    }

    /**
     * Resets the session count and starts the next schedule window. Called once
     * the prompt went out, and to fold in a legacy counter, see [onSession].
     */
    private fun markRequested() {
        AppSettings.launchReviewCount.set(activity, 0)
        AppSettings.lastRatingPromptTime.set(activity, System.currentTimeMillis())
    }

    private fun firstInstallTime(): Long {
        val packageManager = activity.packageManager
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(activity.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(activity.packageName, 0)
        }
        return packageInfo.firstInstallTime
    }

    companion object {
        private const val DAY_MILLIS = 24 * 60 * 60 * 1000L

        private const val SESSION_GAP_MILLIS = 60_000L

        private const val FIRST_ASK_SESSIONS = 4
        private const val FIRST_ASK_AGE_DAYS = 7

        private const val REPEAT_ASK_SESSIONS = 10
        private const val REPEAT_ASK_DAYS = 60
        private const val MAX_ASK_INTERVAL_DAYS = 180

        /** Process-wide: survives activity recreation, which must not start a new session. */
        private var lastBackgroundTime = 0L
    }
}