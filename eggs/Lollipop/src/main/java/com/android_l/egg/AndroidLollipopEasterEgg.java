package com.android_l.egg;

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
public class AndroidLollipopEasterEgg implements EasterEggProvider {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.l_android_logo,
                R.string.l_lland,
                R.string.l_android_nickname,
                new IntRange(Build.VERSION_CODES.LOLLIPOP, Build.VERSION_CODES.LOLLIPOP_MR1),
                true
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
                        Build.VERSION_CODES.LOLLIPOP_MR1,
                        "L MR1.\nReleased publicly as Android 5.1 in March 2015."
                ),
                timelineEvent(
                        Build.VERSION_CODES.LOLLIPOP,
                        "L.\nReleased publicly as Android 5.0 in November 2014."
                )
        );
    }
}
