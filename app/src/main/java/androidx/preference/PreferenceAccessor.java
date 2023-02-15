package androidx.preference;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Nullable;

import java.util.List;

public class PreferenceAccessor {

    /**
     * ExpandButton id offset
     *
     * @see ExpandButton#ExpandButton(Context, List, long)
     * @see ExpandButton#mId
     */
    private static final int EXPAND_BUTTON_ID_OFFSET = 1000000;

    public static long getId(Preference preference) {
        return preference.getId();
    }

    public static boolean isExpandButton(Preference preference) {
        return preference instanceof ExpandButton;
    }

    /**
     * Look for the ExpandButton corresponding to the PreferenceGroup
     *
     * @param preferenceGroupAdapter PreferenceGroupAdapter
     * @param preferenceGroup        PreferenceGroup
     * @return ExpandButton
     * @see ExpandButton#ExpandButton(Context, List, long)
     * @see ExpandButton#mId
     */
    @SuppressLint("RestrictedApi")
    @Nullable
    public static Preference findGroupExpandButton(PreferenceGroupAdapter preferenceGroupAdapter, PreferenceGroup preferenceGroup) {
        long targetId = preferenceGroup.getId() + EXPAND_BUTTON_ID_OFFSET;
        for (int i = 0, l = preferenceGroupAdapter.getItemCount(); i < l; i++) {
            long id = preferenceGroupAdapter.getItemId(i);
            if (id == targetId) {
                Preference preference = preferenceGroupAdapter.getItem(i);
                if (preference == null) {
                    return null;
                }
                if (isExpandButton(preference)) {
                    return preference;
                }
            }
        }
        return null;
    }
}
