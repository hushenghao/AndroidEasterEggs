package com.android_u.egg;

import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
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
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.dede.basic.SpUtils;

import java.util.Random;

/* loaded from: classes4.dex */
public class PlatLogoActivity extends Activity {
    private static final float MAX_WARP = 10.0f;
    private static final String TAG = "PlatLogoActivity";
    private static final String U_EGG_UNLOCK_SETTING = "egg_mode_u";
    private TimeAnimator mAnim;
    private float mDp;
    private FrameLayout mLayout;
    private ImageView mLogo;
    private Random mRandom;
    private RumblePack mRumble;
    private Starfield mStarfield;
    private ObjectAnimator mWarpAnim;
    private boolean mAnimationsEnabled = true;

    // from class: com.android.internal.app.PlatLogoActivity.1
    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    PlatLogoActivity.this.startWarp();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    PlatLogoActivity.this.stopWarp();
                    return true;
                case MotionEvent.ACTION_MOVE:
                default:
                    return false;
            }
        }
    };
    private final Runnable mLaunchNextStage = new Runnable() { // from class: com.android.internal.app.PlatLogoActivity$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public void run() {
            PlatLogoActivity.this.lambda$new$0();
        }
    };
    private final TimeAnimator.TimeListener mTimeListener = new TimeAnimator.TimeListener() { // from class: com.android.internal.app.PlatLogoActivity.2
        @Override // android.animation.TimeAnimator.TimeListener
        public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
            PlatLogoActivity.this.mStarfield.update(deltaTime);
            float warpFrac = (PlatLogoActivity.this.mStarfield.getWarp() - 1.0f) / 9.0f;
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

    /* loaded from: classes4.dex */
    private class RumblePack implements Handler.Callback {
        private static final int MSG = 6464;
        private long mLastVibe = 0;
        private boolean mSpinPrimitiveSupported = false;
        private final Handler mVibeHandler;
        @Nullable
        private final VibratorManager mVibeMan;
        private final HandlerThread mVibeThread;

        @Override // android.p009os.Handler.Callback
        public boolean handleMessage(Message msg) {
            float warpFrac = msg.arg1 / 100.0f;
            if (this.mSpinPrimitiveSupported) {
                if (msg.getWhen() > this.mLastVibe + 50) {
                    this.mLastVibe = msg.getWhen();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && mVibeMan != null) {
                        VibrationEffect vibrationEffect = VibrationEffect.startComposition()
                                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SPIN, (float) Math.pow(warpFrac, 3.0d))
                                .compose();
                        this.mVibeMan.vibrate(CombinedVibration.createParallel(vibrationEffect));
                    }
                    return false;
                }
                return false;
            } else if (PlatLogoActivity.this.mRandom.nextFloat() < warpFrac) {
                PlatLogoActivity.this.mLogo.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
                return false;
            } else {
                return false;
            }
        }

        RumblePack() {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                VibratorManager vibratorManager = (VibratorManager) PlatLogoActivity.this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                this.mVibeMan = vibratorManager;
                this.mSpinPrimitiveSupported = vibratorManager.getDefaultVibrator()
                        .areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_SPIN);
            } else {
                this.mVibeMan = null;
            }
            HandlerThread handlerThread = new HandlerThread("VibratorThread");
            this.mVibeThread = handlerThread;
            handlerThread.start();
            this.mVibeHandler = HandlerCompat.createAsync(handlerThread.getLooper(), this);
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onDestroy() {
        this.mRumble.destroy();
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        getWindow().getDecorView().setFitsSystemWindows(false);
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
        Starfield starfield = new Starfield(this.mRandom, this.mDp * 2.0f);
        this.mStarfield = starfield;
        starfield.setVelocity((this.mRandom.nextFloat() - 0.5f) * 200.0f, (this.mRandom.nextFloat() - 0.5f) * 200.0f);
        this.mLayout.setBackground(this.mStarfield);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int minSide = Math.min(dm.widthPixels, dm.heightPixels);
        int widgetSize = (int) (minSide * 0.75d);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(widgetSize, widgetSize);
        lp.gravity = Gravity.CENTER;
        ImageView imageView = new ImageView(this);
        this.mLogo = imageView;
        imageView.setImageResource(R.drawable.u_platlogo);
        this.mLogo.setOnTouchListener(this.mTouchListener);
        this.mLogo.requestFocus();
        this.mLayout.addView(this.mLogo, lp);
        Log.v(TAG, "Hello");
        setContentView(this.mLayout);
    }

    private void startAnimating() {
        TimeAnimator timeAnimator = new TimeAnimator();
        this.mAnim = timeAnimator;
        timeAnimator.setTimeListener(this.mTimeListener);
        this.mAnim.start();
    }

    private void stopAnimating() {
        this.mAnim.cancel();
        this.mAnim = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWarp() {
        stopWarp();
        ObjectAnimator duration = ObjectAnimator.ofFloat(this.mStarfield, "warp", 1.0f, MAX_WARP).setDuration(5000L);
        this.mWarpAnim = duration;
        duration.start();
        this.mLogo.postDelayed(this.mLaunchNextStage, 6000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopWarp() {
        ObjectAnimator objectAnimator = this.mWarpAnim;
        if (objectAnimator != null) {
            objectAnimator.cancel();
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
        Log.v(TAG, "Saving egg locked=" + locked);
        SpUtils.putLong(this, U_EGG_UNLOCK_SETTING, locked ? 0L : System.currentTimeMillis());
        try {
            // It cannot be decompiled,
            // and the kotlin version is not compatible as a jar package dependency.
            //  R. reference issue needs to be resolved
            Toast.makeText(this, "Decompiled version does not support more features!", Toast.LENGTH_SHORT).show();
//            Intent eggActivity = new Intent(Intent.ACTION_MAIN);
//                    .setClass(this, Class.forName("com.android.egg.landroid.MainActivity"));
//            Log.v(TAG, "launching: " + eggActivity);
//            startActivity(eggActivity);
        } catch (ActivityNotFoundException e2) {
            Log.e(TAG, "No more eggs.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class Starfield extends Drawable {
        private float mBuffer;
        private final Random mRng;
        private final float mSize;
        private final Paint mStarPaint;
        private float mVx;
        private float mVy;
        private final float[] mStars = new float[136];
        private long mDt = 0;
        private final Rect mSpace = new Rect();
        private float mWarp = 1.0f;

        public void setWarp(float warp) {
            this.mWarp = warp;
        }

        public float getWarp() {
            return this.mWarp;
        }

        Starfield(Random rng, float size) {
            this.mRng = rng;
            this.mSize = size;
            Paint paint = new Paint();
            this.mStarPaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(-1);
        }

        @Override // android.graphics.drawable.Drawable
        public void onBoundsChange(Rect bounds) {
            this.mSpace.set(bounds);
            float f = this.mSize * 2.0f * 2.0f * PlatLogoActivity.MAX_WARP;
            this.mBuffer = f;
            this.mSpace.inset(-((int) f), -((int) f));
            float w = this.mSpace.width();
            float h = this.mSpace.height();
            for (int i = 0; i < 34; i++) {
                this.mStars[i * 4] = this.mRng.nextFloat() * w;
                this.mStars[(i * 4) + 1] = this.mRng.nextFloat() * h;
                float[] fArr = this.mStars;
                fArr[(i * 4) + 2] = fArr[i * 4];
                fArr[(i * 4) + 3] = fArr[(i * 4) + 1];
            }
        }

        public void setVelocity(float x, float y) {
            this.mVx = x;
            this.mVy = y;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            float dtSec = ((float) this.mDt) / 1000.0f;
            float f = this.mWarp;
            float dx = this.mVx * dtSec * f;
            float dy = this.mVy * dtSec * f;
            int i = 0;
            int i2 = 1;
            boolean inWarp = f > 1.0f;
            canvas.drawColor(-16777216);
            long j = this.mDt;
            if (j > 0 && j < 1000) {
                canvas.translate((-this.mBuffer) + (this.mRng.nextFloat() * (this.mWarp - 1.0f)), (-this.mBuffer) + (this.mRng.nextFloat() * (this.mWarp - 1.0f)));
                float w = this.mSpace.width();
                float h = this.mSpace.height();
                int i3 = 0;
                while (i3 < 34) {
                    int plane = ((int) ((i3 / 34.0f) * 2.0f)) + i2;
                    float[] fArr = this.mStars;
                    fArr[(i3 * 4) + 2] = ((fArr[(i3 * 4) + 2] + (plane * dx)) + w) % w;
                    fArr[(i3 * 4) + 3] = ((fArr[(i3 * 4) + 3] + (plane * dy)) + h) % h;
                    fArr[(i3 * 4) + i] = inWarp ? fArr[(i3 * 4) + 2] - (((this.mWarp * dx) * 2.0f) * plane) : -100.0f;
                    fArr[(i3 * 4) + 1] = inWarp ? fArr[(i3 * 4) + 3] - (((this.mWarp * dy) * 2.0f) * plane) : -100.0f;
                    i3++;
                    i = 0;
                }
            }
            int slice = ((this.mStars.length / 2) / 4) * 4;
            for (int p = 0; p < 2; p++) {
                this.mStarPaint.setStrokeWidth(this.mSize * (p + 1));
                if (inWarp) {
                    canvas.drawLines(this.mStars, p * slice, slice, this.mStarPaint);
                }
                canvas.drawPoints(this.mStars, p * slice, slice, this.mStarPaint);
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

        public void update(long dt) {
            this.mDt = dt;
        }
    }
}