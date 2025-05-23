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

package com.dede.basic.views;

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

import com.dede.basic.DrawableKt;
import com.dede.basic.R;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

/**
 * This widget display an analogic clock with two hands for hours and
 * minutes.
 * <p>
 * Copy from <a href="https://cs.android.com/android/platform/superproject/+/android-7.1.2_r39:frameworks/base/core/java/android/widget/AnalogClock.java">android-7.1.2_r39:frameworks:AnalogClock.java</a>.
 *
 * @attr ref android.R.styleable#AnalogClock_dial
 * @attr ref android.R.styleable#AnalogClock_hand_hour
 * @attr ref android.R.styleable#AnalogClock_hand_minute
 */
public class AnalogClock extends View {
    // Use java.util.Calendar replace android.text.format.Time
    private Calendar mCalendar;

    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mDial;

    private final int mDialWidth;
    private final int mDialHeight;

    private boolean mAttached;

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

        mCalendar = Calendar.getInstance();

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }

    private void setDrawableTint(Drawable drawable, String tintColor) {
        if (drawable == null) return;
        try {
            int systemColor = DrawableKt.getSystemColor(getContext(), tintColor);
            drawable.setTint(systemColor);
        } catch (Exception ignore) {
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();

            filter.addAction(Intent.ACTION_TIME_TICK);
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
        }

        // NOTE: It's safe to do these after registering the receiver since the receiver always runs
        // in the main thread, therefore the receiver can't run before this method returns.

        // The time zone may have changed while the receiver wasn't registered, so update the Time
        mCalendar = Calendar.getInstance();

        // Make sure we update to the current time
        onTimeChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
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

    protected Calendar now() {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        return mCalendar;
    }

    protected void onTimeChanged() {
        Calendar now = this.now();

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        mChanged = true;

        updateContentDescription(now);
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = Calendar.getInstance(TimeZone.getTimeZone(tz));
            }

            onTimeChanged();

            invalidate();
        }
    };

    private void updateContentDescription(Calendar time) {
        final int flags = DateUtils.FORMAT_SHOW_TIME;// | DateUtils.FORMAT_24HOUR;
        String contentDescription = DateUtils.formatDateTime(getContext(),
                time.getTimeInMillis(), flags);
        setContentDescription(contentDescription);
    }
}
