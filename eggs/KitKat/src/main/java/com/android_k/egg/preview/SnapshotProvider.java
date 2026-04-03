package com.android_k.egg.preview;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android_k.egg.R;


public class SnapshotProvider extends com.dede.basic.provider.SnapshotProvider {

    @Override
    public View create(Context context) {
        FrameLayout mContent = new FrameLayout(context);

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;

        final ImageView logo = new ImageView(context);
        logo.setImageResource(R.drawable.k_platlogo_preview);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        mContent.addView(logo, lp);
        return mContent;
    }
}
