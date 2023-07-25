package com.android_o.egg;

import android.content.Context;
import android.graphics.Outline;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.dede.basic.DrawableKt;

public class PlatLogoSnapshotProvider extends com.dede.basic.PlatLogoSnapshotProvider {

    public static class Point1 extends PlatLogoSnapshotProvider {
        public Point1() {
            super(true);
        }
    }

    private final boolean isOreoPoint;

    public PlatLogoSnapshotProvider() {
        this(false);
    }

    PlatLogoSnapshotProvider(boolean isOreoPoint) {
        this.isOreoPoint = isOreoPoint;
    }

    @NonNull
    @Override
    public View create(@NonNull Context context) {
        FrameLayout mLayout = new FrameLayout(context);

        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final float dp = dm.density;

        final ImageView im = new ImageView(context);
        im.setTranslationZ(20);
        im.setAdjustViewBounds(true);

        if (!isOreoPoint) {
            im.setImageResource(R.drawable.o_platlogo);
        } else {
//            im.setImageResource(R.drawable.o_point_platlogo);
            im.setImageDrawable(DrawableKt.createVectorDrawableCompat(context, R.drawable.o_point_platlogo));
            im.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    final int w = view.getWidth();
                    final int h = view.getHeight();
                    outline.setOval((int) (w * .125), (int) (h * .125), (int) (w * .96), (int) (h * .96));
                }
            });
            im.setElevation(12f * dp);
        }

        mLayout.addView(im, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        return mLayout;
    }
}
