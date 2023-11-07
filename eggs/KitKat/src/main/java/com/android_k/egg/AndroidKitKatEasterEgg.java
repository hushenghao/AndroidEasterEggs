package com.android_k.egg;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.ComponentProvider;
import com.dede.basic.provider.EasterEgg;
import com.dede.basic.provider.EasterEggProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoSet;
import kotlin.ranges.IntRange;

@Module
@InstallIn(SingletonComponent.class)
public class AndroidKitKatEasterEgg implements EasterEggProvider, ComponentProvider {

    @IntoSet
    @Provides
    @Singleton
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.k_android_logo,
                R.string.k_dessert_case,
                R.string.k_android_nickname,
                new IntRange(Build.VERSION_CODES.KITKAT, Build.VERSION_CODES.KITKAT_WATCH),
                false
        ) {
            @Override
            public Class<? extends Activity> provideEasterEgg() {
                return PlatLogoActivity.class;
            }

            @Override
            public SnapshotProvider provideSnapshotProvider() {
                return new SnapshotProvider();
            }
        };
    }

    @IntoSet
    @Provides
    @Singleton
    @Override
    public Component provideComponent() {
        return new Component(
                R.string.k_dessert_case,
                R.string.k_android_nickname,
                new IntRange(Build.VERSION_CODES.KITKAT, Build.VERSION_CODES.KITKAT_WATCH)
        ) {
            @Override
            public boolean isSupported() {
                return true;
            }

            @Override
            public boolean isEnabled(@NonNull Context context) {
                ComponentName cn = new ComponentName(context, DessertCaseDream.class);
                return Component.isEnabled(cn, context);
            }

            @Override
            public void setEnabled(@NonNull Context context, boolean enable) {
                ComponentName cn = new ComponentName(context, DessertCaseDream.class);
                Component.setEnable(cn, context, enable);
            }
        };
    }
}
