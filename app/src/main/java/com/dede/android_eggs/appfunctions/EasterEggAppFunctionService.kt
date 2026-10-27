package com.dede.android_eggs.appfunctions

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import androidx.core.graphics.drawable.toBitmap
import com.dede.android_eggs.cat_editor.Utilities
import com.dede.android_eggs.views.main.compose.filterEasterEggs
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.main.util.IntentHandler
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.toApiLevelRange
import com.dede.basic.requireDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/** An Android platform Easter egg available in this app. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class EasterEggInfo(
    /** The Android API level of this egg (e.g., 34 for Android 14). */
    val apiLevel: Int,
    /** The Android version name (e.g., "14", "15", "16"). */
    val versionName: String,
    /** The dessert codename nickname (e.g., "Upside Down Cake", "Vanilla Ice Cream"). */
    val nickname: String,
    /** Whether this is the most recent/latest available egg. */
    val isLatest: Boolean,
)

/** A Neko cat seed that can be restored in the Cat Editor. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class GeneratedCat(
    /** The numeric seed used to generate this cat. Pass it to recreate the same cat. */
    val seed: Long,
    /** The seed as a string, for sharing or pasting into the Cat Editor. */
    val seedString: String,
    /** A PendingIntent that opens the Cat Editor with this cat pre-filled. Fire it to edit the cat. Prefer this over the deep link Uri. */
    val editorIntent: PendingIntent,
    /** The deep link that opens the Cat Editor with this cat pre-filled. Fallback for when firing PendingIntents is unavailable. */
    val editorUri: Uri,
)

/** The result of preparing an Easter egg launch. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class EasterEggLaunch(
    /** The egg the launch intent opens. */
    val egg: EasterEggInfo,
    /** A PendingIntent that opens the egg's activity. Fire it to launch the egg. Prefer this over the deep link Uri. */
    val launchIntent: PendingIntent,
    /** The deep link that opens the egg's activity. Fallback for when firing PendingIntents is unavailable. */
    val launchUri: Uri,
)

/** The Easter egg logo image. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class EggIcon(
    /** The image MIME type, always "image/png". */
    val mimeType: String,
    /** The logo rendered at 192x192 pixels as PNG bytes. */
    val png: ByteArray,
)

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@AndroidEntryPoint
@AppFunctionServiceEntryPoint(
    serviceName = "EasterEggAppFunctionService",
    appFunctionXmlFileName = "easter_egg_app_function_service",
)
abstract class BaseEasterEggAppFunctionService : AppFunctionService() {

    @Inject
    internal lateinit var pureEasterEggs: List<@JvmSuppressWildcards EasterEgg>

    /**
     * List all available Android platform Easter eggs with their API levels, version names,
     * and nicknames.
     *
     * @return A list of [EasterEggInfo] describing each available egg.
     */
    @AppFunction(isDescribedByKDoc = true)
    internal suspend fun listEasterEggs(): List<EasterEggInfo> = withContext(Dispatchers.IO) {
        val latestApiLevel = latestPlainApiLevel()
        pureEasterEggs.map { it.toEasterEggInfo(latestApiLevel) }
    }

    /**
     * Search Easter eggs by name, nickname, API level, or Android version.
     * Required workflow: Call this before [launchEasterEgg] to find the correct API level.
     *
     * @param query Search text matching an egg's name, nickname, API level (e.g., "14"),
     * or Android version (e.g., "4.4").
     * @return A list of [EasterEggInfo] matching the query, or an empty list if no matches.
     */
    @AppFunction(isDescribedByKDoc = true)
    internal suspend fun searchEasterEggs(query: String): List<EasterEggInfo> =
        withContext(Dispatchers.IO) {
            val latestApiLevel = latestPlainApiLevel()
            filterEasterEggs(applicationContext, pureEasterEggs, query)
                .map { it.toEasterEggInfo(latestApiLevel) }
        }

    /**
     * Generate a random Neko cat with a unique seed.
     * Use the returned seed to recreate or share the same cat in the Cat Editor.
     *
     * @param seed An optional seed to recreate a specific cat. If null, a random seed is generated.
     * @return A [GeneratedCat] with the seed and the deep link opening it in the Cat Editor.
     */
    @AppFunction(isDescribedByKDoc = true)
    internal suspend fun generateRandomCat(seed: Long? = null): GeneratedCat =
        withContext(Dispatchers.IO) {
            val finalSeed = seed ?: Utilities.randomSeed()
            val editorUri = IntentHandler.createCatEditorUri(finalSeed)
            GeneratedCat(
                seed = finalSeed,
                seedString = finalSeed.toString(),
                editorIntent = createDeepLinkPendingIntent(editorUri, requestCode = 0),
                editorUri = editorUri,
            )
        }

