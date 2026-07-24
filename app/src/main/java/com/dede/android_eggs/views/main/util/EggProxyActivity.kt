package com.dede.android_eggs.views.main.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.dede.android_eggs.views.main.util.EggProxyActivity.Companion.EXTRA_TARGET_EGG_CLASS

/**
 * Stub activity 0–4.
 *
 * Each stub is registered in the manifest with a unique [taskAffinity][android.R.attr.taskAffinity]
 * (`com.dede.android_eggs.egg.stub*`), so they occupy independent entries in the
 * "Recent Tasks" list. [EggActionHelp] selects one of these stubs as a proxy when
 * a new Easter egg task should be retained in recents.
 */
internal class EggProxyActivity0 : EggProxyActivity()
internal class EggProxyActivity1 : EggProxyActivity()
internal class EggProxyActivity2 : EggProxyActivity()
internal class EggProxyActivity3 : EggProxyActivity()
internal class EggProxyActivity4 : EggProxyActivity()

/**
 * A transparent proxy activity that acts as a task container for an Easter egg.
 *
 * ## Purpose
 *
 * Android's "Recent Tasks" list groups tasks by their root activity. If every
 * egg launched directly, they would all land in the same task (the app's default
 * affinity), making it impossible to have multiple egg entries in recents.
 *
 * By routing launches through distinct stub activities with unique task affinities,
 * each egg gets its own task slot while the stub itself remains invisible to the
 * user — it forwards to the real egg activity and immediately finishes.
 *
 * ## Lifecycle
 *
 * 1. [EggActionHelp.launchEgg] picks the appropriate stub via
 *    [EggActionHelp.findOrPickProxy]; its intent carries the real egg's
 *    class name in [EXTRA_TARGET_EGG_CLASS].
 * 2. `onCreate` reads the extra, starts the real egg activity inside the same
 *    task (same [android.R.attr.taskAffinity]), and finishes itself.
 * 3. The user sees only the egg activity. The stub is never visible.
 * 4. On subsequent launches of the same egg, [EggActionHelp.findOrPickProxy]
 *    reuses this stub so the existing recents entry is refreshed in place.
 *
 * ## Manifest Configuration
 *
 * Each stub must be declared with:
 * - A unique [taskAffinity][android.R.attr.taskAffinity] to create a separate recents entry.
 * - `android:theme="@android:style/Theme.NoDisplay"` so the proxy is invisible.
 * - `android:exported="false"` — internal only.
 *
 * The `FLAG_ACTIVITY_RETAIN_IN_RECENTS` flag is set at runtime by the caller,
 * because `android:retainInRecents` is not a public manifest attribute.
 *
 * @see EggActionHelp
 */
internal open class EggProxyActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val targetClassName = intent.getStringExtra(EXTRA_TARGET_EGG_CLASS)
        if (targetClassName != null) {
            try {
                @Suppress("UNCHECKED_CAST")
                val targetClass = Class.forName(targetClassName) as Class<out Activity>
                startActivity(Intent(this, targetClass))
            } catch (_: Exception) {
            }
        }
        finish()
    }

    companion object {
        /**
         * The fully-qualified class name of the real egg activity to launch.
         * Set by [EggActionHelp.launchEgg].
         */
        const val EXTRA_TARGET_EGG_CLASS = "extra_target_egg_class"
    }
}
