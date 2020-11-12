/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android_r.egg.neko;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.dede.basic.SpUtils;

//import com.android.internal.logging.MetricsLogger;

public class NekoActivationActivity extends Activity {
    private static final String R_EGG_UNLOCK_SETTING = "r_egg_mode";

    private void toastUp(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (SpUtils.getLong(this,
                    R_EGG_UNLOCK_SETTING, 0) == 0) {
                Log.v("Neko", "Disabling controls.");
                toastUp("\uD83D\uDEAB");
            } else {
                Log.v("Neko", "Enabling controls.");
                toastUp("\uD83D\uDC31");
            }
            finish();
            return;
        }

        final PackageManager pm = getPackageManager();
        final ComponentName cn = new ComponentName(this, NekoControlsService.class);
        final boolean componentEnabled = pm.getComponentEnabledSetting(cn)
                == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        if (SpUtils.getLong(this,
                R_EGG_UNLOCK_SETTING, 0) == 0) {
            if (componentEnabled) {
                Log.v("Neko", "Disabling controls.");
                pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
//                MetricsLogger.histogram(this, "egg_neko_enable", 0);
                toastUp("\uD83D\uDEAB");
            } else {
                Log.v("Neko", "Controls already disabled.");
            }
        } else {
            if (!componentEnabled) {
                Log.v("Neko", "Enabling controls.");
                pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
//                MetricsLogger.histogram(this, "egg_neko_enable", 1);
                toastUp("\uD83D\uDC31");
            } else {
                Log.v("Neko", "Controls already enabled.");
            }
        }
        finish();
    }
}