    /**
     * Prepare to launch a specific Easter egg by its Android API level.
     * Required workflow: Call [listEasterEggs] or [searchEasterEggs] first to obtain valid
     * API levels, then fire the returned [EasterEggLaunch.launchIntent] PendingIntent
     * (preferred), or fall back to opening the [EasterEggLaunch.launchUri] deep link,
     * to display the egg.
     *
     * @param apiLevel The Android API level of the egg to launch (e.g., 34 for Android 14).
     * @return An [EasterEggLaunch] with the egg info and the launch token opening the egg.
     * @throws AppFunctionElementNotFoundException if no egg exists for the given API level.
     */
    @AppFunction(isDescribedByKDoc = true)
    internal suspend fun launchEasterEgg(apiLevel: Int): EasterEggLaunch =
        withContext(Dispatchers.IO) {
            val egg = pureEasterEggs.find { apiLevel in it.fullApiLevelRange.toApiLevelRange() }
                ?: throw AppFunctionElementNotFoundException(
                    "No Easter egg for API level $apiLevel",
                )
            // App functions execute without a visible activity, a startActivity from here is
            // blocked by the background activity launch restriction. Return a PendingIntent
            // plus the equivalent deep link Uri instead: the app opens in the foreground and
            // launches the egg itself, see IntentHandler.UriHandler.
            val launchUri = IntentHandler.createEggUri(apiLevel)
            EasterEggLaunch(
                egg = egg.toEasterEggInfo(latestPlainApiLevel()),
                launchIntent = createDeepLinkPendingIntent(launchUri, requestCode = apiLevel),
                launchUri = launchUri,
            )
        }

    /**
     * Get the Easter egg logo image for a specific Android API level.
     * Required workflow: Call [listEasterEggs] or [searchEasterEggs] first to obtain valid
     * API levels.
     *
     * @param apiLevel The Android API level of the egg (e.g., 34 for Android 14).
     * @return The egg logo rendered at 192x192 pixels as a PNG [EggIcon].
     * @throws AppFunctionElementNotFoundException if no egg exists for the given API level.
     */
    @AppFunction(isDescribedByKDoc = true)
    internal suspend fun getEasterEggIcon(apiLevel: Int): EggIcon =
        withContext(Dispatchers.IO) {
            val egg = pureEasterEggs.find { apiLevel in it.fullApiLevelRange.toApiLevelRange() }
                ?: throw AppFunctionElementNotFoundException(
                    "No Easter egg for API level $apiLevel",
                )
            EggIcon(
                mimeType = "image/png",
                png = requireDrawable(egg.iconRes).toBitmap(ICON_SIZE_PX, ICON_SIZE_PX).toPngBytes(),
            )
        }

    private fun createDeepLinkPendingIntent(uri: Uri, requestCode: Int): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, uri)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val options = ActivityOptions.makeBasic()
            .setPendingIntentCreatorBackgroundActivityStartMode(
                ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOW_ALWAYS,
            )
        return PendingIntent.getActivity(
            applicationContext,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            options.toBundle(),
        )
    }

    private fun EasterEgg.toEasterEggInfo(latestApiLevel: Int): EasterEggInfo {
        val plainApiLevel = fullApiLevelRange.toApiLevelRange().first
        return EasterEggInfo(
            apiLevel = plainApiLevel,
            versionName = EasterEggHelp.getVersionNameByApiLevel(plainApiLevel),
            nickname = getString(nicknameRes),
            isLatest = plainApiLevel == latestApiLevel,
        )
    }

    private fun latestPlainApiLevel(): Int =
        pureEasterEggs.maxOf { it.fullApiLevelRange.toApiLevelRange().first }
}

private const val ICON_SIZE_PX = 192

private fun Bitmap.toPngBytes(): ByteArray {
    val bytes = ByteArrayOutputStream().use { output ->
        compress(Bitmap.CompressFormat.PNG, 100, output)
        output.toByteArray()
    }
    recycle()
    return bytes
}
