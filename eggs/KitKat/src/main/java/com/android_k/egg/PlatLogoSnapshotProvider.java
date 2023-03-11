package com.android_k.egg;

import static com.android_k.egg.PlatLogoActivity.BGCOLOR;

import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class PlatLogoSnapshotProvider extends com.dede.basic.PlatLogoSnapshotProvider {

    @Override
    public View create(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        Typeface light = Typeface.create("sans-serif-light", Typeface.NORMAL);

        FrameLayout mContent = new FrameLayout(context);
        mContent.setBackgroundColor(0xC0000000);

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;

        final ImageView logo = new ImageView(context);
        logo.setImageResource(R.drawable.k_platlogo);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        final View bg = new View(context);
        bg.setBackgroundColor(BGCOLOR);


        final int p = (int) (4 * metrics.density);

        final TextView tv = new TextView(context);
        if (light != null) tv.setTypeface(light);
        tv.setTextSize(30);
        tv.setPadding(p, p, p, p);
        tv.setTextColor(0xFFFFFFFF);
        tv.setGravity(Gravity.CENTER);
        tv.setTransformationMethod(new AllCapsTransformationMethod(context));
        tv.setText("Android 4.4");

        mContent.addView(bg);
        mContent.addView(logo, lp);

        final FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(lp);
        lp2.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp2.bottomMargin = 10 * p;

        mContent.addView(tv, lp2);

        return mContent;
    }
}
