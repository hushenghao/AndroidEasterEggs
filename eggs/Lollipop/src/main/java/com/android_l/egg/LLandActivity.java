/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android_l.egg;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdgeCompat;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class LLandActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_lland);
        LLand world = (LLand) findViewById(R.id.world);
        world.setScoreField((TextView) findViewById(R.id.score));
        world.setSplash(findViewById(R.id.welcome));
        Log.v(LLand.TAG, "focus: " + world.requestFocus());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.score), new OnApplyWindowInsetsListener() {

            private ViewGroup.MarginLayoutParams copyParams;

            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets edge = insets.getInsets(EdgeToEdgeCompat.EDGE_INSETS_MASK);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)v.getLayoutParams();
                if (copyParams == null) {
                    copyParams = new ViewGroup.MarginLayoutParams(params);
                }
                params.topMargin = edge.top + copyParams.topMargin;
                params.leftMargin = edge.left + copyParams.leftMargin;
                v.setLayoutParams(params);
                return insets;
            }
        });
    }
}
