package com.android_j.egg;

import static com.dede.basic.provider.TimelineEvent.timelineEvent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.dede.basic.provider.BaseEasterEgg;
import com.dede.basic.provider.ComponentProvider;
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
public class AndroidJellyBeanEasterEgg implements EasterEggProvider, ComponentProvider {

    @NonNull
    @IntoSet
    @Provides
    @Singleton
    @Override
    public BaseEasterEgg provideEasterEgg() {
        return new EasterEgg(
                R.drawable.j_android_logo,
                R.string.j_egg_name,
                R.string.j_android_nickname,
                new IntRange(Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.JELLY_BEAN_MR2),
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
    public Component provideComponent() {
        return new Component(
                R.drawable.j_redbean2,
                R.string.j_jelly_bean_dream_name,
                R.string.j_android_nickname,
                new IntRange(Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.JELLY_BEAN_MR2)
        ) {
            @Override
            public boolean isSupported() {
                return true;
            }

            @Override
            public boolean isEnabled(@NonNull Context context) {
                ComponentName cn = new ComponentName(context, BeanBagDream.class);
                return Component.isEnabled(cn, context);
            }

            @Override
            public void setEnabled(@NonNull Context context, boolean enable) {
                ComponentName cn = new ComponentName(context, BeanBagDream.class);
                Component.setEnable(cn, context, enable);
            }
        };
    }

    @NonNull
    @Override
    public List<TimelineEvent> provideTimelineEvents() {
        return Arrays.asList(
                timelineEvent(
                        Build.VERSION_CODES.JELLY_BEAN_MR2,
                        "J MR2.\nReleased publicly as Android 4.3 in July 2013."
                ),
                timelineEvent(
                        Build.VERSION_CODES.JELLY_BEAN_MR1,
                        "J MR1.\nReleased publicly as Android 4.2 in November 2012."
                ),
                timelineEvent(
                        Build.VERSION_CODES.JELLY_BEAN,
                        "J.\nReleased publicly as Android 4.1 in July 2012."
                )
        );
    }
}
