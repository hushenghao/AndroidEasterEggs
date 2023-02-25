package androidx.appcompat.widget;

import android.content.res.Resources;

import androidx.annotation.NonNull;

/**
 * Override {@link android.R.string.config_icon_mask}
 */
public class ResourcesOverride extends ResourcesWrapper {

    private final int mOverrideId;
    private final String mOverrideValue;

    public ResourcesOverride(Resources resources, int overrideId, String overrideValue) {
        super(resources);
        mOverrideId = overrideId;
        mOverrideValue = overrideValue;
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        if (id == mOverrideId) {
            return mOverrideValue;
        }
        return super.getString(id);
    }
}
