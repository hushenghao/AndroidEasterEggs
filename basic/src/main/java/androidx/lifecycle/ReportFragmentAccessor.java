package androidx.lifecycle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


public class ReportFragmentAccessor {

    private static class Fake extends Activity implements LifecycleOwner {
        private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
        private final Activity delegate;

        public Fake(Activity delegate) {
            this.delegate = delegate;
        }

        @Override
        public FragmentManager getFragmentManager() {
            return delegate.getFragmentManager();
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void registerActivityLifecycleCallbacks(@NonNull Application.ActivityLifecycleCallbacks callback) {
            delegate.registerActivityLifecycleCallbacks(callback);
        }

        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            return lifecycleRegistry;
        }
    }

    @SuppressLint("RestrictedApi")
    public static Lifecycle injectIfNeededIn(Activity activity) {
        Fake fake = new Fake(activity);
        ReportFragment.injectIfNeededIn(fake);
        return fake.getLifecycle();
    }
}
