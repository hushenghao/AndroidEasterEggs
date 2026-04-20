package com.android_cinnamon_bun.egg;

import static android.os.VibrationEffect.Composition.PRIMITIVE_SPIN;

import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CombinedVibration;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.VibratorManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ChecksSdkIntAtLeast;
import androidx.annotation.RequiresApi;
import androidx.core.os.HandlerCompat;
import androidx.core.view.HapticFeedbackConstantsCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.Random;

/* JADX WARN: Classes with same name are omitted:
  classes5.dex
 */
/* loaded from: /var/folders/sf/d3h9bdnn53dc5z0lw5z5dvc80000gn/T/jadx-13338122255001829222/classes5.dex */
public class PlatLogoActivity extends Activity {
    private static final float MAX_WARP = 16.0f;
    private static final float MIN_WARP = 1.0f;
    private static final String TAG = "PlatLogoActivity";
    private TimeAnimator mAnim;
    private float mDp;
    private View mHeptaDecaView;
    private FrameLayout mLayout;
    private ImageView mLogo;
    private Random mRandom;
    private RumblePack mRumble;
    private Starfield mStarfield;
    private ObjectAnimator mWarpAnim;
    private boolean mAnimationsEnabled = true;
    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() { // from class: com.android.internal.app.PlatLogoActivity.1
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    PlatLogoActivity.this.startWarp();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    PlatLogoActivity.this.stopWarp();
                    break;
            }
            return true;
        }
    };
    private final Runnable mLaunchNextStage = new Runnable() { // from class: com.android.internal.app.PlatLogoActivity$$ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            lambda$new$0();
        }
    };
    private final TimeAnimator.TimeListener mTimeListener = new TimeAnimator.TimeListener() { // from class: com.android.internal.app.PlatLogoActivity.2
        @Override // android.animation.TimeAnimator.TimeListener
        public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
            PlatLogoActivity.this.mStarfield.update(deltaTime);
            float warpFrac = (PlatLogoActivity.this.mStarfield.getWarp() - 1.0f) / 15.0f;
            if (PlatLogoActivity.this.mAnimationsEnabled) {
                PlatLogoActivity.this.mLogo.setTranslationX(PlatLogoActivity.this.mRandom.nextFloat() * warpFrac * 5.0f * PlatLogoActivity.this.mDp);
                PlatLogoActivity.this.mLogo.setTranslationY(PlatLogoActivity.this.mRandom.nextFloat() * warpFrac * 5.0f * PlatLogoActivity.this.mDp);
            }
            if (warpFrac > 0.0f) {
                PlatLogoActivity.this.mRumble.rumble(warpFrac);
            }
            PlatLogoActivity.this.mLayout.postInvalidate();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        stopWarp();
        launchNextStage(false);
    }

    /* JADX WARN: Classes with same name are omitted:
      classes5.dex
     */
    private class RumblePack implements Handler.Callback {
        private static final int INTERVAL = 50;
        private static final int MSG = 6464;
        private boolean mSpinPrimitiveSupported;
        private final Handler mVibeHandler;
        private final VibratorManager mVibeMan;
        private long mLastVibe = 0;
        private final HandlerThread mVibeThread = new HandlerThread("VibratorThread");

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            float warpFrac = msg.arg1 / 100.0f;
            if (!this.mSpinPrimitiveSupported) {
                if (PlatLogoActivity.this.mRandom.nextFloat() < warpFrac) {
                    PlatLogoActivity.this.mLogo.performHapticFeedback(4);
                    return false;
                }
                return false;
            }
            if (msg.getWhen() > this.mLastVibe + 50) {
                this.mLastVibe = msg.getWhen();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && mVibeMan != null) {
                    mVibeMan.vibrate(CombinedVibration.createParallel(
                            VibrationEffect.startComposition()
                                    .addPrimitive(PRIMITIVE_SPIN, (float) Math.pow(warpFrac, 3.0))
                                    .compose()
                    ));
                }
                return false;
            }
            return false;
        }

        RumblePack() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mVibeMan = getSystemService(VibratorManager.class);
                mSpinPrimitiveSupported = mVibeMan.getDefaultVibrator()
                        .areAllPrimitivesSupported(PRIMITIVE_SPIN);
            } else {
                mVibeMan = null;
                mSpinPrimitiveSupported = false;
            }
            this.mVibeThread.start();
            this.mVibeHandler = HandlerCompat.createAsync(this.mVibeThread.getLooper(), this);
        }

        public void destroy() {
            this.mVibeThread.quit();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void rumble(float warpFrac) {
            if (this.mVibeThread.isAlive()) {
                Message msg = Message.obtain();
                msg.what = MSG;
                msg.arg1 = (int) (100.0f * warpFrac);
                this.mVibeHandler.removeMessages(MSG);
                this.mVibeHandler.sendMessage(msg);
            }
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.mRumble.destroy();
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setFitsSystemWindows(false);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // This will be silently ignored on displays that don't support HDR color, which is fine
            getWindow().setColorMode(ActivityInfo.COLOR_MODE_HDR);
        }
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.hide();
        }
        try {
            this.mAnimationsEnabled = Settings.Global.getFloat(getContentResolver(), "animator_duration_scale") > 0.0f;
        } catch (Settings.SettingNotFoundException e) {
            this.mAnimationsEnabled = true;
        }
        this.mRumble = new RumblePack();
        this.mLayout = new FrameLayout(this);
        this.mRandom = new Random();
        this.mDp = getResources().getDisplayMetrics().density;
        this.mStarfield = new Starfield(this.mRandom, this.mDp * 2.0f);
        this.mStarfield.setWarp(0.1f);
        this.mLayout.setBackground(this.mStarfield);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float dp = dm.density;
        int minSide = Math.min(dm.widthPixels, dm.heightPixels);
        int widgetSize = (int) (minSide * 0.75d);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(widgetSize, widgetSize);
        lp.gravity = 17;
        this.mLogo = new ImageView(this);
        this.mLogo.setImageResource(R.drawable.cinnamon_bun_platlogo);
        this.mLogo.setOnTouchListener(this.mTouchListener);
        this.mLogo.setVisibility(View.GONE);
        this.mHeptaDecaView = new View(this);
        final Heptadecagram heptadecagram = new Heptadecagram(dp);
        this.mHeptaDecaView.setBackground(heptadecagram);
        this.mHeptaDecaView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.internal.app.PlatLogoActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return lambda$onCreate$1(heptadecagram, view, motionEvent);
            }
        });
        this.mLayout.addView(this.mHeptaDecaView, lp);
        this.mLayout.addView(this.mLogo, lp);
        setContentView(this.mLayout);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$1(Heptadecagram heptadecagram, View v, MotionEvent event) {
        if (heptadecagram.onTouch(event)) {
            this.mHeptaDecaView.performHapticFeedback(HapticFeedbackConstantsCompat.CONFIRM);
        }
        if (heptadecagram.getPathLength() > 17) {
            swapToPlatlogo();
            return true;
        }
        return true;
    }

    private void swapToPlatlogo() {
        this.mHeptaDecaView.animate().alpha(0.0f).setDuration(500L).withEndAction(new Runnable() { // from class: com.android.internal.app.PlatLogoActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                lambda$swapToPlatlogo$2();
            }
        }).start();
        this.mLogo.setAlpha(0.0f);
        this.mLogo.setVisibility(View.VISIBLE);
        this.mLogo.animate().alpha(1.0f).setDuration(500L).start();
        this.mLogo.requestFocus();
        ObjectAnimator.ofFloat(this.mStarfield, "warp", 1.0f).setDuration(250L).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$swapToPlatlogo$2() {
        this.mHeptaDecaView.setVisibility(View.GONE);
    }

    private void startAnimating() {
        this.mAnim = new TimeAnimator();
        this.mAnim.setTimeListener(this.mTimeListener);
        this.mAnim.start();
    }

    private void stopAnimating() {
        this.mAnim.cancel();
        this.mAnim = null;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            if (this.mLogo.getVisibility() != View.VISIBLE) {
                swapToPlatlogo();
            }
            if (event.getRepeatCount() == 0) {
                startWarp();
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            stopWarp();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWarp() {
        stopWarp();
        this.mWarpAnim = ObjectAnimator.ofFloat(this.mStarfield, "warp", 1.0f, MAX_WARP).setDuration(5000L);
        this.mWarpAnim.start();
        this.mLogo.postDelayed(this.mLaunchNextStage, 6000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopWarp() {
        if (this.mWarpAnim != null) {
            this.mWarpAnim.cancel();
            this.mWarpAnim.removeAllListeners();
            this.mWarpAnim = null;
        }
        this.mStarfield.setWarp(1.0f);
        this.mLogo.removeCallbacks(this.mLaunchNextStage);
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        startAnimating();
    }

    @Override // android.app.Activity
    public void onPause() {
        stopWarp();
        stopAnimating();
        super.onPause();
    }

    private void launchNextStage(boolean locked) {
        try {
            Intent eggActivity = new Intent(this, Class.forName("com.android_baklava.egg.landroid.MainActivity"));
            startActivity(eggActivity);
            Toast.makeText(this, "launch Android 16 Easter Egg", Toast.LENGTH_SHORT).show();
        } catch (Exception e2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    private static final boolean isSRgbExtSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

    public static int packColor(float value, float alpha) {
        int v = (int) (value * 255 + 0.5f);
        int a = (int) (alpha * 255 + 0.5f);
        return Color.argb(a, v, v, v);
    }

    private static ColorSpace sSrgbExt;

    @RequiresApi(api = Build.VERSION_CODES.O)
    /* JADX INFO: Access modifiers changed from: private */
    public static long packHdrWhite(float value, float alpha) {
        if (sSrgbExt == null) {
            sSrgbExt = ColorSpace.get(ColorSpace.Named.EXTENDED_SRGB);
        }
        return Color.valueOf(value, value, value, alpha, sSrgbExt).pack();
    }


    /* JADX INFO: Access modifiers changed from: private */
    public static boolean pointInRadius(float x, float y, float r) {
        return (x * x) + (y * y) < r * r;
    }

    /* JADX WARN: Classes with same name are omitted:
      classes5.dex
     */
    public static class Starfield extends Drawable {
        private static final int NUM_PLANES = 4;
        private static final int NUM_STARS = 128;
        private static final float ROTATION = 45.0f;
        private float mBuffer;
        private final Random mRng;
        private final float mSize;
        private final float[] mStars = new float[512];
        private long mDt = 0;
        private float mRadius = 0.0f;
        private float mWarp = 1.0f;
        private final Paint mStarPaint = new Paint();

        public void setWarp(float warp) {
            this.mWarp = warp;
        }

        public float getWarp() {
            return this.mWarp;
        }

        Starfield(Random rng, float size) {
            this.mRng = rng;
            this.mSize = size;
            this.mStarPaint.setStyle(Paint.Style.STROKE);
            this.mStarPaint.setColor(Color.WHITE);
        }

        @Override // android.graphics.drawable-nodpi.Drawable
        public void onBoundsChange(Rect bounds) {
            this.mBuffer = this.mSize * 4.0f * 2.0f * PlatLogoActivity.MAX_WARP;
            this.mRadius = (((float) Math.hypot(bounds.width(), bounds.height())) / 2.0f) + this.mBuffer;
            for (int i = 0; i < 128; i++) {
                double angle = this.mRng.nextDouble() * 2.0d * 3.141592653589793d;
                float dist = this.mRng.nextFloat() * this.mRadius;
                this.mStars[(i * 4) + 2] = (float) (Math.cos(angle) * dist);
                this.mStars[(i * 4) + 3] = (float) (Math.sin(angle) * dist);
                this.mStars[(i * 4) + 0] = -10000.0f;
                this.mStars[(i * 4) + 1] = -10000.0f;
            }
        }

        @Override // android.graphics.drawable-nodpi.Drawable
        public void draw(Canvas canvas) {
            int i;
            float cx;
            float cy;
            float dtSec = this.mDt / 1000.0f;
            int i2 = 1;
            boolean inWarp = this.mWarp > 1.0f;
            canvas.drawColor(-16777216);
            float cx2 = getBounds().width() / 2.0f;
            float cy2 = getBounds().height() / 2.0f;
            canvas.translate(cx2, cy2);
            canvas.rotate(ROTATION);
            if (this.mDt > 0 && this.mDt < 1000) {
                canvas.translate(this.mRng.nextFloat() * (this.mWarp - 1.0f), this.mRng.nextFloat() * (this.mWarp - 1.0f));
                float speedBase = 0.05f * dtSec * this.mWarp;
                int i3 = 0;
                while (i3 < 128) {
                    int plane = ((int) ((i3 / 128.0f) * 4.0f)) + i2;
                    float x = this.mStars[(i3 * 4) + 2];
                    float y = this.mStars[(i3 * 4) + 3];
                    float speed = plane * speedBase;
                    float x2 = x + (x * speed);
                    float y2 = y + (y * speed);
                    if (PlatLogoActivity.pointInRadius(x2, y2, this.mRadius)) {
                        i = i2;
                        cx = cx2;
                        cy = cy2;
                    } else {
                        double angle = this.mRng.nextDouble() * 2.0d * 3.141592653589793d;
                        i = i2;
                        float dist = this.mRng.nextFloat() * 0.1f * this.mRadius;
                        cy = cy2;
                        x2 = (float) (dist * Math.cos(angle));
                        cx = cx2;
                        y2 = (float) (Math.sin(angle) * dist);
                    }
                    this.mStars[(i3 * 4) + 2] = x2;
                    this.mStars[(i3 * 4) + 3] = y2;
                    if (inWarp) {
                        float tailScale = 1.0f / ((this.mWarp * speed) + 1.0f);
                        this.mStars[(i3 * 4) + 0] = x2 * tailScale;
                        this.mStars[(i3 * 4) + 1] = y2 * tailScale;
                    } else {
                        this.mStars[(i3 * 4) + 0] = -10000.0f;
                        this.mStars[(i3 * 4) + 1] = -10000.0f;
                    }
                    i3++;
                    i2 = i;
                    cx2 = cx;
                    cy2 = cy;
                }
            }
            int slice = ((this.mStars.length / 4) / 4) * 4;
            for (int p = 0; p < 4; p++) {
                float value = (p + 1.0f) / 3.0f;
                if (isSRgbExtSupported) {
                    mStarPaint.setColor(packHdrWhite(value, 1.0f));
                } else {
                    mStarPaint.setColor(packColor(value, 1.0f));
                }
                this.mStarPaint.setStrokeWidth(this.mSize * (p + 1));
                if (inWarp) {
                    canvas.drawLines(this.mStars, p * slice, slice, this.mStarPaint);
                }
                canvas.drawPoints(this.mStars, p * slice, slice, this.mStarPaint);
            }
            if (inWarp) {
                float frac = (this.mWarp - 1.0f) / 15.0f;
                if (isSRgbExtSupported) {
                    canvas.drawColor(packHdrWhite(2.0f, frac * frac));
                } else {
                    canvas.drawColor(packColor(2.0f, frac * frac));
                }
            }
        }

        @Override // android.graphics.drawable-nodpi.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable-nodpi.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable-nodpi.Drawable
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

        public void update(long dt) {
            this.mDt = dt;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Classes with same name are omitted:
      classes5.dex
     */
    static class Heptadecagram extends Drawable {
        public static final int MAX_DOTS = 17;
        private final Paint mBgPaint;
        private float mDotRadius;
        private final float mDp;
        private float mHitRadius;
        private final Paint mLinePaint;
        private float mRadius;
        private float mTouchX;
        private float mTouchY;
        private final float[] mDotsXY = new float[34];
        private final int[] mPath = new int[18];
        private int mPathLength = 0;
        private boolean mIsTracking = false;
        private Path mDrawingPath = new Path();
        private final Paint mDotPaint = new Paint(1);

        Heptadecagram(float dp) {
            this.mDp = dp;
            if (isSRgbExtSupported) {
                this.mDotPaint.setColor(packHdrWhite(1.5f, 1.0f));
            } else {
                this.mDotPaint.setColor(packColor(1.5f, 1.0f));
            }
            this.mDotPaint.setStyle(Paint.Style.FILL);
            this.mLinePaint = new Paint(1);
            this.mLinePaint.setColor(-5038209);
            this.mLinePaint.setStyle(Paint.Style.STROKE);
            this.mLinePaint.setStrokeWidth(4.0f * dp);
            this.mLinePaint.setStrokeJoin(Paint.Join.ROUND);
            this.mLinePaint.setStrokeCap(Paint.Cap.ROUND);
            this.mBgPaint = new Paint(1);
            this.mBgPaint.setColor(-16777216);
            this.mDotPaint.setStyle(Paint.Style.FILL);
        }

        @Override // android.graphics.drawable-nodpi.Drawable
        public void onBoundsChange(Rect bounds) {
            float cx = bounds.width() / 2.0f;
            float cy = bounds.height() / 2.0f;
            this.mRadius = Math.min(cx, cy) * 0.9f;
            this.mDotRadius = this.mDp * 4.0f;
            this.mHitRadius = this.mDp * 24.0f;
            for (int i = 0; i < 17; i++) {
                double angle = ((i * 6.283185307179586d) / 17.0d) - 1.5707963267948966d;
                this.mDotsXY[i * 2] = (((float) Math.cos(angle)) * this.mRadius) + cx;
                this.mDotsXY[(i * 2) + 1] = (((float) Math.sin(angle)) * this.mRadius) + cy;
            }
        }

        public boolean onTouch(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    this.mPathLength = 0;
                    this.mIsTracking = true;
                    this.mTouchX = x;
                    this.mTouchY = y;
                    invalidateSelf();
                    return checkDot(x, y);
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    this.mIsTracking = false;
                    this.mPathLength = 0;
                    invalidateSelf();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (this.mIsTracking) {
                        this.mTouchX = x;
                        this.mTouchY = y;
                        invalidateSelf();
                        return checkDot(x, y);
                    }
                    break;
            }
            if (this.mPathLength != 18) {
                return false;
            }
            this.mIsTracking = false;
            return true;
        }

        private boolean checkDot(float x, float y) {
            for (int i = 0; i < 17; i++) {
                float dx = x - this.mDotsXY[i * 2];
                float dy = y - this.mDotsXY[(i * 2) + 1];
                if (PlatLogoActivity.pointInRadius(dx, dy, this.mHitRadius)) {
                    int i2 = this.mPathLength;
                    int[] iArr = this.mPath;
                    if (i2 == 0) {
                        int i3 = this.mPathLength;
                        this.mPathLength = i3 + 1;
                        iArr[i3] = i;
                        return true;
                    }
                    int lastDot = iArr[this.mPathLength - 1];
                    if (lastDot != i) {
                        boolean visited = false;
                        int j = 0;
                        while (true) {
                            if (j >= this.mPathLength) {
                                break;
                            }
                            if (this.mPath[j] != i) {
                                j++;
                            } else {
                                visited = true;
                                break;
                            }
                        }
                        if (!visited || (this.mPathLength == 17 && this.mPath[0] == i)) {
                            int[] iArr2 = this.mPath;
                            int i4 = this.mPathLength;
                            this.mPathLength = i4 + 1;
                            iArr2[i4] = i;
                            return true;
                        }
                    } else {
                        continue;
                    }
                }
            }
            return false;
        }

        private void drawTargetDot(Canvas canvas, float x, float y, float r, Paint paint) {
            canvas.save();
            canvas.translate(x, y);
            canvas.rotate(45.0f);
            canvas.drawRect(-r, -r, r, r, paint);
            canvas.restore();
        }

        @Override // android.graphics.drawable-nodpi.Drawable
        public void draw(Canvas canvas) {
            canvas.drawCircle(getBounds().width() * 0.5f, getBounds().height() * 0.5f, this.mRadius, this.mBgPaint);
            if (this.mPathLength > 0) {
                this.mDrawingPath.reset();
                this.mDrawingPath.moveTo(this.mDotsXY[this.mPath[0] * 2], this.mDotsXY[(this.mPath[0] * 2) + 1]);
                for (int i = 1; i < this.mPathLength; i++) {
                    this.mDrawingPath.lineTo(this.mDotsXY[this.mPath[i] * 2], this.mDotsXY[(this.mPath[i] * 2) + 1]);
                }
                if (this.mIsTracking && this.mPathLength <= 17) {
                    this.mDrawingPath.lineTo(this.mTouchX, this.mTouchY);
                }
                canvas.drawPath(this.mDrawingPath, this.mLinePaint);
            }
            for (int i2 = 0; i2 < 17; i2++) {
                drawTargetDot(canvas, this.mDotsXY[i2 * 2], this.mDotsXY[(i2 * 2) + 1], this.mDotRadius, this.mDotPaint);
            }
        }

        @Override // android.graphics.drawable-nodpi.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable-nodpi.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable-nodpi.Drawable
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        public int getPathLength() {
            return this.mPathLength;
        }
    }
}
