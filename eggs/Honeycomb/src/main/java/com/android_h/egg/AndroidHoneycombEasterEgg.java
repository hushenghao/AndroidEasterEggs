package com.android_h.egg;

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
public class AndroidHoneycombEasterEgg {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    public static BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.h_android_logo,
                R.string.h_egg_name,
                R.string.h_egg_name,
                AndroidVersion.HONEYCOMB,
                PlatLogoActivity.class,
                List.of(AndroidVersion.HONEYCOMB_MR1, AndroidVersion.HONEYCOMB_MR2)
        ) {
            @Override
            public SnapshotProvider provideSnapshotProvider() {
                return new SnapshotProvider();
            }
        };
    }

    @IntoSet
    @Provides
    @Singleton
    @NonNull
    public static List<TimelineEvent> provideTimelineEvents() {
        return List.of(
                timelineEvent(
                        AndroidVersion.HONEYCOMB_MR2,
                        "H MR2.\nReleased publicly as Android 3.2 in July 2011."
                ),
                timelineEvent(
                        AndroidVersion.HONEYCOMB_MR1,
                        "H MR1.\nReleased publicly as Android 3.1 in May 2011."
                ),
                timelineEvent(
                        AndroidVersion.HONEYCOMB,
                        "H.\nReleased publicly as Android 3.0 in February 2011."
                )
        );
    }
}
