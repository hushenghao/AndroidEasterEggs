package com.android_l.egg;

import static com.dede.basic.provider.TimelineEvent.timelineEvent;

import android.os.Build;

import androidx.annotation.NonNull;

import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.EasterEgg;
import com.dede.basic.provider.EasterEggGroup;
import com.dede.basic.provider.EasterEggProvider;
import com.dede.basic.provider.SnapshotProvider;
import com.dede.basic.provider.TimelineEvent;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoSet;
import kotlin.ranges.IntRange;

@Module
@InstallIn(SingletonComponent.class)
public class AndroidLollipopEasterEgg implements EasterEggProvider {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEggGroup(
                new EasterEgg(
                        R.drawable.l_android_logo,
                        R.string.l_lland,
                        R.string.l_android_nickname,
                        new IntRange(Build.VERSION_CODES.LOLLIPOP, Build.VERSION_CODES.LOLLIPOP_MR1),
                        com.android_l.egg.PlatLogoActivity.class
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
                        Build.VERSION_CODES.LOLLIPOP,
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
    @Override
    public List<TimelineEvent> provideTimelineEvents() {
        TimelineEvent e2 = timelineEvent(
                Build.VERSION_CODES.LOLLIPOP,
                "L.\nReleased publicly as Android 5.0 in November 2014."
        );
        e2.setAndroidLogo(R.drawable.l_android_logo_2015_2019);
        return List.of(
                timelineEvent(
                        Build.VERSION_CODES.LOLLIPOP_MR1,
                        "L MR1.\nReleased publicly as Android 5.1 in March 2015."
                ),
                e2
        );
    }
}
