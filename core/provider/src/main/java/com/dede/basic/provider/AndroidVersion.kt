package com.dede.basic.provider

/**
 * How a version was released, driving the "API X" label and the stable-first
 * lookup preference for ambiguous public api levels (e.g. 33 -> Tiramisu, not T Beta).
 */
enum class ReleaseType { RELEASE, PREVIEW, BETA }

/** `Build.VERSION_CODES_FULL#SDK_INT_MULTIPLIER`. */
const val SDK_INT_MULTIPLIER = 100_000

/**
 * Encodes the legacy full api level (`major * 100000 + minor`) from a public api
 * level and its minor release, so enum entries read `fullApiLevel(36)` instead of
 * a bare `3600000`. Preview/beta entries subtract one from the stable level, e.g.
 * `fullApiLevel(14, -1)` for the Ice Cream Sandwich preview.
 */
inline fun fullApiLevel(apiLevel: Int, minor: Int = 0): Int =
    apiLevel * SDK_INT_MULTIPLIER + minor

/**
 * The Android releases behind the Easter eggs, replacing `Build.VERSION_CODES_FULL`
 * as the egg identity.
 *
 * This is a pure Kotlin catalog with no Android dependency: egg identity, ordering,
 * search and statistics use [id] / [fullApiLevel], while device-capability checks and
 * `@RequiresApi` continue to use `Build.VERSION.SDK_INT` / `Build.VERSION_CODES`.
 *
 * - [id] is stable and append-only. It is the persisted identity (statistics,
 *   shortcuts, deep links resolve to it).
 * - [fullApiLevel] keeps the legacy `major * 100000 + minor` numeric encoding so old
 *   full-api-level values map back to their [AndroidVersion] during migration. It is
 *   also the ordering key, so the old list order is reproduced without `ordinal`.
 */
enum class AndroidVersion(
    val id: String,
    val versionName: String,
    val fullApiLevel: Int,
    val releaseType: ReleaseType = ReleaseType.RELEASE,
) {
    BASE("android_1_0", "1.0", fullApiLevel(1)),
    BASE_1_1("android_1_1", "1.1", fullApiLevel(2)),
    CUPCAKE("android_1_5", "1.5", fullApiLevel(3)),
    DONUT("android_1_6", "1.6", fullApiLevel(4)),
    ECLAIR("android_2_0", "2.0", fullApiLevel(5)),
    ECLAIR_0_1("android_2_0_1", "2.0.1", fullApiLevel(6)),
    ECLAIR_MR1("android_2_1", "2.1", fullApiLevel(7)),
    FROYO("android_2_2", "2.2", fullApiLevel(8)),
    GINGERBREAD("android_2_3", "2.3", fullApiLevel(9)),
    GINGERBREAD_MR1("android_2_3_3", "2.3.3", fullApiLevel(10)),
    HONEYCOMB("android_3_0", "3.0", fullApiLevel(11)),
    HONEYCOMB_MR1("android_3_1", "3.1", fullApiLevel(12)),
    HONEYCOMB_MR2("android_3_2", "3.2", fullApiLevel(13)),
    ICE_CREAM_SANDWICH_PREVIEW("android_ics_preview", "ICS", fullApiLevel(14, -1), ReleaseType.PREVIEW),
    ICE_CREAM_SANDWICH("android_4_0", "4.0", fullApiLevel(14)),
    ICE_CREAM_SANDWICH_MR1("android_4_0_3", "4.0.3", fullApiLevel(15)),
    JELLY_BEAN("android_4_1", "4.1", fullApiLevel(16)),
    JELLY_BEAN_MR1("android_4_2", "4.2", fullApiLevel(17)),
    JELLY_BEAN_MR2("android_4_3", "4.3", fullApiLevel(18)),
    KITKAT_PREVIEW("android_k_preview", "K", fullApiLevel(19, -1), ReleaseType.PREVIEW),
    KITKAT("android_4_4", "4.4", fullApiLevel(19)),
    KITKAT_WATCH("android_4_4w", "4.4W", fullApiLevel(20)),
    LOLLIPOP_PREVIEW("android_l_preview", "L", fullApiLevel(21, -1), ReleaseType.PREVIEW),
    LOLLIPOP("android_5_0", "5.0", fullApiLevel(21)),
    LOLLIPOP_MR1("android_5_1", "5.1", fullApiLevel(22)),
    M("android_6_0", "6.0", fullApiLevel(23)),
    M_PREVIEW("android_m_preview", "M", fullApiLevel(23, -1), ReleaseType.PREVIEW),
    N("android_7_0", "7.0", fullApiLevel(24)),
    N_PREVIEW("android_n_preview", "N", fullApiLevel(24, -1), ReleaseType.PREVIEW),
    N_MR1("android_7_1", "7.1", fullApiLevel(25)),
    O("android_8_0", "8.0", fullApiLevel(26)),
    O_MR1("android_8_1", "8.1", fullApiLevel(27)),
    P("android_9", "9", fullApiLevel(28)),
    Q("android_10", "10", fullApiLevel(29)),
    R("android_11", "11", fullApiLevel(30)),
    S("android_12", "12", fullApiLevel(31)),
    S_V2("android_12l", "12L", fullApiLevel(32)),
    T_BETA("android_t_beta", "T", fullApiLevel(33, -1), ReleaseType.BETA),
    TIRAMISU("android_13", "13", fullApiLevel(33)),
    UPSIDE_DOWN_CAKE("android_14", "14", fullApiLevel(34)),
    VANILLA_ICE_CREAM("android_15", "15", fullApiLevel(35)),
    BAKLAVA("android_16", "16", fullApiLevel(36)),
    BAKLAVA_1("android_16_1", "16.1", fullApiLevel(36, 1)),
    CINNAMON_BUN("android_17", "17", fullApiLevel(37)),
    CINNAMON_BUN_1("android_17_1", "17.1", fullApiLevel(37, 1)),
    CINNAMON_BUN_2("android_17_2", "17.2", fullApiLevel(37, 2)),
    NEXT("android_next", "Next", fullApiLevel(38), ReleaseType.PREVIEW),
    ;

    companion object {
        val byFullApiLevel: Map<Int, AndroidVersion> = values().associateBy { it.fullApiLevel }
    }

    /**
     * The public Android API level (e.g. 34) used for deep links, App Functions and the
     * "API X" label. Preview/beta releases resolve to the stable API they precede (their
     * full level is `stable - 1`, i.e. `minor == SDK_INT_MULTIPLIER - 1`); [NEXT] has none.
     */
    val apiLevel: Int?
        get() {
            if (this == NEXT) return null
            val major = fullApiLevel / SDK_INT_MULTIPLIER
            val minor = fullApiLevel % SDK_INT_MULTIPLIER
            return major + if (minor == SDK_INT_MULTIPLIER - 1) 1 else 0
        }

    /**
     * The displayed "API X" value. QPR releases (RELEASE with a non-zero minor) include the
     * minor (e.g. "36.1"); preview/beta releases show the stable integer they map to.
     */
    val apiLevelLabel: String
        get() {
            val api = apiLevel ?: return "Next"
            val minor = fullApiLevel % SDK_INT_MULTIPLIER
            return if (releaseType == ReleaseType.RELEASE && minor > 0) "$api.$minor" else "$api"
        }
}