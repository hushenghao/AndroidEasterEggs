package com.dede.android_eggs.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ProgressBar
import com.dede.basic.dp
import kotlin.math.min
import com.google.android.material.R as M3R

/**
 * Android release timeline.
 *
 * @author shhu
 * @since 2023/6/9
 */
class AndroidTimelineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = M3R.attr.linearProgressIndicatorStyle,
    defStyleRes: Int = 0,
) : ProgressBar(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        /**
         * Android release timeline.
         *
         * first:   The number of months since the previous version for this version
         * second:  Document description
         * @see [android.os.Build.VERSION_CODES]
         */
        private val timelines = arrayOf(
            +0xC to "Upside Down Cake.",                                              // 2023.09
            +0xC to "Tiramisu.",                                                      // 2022.09
            +0x3 to "S V2. Once more unto the breach, dear friends, once more.",      // 2021.12
            +0xC to "S.",                                                             // 2021.09
            +0xC to "R.\nReleased publicly as Android 11 in September 2020.",         // 2020.09
            +0xD to "Q.\nReleased publicly as Android 10 in September 2019.",         // 2019.09
            +0x8 to "P.\nReleased publicly as Android 9 in August 2018.",             // 2018.08
            +0x4 to "O MR1.\nReleased publicly as Android 8.1 in December 2017.",     // 2017.12
            +0xA to "O.\nReleased publicly as Android 8.0 in August 2017.",           // 2017.08
            +0x2 to "N MR1.\nReleased publicly as Android 7.1 in October 2016.",      // 2016.10
            +0xA to "N.\nReleased publicly as Android 7.0 in August 2016.",           // 2016.08
            +0x7 to "M.\nReleased publicly as Android 6.0 in October 2015.",          // 2015.10
            +0x4 to "L MR1.\nReleased publicly as Android 5.1 in March 2015.",        // 2015.03
            +0x5 to "L.\nReleased publicly as Android 5.0 in November 2014.",         // 2014.11
            +0x8 to "K for watches.\nReleased publicly as Android 4.4W in June 2014.",// 2014.06
            +0x3 to "K.\nReleased publicly as Android 4.4 in October 2013.",          // 2013.10
            +0x8 to "J MR2.\nReleased publicly as Android 4.3 in July 2013.",         // 2013.07
            +0x4 to "J MR1.\nReleased publicly as Android 4.2 in November 2012.",     // 2012.11
            +0x7 to "J.\nReleased publicly as Android 4.1 in July 2012.",             // 2012.07
            +0x2 to "I MR1.\nReleased publicly as Android 4.03 in December 2011.",    // 2011.12
            +0x3 to "I.\nReleased publicly as Android 4.0 in October 2011.",          // 2011.10
            +0x2 to "H MR2.\nReleased publicly as Android 3.2 in July 2011.",         // 2011.07
            +0x3 to "H MR1.\nReleased publicly as Android 3.1 in May 2011.",          // 2011.05
            +0x1 to "H.\nReleased publicly as Android 3.0 in February 2011.",         // 2011.02  month offset +1
            +0x2 to "G MR1.\nReleased publicly as Android 2.3.3 in February 2011.",   // 2011.02
            +0x7 to "G.\nReleased publicly as Android 2.3 in December 2010.",         // 2010.12
            +0x4 to "F.\nReleased publicly as Android 2.2 in May 2010.",              // 2010.05
            +0x1 to "E MR1.\nReleased publicly as Android 2.1 in January 2010.",      // 2010.01
            +0x2 to "E incremental update.\nReleased publicly as Android 2.0.1 in December 2009.",// 2009.12
            +0x1 to "E.\nReleased publicly as Android 2.0 in October 2009.",          // 2009.10
            +0x5 to "D.\nReleased publicly as Android 1.6 in September 2009.",        // 2009.09
            +0x2 to "C.\nReleased publicly as Android 1.5 in April 2009.",            // 2009.04
            +0x5 to "First Android update.\nReleased publicly as Android 1.1 in February 2009.",  // 2009.02
            0x0 to "The original, first, version of Android. Yay!\nReleased publicly as Android 1.0 in September 2008."// 2008.09
        )
    }

    init {
        progressDrawable = TimelineDrawable()
        isIndeterminate = true
    }

    private class TimelineDrawable : Drawable(), Drawable.Callback {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        override fun getIntrinsicWidth(): Int {
            return 18.dp
        }

        override fun draw(canvas: Canvas) {
            val bound = bounds
            val w = bound.width().toFloat()
            val h = bound.height().toFloat()
            val r = min(w, h) / 2f
            paint.color = Color.MAGENTA
            canvas.drawRoundRect(
                0f, 0f, w, h,
                r, r,
                paint
            )
        }

        override fun setAlpha(alpha: Int) {
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

        override fun invalidateDrawable(who: Drawable) {

        }

        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        }

        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        }
    }
}