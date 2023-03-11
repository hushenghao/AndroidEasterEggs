package com.android_n.egg;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class PlatLogoSnapshotProvider extends com.dede.basic.PlatLogoSnapshotProvider {
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

        im.setBackground(new RippleDrawable(
                ColorStateList.valueOf(0xFFFFFFFF),
                context.getDrawable(R.drawable.n_platlogo),
                null));

        mLayout.addView(im, new FrameLayout.LayoutParams(size, size, Gravity.CENTER));
        return mLayout;
    }
}
