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

package com.android_m.egg.preview;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;

import com.android_m.egg.R;
import com.dede.basic.SpUtils;

public class PlatLogoActivity extends Activity {
    FrameLayout mLayout;
    int mTapCount;
    int mKeyCount;
    PathInterpolator mInterpolator = new PathInterpolator(0f, 0f, 0.5f, 1f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayout = new FrameLayout(this);
        setContentView(mLayout);
    }

    @Override
    public void onAttachedToWindow() {
        final DisplayMetrics dm = getResources().getDisplayMetrics();
        final float dp = dm.density;
        final int size = (int)
                (Math.min(Math.min(dm.widthPixels, dm.heightPixels), 600*dp) - 100*dp);

        final View im = new View(this);
        im.setTranslationZ(20);
        im.setScaleX(0.5f);
        im.setScaleY(0.5f);
        im.setAlpha(0f);
        im.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                final int pad = (int) (8*dp);
                outline.setOval(pad, pad, view.getWidth()-pad, view.getHeight()-pad);
            }
        });
        final Drawable platlogo = getDrawable(R.drawable.m_platlogo_preview);
        im.setBackground(new RippleDrawable(
                ColorStateList.valueOf(0xFFFFFFFF),
                platlogo,
                null));
        im.setClickable(true);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mTapCount < 5) return false;

                        if (SpUtils.getLong(PlatLogoActivity.this, "m_egg_mode", 0)
                                == 0) {
                            // For posterity: the moment this user unlocked the easter egg
                            try {
                                SpUtils.putLong(PlatLogoActivity.this,
                                        "m_egg_mode",
                                        System.currentTimeMillis());
                            } catch (RuntimeException e) {
                                Log.e("PlatLogoActivity", "Can't write settings", e);
                            }
                        }
                        im.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    startActivity(new Intent(PlatLogoActivity.this, ShruggyActivity.class));
                                } catch (ActivityNotFoundException ex) {
                                    Log.e("PlatLogoActivity", "No more eggs.");
                                }
                                finish();
                            }
                        });
                        return true;
                    }
                });
                mTapCount++;
            }
        });

        // Enable hardware keyboard input for TV compatibility.
        im.setFocusable(true);
        im.requestFocus();
        im.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    ++mKeyCount;
                    if (mKeyCount > 2) {
                        if (mTapCount > 5) {
                            im.performLongClick();
                        } else {
                            im.performClick();
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        mLayout.addView(im, new FrameLayout.LayoutParams(size, size, Gravity.CENTER));

        im.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setInterpolator(mInterpolator)
                .setDuration(500)
                .setStartDelay(800)
                .start();
    }
}