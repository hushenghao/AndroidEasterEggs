package com.android_i.egg;

import com.dede.basic.provider.AndroidVersion;

import static com.dede.basic.provider.TimelineEvent.timelineEvent;


import androidx.annotation.NonNull;

import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.EasterEgg;
import com.dede.basic.provider.EasterEggGroup;
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
public class AndroidIceCreamSandwichEasterEgg {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    public static BaseEasterEgg provideEasterEgg() {
        return new EasterEggGroup(
                new EasterEgg(
                        R.drawable.i_platlogo_rectangle,
                        R.string.i_egg_name,
                        R.string.i_android_nickname,
                        AndroidVersion.ICE_CREAM_SANDWICH,
                        PlatLogoActivity.class,
                        List.of(AndroidVersion.ICE_CREAM_SANDWICH_MR1)
                ) {
                    @Override
                    public SnapshotProvider provideSnapshotProvider() {
                        return new SnapshotProvider(R.drawable.i_platlogo);
                    }
                },
                new EasterEgg(
                        R.drawable.i_platlogo_preview,
                        R.string.i_android_nickname,
                        R.string.i_preview_nickname,
                        AndroidVersion.ICE_CREAM_SANDWICH_PREVIEW,
                        com.android_i.egg.preview.PlatLogoActivity.class
                ) {
                    @Override
                    public SnapshotProvider provideSnapshotProvider() {
                        return new SnapshotProvider(R.drawable.i_platlogo_preview);
                    }
                }
        );
    }

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    public static List<TimelineEvent> provideTimelineEvents() {
        return List.of(
                timelineEvent(
                        AndroidVersion.ICE_CREAM_SANDWICH_MR1,
                        "I MR1.\nReleased publicly as Android 4.03 in December 2011."
                ),
                timelineEvent(
                        AndroidVersion.ICE_CREAM_SANDWICH,
                        "I.\nReleased publicly as Android 4.0 in October 2011."
                )
        );
    }
}
