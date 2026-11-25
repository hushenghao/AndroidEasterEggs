package com.android_j.egg;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;

import com.dede.android_eggs.preferences.AppSettings;
import com.dede.android_eggs.system_colors.ResourcesUtils;
import com.dede.android_eggs.system_colors.SystemColor;

import java.util.Random;

/**
 * Material You coloring for the Jelly Bean artwork: maps the red channel
 * excess (R - W, where W = (G + B) / 2) to the target accent color while
 * keeping the neutral gray component W intact.
 *
 * <pre>
 *   R' = r * R + (1 - r) * W
 *   G' = g * R + (1 - g) * W
 *   B' = b * R + (1 - b) * W
 * </pre>
 *
 * Black outlines (0, 0, 0) stay black, white highlights (1, 1, 1) stay white,
 * and pure red bean pixels (1, 0, 0) map directly to (r, g, b) without the
 * luminance distortion of CSS hue rotation.
 */
final class DynamicBeanColors {

    /**
     * Wallpaper-seeded palette colors shared by PlatLogo, Snapshot, and BeanBag.
     * Two hue families (accent1 and accent3, 60 degrees apart) give distinguishable
     * hues, and stepping across shades 100/300/500/700 (20 tone units apart)
     * keeps clear light-to-dark contrast between beans; accent2 is left out
     * because it shares accent1's hue with a much lower chroma.
     */
    private static final SystemColor[] DYNAMIC_COLORS = {
            SystemColor.ACCENT1_100,
            SystemColor.ACCENT1_300,
            SystemColor.ACCENT1_500,
            SystemColor.ACCENT1_700,
            SystemColor.ACCENT3_100,
            SystemColor.ACCENT3_300,
            SystemColor.ACCENT3_500,
            SystemColor.ACCENT3_700,
    };

    private static final Random RANDOM = new Random();

    private DynamicBeanColors() {
    }

    static boolean isEnabled(Context context) {
        return AppSettings.INSTANCE.getDynamicEggColors().get(context);
    }

    static int pickColor(Context context) {
        SystemColor source = DYNAMIC_COLORS[RANDOM.nextInt(DYNAMIC_COLORS.length)];
        return ResourcesUtils.getSystemColor(context, source);
    }

    static void applyTo(ImageView view) {
        if (!isEnabled(view.getContext())) {
            return;
        }
        int color;
        try {
            color = pickColor(view.getContext());
        } catch (Exception ignore) {
            // Keep the original artwork when the palette is unavailable.
            return;
        }
        view.setColorFilter(createColorFilter(color));
    }

    /**
     * Build a filter that recolors the red bean artwork to [targetColor] while
     * keeping the black outline and white highlight intact.
     */
    static ColorMatrixColorFilter createColorFilter(int targetColor) {
        float r = Color.red(targetColor) / 255f;
        float g = Color.green(targetColor) / 255f;
        float b = Color.blue(targetColor) / 255f;
        float rw = (1f - r) * 0.5f;
        float gw = (1f - g) * 0.5f;
        float bw = (1f - b) * 0.5f;
        return new ColorMatrixColorFilter(new float[]{
                r, rw, rw, 0f, 0f,
                g, gw, gw, 0f, 0f,
                b, bw, bw, 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
        });
    }
}
