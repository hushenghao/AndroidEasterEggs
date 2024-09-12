package com.android_o.egg;

import static com.dede.basic.provider.TimelineEvent.timelineEvent;

import android.os.Build;

import androidx.annotation.NonNull;

import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.EasterEgg;
import com.dede.basic.provider.EasterEggGroup;
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
public class AndroidOreoEasterEgg implements EasterEggProvider {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEggGroup(
                new EasterEgg(
                        R.drawable.o_android_logo,
                        R.string.o_app_name,
                        R.string.o_android_nickname,
                        Build.VERSION_CODES.O,
                        PlatLogoActivity.class
                ) {
                    @Override
                    public SnapshotProvider provideSnapshotProvider() {
                        return new SnapshotProvider();
                    }
                },
                new EasterEgg(
                        R.drawable.o_android_logo,
                        R.string.o_app_name,
                        R.string.o_android_nickname,
                        Build.VERSION_CODES.O_MR1,
                        PlatLogoActivity.Point1.class
                ) {
                    @Override
                    public SnapshotProvider provideSnapshotProvider() {
                        return new SnapshotProvider(true);
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
        return List.of(
                timelineEvent(
                        Build.VERSION_CODES.O_MR1,
                        "O MR1.\nReleased publicly as Android 8.1 in December 2017."
                ),
                timelineEvent(
                        Build.VERSION_CODES.O,
                        "O.\nReleased publicly as Android 8.0 in August 2017."
                )
        );
    }
}
