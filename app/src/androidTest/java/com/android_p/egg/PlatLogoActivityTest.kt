package com.android_p.egg


import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.android_p.egg.paint.Painting
import com.dede.android_eggs.EasterEggsActivityBaseTest
import com.dede.android_eggs.R
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android Pie PlatLogo test
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class PlatLogoActivityTest : EasterEggsActivityBaseTest() {

    @Test
    fun platLogoActivityTest() {

        testPlatLogo(R.string.title_android_p)

        onView(
            allOf(
                withId(android.R.id.content),
                withChild(`is`(instanceOf(FrameLayout::class.java)))
            )
        ).check(matches(isDisplayed()))
            .perform(
                click(),
                doubleTouchMove(),
                click(),
                click(),
                click(),
                click(),
                click(),
                click(),// tapCount >= 7
            )

        val painting = onView(
            allOf(
                withId(com.android_p.egg.R.id.contentView),
                withChild(`is`(instanceOf(Painting::class.java)))
            )
        ).check(matches(isDisplayed()))
            .perform(click(), move())

        // brush
        onView(withId(com.android_p.egg.R.id.btnBrush))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withId(com.android_p.egg.R.id.brushes))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(hasChildCount(6)))// NUM_BRUSHES
//        onView(withChild(withId(com.android_p.egg.R.id.brushes)))
//            .perform(click())
        painting.perform(move())

        // colors
        val btnColor = onView(withId(com.android_p.egg.R.id.btnColor))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withId(com.android_p.egg.R.id.colors))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(hasChildCount(8)))// NUM_COLORS + 2, (white, black)
        btnColor.perform(longClick())// refresh colors
        onView(withChild(withId(com.android_p.egg.R.id.colors)))
            .perform(click())
        painting.perform(move())

        // sample
        onView(withId(com.android_p.egg.R.id.btnSample))
            .check(matches(isDisplayed()))
            .perform(click())
        painting.perform(move())

        // zen
        onView(withId(com.android_p.egg.R.id.btnZen))
            .check(matches(isDisplayed()))
            .perform(click())

        // clear
        val btnClear = onView(withId(com.android_p.egg.R.id.btnClear))
            .check(matches(isDisplayed()))
            .perform(click())
        painting.perform(move())
        btnClear.perform(longClick())// invert

        pressBack()
    }

    private fun move(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isDisplayingAtLeast(90)
            }

            override fun getDescription(): String {
                return "move touch"
            }

            override fun perform(uiController: UiController, view: View) {
                val xy = IntArray(2)
                view.getLocationOnScreen(xy)

                fun getCoordinates(percent: Float): FloatArray {
                    return floatArrayOf(
                        xy[0] + view.width * percent,
                        xy[1] + view.height * percent
                    )
                }

                val down = MotionEvents.obtainDownEvent(
                    getCoordinates(0.3f),
                    Press.FINGER.describePrecision(),
                    InputDevice.SOURCE_UNKNOWN,
                    MotionEvent.BUTTON_PRIMARY
                )
                MotionEvents.sendMovement(
                    uiController,
                    down,
                    getCoordinates(0.8f)
                )
            }
        }
    }

    private fun doubleTouchMove(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isDisplayingAtLeast(90)
            }

            override fun getDescription(): String {
                return "double touch"
            }

            override fun perform(uiController: UiController, view: View) {
                val xy = IntArray(2)
                view.getLocationOnScreen(xy)

                fun getCoordinates(percent: Float): FloatArray {
                    return floatArrayOf(
                        xy[0] + view.width * percent,
                        xy[1] + view.height * percent
                    )
                }

                MotionEvents.sendDown(
                    uiController,
                    getCoordinates(0.3f),
                    Press.FINGER.describePrecision(),
                    InputDevice.SOURCE_UNKNOWN,
                    MotionEvent.ACTION_BUTTON_PRESS
                )
                uiController.loopMainThreadForAtLeast(500)

                val secondDown = MotionEvents.obtainDownEvent(
                    getCoordinates(0.6f),
                    Press.FINGER.describePrecision(),
                    InputDevice.SOURCE_UNKNOWN,
                    MotionEvent.BUTTON_SECONDARY
                )
                MotionEvents.sendMovement(
                    uiController,
                    secondDown,
                    getCoordinates(0.8f)
                )
            }
        }
    }
}
