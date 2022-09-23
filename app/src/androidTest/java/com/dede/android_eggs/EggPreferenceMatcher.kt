package com.dede.android_eggs

import android.content.res.Resources
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.preference.PreferenceViewHolder
import org.hamcrest.Description
import org.hamcrest.TypeSafeDiagnosingMatcher

/**
 * Find Preference by title res
 *
 * @author shhu
 * @since 2022/9/2
 */
class EggPreferenceMatcher(@StringRes val titleRes: Int) :
    TypeSafeDiagnosingMatcher<PreferenceViewHolder>() {

    private var expectedText: String? = null

    override fun describeTo(description: Description) {
        description.appendText("item title res id: $titleRes,")
    }

    override fun matchesSafely(item: PreferenceViewHolder, description: Description): Boolean {
        if (expectedText == null) {
            try {
                expectedText = item.itemView.resources.getString(titleRes)
            } catch (e: Resources.NotFoundException) {
                description.appendText(" title res not found")
            }
        } else {
            description.appendText(" title: $expectedText")
        }
        val expected = expectedText
        val titleView = item.findViewById(android.R.id.title) as TextView
        val actual = titleView.text.toString()
        return actual == expected || (expected != null && expected.startsWith(actual))
    }
}