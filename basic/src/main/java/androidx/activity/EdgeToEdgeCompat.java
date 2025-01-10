package androidx.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.dede.basic.utils.DynamicObjectUtils;
import com.dede.basic.utils.dynamic.DynamicResult;

import org.jetbrains.annotations.Nullable;

/**
 * EdgeToEdge compat
 *
 * @see EdgeToEdge
 */
public class EdgeToEdgeCompat {

    private static final String TAG = "EdgeToEdgeCompat";

    @Nullable
    private final static EdgeToEdgeImpl impl = createEdgeToEdgeImpl();

    /**
     * Create EdgeToEdge instance
     *
     * @return EdgeToEdgeImpl instance
     * @see EdgeToEdge#enable(ComponentActivity)
     */
    @Nullable
    private static EdgeToEdgeImpl createEdgeToEdgeImpl() {
        String className = "androidx.activity.EdgeToEdgeBase";
        final int[] apis = {
                Build.VERSION_CODES.R,      // 30
                Build.VERSION_CODES.Q,      // 29
                Build.VERSION_CODES.P,      // 28
                Build.VERSION_CODES.O,      // 26
                Build.VERSION_CODES.M,      // 23
                Build.VERSION_CODES.LOLLIPOP,// 21
        };
        for (int api : apis) {
            if (Build.VERSION.SDK_INT >= api) {
                className = "androidx.activity.EdgeToEdgeApi" + api;
                break;
            }
        }
        DynamicResult dynamicResult = DynamicObjectUtils.asDynamicObject(className)
                .newInstance(new Class[0], new Object[0]);
        EdgeToEdgeImpl impl = DynamicResult.getTypeValue(dynamicResult, EdgeToEdgeImpl.class);
        Log.i(TAG, "EdgeToEdgeImpl: " + impl);
        return impl;
    }

    private static boolean getDetectDarkMode(SystemBarStyle systemBarStyle, View view) {
        // internal val detectDarkMode: (Resources) -> Boolean
        //  androidx.activity library
        //  release build
        return systemBarStyle.getDetectDarkMode$activity_release().invoke(view.getResources());
    }

    public static void enable(Activity activity, SystemBarStyle statusBarStyle, SystemBarStyle navigationBarStyle) {
        if (activity instanceof ComponentActivity) {
            EdgeToEdge.enable((ComponentActivity) activity, statusBarStyle, navigationBarStyle);
            return;
        }

        if (impl == null) {
            Log.w(TAG, "enableEdgeToEdge, impl == null");
            return;
        }

        Window window = activity.getWindow();
        View view = window.getDecorView();
        boolean statusBarIsDark = getDetectDarkMode(statusBarStyle, view);
        boolean navigationBarIsDark = getDetectDarkMode(navigationBarStyle, view);
        impl.setUp(
                statusBarStyle, navigationBarStyle, window, view, statusBarIsDark, navigationBarIsDark
        );
        impl.adjustLayoutInDisplayCutoutMode(window);
    }

    public static void enable(Activity activity) {
        enable(activity, SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
                SystemBarStyle.auto(EdgeToEdge.getDefaultLightScrim(), EdgeToEdge.getDefaultDarkScrim()));
    }
}
