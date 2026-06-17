/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android_cinnamon_bun.egg;

import static android.os.VibrationEffect.Composition.PRIMITIVE_SPIN;

import static java.lang.Math.hypot;

import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.ChecksSdkIntAtLeast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.os.HandlerCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.Random;

import com.dede.basic.SpUtils;

/**
 * @hide
 */
public class PlatLogoActivity extends Activity {
    private static final String TAG = "PlatLogoActivity";

    private static final long LAUNCH_TIME = 5000L;

    private static final String EGG_UNLOCK_SETTING = "egg_mode_cinnamon_bun";

    private static final float MIN_WARP = 1f;
    private static final float MAX_WARP = 16f; // must go faster
    private static final boolean FINISH_AFTER_NEXT_STAGE_LAUNCH = false;

    private ImageView mLogo;
    private View mHeptaDecaView;
    private Starfield mStarfield;

    private FrameLayout mLayout;

    private TimeAnimator mAnim;
    private ObjectAnimator mWarpAnim;
    private Random mRandom;
    private float mDp;

    private RumblePack mRumble;

    private boolean mAnimationsEnabled = true;

    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startWarp();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopWarp();
                    break;
            }
            return true;
        }

    };

    private final Runnable mLaunchNextStage = () -> {
        stopWarp();
        launchNextStage(false);
    };

    private final TimeAnimator.TimeListener mTimeListener = new TimeAnimator.TimeListener() {
        @Override
        public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
            mStarfield.update(deltaTime);
            final float warpFrac = (mStarfield.getWarp() - MIN_WARP) / (MAX_WARP - MIN_WARP);
            if (mAnimationsEnabled) {
                mLogo.setTranslationX(mRandom.nextFloat() * warpFrac * 5 * mDp);
                mLogo.setTranslationY(mRandom.nextFloat() * warpFrac * 5 * mDp);
            }
            if (warpFrac > 0f) {
                mRumble.rumble(warpFrac);
            }
            mLayout.postInvalidate();
        }
    };

    private class RumblePack implements Handler.Callback {
        private static final int MSG = 6464;
        private static final int INTERVAL = 50;

        private final VibratorManager mVibeMan;
        private final HandlerThread mVibeThread;
        private final Handler mVibeHandler;
        private boolean mSpinPrimitiveSupported;

        private long mLastVibe = 0;

        @Override
        public boolean handleMessage(Message msg) {
            final float warpFrac = msg.arg1 / 100f;
            if (mSpinPrimitiveSupported) {
                if (msg.getWhen() > mLastVibe + INTERVAL) {
                    mLastVibe = msg.getWhen();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && mVibeMan != null) {
                        mVibeMan.vibrate(CombinedVibration.createParallel(
                                VibrationEffect.startComposition()
                                        .addPrimitive(PRIMITIVE_SPIN, (float) Math.pow(warpFrac, 3.0))
                                        .compose()
                        ));
                    }
                }
            } else {
                if (mRandom.nextFloat() < warpFrac) {
                    mLogo.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
                }
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

            mVibeThread = new HandlerThread("VibratorThread");
            mVibeThread.start();
            mVibeHandler = HandlerCompat.createAsync(mVibeThread.getLooper(), this);
        }

        public void destroy() {
            mVibeThread.quit();
        }

        private void rumble(float warpFrac) {
            if (!mVibeThread.isAlive()) return;

            final Message msg = Message.obtain();
            msg.what = MSG;
            msg.arg1 = (int) (warpFrac * 100);
            mVibeHandler.removeMessages(MSG);
            mVibeHandler.sendMessage(msg);
        }

    }

    @Override
    protected void onDestroy() {
        mRumble.destroy();

        super.onDestroy();
    }

    @Override
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

        final ActionBar ab = getActionBar();
        if (ab != null) ab.hide();

        try {
            mAnimationsEnabled = Settings.Global.getFloat(getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE) > 0f;
        } catch (Settings.SettingNotFoundException e) {
            mAnimationsEnabled = true;
        }

        mRumble = new RumblePack();

        mLayout = new FrameLayout(this);
        mRandom = new Random();
        mDp = getResources().getDisplayMetrics().density;
        mStarfield = new Starfield(mRandom, mDp * 2f);
        mStarfield.setWarp(0.1f); // very slow to start
        mLayout.setBackground(mStarfield);

        final DisplayMetrics dm = getResources().getDisplayMetrics();
        final float dp = dm.density;
        final int minSide = Math.min(dm.widthPixels, dm.heightPixels);
        final int widgetSize = (int) (minSide * 0.75);
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(widgetSize, widgetSize);
        lp.gravity = Gravity.CENTER;

        mLogo = new ImageView(this);
        mLogo.setImageResource(R.drawable.cinnamon_bun_platlogo);
        mLogo.setOnTouchListener(mTouchListener);
        mLogo.setVisibility(View.GONE);

        mHeptaDecaView = new View(this);
        final Heptadecagram heptadecagram = new Heptadecagram(dp);
        mHeptaDecaView.setBackground(heptadecagram);
        mHeptaDecaView.setOnTouchListener((v, event) -> {
            if (heptadecagram.onTouch(event)) {
                mHeptaDecaView.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }
            if (heptadecagram.getPathLength() > Heptadecagram.MAX_DOTS) {
                isPendingSwapToPlatlogo = true;
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP && isPendingSwapToPlatlogo) {
                swapToPlatlogo();
                isPendingSwapToPlatlogo = false;
            }
            return true;
        });
        mLayout.addView(mHeptaDecaView, lp);
        mLayout.addView(mLogo, lp);

        setContentView(mLayout);
    }

    private boolean isPendingSwapToPlatlogo = false;

    private void swapToPlatlogo() {
        mHeptaDecaView.animate().alpha(0f).setDuration(500).withEndAction(() -> {
            mHeptaDecaView.setVisibility(View.GONE);
        }).start();
        mLogo.setAlpha(0f);
        mLogo.setVisibility(View.VISIBLE);
        mLogo.animate().alpha(1f).setDuration(500).start();
        mLogo.requestFocus();
        ObjectAnimator.ofFloat(mStarfield, "warp", MIN_WARP)
                .setDuration(250).start();
    }

    private void startAnimating() {
        mAnim = new TimeAnimator();
        mAnim.setTimeListener(mTimeListener);
        mAnim.start();
    }

    private void stopAnimating() {
        mAnim.cancel();
        mAnim = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            if (mLogo.getVisibility() != View.VISIBLE) {
                // If using the keyboard, skip the cute star-drawing minigame.
                swapToPlatlogo();
            }
            if (event.getRepeatCount() == 0) {
                startWarp();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            stopWarp();
            return true;
        }
        return super.onKeyUp(keyCode,event);
    }

    private void startWarp() {
        stopWarp();
        mWarpAnim = ObjectAnimator.ofFloat(mStarfield, "warp", MIN_WARP, MAX_WARP)
                .setDuration(LAUNCH_TIME);
        mWarpAnim.start();

        mLogo.postDelayed(mLaunchNextStage, LAUNCH_TIME + 1000L);
    }

    private void stopWarp() {
        if (mWarpAnim != null) {
            mWarpAnim.cancel();
            mWarpAnim.removeAllListeners();
            mWarpAnim = null;
        }
        mStarfield.setWarp(1f);
        mLogo.removeCallbacks(mLaunchNextStage);
    }

    @Override
    public void onResume() {
        super.onResume();
        startAnimating();
    }

    @Override
    public void onPause() {
        stopWarp();
        stopAnimating();
        super.onPause();
    }

    private boolean shouldWriteSettings() {
        return true;
    }

    private void launchNextStage(boolean locked) {
        try {
            if (shouldWriteSettings()) {
                Log.v(TAG, "Saving egg locked=" + locked);
                SpUtils.putLong(this,
                        EGG_UNLOCK_SETTING,
                        locked ? 0 : System.currentTimeMillis());
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Can't write settings", e);
        }

        try {
            final Intent eggActivity = new Intent(Intent.ACTION_MAIN)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addCategory("com.android.internal.category.PLATLOGO");
            Log.v(TAG, "launching: " + eggActivity);
            startActivity(eggActivity);
        } catch (ActivityNotFoundException ex) {
            Log.e("com.android.internal.app.PlatLogoActivity", "No more eggs.");
        }
        if (FINISH_AFTER_NEXT_STAGE_LAUNCH) {
            finish(); // we're done here.
        }
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    private static final boolean isSRgbExtSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

    public static int packColor(float value, float alpha) {
        int v = (int) (value * 255 + 0.5f);
        int a = (int) (alpha * 255 + 0.5f);
        return Color.argb(a, v, v, v);
    }

    private static ColorSpace sSrgbExt;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static long packHdrWhite(float value, float alpha) {
        if (sSrgbExt == null) {
            sSrgbExt = ColorSpace.get(ColorSpace.Named.EXTENDED_SRGB);
        }
        return Color.valueOf(value, value, value, alpha, sSrgbExt).pack();
    }

    private static boolean pointInRadius(float x, float y, float r) {
        return (x * x + y * y) < (r * r);
    }

    static class Starfield extends Drawable {
        private static final int NUM_STARS = 128;

        private static final int NUM_PLANES = 4;

        private static final float ROTATION = 45;
        private final float[] mStars = new float[NUM_STARS * 4];
        private long mDt = 0;
        private final Paint mStarPaint;

        private final Random mRng;
        private final float mSize;

        private float mRadius = 0f;
        private float mWarp = MIN_WARP;

        private float mBuffer;

        public void setWarp(float warp) {
            mWarp = warp;
        }

        public float getWarp() {
            return mWarp;
        }

        Starfield(Random rng, float size) {
            mRng = rng;
            mSize = size;
            mStarPaint = new Paint();
            mStarPaint.setStyle(Paint.Style.STROKE);
            mStarPaint.setColor(Color.WHITE);
        }

        @Override
        public void onBoundsChange(Rect bounds) {
            mBuffer = mSize * NUM_PLANES * 2 * MAX_WARP;
            mRadius = ((float) hypot(bounds.width(), bounds.height()) / 2f) + mBuffer;
            // I didn't clarify this the last time, but we store both the beginning and
            // end of each star's trail in this data structure. When we're not in warp that means
            // that we've got each star in there twice. It's fine, we're gonna move it off-screen
            for (int i = 0; i < NUM_STARS; i++) {
                // New in C: we're zooming out from the center this time. Classic.
                final double angle = mRng.nextDouble() * 2 * Math.PI;
                final float dist = mRng.nextFloat() * mRadius;
                mStars[4 * i + 2] = (float) (Math.cos(angle) * dist);
                mStars[4 * i + 3] = (float) (Math.sin(angle) * dist);
                // duplicate copy (for now)
                mStars[4 * i + 0] = -10000;
                mStars[4 * i + 1] = -10000;
            }
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            final float dtSec = mDt / 1000f;
            final boolean inWarp = mWarp > 1f;

            canvas.drawColor(Color.BLACK);

            final float cx = getBounds().width() / 2f;
            final float cy = getBounds().height() / 2f;
            canvas.translate(cx, cy);

            canvas.rotate(ROTATION);

            if (mDt > 0 && mDt < 1000) {
                canvas.translate(
                        mRng.nextFloat() * (mWarp - 1f),
                        mRng.nextFloat() * (mWarp - 1f)
                );

                final float speedBase = 0.05f * dtSec * mWarp;

                for (int i = 0; i < NUM_STARS; i++) {
                    final int plane = (int) ((((float) i) / NUM_STARS) * NUM_PLANES) + 1;

                    float x = mStars[4 * i + 2];
                    float y = mStars[4 * i + 3];

                    final float speed = speedBase * plane;
                    x += x * speed;
                    y += y * speed;

                    if (!pointInRadius(x, y, mRadius)) {
                        final double angle = mRng.nextDouble() * 2 * Math.PI;
                        final float dist = mRng.nextFloat() * 0.1f * mRadius;
                        x = (float) (Math.cos(angle) * dist);
                        y = (float) (Math.sin(angle) * dist);
                    }

                    mStars[4 * i + 2] = x;
                    mStars[4 * i + 3] = y;

                    if (inWarp) {
                        final float tailScale = 1f / (1f + speed * mWarp);
                        mStars[4 * i + 0] = x * tailScale;
                        mStars[4 * i + 1] = y * tailScale;
                    } else {
                        mStars[4 * i + 0] = -10000;
                        mStars[4 * i + 1] = -10000;
                    }
                }
            }
            final int slice = (mStars.length / NUM_PLANES / 4) * 4;
            for (int p = 0; p < NUM_PLANES; p++) {
                final float value = (p + 1f) / (NUM_PLANES - 1);
                if (isSRgbExtSupported) {
                    mStarPaint.setColor(packHdrWhite(value, 1.0f));
                } else {
                    mStarPaint.setColor(packColor(value, 1.0f));
                }
                mStarPaint.setStrokeWidth(mSize * (p + 1));
                if (inWarp) {
                    canvas.drawLines(mStars, p * slice, slice, mStarPaint);
                }
                canvas.drawPoints(mStars, p * slice, slice, mStarPaint);
            }

            if (inWarp) {
                final float frac = (mWarp - MIN_WARP) / (MAX_WARP - MIN_WARP);
                if (isSRgbExtSupported) {
                    canvas.drawColor(packHdrWhite(2.0f, frac * frac));
                } else {
                    canvas.drawColor(packColor(2.0f, frac * frac));
                }
            }
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

        public void update(long dt) {
            mDt = dt;
        }
    }

    private static class Heptadecagram extends Drawable {
        public static final int MAX_DOTS = 17;
        private final Paint mDotPaint, mLinePaint, mBgPaint;
        private final float[] mDotsXY = new float[MAX_DOTS * 2];
        private final int[] mPath = new int[MAX_DOTS + 1];
        private int mPathLength = 0;
        private float mTouchX, mTouchY;
        private boolean mIsTracking = false;
        private final float mDp;
        private float mRadius;
        private float mDotRadius;
        private float mHitRadius;

        private Path mDrawingPath = new Path();

        Heptadecagram(float dp) {
            mDp = dp;

            mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            if (isSRgbExtSupported) {
                mDotPaint.setColor(packHdrWhite(1.5f, 1.0f));
            } else {
                mDotPaint.setColor(packColor(1.5f, 1.0f));
            }
            mDotPaint.setStyle(Paint.Style.FILL);

            mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLinePaint.setColor(0xFFB31F7F);
            mLinePaint.setStyle(Paint.Style.STROKE);
            mLinePaint.setStrokeWidth(4 * dp);
            mLinePaint.setStrokeJoin(Paint.Join.ROUND);
            mLinePaint.setStrokeCap(Paint.Cap.ROUND);

            mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBgPaint.setColor(Color.BLACK);
            mDotPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void onBoundsChange(Rect bounds) {
            float cx = bounds.width() / 2f;
            float cy = bounds.height() / 2f;
            mRadius = Math.min(cx, cy) * 0.9f;
            mDotRadius = 4 * mDp;
            mHitRadius = 24 * mDp;

            for (int i = 0; i < MAX_DOTS; i++) {
                // start at the top
                double angle = -Math.PI / 2 + (2 * Math.PI * i / MAX_DOTS);
                mDotsXY[i * 2] = cx + (float) Math.cos(angle) * mRadius;
                mDotsXY[i * 2 + 1] = cy + (float) Math.sin(angle) * mRadius;
            }
        }

        public boolean onTouch(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mPathLength = 0;
                    mIsTracking = true;
                    mTouchX = x;
                    mTouchY = y;
                    invalidateSelf();
                    return checkDot(x, y);
                case MotionEvent.ACTION_MOVE:
                    if (mIsTracking) {
                        mTouchX = x;
                        mTouchY = y;
                        invalidateSelf();
                        return checkDot(x, y);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mIsTracking = false;
                    mPathLength = 0;
                    invalidateSelf();
                    break;
            }

            if (mPathLength == MAX_DOTS + 1) {
                mIsTracking = false;
                return true;
            }
            return false;
        }

        private boolean checkDot(float x, float y) {
            for (int i = 0; i < MAX_DOTS; i++) {
                float dx = x - mDotsXY[i * 2];
                float dy = y - mDotsXY[i * 2 + 1];
                if (pointInRadius(dx, dy, mHitRadius)) {
                    if (mPathLength == 0) {
                        mPath[mPathLength++] = i;
                        return true;
                    } else {
                        int lastDot = mPath[mPathLength - 1];
                        if (lastDot != i) {
                            boolean visited = false;
                            for (int j = 0; j < mPathLength; j++) {
                                if (mPath[j] == i) {
                                    visited = true;
                                    break;
                                }
                            }
                            if (!visited || mPathLength == MAX_DOTS && mPath[0] == i) {
                                // another dot reached!
                                mPath[mPathLength++] = i;
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        private void drawTargetDot(Canvas canvas, float x, float y, float r, Paint paint) {
            canvas.save();
            canvas.translate(x, y);
            canvas.rotate(45);
            canvas.drawRect(-r, -r, r, r, paint);
            canvas.restore();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawCircle(getBounds().width() * 0.5f, getBounds().height() * 0.5f,
                    mRadius, mBgPaint);

            if (mPathLength > 0) {
                mDrawingPath.reset();
                mDrawingPath.moveTo(mDotsXY[mPath[0] * 2], mDotsXY[mPath[0] * 2 + 1]);
                for (int i = 1; i < mPathLength; i++) {
                    mDrawingPath.lineTo(mDotsXY[mPath[i] * 2], mDotsXY[mPath[i] * 2 + 1]);
                }
                if (mIsTracking && mPathLength <= MAX_DOTS) {
                    mDrawingPath.lineTo(mTouchX, mTouchY);
                }
                canvas.drawPath(mDrawingPath, mLinePaint);
            }

            for (int i = 0; i < MAX_DOTS; i++) {
                drawTargetDot(canvas, mDotsXY[i * 2], mDotsXY[i * 2 + 1], mDotRadius, mDotPaint);
            }
        }

        @Override
        public void setAlpha(int alpha) { }
        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) { }
        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        public int getPathLength() {
            return mPathLength;
        }
    }
}
