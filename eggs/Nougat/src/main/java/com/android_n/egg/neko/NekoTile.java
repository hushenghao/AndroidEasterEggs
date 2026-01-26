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

package com.android_n.egg.neko;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android_n.egg.neko.PrefState.PrefsListener;
//import com.android.internal.logging.MetricsLogger;

@RequiresApi(api = Build.VERSION_CODES.N)
public class NekoTile extends TileService implements PrefsListener {

    private static final String TAG = "NekoTile";

    private PrefState mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = new PrefState(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (Build.VERSION_CODES.TIRAMISU >= Build.VERSION.SDK_INT) {
            return super.onBind(intent);
        }
        // fix https://github.com/hushenghao/AndroidEasterEggs/issues/729
        // Unable to reach IQSService
        // https://cs.android.com/android/platform/superproject/+/android-12.1.0_r1:frameworks/base/core/java/android/service/quicksettings/TileService.java
        try {
            return super.onBind(intent);
        } catch (RuntimeException ignore) {
            return null;
        }
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        mPrefs.setListener(this);
        updateState();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        mPrefs.setListener(null);
    }

//    @Override
//    public void onTileAdded() {
//        super.onTileAdded();
//        MetricsLogger.count(this, "egg_neko_tile_added", 1);
//    }

//    @Override
//    public void onTileRemoved() {
//        super.onTileRemoved();
//        MetricsLogger.count(this, "egg_neko_tile_removed", 1);
//    }

    @Override
    public void onPrefsChanged() {
        updateState();
    }

    private void updateState() {
        Tile tile = getQsTile();
        int foodState = mPrefs.getFoodState();
        Food food = new Food(foodState);
        if (foodState != 0) {
            NekoService.registerJobIfNeeded(this, food.getInterval(this));
        }
        if (tile == null) return;
        tile.setIcon(food.getIcon(this));
        tile.setLabel(food.getName(this));
        tile.setState(foodState != 0 ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onClick() {
        if (mPrefs.getFoodState() != 0) {
            // there's already food loaded, let's empty it
//            MetricsLogger.count(this, "egg_neko_empty_food", 1);
            mPrefs.setFoodState(0);
            NekoService.cancelJob(this);
        } else {
            // time to feed the cats
            if (isLocked()) {
                if (isSecure()) {
                    Log.d(TAG, "startActivityAndCollapse");
                    Intent intent = new Intent(this, NekoLockedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
                        startActivityAndCollapse(pendingIntent);
                    } else {
                        startActivityAndCollapse(intent);
                    }
                } else {
                    unlockAndRun(new Runnable() {
                        @Override
                        public void run() {
                            showNekoDialog();
                        }
                    });
                }
            } else {
                showNekoDialog();
            }
        }
    }

    private void showNekoDialog() {
        Log.d(TAG, "showNekoDialog");
//        MetricsLogger.count(this, "egg_neko_select_food", 1);
        try {
            showDialog(new NekoDialog(this));
        } catch (Exception ignore) {
        }
    }
}
