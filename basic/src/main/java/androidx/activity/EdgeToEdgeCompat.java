package androidx.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.view.WindowInsetsCompat;

import com.dede.basic.utils.DynamicObjectUtils;
import com.dede.basic.utils.dynamic.DynamicResult;

/**
 * EdgeToEdge compat
 *
 * @see EdgeToEdge
 */
public class EdgeToEdgeCompat {

    public static final int EDGE_INSETS_MASK = WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout();

    private static final String TAG = "EdgeToEdgeCompat";

    private final static EdgeToEdgeImpl impl = createEdgeToEdgeImpl();

    /**
     * Create EdgeToEdge instance
     *
     * @return EdgeToEdgeImpl instance
     * @see EdgeToEdge#enable(ComponentActivity)
     */
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
        if (impl == null) {
            impl = new MaterialEdgeToEdgeUtilsImpl();
        }
        Log.i(TAG, "EdgeToEdgeImpl: " + impl);
        return impl;
    }

    private static class MaterialEdgeToEdgeUtilsImpl implements EdgeToEdgeImpl {

        private static int getScrimColor(SystemBarStyle systemBarStyle, boolean isDark) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // support dark mode
                return systemBarStyle.getScrimWithEnforcedContrast$activity_release(isDark);
            } else {
                return systemBarStyle.getScrim$activity_release(isDark);
            }
        }

        @Override
        public void setUp(@NonNull SystemBarStyle statusBarStyle,
                          @NonNull SystemBarStyle navigationBarStyle,
                          @NonNull Window window,
                          @NonNull View view,
                          boolean statusBarIsDark,
                          boolean navigationBarIsDark) {
            DynamicObjectUtils.asDynamicObject("com.google.android.material.internal.EdgeToEdgeUtils")
                    .invokeMethod("applyEdgeToEdge",
                            new Class[]{Window.class, boolean.class, Integer.class, Integer.class},
                            new Object[]{window, true, getScrimColor(statusBarStyle, statusBarIsDark), getScrimColor(navigationBarStyle, navigationBarIsDark)});
        }

        @Override
        public void adjustLayoutInDisplayCutoutMode(@NonNull Window window) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                attributes.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                attributes.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
        }
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
