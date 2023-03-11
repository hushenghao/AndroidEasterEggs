package com.android_l.egg;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class PlatLogoSnapshotProvider extends com.dede.basic.PlatLogoSnapshotProvider {
    @Override
    public View create(Context context) {
        FrameLayout mLayout = new FrameLayout(context);

        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final float dp = dm.density;
        final int size = (int)
                (Math.min(Math.min(dm.widthPixels, dm.heightPixels), 600 * dp) - 100 * dp);

        final View stick = new View(context) {
            Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Path mShadow = new Path();

            @Override
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                setWillNotDraw(false);
                setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRect(0, getHeight() / 2, getWidth(), getHeight());
                    }
                });
            }

            @Override
            public void onDraw(Canvas c) {
                final int w = c.getWidth();
                final int h = c.getHeight() / 2;
                c.translate(0, h);
                final GradientDrawable g = new GradientDrawable();
                g.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                g.setGradientCenter(w * 0.75f, 0);
                g.setColors(new int[]{0xFFFFFFFF, 0xFFAAAAAA});
                g.setBounds(0, 0, w, h);
                g.draw(c);
                mPaint.setColor(0xFFAAAAAA);
                mShadow.reset();
                mShadow.moveTo(0, 0);
                mShadow.lineTo(w, 0);
                mShadow.lineTo(w, size / 2 + 1.5f * w);
                mShadow.lineTo(0, size / 2);
                mShadow.close();
                c.drawPath(mShadow, mPaint);
            }
        };
        mLayout.addView(stick, new FrameLayout.LayoutParams((int) (32 * dp),
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));

        final ImageView im = new ImageView(context);
        im.setTranslationZ(20);
        final Drawable platlogo = context.getDrawable(R.drawable.l_platlogo);
        im.setImageDrawable(platlogo);
        im.setBackground(makeRipple());
        final ShapeDrawable highlight = new ShapeDrawable(new OvalShape());
        highlight.getPaint().setColor(0x10FFFFFF);
        highlight.setBounds((int) (size * .15f), (int) (size * .15f),
                (int) (size * .6f), (int) (size * .6f));
        im.getOverlay().add(highlight);

        mLayout.addView(im, new FrameLayout.LayoutParams(size, size, Gravity.CENTER));
        return mLayout;
    }

    final static int[] FLAVORS = {
            0xFF9C27B0, 0xFFBA68C8, // grape
            0xFFFF9800, 0xFFFFB74D, // orange
            0xFFF06292, 0xFFF8BBD0, // bubblegum
            0xFFAFB42B, 0xFFCDDC39, // lime
            0xFFFFEB3B, 0xFFFFF176, // lemon
            0xFF795548, 0xFFA1887F, // mystery flavor
    };

    static int newColorIndex() {
        return 2 * ((int) (Math.random() * FLAVORS.length / 2));
    }

    Drawable makeRipple() {
        final int idx = newColorIndex();
        final ShapeDrawable popbg = new ShapeDrawable(new OvalShape());
        popbg.getPaint().setColor(FLAVORS[idx]);
        final RippleDrawable ripple = new RippleDrawable(
                ColorStateList.valueOf(FLAVORS[idx + 1]),
                popbg, null);
        return ripple;
    }
}
