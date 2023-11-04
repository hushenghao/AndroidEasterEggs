package com.android_j.egg;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

public class SnapshotProvider extends com.dede.basic.provider.SnapshotProvider {
    @Override
    public View create(Context context) {
        ImageView mContent = new ImageView(context);
        mContent.setImageResource(R.drawable.j_platlogo);
        mContent.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        return mContent;
    }

}
