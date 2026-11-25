package com.android_j.egg;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;

import com.dede.android_eggs.preferences.AppSettings;
import com.dede.android_eggs.system_colors.ResourcesUtils;
import com.dede.android_eggs.system_colors.SystemColor;

import java.util.Random;

/**
 * Material You coloring for the Jelly Bean artwork: rotates the red bean's hue
 * to the system accent color while keeping the black outline and the white
 * highlight intact, unlike a plain tint which would flatten the bean into a
 * silhouette. The rotation matrix preserves luminance (the standard CSS
 * hue-rotate weights), then saturation and a bounded brightness scale align
 * the bean with the target tonal palette without clipping cooler hues.
 */
final class DynamicBeanColors {

    /** Hue of the bean body in the artwork, i.e. pure red. */
    private static final float BEAN_HUE = 0f;

    /**
     * Hue sources for the random rotation. accent2 shares accent1's hue with a
     * lower chroma, so rotating to it would produce the same hue.
     */
    private static final SystemColor[] HUE_SOURCES = {
            SystemColor.ACCENT1_400,
            SystemColor.ACCENT3_400,
    };

    private static final float MIN_SATURATION = 0.35f;
    private static final float SATURATION_BOOST = 1.15f;
    private static final float BASE_BRIGHTNESS = 1.0f;
    private static final float BRIGHTNESS_VALUE_WEIGHT = 0.35f;

    private static final Random RANDOM = new Random();

    private DynamicBeanColors() {
    }

    static boolean isEnabled(Context context) {
        return AppSettings.INSTANCE.getDynamicEggColors().get(context);
    }

    static void applyTo(ImageView view) {
        if (!isEnabled(view.getContext())) {
            return;
        }
        SystemColor source = HUE_SOURCES[RANDOM.nextInt(HUE_SOURCES.length)];
        int color;
        try {
            color = ResourcesUtils.getSystemColor(view.getContext(), source);
        } catch (Exception ignore) {
            // Keep the original artwork when the palette is unavailable.
            return;
        }
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        ColorMatrix matrix = hueRotationMatrix(hsv[0] - BEAN_HUE);
        matrix.postConcat(saturationMatrix(hsv[1]));
        matrix.postConcat(brightnessMatrix(hsv[2]));
        view.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private static ColorMatrix saturationMatrix(float targetSaturation) {
        float saturation = Math.max(MIN_SATURATION, Math.min(1f, targetSaturation * SATURATION_BOOST));
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(saturation);
        return matrix;
    }

    private static ColorMatrix brightnessMatrix(float targetValue) {
        float scale = BASE_BRIGHTNESS + BRIGHTNESS_VALUE_WEIGHT * targetValue;
        ColorMatrix matrix = new ColorMatrix();
        matrix.setScale(scale, scale, scale, 1f);
        return matrix;
    }

    private static ColorMatrix hueRotationMatrix(float degrees) {
        double radians = Math.toRadians(degrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        return new ColorMatrix(new float[]{
                0.213f + cos * 0.787f - sin * 0.213f,
                0.715f - cos * 0.715f - sin * 0.715f,
                0.072f - cos * 0.072f + sin * 0.928f,
                0f, 0f,

                0.213f - cos * 0.213f + sin * 0.143f,
                0.715f + cos * 0.285f + sin * 0.140f,
                0.072f - cos * 0.072f - sin * 0.283f,
                0f, 0f,

                0.213f - cos * 0.213f - sin * 0.787f,
                0.715f - cos * 0.715f + sin * 0.715f,
                0.072f + cos * 0.928f + sin * 0.072f,
                0f, 0f,

                0f, 0f, 0f, 1f, 0f,
        });
    }
}
