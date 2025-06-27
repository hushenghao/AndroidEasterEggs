package com.dede.android_eggs.util

import android.app.Activity
import android.content.res.Resources
import android.graphics.drawable.Animatable
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.splashscreen.R
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.dede.basic.delay

private fun Resources.Theme.resolveAttribute(attr: Int, typedValue: TypedValue): Boolean {
    return resolveAttribute(attr, typedValue, true)
}

fun Activity.setupSplashScreen() {
    val splashScreen = installSplashScreen()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // For Android 12 and above, we can use the default splash screen
        return
    }

    // For Android 12 below, we can set an animated icon for the splash screen
    val typedValue = TypedValue()
    var animatedIcon: Drawable? = null
    if (theme.resolveAttribute(R.attr.windowSplashScreenAnimatedIcon, typedValue)) {
        // android N dont use vector compat
        animatedIcon = AppCompatResources.getDrawable(this, typedValue.resourceId)
    }

    var animationDuration = 0L
    if (theme.resolveAttribute(R.attr.windowSplashScreenAnimationDuration, typedValue)) {
        animationDuration = typedValue.data.toLong()
    }

    if (animatedIcon is Animatable && animationDuration > 0) {
        splashScreen.setOnExitAnimationListener { viewProvider ->
            val imageView: ImageView? = viewProvider.iconView as? ImageView
            if (imageView == null) {
                viewProvider.remove()
                return@setOnExitAnimationListener
            }

            imageView.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    imageView.viewTreeObserver.removeOnPreDrawListener(this)

                    animatedIcon.setAnimationEndCallback(animationDuration) {
                        // Remove the splash screen view after the animation ends
                        viewProvider.remove()
                    }
                    // Start the animation on the icon view
                    animatedIcon.start()
                    return true
                }
            })
            imageView.setImageDrawable(animatedIcon)
        }
    }
}

private fun Drawable.setAnimationEndCallback(defaultDuration: Long, callback: () -> Unit) {
    if (this is Animatable2Compat) {
        registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                callback()
            }
        })
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this is Animatable2) {
        registerAnimationCallback(object : Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                callback()
            }
        })
    } else {
        // android.graphics.drawable.Animatable without callback support
        delay(defaultDuration) {
            callback()
        }
    }
}
