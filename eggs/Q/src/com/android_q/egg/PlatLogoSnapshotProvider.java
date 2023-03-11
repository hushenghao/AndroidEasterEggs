package com.android_q.egg;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.dede.basic.UtilExt;

public class PlatLogoSnapshotProvider extends com.dede.basic.PlatLogoSnapshotProvider {
    @NonNull
    @Override
    public View create(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.q_platlogo_layout, null, false);

        Drawable mBackslash = new PlatLogoActivity.BackslashDrawable(UtilExt.getDp(50));

        ImageView mOneView = view.findViewById(R.id.one);
        mOneView.setImageDrawable(new PlatLogoActivity.OneDrawable());
        ImageView mZeroView = view.findViewById(R.id.zero);
        mZeroView.setImageDrawable(new PlatLogoActivity.ZeroDrawable());

        final ViewGroup root = (ViewGroup) mOneView.getParent();
        root.setClipChildren(false);
        root.setBackground(mBackslash);
        root.getBackground().setAlpha(0x20);
        return view;
    }
}
