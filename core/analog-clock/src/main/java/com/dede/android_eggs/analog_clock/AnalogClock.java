/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.dede.android_eggs.analog_clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.dede.android_eggs.analog_clock.R;
import com.dede.android_eggs.system_colors.ResourcesUtils;
import com.dede.basic.DrawableKt;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Formatter;
import java.util.Locale;

/**
 * This widget display an analogic clock with two hands for hours and
 * minutes.
 * <p>
 * Copy from <a href="https://cs.android.com/android/platform/superproject/+/android13-release:frameworks/base/core/java/android/widget/AnalogClock.java">android13-release:frameworks:AnalogClock.java</a>.
 * The second hand and the configurable time zone support are removed, and the
 * default dial/hands are tinted with the system palette colors.
 *
 * @attr ref android.R.styleable#AnalogClock_dial
 * @attr ref android.R.styleable#AnalogClock_hand_hour
 * @attr ref android.R.styleable#AnalogClock_hand_minute
 */
public class AnalogClock extends View {
    private Clock mClock;

    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mDial;

    private final int mDialWidth;
    private final int mDialHeight;

    private boolean mVisible;

    private float mMinutes;
    private float mHour;
    private boolean mChanged;

    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        //final Resources r = context.getResources();
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.AnalogClock, defStyleAttr, defStyleRes);

        mDial = a.getDrawable(R.styleable.AnalogClock_dial);
        if (mDial == null) {
            mDial = DrawableKt.requireDrawable(context, R.drawable.clock_dial);
            setDrawableTint(mDial, "system_neutral1_200");
        }

        mHourHand = a.getDrawable(R.styleable.AnalogClock_hand_hour);
        if (mHourHand == null) {
            mHourHand = DrawableKt.requireDrawable(context, R.drawable.clock_hand_hour);
            setDrawableTint(mHourHand, "system_accent1_700");
        }

        mMinuteHand = a.getDrawable(R.styleable.AnalogClock_hand_minute);
        if (mMinuteHand == null) {
            mMinuteHand = DrawableKt.requireDrawable(context, R.drawable.clock_hand_minute);
            setDrawableTint(mMinuteHand, "system_accent2_500");
        }
        a.recycle();

        mClock = Clock.systemDefaultZone();

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }

    private void setDrawableTint(Drawable drawable, String tintColor) {
        if (drawable == null) return;
        try {
            int systemColor = ResourcesUtils.getSystemColor(getContext(), tintColor);
            drawable.setTint(systemColor);
        } catch (Exception ignore) {
        }
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);

        if (isVisible) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();

        if (!mReceiverAttached) {
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            // OK, this is gross but needed. This class is supported by the
            // remote views machanism and as a part of that the remote views
            // can be inflated by a context for another user without the app
            // having interact users permission - just for loading resources.
            // For exmaple, when adding widgets from a user profile to the
            // home screen. Therefore, we register the receiver as the current
            // user not the one the context is for.
            getContext().registerReceiver(mIntentReceiver,
                    filter, null, getHandler());
            mReceiverAttached = true;
        }

        // NOTE: It's safe to do these after registering the receiver since the receiver always runs
        // in the main thread, therefore the receiver can't run before this method returns.

        // The time zone may have changed while the receiver wasn't registered, so update the clock.
        createClock();

        // Make sure we update to the current time
        onTimeChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mReceiverAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mReceiverAttached = false;
        }
        super.onDetachedFromWindow();
    }

    private void onVisible() {
        if (!mVisible) {
            mVisible = true;
            mTick.run();
        }
    }

    private void onInvisible() {
        if (mVisible) {
            removeCallbacks(mTick);
            mVisible = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float) heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        final Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        boolean scaled = false;

        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w,
                    (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);

        canvas.save();
        canvas.rotate(mHour / 12.0f * 360.0f, x, y);
        final Drawable hourHand = mHourHand;
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

        final Drawable minuteHand = mMinuteHand;
        if (changed) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        minuteHand.draw(canvas);
        canvas.restore();

        if (scaled) {
            canvas.restore();
        }
    }

    protected Instant now() {
        return mClock.instant();
    }

    protected void onTimeChanged() {
        Instant now = now();
        onTimeChanged(now.atZone(mClock.getZone()).toLocalTime(), now.toEpochMilli());
    }

    private void onTimeChanged(LocalTime localTime, long nowMillis) {
        float previousHour = mHour;
        float previousMinutes = mMinutes;

        mMinutes = localTime.getMinute() + localTime.getSecond() / 60.0f;
        mHour = localTime.getHour() + mMinutes / 60.0f;
        mChanged = true;

        // Update the content description only if the announced hours and minutes have changed.
        if ((int) previousHour != (int) mHour || (int) previousMinutes != (int) mMinutes) {
            updateContentDescription(nowMillis);
        }
    }

    /** Intent receiver for the time or time zone changing. */
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                createClock();
            }

            mTick.run();
        }
    };
    private boolean mReceiverAttached;

    private final Runnable mTick = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(this);
            if (!mVisible) {
                return;
            }

            Instant now = now();
            ZonedDateTime zonedDateTime = now.atZone(mClock.getZone());
            LocalTime localTime = zonedDateTime.toLocalTime();

            // If there's no second hand, then tick at the start of the next minute.
            //
            // This must be done with ZonedDateTime as opposed to LocalDateTime to ensure proper
            // handling of DST. Also note that because of leap seconds, it should not be assumed
            // that one minute == 60 seconds.
            Instant startOfNextMinute = zonedDateTime.plusMinutes(1).withSecond(0).toInstant();
            long millisUntilNextTick = Duration.between(now, startOfNextMinute).toMillis();
            if (millisUntilNextTick <= 0) {
                // This should never occur, but if it does, then just check the tick again in
                // one minute to ensure we're always moving forward.
                millisUntilNextTick = Duration.ofMinutes(1).toMillis();
            }

            // Schedule a callback for when the next tick should occur.
            postDelayed(this, millisUntilNextTick);

            onTimeChanged(localTime, now.toEpochMilli());

            invalidate();
        }
    };

    private void createClock() {
        mClock = Clock.systemDefaultZone();
    }

    private void updateContentDescription(long timeMillis) {
        final int flags = DateUtils.FORMAT_SHOW_TIME;// | DateUtils.FORMAT_24HOUR;
        String contentDescription = DateUtils.formatDateRange(
                getContext(),
                new Formatter(new StringBuilder(50), Locale.getDefault()),
                timeMillis /* startMillis */,
                timeMillis /* endMillis */,
                flags,
                null /* timeZone */)
                .toString();
        setContentDescription(contentDescription);
    }
}
