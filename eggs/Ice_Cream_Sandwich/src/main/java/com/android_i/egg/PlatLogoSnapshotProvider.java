package com.android_i.egg;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;


public class PlatLogoSnapshotProvider extends com.dede.basic.PlatLogoSnapshotProvider {
    @Override
    public View create(Context context) {
        ImageView mContent = new ImageView(context);
        mContent.setImageResource(R.drawable.i_platlogo);
        mContent.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return mContent;
    }
}
