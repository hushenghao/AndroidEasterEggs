package com.android_m.egg.preview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.android_m.egg.R;

public class SnapshotProvider extends com.dede.basic.provider.SnapshotProvider {
    @NonNull
    @Override
    public View create(@NonNull Context context) {
        FrameLayout mLayout = new FrameLayout(context);

        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final float dp = dm.density;
        final int size = (int)
                (Math.min(Math.min(dm.widthPixels, dm.heightPixels), 500 * dp) - 200 * dp);

        final View im = new View(context);
        im.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                final int pad = (int) (8 * dp);
                outline.setOval(pad, pad, view.getWidth() - pad, view.getHeight() - pad);
            }
        });
        final Drawable platlogo = context.getDrawable(R.drawable.m_platlogo_preview);
        im.setBackground(new RippleDrawable(
                ColorStateList.valueOf(0xFFFFFFFF),
                platlogo,
                null));

        mLayout.addView(im, new FrameLayout.LayoutParams(size, size, Gravity.CENTER));
        return mLayout;
    }
}
