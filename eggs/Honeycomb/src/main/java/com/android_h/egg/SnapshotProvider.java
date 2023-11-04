package com.android_h.egg;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;


public class SnapshotProvider extends com.dede.basic.provider.SnapshotProvider {
    @Override
    public View create(Context context) {
        ImageView content = new ImageView(context);
        content.setImageResource(R.drawable.h_platlogo);
        content.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return content;
    }
}
