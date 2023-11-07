package com.android_n.egg;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android_n.egg.neko.NekoTile;
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
public class AndroidNougatEasterEgg implements EasterEggProvider, ComponentProvider {

    @IntoSet
    @Provides
    @Singleton
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

    @IntoSet
    @Provides
    @Singleton
    @Override
    public Component provideComponent() {
        return new ComponentProvider.Component(
                R.drawable.n_stat_tint_icon,
                R.string.n_default_tile_name,
                R.string.n_android_nickname,
                new IntRange(Build.VERSION_CODES.N, Build.VERSION_CODES.N_MR1)
        ) {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void setEnabled(@NonNull Context context, boolean enable) {
                final ComponentName cn = new ComponentName(context, NekoTile.class);
                Component.setEnable(cn, context, enable);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean isEnabled(@NonNull Context context) {
                final ComponentName cn = new ComponentName(context, NekoTile.class);
                return Component.isEnabled(cn, context);
            }

            @Override
            public boolean isSupported() {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
            }
        };
    }
}
