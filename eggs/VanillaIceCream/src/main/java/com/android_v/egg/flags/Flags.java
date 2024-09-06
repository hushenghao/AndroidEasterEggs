package com.android_v.egg.flags;

/**
 * Generate by <a href="https://cs.android.com/android/platform/superproject/+/android15-release:frameworks/base/packages/EasterEgg/easter_egg_flags.aconfig">easter_egg_flags.aconfig</a>
 *
 * <li><a href="https://cs.android.com/android/platform/superproject/+/android15-release:build/make/tools/aconfig/aconfig/">Project aconfig</a></li>
 * <li><a href="https://cs.android.com/android/platform/superproject/+/android15-release:build/make/tools/aconfig/aconfig/templates/Flags.java.template">Flags.java.template</a></li>
 *
 * <pre>
 * package: "com.android.egg.flags"
 * container: "system"
 *
 * flag {
 *     name: "flag_flag"
 *     namespace: "systemui"
 *     description: "Flags are planted on planets when you land. Yes, it's a flag for flags."
 *     bug: "320150798"
 * }
 * </pre>
 */
public final class Flags {

    public static final String FLAG_FLAG_FLAG = "com.android_v.egg.flags.flag_flag";

    public static boolean flagFlag() {
//        FEATURE_FLAGS.flagFlag();
        return true;
    }

//    private static final FeatureFlags FEATURE_FLAGS = new FeatureFlagsImpl();
}
