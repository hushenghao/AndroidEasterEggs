package com.android_n.egg.preview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.android_n.egg.R;

public class SnapshotProvider extends com.dede.basic.provider.SnapshotProvider {
    @NonNull
    @Override
    public View create(@NonNull Context context) {
        FrameLayout mLayout = new FrameLayout(context);

        final ImageView im = new ImageView(context);
        im.setAdjustViewBounds(true);
        im.setTranslationZ(20);

        im.setImageResource(R.drawable.n_platlogo_preview);

        im.post(() -> {
            final Drawable overlay = context.getDrawable(R.drawable.n_platlogo_m);
            overlay.setBounds(0, 0, im.getMeasuredWidth(), im.getMeasuredHeight());
            im.getOverlay().clear();
            im.getOverlay().add(overlay);
        });

        mLayout.addView(im, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        return mLayout;
    }
}
