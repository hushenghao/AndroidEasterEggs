/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android_i.egg;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.Toast;

public class PlatLogoActivity extends Activity {
    Toast mToast;
    ImageView mContent;
    //    Vibrator mZzz = new Vibrator();
    int mCount;
    final Handler mHandler = new Handler();

    Runnable mSuperLongPress = new Runnable() {
        public void run() {
            mCount++;
            Vibrator mZzz = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            mZzz.vibrate(50L * mCount);
            final float scale = 1f + 0.25f * mCount * mCount;

            Drawable drawable = mContent.getDrawable();
            if (drawable instanceof VectorDrawable) {
                // Scale VectorDrawable
                float newWidth = drawable.getIntrinsicWidth() * scale;
                float newHeight = drawable.getIntrinsicHeight() * scale;
                int left = -(int) ((newWidth - drawable.getIntrinsicWidth()) / 2f);
                int top = -(int) ((newHeight - drawable.getIntrinsicHeight()) / 2f);
                int right = (int) (newWidth + left);
                int bottom = (int) (newHeight + top);
                drawable.setBounds(left, top, right, bottom);
            } else {
                mContent.setScaleX(scale);
                mContent.setScaleY(scale);
            }

            if (mCount <= 3) {
                mHandler.postDelayed(mSuperLongPress, ViewConfiguration.getLongPressTimeout());
            } else {
                try {
                    startActivity(new Intent(PlatLogoActivity.this, Nyandroid.class));
//                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
                } catch (ActivityNotFoundException ex) {
                    android.util.Log.e("PlatLogoActivity", "Couldn't find platlogo screensaver.");
                }
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToast = Toast.makeText(this, "Android 4.0: Ice Cream Sandwich", Toast.LENGTH_SHORT);

        mContent = new ImageView(this);
        mContent.setImageResource(R.drawable.i_platlogo);
        mContent.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        mContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mContent.setPressed(true);
                    mHandler.removeCallbacks(mSuperLongPress);
                    mCount = 0;
                    mHandler.postDelayed(mSuperLongPress, 2 * ViewConfiguration.getLongPressTimeout());
                } else if (action == MotionEvent.ACTION_UP) {
                    if (mContent.isPressed()) {
                        mContent.setPressed(false);
                        mHandler.removeCallbacks(mSuperLongPress);
                        mToast.show();
                    }
                }
                return true;
            }
        });

        setContentView(mContent);
    }

    @Override
    protected void onDestroy() {
        // fix leak
        mHandler.removeCallbacks(mSuperLongPress);
        super.onDestroy();
    }
}
