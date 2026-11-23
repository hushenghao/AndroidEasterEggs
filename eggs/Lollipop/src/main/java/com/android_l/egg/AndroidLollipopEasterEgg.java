package com.android_l.egg;

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
public class AndroidLollipopEasterEgg {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    public static BaseEasterEgg provideEasterEgg() {
        return new EasterEggGroup(
                new EasterEgg(
                        R.drawable.l_android_logo,
                        R.string.l_lland,
                        R.string.l_android_nickname,
                        AndroidVersion.LOLLIPOP,
                        com.android_l.egg.PlatLogoActivity.class,
                        List.of(AndroidVersion.LOLLIPOP_MR1)
                ) {
                    @Override
                    public SnapshotProvider provideSnapshotProvider() {
                        return new com.android_l.egg.SnapshotProvider();
                    }
                },
                new EasterEgg(
                        R.drawable.l_android_preview_logo,
                        R.string.l_webdriver_torso,
                        R.string.l_preview_nickname,
                        AndroidVersion.LOLLIPOP_PREVIEW,
                        com.android_l.egg.preview.PlatLogoActivity.class
                ) {
                    @Override
                    public SnapshotProvider provideSnapshotProvider() {
                        return new com.android_l.egg.preview.SnapshotProvider();
                    }
                }
        );
    }

    @IntoSet
    @Provides
    @Singleton
    @NonNull
    public static List<TimelineEvent> provideTimelineEvents() {
        TimelineEvent e2 = timelineEvent(
                AndroidVersion.LOLLIPOP,
                "L.\nReleased publicly as Android 5.0 in November 2014."
        );
        e2.setAndroidLogo(R.drawable.l_android_logo_2015_2019);
        return List.of(
                timelineEvent(
                        AndroidVersion.LOLLIPOP_MR1,
                        "L MR1.\nReleased publicly as Android 5.1 in March 2015."
                ),
                e2
        );
    }
}
