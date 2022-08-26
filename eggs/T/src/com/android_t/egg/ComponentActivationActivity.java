/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android_t.egg;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android_t.egg.neko.NekoControlsService;
import com.android_t.egg.widget.PaintChipsActivity;
import com.android_t.egg.widget.PaintChipsWidget;
import com.dede.basic.SpUtils;

/**
 * Launched from the PlatLogoActivity. Enables everything else in this easter egg.
 */
public class ComponentActivationActivity extends Activity {
    private static final String TAG = "EasterEgg";

    private static final String S_EGG_UNLOCK_SETTING = "t_egg_mode";

    private void toastUp(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        final ComponentName[] cns;
        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
            finish();
            return;
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            cns = new ComponentName[]{
                    new ComponentName(this, NekoControlsService.class),
                    new ComponentName(this, PaintChipsActivity.class),
                    new ComponentName(this, PaintChipsWidget.class)
            };
        } else {
            cns = new ComponentName[]{
                    new ComponentName(this, NekoControlsService.class),
            };
        }

        final PackageManager pm = getPackageManager();
        final long unlockValue = SpUtils.getLong(this,
                S_EGG_UNLOCK_SETTING, 0);
        for (ComponentName cn : cns) {
            final boolean componentEnabled = pm.getComponentEnabledSetting(cn)
                    == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            if (unlockValue == 0) {
                if (componentEnabled) {
                    Log.v(TAG, "Disabling component: " + cn);
                    pm.setComponentEnabledSetting(cn,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                    //toastUp("\uD83D\uDEAB");
                } else {
                    Log.v(TAG, "Already disabled: " + cn);
                }
            } else {
                if (!componentEnabled) {
                    Log.v(TAG, "Enabling component: " + cn);
                    pm.setComponentEnabledSetting(cn,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                    //toastUp("\uD83D\uDC31");
                } else {
                    Log.v(TAG, "Already enabled: " + cn);
                }
            }
        }

        finish();
    }
}
