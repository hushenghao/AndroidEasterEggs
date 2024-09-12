package com.android_k.egg;

import static com.dede.basic.provider.TimelineEvent.timelineEvent;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.ComponentProvider;
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
import kotlin.ranges.IntRange;

@Module
@InstallIn(SingletonComponent.class)
public class AndroidKitKatEasterEgg implements EasterEggProvider, ComponentProvider {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.k_android_logo,
                R.string.k_dessert_case,
                R.string.k_android_nickname,
                new IntRange(Build.VERSION_CODES.KITKAT, Build.VERSION_CODES.KITKAT_WATCH),
                PlatLogoActivity.class
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
    @Override
    public Component provideComponent() {
        return new Component(
                R.drawable.k_platlogo,
                R.string.k_dessert_case,
                R.string.k_android_nickname,
                new IntRange(Build.VERSION_CODES.KITKAT, Build.VERSION_CODES.KITKAT_WATCH)
        ) {
            @Override
            public boolean isSupported() {
                return true;
            }

            @Override
            public boolean isEnabled(@NonNull Context context) {
                ComponentName cn = new ComponentName(context, DessertCaseDream.class);
                return Component.isEnabled(cn, context);
            }

            @Override
            public void setEnabled(@NonNull Context context, boolean enable) {
                ComponentName cn = new ComponentName(context, DessertCaseDream.class);
                Component.setEnable(cn, context, enable);
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
                        Build.VERSION_CODES.KITKAT_WATCH,
                        "K for watches.\nReleased publicly as Android 4.4W in June 2014."
                ),
                timelineEvent(
                        Build.VERSION_CODES.KITKAT,
                        "K.\nReleased publicly as Android 4.4 in October 2013."
                )
        );
    }
}
