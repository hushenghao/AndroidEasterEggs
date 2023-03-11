package com.android_o.egg;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Outline;
import android.graphics.drawable.RippleDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class PlatLogoSnapshotProvider extends com.dede.basic.PlatLogoSnapshotProvider {

    private final boolean isOreoPoint;

    public PlatLogoSnapshotProvider(boolean isOreoPoint) {
        this.isOreoPoint = isOreoPoint;
    }

    @NonNull
    @Override
    public Intent getPlatLogoIntent(@NonNull Context context) {
        return super.getPlatLogoIntent(context)
                .putExtra("isOreoPoint", isOreoPoint);
    }

    @NonNull
    @Override
    public View create(@NonNull Context context) {
        FrameLayout mLayout = new FrameLayout(context);

        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final float dp = dm.density;
        final int size = (int)
                (Math.min(Math.min(dm.widthPixels, dm.heightPixels), 600 * dp) - 100 * dp);

        final ImageView im = new ImageView(context);
        final int pad = (int) (40 * dp);
        im.setPadding(pad, pad, pad, pad);
        im.setTranslationZ(20);

        if (!isOreoPoint) {
            im.setBackground(new RippleDrawable(
                    ColorStateList.valueOf(0xFF776677),
                    context.getDrawable(R.drawable.o_platlogo),
                    null));
        } else {
            im.setBackground(new RippleDrawable(
                    ColorStateList.valueOf(0xFF776677),
                    context.getDrawable(R.drawable.o_point_platlogo),
                    null));
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

        mLayout.addView(im, new FrameLayout.LayoutParams(size, size, Gravity.CENTER));
        return mLayout;
    }
}
