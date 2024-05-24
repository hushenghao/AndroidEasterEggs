package com.android_i.egg;

import android.app.Activity;
import android.os.Build;

import com.dede.basic.provider.BaseEasterEgg;
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
public class AndroidIceCreamSandwichEasterEgg implements EasterEggProvider {

    @IntoSet
    @Provides
    @Singleton
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.i_platlogo_scale,
                R.string.i_egg_name,
                R.string.i_android_nickname,
                new IntRange(Build.VERSION_CODES.ICE_CREAM_SANDWICH, Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1),
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
