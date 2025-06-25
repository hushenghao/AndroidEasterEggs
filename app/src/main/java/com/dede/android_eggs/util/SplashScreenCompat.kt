package com.dede.android_eggs.util

import android.app.Activity
import android.content.res.Resources
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.widget.ImageView
import androidx.core.splashscreen.R
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dede.basic.requireDrawable

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
        animatedIcon = requireDrawable(typedValue.resourceId)
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

            // Start the animation on the icon view
            imageView.setImageDrawable(animatedIcon)
            animatedIcon.start()
            imageView.postDelayed({ viewProvider.remove() }, animationDuration)
        }
    }
}
