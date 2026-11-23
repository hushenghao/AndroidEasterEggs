package com.android_n.egg;

import com.dede.basic.provider.AndroidVersion;

import static com.dede.basic.provider.TimelineEvent.timelineEvent;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.ChecksSdkIntAtLeast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android_n.egg.neko.NekoTile;
import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.ComponentProvider;
import com.dede.basic.provider.ComponentProvider.Component;
import com.dede.basic.provider.EasterEgg;
import com.dede.basic.provider.EasterEggGroup;
import com.dede.basic.provider.SnapshotProvider;
import com.dede.basic.provider.TimelineEvent;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoSet;

@Module
@InstallIn(SingletonComponent.class)
public class AndroidNougatEasterEgg {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    public static BaseEasterEgg provideEasterEgg() {
        return new EasterEggGroup(
                new EasterEgg(
                        R.drawable.n_android_logo,
                        R.string.n_app_name,
                        R.string.n_android_nickname,
                        AndroidVersion.N,
                        PlatLogoActivity.class,
                        List.of(AndroidVersion.N_MR1)
                ) {
                    @Override
                    public SnapshotProvider provideSnapshotProvider() {
                        return new com.android_n.egg.SnapshotProvider();
                    }
                },
                new EasterEgg(
                        R.drawable.n_android_logo,
                        R.string.n_app_name,
                        R.string.n_android_nickname_preview,
                        AndroidVersion.N_PREVIEW,
                        com.android_n.egg.preview.PlatLogoActivity.class
                ) {
                    @Override
                    public SnapshotProvider provideSnapshotProvider() {
                        return new com.android_n.egg.preview.SnapshotProvider();
                    }
                }
        );
    }

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    public static Component provideComponent() {
        return new ComponentProvider.Component(
                R.drawable.n_stat_tint_icon,
                R.string.n_default_tile_name,
                R.string.n_android_nickname,
                AndroidVersion.N, List.of(AndroidVersion.N_MR1)
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

            @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
            @Override
            public boolean isSupported() {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
            }
        };
    }

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    public static List<TimelineEvent> provideTimelineEvents() {
        return List.of(
                timelineEvent(
                        AndroidVersion.N_MR1,
                        "N MR1.\nReleased publicly as Android 7.1 in October 2016."
                ),
                timelineEvent(
                        AndroidVersion.N,
                        "N.\nReleased publicly as Android 7.0 in August 2016."
                )
        );
    }
}
