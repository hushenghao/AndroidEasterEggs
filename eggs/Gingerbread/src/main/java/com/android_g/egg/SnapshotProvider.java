package com.android_g.egg;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;


public class SnapshotProvider extends com.dede.basic.provider.SnapshotProvider {

    @Override
    public boolean getIncludeBackground() {
        return true;
    }

    @NonNull
    @Override
    public View create(@NonNull Context context) {
        ImageView content = new ImageView(context);
        content.setImageResource(R.drawable.g_platlogo);
        content.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return content;
    }
}
