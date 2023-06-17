package com.dede.android_eggs

import android.content.res.Resources
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher


/**
 * Find EggItem by title res
 *
 * @author shhu
 * @since 2022/9/2
 */
class EggItemMatcher(@StringRes val titleRes: Int) :
    TypeSafeDiagnosingMatcher<ViewHolder>() {

    private var expectedText: String? = null

    override fun describeTo(description: Description) {
        description.appendText("item title res id: $titleRes,")
    }

    override fun matchesSafely(item: ViewHolder, description: Description): Boolean {
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
        val titleView = item.itemView.findViewById(R.id.tv_summary) as? TextView ?: return false
        val actual = titleView.text.toString()
        return actual == expected
    }
}