/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.dede.android_eggs.util;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.dede.android_eggs.R;
import com.dede.android_eggs.databinding.ProgressDialogMaterialBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.reflect.Field;

/**
 * Utility class to override shape of {@link android.graphics.drawable.AdaptiveIconDrawable}.
 */
public class IconShapeOverride {

    private static final String TAG = "IconShapeOverride";

    public static final String KEY_PREFERENCE = "pref_override_icon_shape";

    // Time to wait before killing the process this ensures that the progress bar is visible for
    // sufficient time so that there is no flicker.
    private static final long PROCESS_KILL_DELAY_MS = 1000;

    private static class Api29Impl {
        @Nullable
        private static Resources sOverrideResources;

        public static boolean isSupported() {
            return getConfigResId() != 0;
        }

        public static Resources getOverrideResources(Context context, Resources parent) {
            if (sOverrideResources == null) {
                String path = getAppliedValue(context);
                if (!TextUtils.isEmpty(path)) {
                    sOverrideResources = new ResourcesOverride(parent, getConfigResId(), path);
                }
            }
            return sOverrideResources == null ? parent : sOverrideResources;
        }
    }

    private static class Api26Impl {

        public static boolean isSupported() {
            try {
                if (getSystemResField().get(null) != Resources.getSystem()) {
                    // Our assumption that mSystem is the system resource is not true.
                    return false;
                }
            } catch (Exception e) {
                // Ignore, not supported
                return false;
            }
            return getConfigResId() != 0;
        }

        public static void apply(Context context) {
            String path = getAppliedValue(context);
            if (TextUtils.isEmpty(path)) {
                return;
            }
            if (!isSupported()) {
                return;
            }

            // magic
            try {
                Resources override =
                        new ResourcesOverride(Resources.getSystem(), getConfigResId(), path);
                getSystemResField().set(null, override);
            } catch (Exception e) {
                Log.e(TAG, "Unable to override icon shape", e);
                // revert value.
                getDevicePrefs(context).edit().remove(KEY_PREFERENCE).apply();
            }
        }

        private static Field getSystemResField() throws Exception {
            @SuppressWarnings("JavaReflectionMemberAccess") @SuppressLint("DiscouragedPrivateApi")
            Field staticField = Resources.class.getDeclaredField("mSystem");
            staticField.setAccessible(true);
            return staticField;
        }
    }

    public static class App extends Application {
        @Override
        protected void attachBaseContext(Context base) {
            super.attachBaseContext(base);
            apply(base);
        }

        @Override
        public Resources getResources() {
            return getOverrideResources(this, super.getResources());
        }
    }

    public static void apply(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Api26Impl.apply(context);
        }
    }

    public static Resources getOverrideResources(Context context, Resources parent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return Api29Impl.getOverrideResources(context, parent);
        }
        return parent;
    }

    public static boolean isSupported() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return Api26Impl.isSupported();
        } else {
            return Api29Impl.isSupported();
        }
    }

    private static SharedPreferences getDevicePrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @SuppressLint("DiscouragedApi")
    private static int getConfigResId() {
        return Resources.getSystem().getIdentifier("config_icon_mask", "string", "android");
    }

    private static String getAppliedValue(Context context) {
        return getDevicePrefs(context).getString(KEY_PREFERENCE, "");
    }

    public static void handlePreferenceUi(ListPreference preference) {
        Context context = preference.getContext();
        preference.setValue(getAppliedValue(context));
        preference.setOnPreferenceChangeListener(new PreferenceChangeHandler(context));
    }

    private static class ResourcesOverride extends Resources {

        private final int mOverrideId;
        private final String mOverrideValue;

        public ResourcesOverride(Resources parent, int overrideId, String overrideValue) {
            super(parent.getAssets(), parent.getDisplayMetrics(), parent.getConfiguration());
            mOverrideId = overrideId;
            mOverrideValue = overrideValue;
        }

        @NonNull
        @Override
        public String getString(int id) throws NotFoundException {
            if (id == mOverrideId) {
                return mOverrideValue;
            }
            return super.getString(id);
        }
    }

    private static class PreferenceChangeHandler implements Preference.OnPreferenceChangeListener {

        private final Context mContext;

        private PreferenceChangeHandler(Context context) {
            mContext = context;
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object o) {
            String newValue = (String) o;
            if (!getAppliedValue(mContext).equals(newValue)) {
                // Value has changed
                ProgressDialogMaterialBinding binding = ProgressDialogMaterialBinding.inflate(LayoutInflater.from(mContext));
                binding.progress.setIndeterminate(true);
                binding.message.setText(R.string.icon_shape_override_progress);
                new MaterialAlertDialogBuilder(mContext)
                        .setCancelable(false)
                        .setView(binding.getRoot())
                        .show();

                new Thread(new OverrideApplyHandler(mContext, newValue)).start();
            }
            return false;
        }
    }

    private static class OverrideApplyHandler implements Runnable {

        private final Context mContext;
        private final String mValue;

        private OverrideApplyHandler(Context context, String value) {
            mContext = context;
            mValue = value;
        }

        @Override
        public void run() {
            // Synchronously write the preference.
            getDevicePrefs(mContext).edit().putString(KEY_PREFERENCE, mValue).commit();

            // Wait for it
            try {
                Thread.sleep(PROCESS_KILL_DELAY_MS);
            } catch (Exception e) {
                Log.e(TAG, "Error waiting", e);
            }

            // Schedule an alarm before we kill ourself.
            Intent homeIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
            homeIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            mContext.getApplicationContext().startActivity(homeIntent);

            // Kill process
            Process.killProcess(Process.myPid());
        }
    }
}
