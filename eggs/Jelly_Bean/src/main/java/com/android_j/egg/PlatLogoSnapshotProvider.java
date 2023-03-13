package com.android_j.egg;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

public class PlatLogoSnapshotProvider extends com.dede.basic.PlatLogoSnapshotProvider {
    @Override
    public View create(Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        ImageView mContent = new ImageView(context);
        mContent.setImageResource(R.drawable.j_platlogo);
        mContent.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        final int p = (int) (32 * metrics.density);
        mContent.setPadding(p, 0, p, 0);

        return mContent;
    }

}
