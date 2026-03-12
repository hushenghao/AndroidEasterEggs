package com.android_i.egg;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.dede.basic.DimenUtils;


public class SnapshotProvider extends com.dede.basic.provider.SnapshotProvider {

    @DrawableRes
    private final int platlogo;

    public SnapshotProvider(@DrawableRes int platlogo) {
        this.platlogo = platlogo;
    }

    @NonNull
    @Override
    public View create(@NonNull Context context) {
        ImageView mContent = new ImageView(context);
        mContent.setImageResource(platlogo);
        int pd = DimenUtils.getDp(10);
        mContent.setPadding(0, pd, 0, pd);
        mContent.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return mContent;
    }
}
