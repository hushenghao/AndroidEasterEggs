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

package com.android_l.egg.preview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.RoundedCornerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dede.basic.SpUtils;

/**
 * Webdriver Torso
 * <br>
 * Android L Preview Easter Egg, copy from <a href="https://cs.android.com/android/_/android/platform/frameworks/base/+/54642d141f80d495a475b304052eedd2832fcdb1:core/java/com/android/internal/app/PlatLogoActivity.java;drc=c89deae0a7bed0b54e3152df3a52bca6f8dd0cd4">PlatLogoActivity</a>
 */
public class PlatLogoActivity extends Activity {
    private static class Torso extends FrameLayout {
        boolean mAnimate = false;
        TextView mText;

        public Torso(Context context) {
            this(context, null);
        }

        public Torso(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Torso(Context context, AttributeSet attrs, int flags) {
            super(context, attrs, flags);

            for (int i = 0; i < 2; i++) {
                final View v = new View(context);
                v.setBackgroundColor(i % 2 == 0 ? Color.BLUE : Color.RED);
                addView(v);
            }

            mText = new TextView(context);
            mText.setTextColor(Color.BLACK);
            mText.setTextSize(14 /* sp */);
            mText.setTypeface(Typeface.create("monospace", Typeface.BOLD));

            addView(mText, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM | Gravity.LEFT
            ));
        }

        private Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                mText.setText(String.format("android_%s.flv - build %s",
                        Build.VERSION.CODENAME,
                        Build.VERSION.INCREMENTAL));
                final int N = getChildCount();
                final float parentw = getMeasuredWidth();
                final float parenth = getMeasuredHeight();
                for (int i = 0; i < N; i++) {
                    final View v = getChildAt(i);
                    if (v instanceof TextView) continue;

                    final int w = (int) (Math.random() * parentw);
                    final int h = (int) (Math.random() * parenth);
                    final float x = (float) Math.random() * (parentw - w);
                    final float y = (float) Math.random() * (parenth - h);

                    // add animation
                    RefreshTorsoImpls.get(true).refresh(v, w, h, x, y);
                }

                if (mAnimate) postDelayed(this, 1000);
            }
        };

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            mAnimate = true;
            post(mRunnable);
        }

        @Override
        protected void onDetachedFromWindow() {
            mAnimate = false;
            removeCallbacks(mRunnable);
            super.onDetachedFromWindow();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Torso t = new Torso(this);
        t.setBackgroundColor(Color.WHITE);

        t.getChildAt(0)
                .setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                        final ContentResolver cr = getContentResolver();

                        if (SpUtils.getLong(PlatLogoActivity.this, "l_egg_mode", 0)
                                == 0) {
                            // For posterity: the moment this user unlocked the easter egg
                            SpUtils.putLong(PlatLogoActivity.this,
                                    "l_egg_mode",
                                    System.currentTimeMillis());
                        }
                        try {
                            startActivity(new Intent(PlatLogoActivity.this, Class.forName("com.android_k.egg.DessertCase")));
//                            startActivity(new Intent(Intent.ACTION_MAIN)
//                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
//                                    .addCategory("com.android.internal.category.PLATLOGO"));
                        } catch (Exception ex) {
                            android.util.Log.e("PlatLogoActivity", "Couldn't catch a break.");
                        }
                        finish();
                        return true;
                    }
                });

        ViewCompat.setOnApplyWindowInsetsListener(t.mText, new OnApplyWindowInsetsListener() {
            @Override
            public @NonNull WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                int typeMask = WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout();
                Insets edge = insets.getInsets(typeMask);
                int left = edge.left;
                int bottom = edge.bottom;
                RoundedCornerCompat roundedCorner = insets.getRoundedCorner(RoundedCornerCompat.POSITION_BOTTOM_LEFT);
                if (roundedCorner != null) {
                    float radius = roundedCorner.getRadius() * 1f;
                    int offset = (int) (radius - Math.sqrt((radius * radius) / 2));
                    left += offset;
                    bottom += offset;
                }
                v.setPadding(left, 0, edge.right, bottom);
                return WindowInsetsCompat.CONSUMED;
            }
        });

        setContentView(t);
    }
}
