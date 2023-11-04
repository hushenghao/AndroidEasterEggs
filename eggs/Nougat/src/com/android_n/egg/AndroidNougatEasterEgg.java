package com.android_n.egg;

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
public class AndroidNougatEasterEgg implements EasterEggProvider {

    @IntoMap
    @Provides
    @IntKey(Build.VERSION_CODES.N)
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.n_android_logo,
                R.string.n_app_name,
                R.string.n_android_nickname,
                new IntRange(Build.VERSION_CODES.N, Build.VERSION_CODES.N_MR1),
                true
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
