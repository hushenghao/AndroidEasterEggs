package com.android_m.egg;

import static com.dede.basic.provider.TimelineEvent.timelineEvent;

import android.os.Build;

import androidx.annotation.NonNull;

import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.EasterEgg;
import com.dede.basic.provider.EasterEggProvider;
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
public class AndroidMarshmallowEasterEgg implements EasterEggProvider {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.m_android_logo,
                R.string.m_mland,
                R.string.m_android_nickname,
                Build.VERSION_CODES.M,
                PlatLogoActivity.class
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
    @Override
    public List<TimelineEvent> provideTimelineEvents() {
        return List.of(
                timelineEvent(
                        Build.VERSION_CODES.M,
                        "M.\nReleased publicly as Android 6.0 in October 2015."
                )
        );
    }
}
