package com.android_g.egg;

import com.dede.basic.provider.AndroidVersion;

import static com.dede.basic.provider.TimelineEvent.timelineEvent;


import androidx.annotation.NonNull;

import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.EasterEgg;
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
public class AndroidGingerbreadEasterEgg {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    public static BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.g_android_logo,
                R.string.g_egg_name,
                R.string.g_egg_name,
                AndroidVersion.GINGERBREAD,
                PlatLogoActivity.class,
                List.of(AndroidVersion.GINGERBREAD_MR1)
        ) {
            @Override
            public SnapshotProvider provideSnapshotProvider() {
                return new SnapshotProvider();
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
                        AndroidVersion.GINGERBREAD_MR1,
                        "G MR1.\nReleased publicly as Android 2.3.3 in February 2011."
                ),
                timelineEvent(
                        AndroidVersion.GINGERBREAD,
                        "G.\nReleased publicly as Android 2.3 in December 2010."
                )
        );
    }
}
