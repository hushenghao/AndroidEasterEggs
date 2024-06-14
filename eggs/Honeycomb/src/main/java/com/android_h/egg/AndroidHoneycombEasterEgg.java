package com.android_h.egg;

import static com.dede.basic.provider.TimelineEvent.timelineEvent;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;

import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.EasterEgg;
import com.dede.basic.provider.EasterEggProvider;
import com.dede.basic.provider.TimelineEvent;

import java.util.Arrays;
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
public class AndroidHoneycombEasterEgg implements EasterEggProvider {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.h_android_logo,
                R.string.h_egg_name,
                R.string.h_egg_name,
                new IntRange(Build.VERSION_CODES.HONEYCOMB, Build.VERSION_CODES.HONEYCOMB_MR2),
                false
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
    @NonNull
    @Override
    public List<TimelineEvent> provideTimelineEvents() {
        return Arrays.asList(
                timelineEvent(
                        Build.VERSION_CODES.HONEYCOMB_MR2,
                        "H MR2.\nReleased publicly as Android 3.2 in July 2011."
                ),
                timelineEvent(
                        Build.VERSION_CODES.HONEYCOMB_MR1,
                        "H MR1.\nReleased publicly as Android 3.1 in May 2011."
                ),
                timelineEvent(
                        Build.VERSION_CODES.HONEYCOMB,
                        "H.\nReleased publicly as Android 3.0 in February 2011."
                )
        );
    }
}
