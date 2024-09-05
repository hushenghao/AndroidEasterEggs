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

package com.android_v.egg;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.android_v.egg.flags.Flags;
import com.android_v.egg.landroid.DreamUniverse;

/**
 * Launched from the PlatLogoActivity. Enables everything else in this easter egg.
 */
public class ComponentActivationActivity extends Activity {
    private static final String TAG = "EasterEgg";

    // check PlatLogoActivity.java for these
    private static final String V_EGG_UNLOCK_SETTING = "egg_mode_v";

    private void toastUp(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        lockUnlockComponents(this);

        finish();
    }

    /**
     * Check easter egg unlock state and update unlockable components to match.
     */
    public static void lockUnlockComponents(Context context) {
        final PackageManager pm = context.getPackageManager();
        final ComponentName[] cns;
        final long unlockValue;
        if (Flags.flagFlag()) {
            unlockValue = 1; // since we're not toggling we actually don't need to check the setting
        } else {
            unlockValue = 0;
        }
        cns = new ComponentName[]{
                new ComponentName(context, DreamUniverse.class)
        };
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
    }
}
