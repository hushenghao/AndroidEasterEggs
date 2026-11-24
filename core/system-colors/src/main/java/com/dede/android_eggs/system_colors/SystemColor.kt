package com.dede.android_eggs.system_colors

/**
 * Dynamic system color resources (`system_accent1_400` etc.), referenced by
 * constant instead of a string literal.
 *
 * The names mirror the framework `android.R.color.system_*` palette: on API 31+
 * the bundled aliases forward to those values, on 27-30 [SystemTonalColors]
 * generates them from the wallpaper, and the static palette in `values` covers
 * the rest. Keep the entries in sync with `res/values/colors.xml`.
 */
enum class SystemColor(val resName: String) {
    ACCENT1_0("system_accent1_0"),
    ACCENT1_10("system_accent1_10"),
    ACCENT1_50("system_accent1_50"),
    ACCENT1_100("system_accent1_100"),
    ACCENT1_200("system_accent1_200"),
    ACCENT1_300("system_accent1_300"),
    ACCENT1_400("system_accent1_400"),
    ACCENT1_500("system_accent1_500"),
    ACCENT1_600("system_accent1_600"),
    ACCENT1_700("system_accent1_700"),
    ACCENT1_800("system_accent1_800"),
    ACCENT1_900("system_accent1_900"),
    ACCENT1_1000("system_accent1_1000"),

    ACCENT2_0("system_accent2_0"),
    ACCENT2_10("system_accent2_10"),
    ACCENT2_50("system_accent2_50"),
    ACCENT2_100("system_accent2_100"),
    ACCENT2_200("system_accent2_200"),
    ACCENT2_300("system_accent2_300"),
    ACCENT2_400("system_accent2_400"),
    ACCENT2_500("system_accent2_500"),
    ACCENT2_600("system_accent2_600"),
    ACCENT2_700("system_accent2_700"),
    ACCENT2_800("system_accent2_800"),
    ACCENT2_900("system_accent2_900"),
    ACCENT2_1000("system_accent2_1000"),

    ACCENT3_0("system_accent3_0"),
    ACCENT3_10("system_accent3_10"),
    ACCENT3_50("system_accent3_50"),
    ACCENT3_100("system_accent3_100"),
    ACCENT3_200("system_accent3_200"),
    ACCENT3_300("system_accent3_300"),
    ACCENT3_400("system_accent3_400"),
    ACCENT3_500("system_accent3_500"),
    ACCENT3_600("system_accent3_600"),
    ACCENT3_700("system_accent3_700"),
    ACCENT3_800("system_accent3_800"),
    ACCENT3_900("system_accent3_900"),
    ACCENT3_1000("system_accent3_1000"),

    NEUTRAL1_0("system_neutral1_0"),
    NEUTRAL1_10("system_neutral1_10"),
    NEUTRAL1_50("system_neutral1_50"),
    NEUTRAL1_100("system_neutral1_100"),
    NEUTRAL1_200("system_neutral1_200"),
    NEUTRAL1_300("system_neutral1_300"),
    NEUTRAL1_400("system_neutral1_400"),
    NEUTRAL1_500("system_neutral1_500"),
    NEUTRAL1_600("system_neutral1_600"),
    NEUTRAL1_700("system_neutral1_700"),
    NEUTRAL1_800("system_neutral1_800"),
    NEUTRAL1_900("system_neutral1_900"),
    NEUTRAL1_1000("system_neutral1_1000"),

    NEUTRAL2_0("system_neutral2_0"),
    NEUTRAL2_10("system_neutral2_10"),
    NEUTRAL2_50("system_neutral2_50"),
    NEUTRAL2_100("system_neutral2_100"),
    NEUTRAL2_200("system_neutral2_200"),
    NEUTRAL2_300("system_neutral2_300"),
    NEUTRAL2_400("system_neutral2_400"),
    NEUTRAL2_500("system_neutral2_500"),
    NEUTRAL2_600("system_neutral2_600"),
    NEUTRAL2_700("system_neutral2_700"),
    NEUTRAL2_800("system_neutral2_800"),
    NEUTRAL2_900("system_neutral2_900"),
    NEUTRAL2_1000("system_neutral2_1000"),
}
