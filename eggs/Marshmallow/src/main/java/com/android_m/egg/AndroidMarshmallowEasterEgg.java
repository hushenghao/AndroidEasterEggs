package com.android_m.egg;

import com.dede.basic.provider.AndroidVersion;

import static com.dede.basic.provider.TimelineEvent.timelineEvent;


import androidx.annotation.NonNull;

import com.dede.basic.provider.BaseEasterEgg;
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
public class AndroidMarshmallowEasterEgg {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    public static BaseEasterEgg provideEasterEgg() {
        return new EasterEggGroup(
                new EasterEgg(
                        R.drawable.m_android_logo,
                        R.string.m_mland,
                        R.string.m_android_nickname,
                        AndroidVersion.M,
                        PlatLogoActivity.class
                ) {
                    @Override
                    public SnapshotProvider provideSnapshotProvider() {
                        return new com.android_m.egg.SnapshotProvider();
                    }
                },
                new EasterEgg(
                        R.drawable.m_android_preview_logo,
                        R.string.m_preview_land,
                        R.string.m_preview_nickname,
                        AndroidVersion.M_PREVIEW,
                        com.android_m.egg.preview.PlatLogoActivity.class
                ) {
                    @Override
                    public SnapshotProvider provideSnapshotProvider() {
                        return new com.android_m.egg.preview.SnapshotProvider();
                    }
                }
        );
    }

    @IntoSet
    @Provides
    @Singleton
    @NonNull
    public static List<TimelineEvent> provideTimelineEvents() {
        return List.of(
                timelineEvent(
                        AndroidVersion.M,
                        "M.\nReleased publicly as Android 6.0 in October 2015."
                )
        );
    }
}
