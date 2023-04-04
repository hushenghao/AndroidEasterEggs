package com.android_n.egg;

import android.content.Context;
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

        final ImageView im = new ImageView(context);
        im.setAdjustViewBounds(true);
        im.setTranslationZ(20);

        im.setImageResource(R.drawable.n_platlogo);

        mLayout.addView(im, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        return mLayout;
    }
}
