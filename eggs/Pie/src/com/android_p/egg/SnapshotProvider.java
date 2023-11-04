package com.android_p.egg;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class SnapshotProvider extends com.dede.basic.provider.SnapshotProvider {

    @Override
    public boolean getIncludeBackground() {
        return true;
    }

    @NonNull
    @Override
    public View create(@NonNull Context context) {
        FrameLayout layout = new FrameLayout(context);
        layout.setBackground(new PlatLogoActivity.PBackground(context));
        return layout;
    }
}
