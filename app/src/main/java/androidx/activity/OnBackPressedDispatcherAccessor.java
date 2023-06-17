package androidx.activity;

import android.os.Build;
import android.window.OnBackInvokedCallback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.BuildCompat;

import java.lang.reflect.Field;

import kotlin.Unit;

/**
 * Fix OnBackAnimationCallback on U.
 * Bypass `BuildCompat.isAtLeastU`'s checks.
 *
 * @author shhu
 * @since 2023/6/12
 */
@BuildCompat.PrereleaseSdkCheck
public class OnBackPressedDispatcherAccessor {
    public static void fixApi34(AppCompatActivity host) {
        if (Build.VERSION.SDK_INT < 34 || BuildCompat.isAtLeastU()) return;
        OnBackPressedDispatcher dispatcher = host.getOnBackPressedDispatcher();
        try {
            Field field = dispatcher.getClass().getDeclaredField("onBackInvokedCallback");
            field.setAccessible(true);
            field.set(dispatcher, createOnBackAnimationCallback(dispatcher));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private static OnBackInvokedCallback createOnBackAnimationCallback(OnBackPressedDispatcher dispatcher) {
        // androidx.activity.OnBackPressedDispatcher init block
        //noinspection KotlinInternalInJava
        return OnBackPressedDispatcher.Api34Impl.INSTANCE.createOnBackAnimationCallback(
                backEventCompat -> {
                    dispatcher.dispatchOnBackStarted(backEventCompat);
                    return Unit.INSTANCE;
                },
                backEventCompat -> {
                    dispatcher.dispatchOnBackProgressed(backEventCompat);
                    return Unit.INSTANCE;
                },
                () -> {
                    dispatcher.onBackPressed();
                    return Unit.INSTANCE;
                },
                () -> {
                    dispatcher.dispatchOnBackCancelled();
                    return Unit.INSTANCE;
                }
        );
    }

}
