package com.android_r.egg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class PlatLogoSnapshotProvider extends com.dede.basic.PlatLogoSnapshotProvider {
    @NonNull
    @Override
    public View create(@NonNull Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(new BigDialDrawable(context));
        final FrameLayout layout = new FrameLayout(context);
        layout.setBackgroundColor(0xFFFF0000);
        layout.addView(imageView, FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        return layout;
    }

    private static class BigDialDrawable extends Drawable {

        private static final int COLOR_GREEN = 0xff3ddc84;
        private static final int COLOR_NAVY = 0xff073042;
        private static final int COLOR_ORANGE = 0xfff86734;
        private static final int COLOR_LIGHTBLUE = 0xffd7effe;

        public final int STEPS = 10;
        final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Drawable mEleven;
        private final boolean mNightMode;
        final float mElevenAnim = 1f;

        BigDialDrawable(Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mNightMode = context.getResources().getConfiguration().isNightModeActive();
            } else {
                mNightMode = false;
            }
            mEleven = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.r_ic_number11));
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            final Rect bounds = getBounds();
            final int w = bounds.width();
            final int h = bounds.height();
            final float w2 = w / 2f;
            final float h2 = h / 2f;
            final float radius = w / 4f;

            canvas.drawColor(mNightMode ? COLOR_NAVY : COLOR_LIGHTBLUE);

            canvas.save();
            canvas.rotate(45, w2, h2);
            canvas.clipRect(w2, h2 - radius, Math.min(w, h), h2 + radius);
            final int gradientColor = mNightMode ? 0x60000020 : (0x10FFFFFF & COLOR_NAVY);
            mPaint.setShader(
                    new LinearGradient(w2, h2, Math.min(w, h), h2, gradientColor,
                            0x00FFFFFF & gradientColor, Shader.TileMode.CLAMP));
            mPaint.setColor(Color.BLACK);
            canvas.drawPaint(mPaint);
            mPaint.setShader(null);
            canvas.restore();

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(COLOR_GREEN);

            canvas.drawCircle(w2, h2, radius, mPaint);

            mPaint.setColor(mNightMode ? COLOR_LIGHTBLUE : COLOR_NAVY);
            final float cx = w * 0.85f;
            for (int i = 0; i < STEPS; i++) {
                final float f = (float) i / STEPS;
                canvas.save();
                final float angle = valueToAngle(f);
                canvas.rotate(-angle, w2, h2);
                canvas.drawCircle(cx, h2,  20 , mPaint);
                canvas.restore();
            }

            final int size2 = (int) ((0.5 + 0.5f * mElevenAnim) * w / 14);
            final float cx11 = cx + size2 / 4f;
            mEleven.setBounds((int) cx11 - size2, (int) h2 - size2,
                    (int) cx11 + size2, (int) h2 + size2);
            final int alpha = 0xFFFFFF | ((int) clamp(0xFF * 2 * mElevenAnim, 0, 0xFF)
                    << 24);
            DrawableCompat.setTint(mEleven, alpha & COLOR_ORANGE);
            mEleven.draw(canvas);

            // don't want to use the rounded value here since the quantization will be visible
            float mValue = 9f;
            final float angle = valueToAngle(mValue);

            // it's easier to draw at far-right and rotate backwards
            canvas.rotate(-angle, w2, h2);
            mPaint.setColor(Color.WHITE);
            final float dimple = w2 / 12f;
            canvas.drawCircle(w - radius - dimple * 2, h2, dimple, mPaint);
        }

        float clamp(float x, float a, float b) {
            return x < a ? a : x > b ? b : x;
        }

        // rotation: min is at 4:30, max is at 3:00
        float valueToAngle(float v) {
            return (1f - v) * (360 - 45);
        }

        @Override
        public void setAlpha(int i) {
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
