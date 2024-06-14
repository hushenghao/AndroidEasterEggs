package com.android_i.egg;

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
public class AndroidIceCreamSandwichEasterEgg implements EasterEggProvider {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.i_platlogo_scale,
                R.string.i_egg_name,
                R.string.i_android_nickname,
                new IntRange(Build.VERSION_CODES.ICE_CREAM_SANDWICH, Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1),
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

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    @Override
    public List<TimelineEvent> provideTimelineEvents() {
        return Arrays.asList(
                timelineEvent(
                        Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1,
                        "I MR1.\nReleased publicly as Android 4.03 in December 2011."
                ),
                timelineEvent(
                        Build.VERSION_CODES.ICE_CREAM_SANDWICH,
                        "I.\nReleased publicly as Android 4.0 in October 2011."
                )
        );
    }
}
