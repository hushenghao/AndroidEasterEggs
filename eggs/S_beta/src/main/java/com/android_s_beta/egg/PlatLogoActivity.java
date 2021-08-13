package com.android_s_beta.egg;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AnalogClock;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class PlatLogoActivity extends Activity {
    private static final String TAG = "PlatLogoActivity";
    private BubblesDrawable mBg;
    private SettableAnalogClock mClock;
    private ImageView mLogo;

    /* access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(0);
        getWindow().setStatusBarColor(0);
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.hide();
        }
        FrameLayout layout = new FrameLayout(this);
        this.mClock = new SettableAnalogClock(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float dp = dm.density;
        int widgetSize = (int) (((double) Math.min(dm.widthPixels, dm.heightPixels)) * 0.75d);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(widgetSize, widgetSize);
        lp.gravity = 17;
        layout.addView(this.mClock, lp);
        ImageView imageView = new ImageView(this);
        this.mLogo = imageView;
        imageView.setVisibility(View.GONE);
        this.mLogo.setImageResource(R.drawable.s_platlogo);
        layout.addView(this.mLogo, lp);
        BubblesDrawable bubblesDrawable = new BubblesDrawable();
        this.mBg = bubblesDrawable;
        bubblesDrawable.setLevel(0);
        this.mBg.avoid = (float) (widgetSize / 2);
        this.mBg.padding = 0.5f * dp;
        this.mBg.minR = 1.0f * dp;
        layout.setBackground(this.mBg);
        setContentView(layout);

        // to beta
        this.mClock.setOnClickListener(v -> {
            launchNextStage(false);
        });
    }

    private void launchNextStage(boolean locked) {
        this.mClock.animate()
                .alpha(0.0f)
                .scaleX(0.5f)
                .scaleY(0.5f)
                .withEndAction(() -> this.mClock.setVisibility(View.GONE)).start();
        this.mLogo.setAlpha(0.0f);
        this.mLogo.setScaleX(0.5f);
        this.mLogo.setScaleY(0.5f);
        this.mLogo.setVisibility(View.VISIBLE);
        this.mLogo.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setInterpolator(new OvershootInterpolator()).start();
        this.mLogo.postDelayed(() -> {
            ObjectAnimator anim = ObjectAnimator.ofInt(this.mBg, "level", 0, 10000);
            anim.setInterpolator(new DecelerateInterpolator(1.0f));
            anim.start();
        }, 500);
    }

    public class SettableAnalogClock extends AnalogClock {
        private boolean mOverride = false;
        private int mOverrideHour = -1;
        private int mOverrideMinute = -1;

        @SuppressLint("DiscouragedPrivateApi")
        public SettableAnalogClock(Context context) {
            super(context);
            try {
                Field mDial = AnalogClock.class.getDeclaredField("mDial");
                mDial.setAccessible(true);
                mDial.set(this, context.getDrawable(R.drawable.clock_dial));
                Field mHourHand = AnalogClock.class.getDeclaredField("mHourHand");
                mHourHand.setAccessible(true);
                mHourHand.set(this, context.getDrawable(R.drawable.colok_hand_hour));
                Field mMinuteHand = AnalogClock.class.getDeclaredField("mMinuteHand");
                mMinuteHand.setAccessible(true);
                mMinuteHand.set(this, context.getDrawable(R.drawable.colok_hand_minute));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* access modifiers changed from: protected */
//        @Override // android.widget.AnalogClock
        @RequiresApi(api = Build.VERSION_CODES.O)
        public Instant now() {
            Instant realNow = Clock.systemDefaultZone().instant();
            ZoneId tz = Clock.systemDefaultZone().getZone();
            ZonedDateTime zdTime = realNow.atZone(tz);
            if (!this.mOverride) {
                return realNow;
            }
            if (this.mOverrideHour < 0) {
                this.mOverrideHour = zdTime.getHour();
            }
            return Clock.fixed(zdTime.withHour(this.mOverrideHour).withMinute(this.mOverrideMinute).withSecond(0).toInstant(), tz).instant();
        }

        /* access modifiers changed from: package-private */
        public double toPositiveDegrees(double rad) {
            return ((Math.toDegrees(rad) + 360.0d) - 90.0d) % 360.0d;
        }

        /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
//        @Override // android.view.View
//        public boolean onTouchEvent(MotionEvent ev) {
//            int i;
//            switch (ev.getActionMasked()) {
//                case MotionEvent.ACTION_DOWN:
//                    this.mOverride = true;
//                    break;
//                case MotionEvent.ACTION_UP:
//                    if (this.mOverrideMinute == 0 && this.mOverrideHour % 12 == 0) {
//                        Log.v(PlatLogoActivity.TAG, "12:00 let's gooooo");
//                        performHapticFeedback(0);
//                        PlatLogoActivity.this.launchNextStage(false);
//                    }
//                    return true;
//                case MotionEvent.ACTION_MOVE:
//                    break;
//                default:
//                    return false;
//            }
//            int minutes = (75 - ((int) (((float) toPositiveDegrees(Math.atan2((double) (ev.getX() - (((float) getWidth()) / 2.0f)), (double) (ev.getY() - (((float) getHeight()) / 2.0f))))) / 6.0f))) % 60;
//            int minuteDelta = minutes - this.mOverrideMinute;
//            if (minuteDelta != 0) {
//                if (Math.abs(minuteDelta) > 45 && (i = this.mOverrideHour) >= 0) {
//                    this.mOverrideHour = ((i + 24) + (minuteDelta < 0 ? 1 : -1)) % 24;
//                }
//                this.mOverrideMinute = minutes;
//                if (minutes == 0) {
//                    performHapticFeedback(0);
//                    if (getScaleX() == 1.0f) {
//                        setScaleX(1.05f);
//                        setScaleY(1.05f);
//                        animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
//                    }
//                } else {
//                    performHapticFeedback(4);
//                }
//                //onTimeChanged();
//                postInvalidate();
//            }
//            return true;
//        }
    }

    /* access modifiers changed from: package-private */
    public static class Bubble {
        public int color;
        public float r;
        public float x;
        public float y;

        Bubble() {
        }
    }

    /* access modifiers changed from: package-private */
    public class BubblesDrawable extends Drawable {
        private static final int MAX_BUBBS = 2000;
        public float avoid = 0.0f;
        private final Bubble[] mBubbs = new Bubble[MAX_BUBBS];
        private final int[] mColorIds;
        private int[] mColors;
        private int mNumBubbs;
        private final Paint mPaint = new Paint(1);
        public float minR = 0.0f;
        public float padding = 0.0f;

        BubblesDrawable() {
            int[] iArr = {0xff598df7, 0xff3771df, 0xff2559bc, 0xff8a91a3, 0xff707687, 0xff585e6f};
            this.mColorIds = iArr;
            this.mColors = new int[iArr.length];
            int i = 0;
            while (true) {
                int[] iArr2 = this.mColorIds;
                if (i >= iArr2.length) {
                    break;
                }
                this.mColors[i] = iArr2[i];
                i++;
            }
            int j = 0;
            while (true) {
                Bubble[] bubbleArr = this.mBubbs;
                if (j < bubbleArr.length) {
                    bubbleArr[j] = new Bubble();
                    j++;
                } else {
                    return;
                }
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            float f = ((float) getLevel()) / 10000.0f;
            this.mPaint.setStyle(Paint.Style.FILL);
            int drawn = 0;
            for (int j = 0; j < this.mNumBubbs; j++) {
                if (!(this.mBubbs[j].color == 0 || this.mBubbs[j].r == 0.0f)) {
                    this.mPaint.setColor(this.mBubbs[j].color);
                    canvas.drawCircle(this.mBubbs[j].x, this.mBubbs[j].y, this.mBubbs[j].r * f, this.mPaint);
                    drawn++;
                }
            }
        }

        /* access modifiers changed from: protected */
        @Override // android.graphics.drawable.Drawable
        public boolean onLevelChange(int level) {
            invalidateSelf();
            return true;
        }

        /* access modifiers changed from: protected */
        @Override // android.graphics.drawable.Drawable
        public void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            randomize();
        }

        private void randomize() {
            float x;
            float w = (float) getBounds().width();
            float h = (float) getBounds().height();
            float maxR = Math.min(w, h) / 3.0f;
            this.mNumBubbs = 0;
            if (this.avoid > 0.0f) {
                this.mBubbs[0].x = w / 2.0f;
                this.mBubbs[this.mNumBubbs].y = h / 2.0f;
                this.mBubbs[this.mNumBubbs].r = this.avoid;
                this.mBubbs[this.mNumBubbs].color = 0;
                this.mNumBubbs++;
            }
            for (int j = 0; j < 2000; j++) {
                int tries = 5;
                while (true) {
                    int tries2 = tries - 1;
                    if (tries <= 0) {
                        break;
                    }
                    float x2 = ((float) Math.random()) * w;
                    float y = ((float) Math.random()) * h;
                    float r = Math.min(Math.min(x2, w - x2), Math.min(y, h - y));
                    int i = 0;
                    while (true) {
                        if (i >= this.mNumBubbs) {
                            x = x2;
                            break;
                        }
                        x = x2;
                        r = (float) Math.min((double) r, (Math.hypot((double) (x2 - this.mBubbs[i].x), (double) (y - this.mBubbs[i].y)) - ((double) this.mBubbs[i].r)) - ((double) this.padding));
                        if (r < this.minR) {
                            break;
                        }
                        i++;
                        x2 = x;
                    }
                    if (r >= this.minR) {
                        float r2 = Math.min(maxR, r);
                        this.mBubbs[this.mNumBubbs].x = x;
                        this.mBubbs[this.mNumBubbs].y = y;
                        this.mBubbs[this.mNumBubbs].r = r2;
                        this.mBubbs[this.mNumBubbs].color = this.mColors[(int) (Math.random() * ((double) this.mColors.length))];
                        this.mNumBubbs++;
                        break;
                    }
                    tries = tries2;
                }
            }
            Log.v(PlatLogoActivity.TAG, String.format("successfully placed %d bubbles (%d%%)", Integer.valueOf(this.mNumBubbs), Integer.valueOf((int) ((((float) this.mNumBubbs) * 100.0f) / 2000.0f))));
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
