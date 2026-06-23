package com.dede.android_eggs.views.main

import android.content.Intent
import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.service.AppFunction
import com.dede.android_eggs.cat_editor.CatPartColors
import com.dede.android_eggs.cat_editor.Utilities
import com.dede.android_eggs.views.main.compose.filterEasterEggs
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.main.util.EggActionHelp
import com.dede.basic.provider.EasterEgg
import javax.inject.Inject
import javax.inject.Singleton

@AppFunctionSerializable(isDescribedByKDoc = true)
data class EasterEggInfo(
    /** The Android API level of this egg (e.g., 34 for Android 14) */
    val apiLevel: Int,
    /** The Android version name (e.g., "14", "15", "16") */
    val versionName: String,
    /** The dessert codename nickname (e.g., "Upside Down Cake", "Vanilla Ice Cream") */
    val nickname: String,
    /** Whether this is the most recent/latest available egg */
    val isLatest: Boolean,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class GeneratedCat(
    /** The numeric seed used to generate this cat. Pass this to recreate the same cat. */
    val seed: Long,
    /** The seed as a string for sharing or input into the Cat Editor UI. */
    val seedString: String,
)

@Singleton
class EasterEggFunctions @Inject constructor(
    private val pureEasterEggs: List<@JvmSuppressWildcards EasterEgg>,
) {

    private fun EasterEgg.toEasterEggInfo(
        context: android.content.Context,
        latestApiLevel: Int,
    ): EasterEggInfo {
        val apiLevel = apiLevelRange.first
        val versionName = EasterEggHelp.getVersionNameByApiLevel(apiLevel)
        val nickname = context.getString(nicknameRes)
        return EasterEggInfo(
            apiLevel = apiLevel,
            versionName = versionName,
            nickname = nickname,
            isLatest = apiLevel == latestApiLevel,
        )
    }

    /**
     * List all available Android platform Easter eggs with their API levels, version names, and nicknames.
     *
     * @param appFunctionContext The execution context.
     * @return A list of [EasterEggInfo] describing each available egg.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun listEasterEggs(
        appFunctionContext: AppFunctionContext,
    ): List<EasterEggInfo> {
        val context = appFunctionContext.context
        val latestApiLevel = pureEasterEggs.maxOf { it.apiLevelRange.first }
        return pureEasterEggs.map { it.toEasterEggInfo(context, latestApiLevel) }
    }

    /**
     * Search Easter eggs by name, nickname, API level, or Android version.
     * Required workflow: Call this before "launchEasterEgg" to find the correct API level.
     *
     * @param appFunctionContext The execution context.
     * @param query Search text matching an egg's name, nickname, API level (e.g., "14"), or Android version (e.g., "4.4").
     * @return A list of [EasterEggInfo] matching the query, or an empty list if no matches.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchEasterEggs(
        appFunctionContext: AppFunctionContext,
        query: String,
    ): List<EasterEggInfo> {
        val context = appFunctionContext.context
        val latestApiLevel = pureEasterEggs.maxOf { it.apiLevelRange.first }
        return filterEasterEggs(context, pureEasterEggs, query)
            .map { it.toEasterEggInfo(context, latestApiLevel) }
    }

    /**
     * Generate a random Neko cat with a unique seed.
     * Use the returned seed to recreate or share the same cat in the Cat Editor.
     *
     * @param appFunctionContext The execution context.
     * @param seed An optional seed to recreate a specific cat. If null, a random seed is generated.
     * @return A [GeneratedCat] with the seed that can be used to recreate this cat.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun generateRandomCat(
        appFunctionContext: AppFunctionContext,
        seed: Long? = null,
    ): GeneratedCat {
        val finalSeed = seed ?: Utilities.randomSeed()
        CatPartColors.colors(finalSeed)
        return GeneratedCat(
            seed = finalSeed,
            seedString = finalSeed.toString(),
        )
    }

    /**
     * Launch a specific Easter egg by its Android API level.
     * Required workflow: Call "listEasterEggs" first to obtain valid API levels.
     *
     * @param appFunctionContext The execution context.
     * @param apiLevel The Android API level of the egg to launch (e.g., 34 for Android 14).
     * @return true if the egg was found and launched, false if no egg exists for the given API level.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun launchEasterEgg(
        appFunctionContext: AppFunctionContext,
        apiLevel: Int,
    ): Boolean {
        val context = appFunctionContext.context
        val egg = pureEasterEggs.find { egg ->
            apiLevel in egg.apiLevelRange
        } ?: return false
        val targetClass = egg.actionClass
        if (targetClass != null) {
            val intent = EggActionHelp.createIntent(context, targetClass)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else if (!egg.onEasterEggAction(context)) {
            return false
        }
        return true
    }
}
