package com.android_k.egg;

import android.app.Activity;
import android.os.Build;

import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.EasterEgg;
import com.dede.basic.provider.EasterEggProvider;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;
import kotlin.ranges.IntRange;

@Module
@InstallIn(SingletonComponent.class)
public class AndroidKitKatEasterEgg implements EasterEggProvider {

    @IntoMap
    @Provides
    @IntKey(Build.VERSION_CODES.KITKAT)
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
}
