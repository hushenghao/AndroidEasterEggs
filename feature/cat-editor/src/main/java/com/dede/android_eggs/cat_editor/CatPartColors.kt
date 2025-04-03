package com.dede.android_eggs.cat_editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Random

internal object CatPartColors {

    private val P_BODY_COLORS: IntArray = intArrayOf(
        180, -0xdededf, // black
        180, -0x1,      // white
        140, -0x9e9e9f, // gray
        140, -0x86aab8, // brown
        100, -0x6f5b52, // steel
        100, -0x63c,    // buff
        100, -0x7100,   // orange
        5, -0xd6490a,   // blue..?
        5, -0x322e,     // pink!?
        5, -0x316c28,   // purple?!?!?
        4, -0xbc5fb9,   // yeah, why not green
        1, 0,           // ?!?!?!
    )

    private val P_COLLAR_COLORS: IntArray = intArrayOf(
        250, -0x1,
        250, -0x1000000,
        250, -0xbbcca,
        50, -0xe6892e,
        50, -0x227cb,
        50, -0x47400,
        50, -0xb704f,
        50, -0xb350b0,
    )

    private val P_BELLY_COLORS: IntArray = intArrayOf(
        750, 0,
        250, -0x1,
    )

    private val P_DARK_SPOT_COLORS: IntArray = intArrayOf(
        700, 0,
        250, -0xdededf,
        50, -0x92b3bf,
    )

    private val P_LIGHT_SPOT_COLORS: IntArray = intArrayOf(
        700, 0,
        300, -0x1,
    )

    private fun frandrange(r: Random, a: Float, b: Float): Float {
        return (b - a) * r.nextFloat() + a
    }

    private fun choose(r: Random, vararg l: Any?): Any? {
        return l[r.nextInt(l.size)]
    }

    private fun chooseP(r: Random, a: IntArray): Int {
        var pct = r.nextInt(1000)
        val stop = a.size - 2
        var i = 0
        while (i < stop) {
            pct -= a[i]
            if (pct < 0) break
            i += 2
        }
        return a[i + 1]
    }

    private fun isDark(c: Color): Boolean {
        val color = c.toArgb()
        val r = (color and 0xFF0000) shr 16
        val g = (color and 0x00FF00) shr 8
        val b = color and 0x0000FF
        return (r + g + b) < 0x80
    }

    private const val INDEX_OF_BODY = 24
    private const val INDEX_OF_HEAD = 5
    private const val INDEX_OF_LEG1 = 16
    private const val INDEX_OF_LEG2 = 18
    private const val INDEX_OF_LEG2_SHADOW = 23
    private const val INDEX_OF_LEG3 = 20
    private const val INDEX_OF_LEG4 = 22
    private const val INDEX_OF_LEFT_EAR = 1
    private const val INDEX_OF_RIGHT_EAR = 3
    private const val INDEX_OF_LEFT_EAR_INSIDE = 2
    private const val INDEX_OF_RIGHT_EAR_INSIDE = 4
    private const val INDEX_OF_FOOT1 = 15
    private const val INDEX_OF_FOOT2 = 17
    private const val INDEX_OF_FOOT3 = 19
    private const val INDEX_OF_FOOT4 = 21
    private const val INDEX_OF_TAIL = 12
    private const val INDEX_OF_TAIL_CAP = 13
    private const val INDEX_OF_TAIL_SHADOW = 14
    private const val INDEX_OF_LEFT_EYE = 8
    private const val INDEX_OF_RIGHT_EYE = 9
    private const val INDEX_OF_MOUTH = 10
    private const val INDEX_OF_NOSE = 11
    private const val INDEX_OF_BELLY = 25
    private const val INDEX_OF_FACE_SPOT = 6
    private const val INDEX_OF_CAP = 7
    private const val INDEX_OF_COLLAR = 0
    private const val INDEX_OF_BOWTIE = 26

    fun colors(seed: Long = System.currentTimeMillis()): Array<Color> {
        val arr = Array(27) { Color.Black }
        val nsr = Random(seed)

        var bodyColor = Color(chooseP(nsr, P_BODY_COLORS))
        if (bodyColor == Color.Transparent) {
            // invisible cat
            bodyColor = Color.hsv(
                hue = nsr.nextFloat() * 360f,
                saturation = frandrange(nsr, 0.5f, 1f),
                value = frandrange(nsr, 0.5f, 1f)
            )
        }
        val isDarkBody = isDark(bodyColor)

        // D.body, D.head, D.leg1, D.leg2, D.leg3, D.leg4, D.tail,
        // D.leftEar, D.rightEar, D.foot1, D.foot2, D.foot3, D.foot4, D.tailCap
        arr[INDEX_OF_BODY] = bodyColor
        arr[INDEX_OF_HEAD] = bodyColor
        arr[INDEX_OF_LEG1] = bodyColor
        arr[INDEX_OF_LEG2] = bodyColor
        arr[INDEX_OF_LEG3] = bodyColor
        arr[INDEX_OF_LEG4] = bodyColor
        arr[INDEX_OF_TAIL] = bodyColor
        arr[INDEX_OF_LEFT_EAR] = bodyColor
        arr[INDEX_OF_RIGHT_EAR] = bodyColor
        arr[INDEX_OF_FOOT1] = bodyColor
        arr[INDEX_OF_FOOT2] = bodyColor
        arr[INDEX_OF_FOOT3] = bodyColor
        arr[INDEX_OF_FOOT4] = bodyColor
        arr[INDEX_OF_TAIL_CAP] = bodyColor
        arr[INDEX_OF_BELLY] = bodyColor

        // D.leg2Shadow, D.tailShadow
        val shadowColor = Color(0x20000000)
        arr[INDEX_OF_LEG2_SHADOW] = shadowColor
        arr[INDEX_OF_TAIL_SHADOW] = shadowColor

        // D.leftEye, D.rightEye, D.mouth, D.nose
        if (isDarkBody) {
            arr[INDEX_OF_LEFT_EYE] = Color.White
            arr[INDEX_OF_RIGHT_EYE] = Color.White
            arr[INDEX_OF_MOUTH] = Color.White
            arr[INDEX_OF_NOSE] = Color.White
        }

        // D.leftEarInside, D.rightEarInside
        if (isDarkBody) {
            arr[INDEX_OF_LEFT_EAR_INSIDE] = Color(-0x106566)
            arr[INDEX_OF_RIGHT_EAR_INSIDE] = Color(-0x106566)
        } else {
            arr[INDEX_OF_LEFT_EAR_INSIDE] = Color(0x20D50000)
            arr[INDEX_OF_RIGHT_EAR_INSIDE] = Color(0x20D50000)
        }

        // D.belly
        val bellyColor = Color(chooseP(nsr, P_BELLY_COLORS))
        if (bellyColor != Color.Transparent) {
            arr[INDEX_OF_BELLY] = bellyColor
        }

        val faceColor = Color(chooseP(nsr, P_BELLY_COLORS))
        // D.faceSpot
        arr[INDEX_OF_FACE_SPOT] = faceColor
        if (!isDark(faceColor)) {
            // D.mouth, D.nose
            arr[INDEX_OF_MOUTH] = Color.Black
            arr[INDEX_OF_NOSE] = Color.Black
        }

        if (nsr.nextFloat() < 0.25f) {
            // D.foot1, D.foot2, D.foot3, D.foot4
            arr[INDEX_OF_FOOT1] = Color.White
            arr[INDEX_OF_FOOT2] = Color.White
            arr[INDEX_OF_FOOT3] = Color.White
            arr[INDEX_OF_FOOT4] = Color.White
        } else if (nsr.nextFloat() < 0.25f) {
            // D.foot1, D.foot3
            arr[INDEX_OF_FOOT1] = Color.White
            arr[INDEX_OF_FOOT3] = Color.White
        } else if (nsr.nextFloat() < 0.25f) {
            // D.foot2, D.foot4
            arr[INDEX_OF_FOOT2] = Color.White
            arr[INDEX_OF_FOOT4] = Color.White
        } else if (nsr.nextFloat() < 0.1f) {
            // D.foot1, D.foot2, D.foot3, D.foot4
            val footIndex =
                choose(nsr, INDEX_OF_FOOT1, INDEX_OF_FOOT2, INDEX_OF_FOOT3, INDEX_OF_FOOT4) as Int
            arr[footIndex] = Color.White
        }

        // D.tailCap
        arr[INDEX_OF_TAIL_CAP] = if (nsr.nextFloat() < 0.333f) Color.White else bodyColor

        // D.cap
        arr[INDEX_OF_CAP] =
            Color(chooseP(nsr, if (isDarkBody) P_LIGHT_SPOT_COLORS else P_DARK_SPOT_COLORS))

        // D.collar
        val collarColor = Color(chooseP(nsr, P_COLLAR_COLORS))
        arr[INDEX_OF_COLLAR] = collarColor

        // D.bowtie
        arr[INDEX_OF_BOWTIE] = if (nsr.nextFloat() < 0.1f) collarColor else Color.Transparent

        // 0:collar,
        // 1:leftEar, 2:leftEarInside, 3:rightEar, 4:rightEarInside,
        // 5:head,
        // 6:faceSpot,
        // 7:cap,
        // 8:leftEye, 9:rightEye,
        // 10:mouth, 11:nose,
        // 12:tail, 13:tailCap, 14:tailShadow,
        // 15:foot1, 16:leg1,
        // 17:foot2, 18:leg2,
        // 19:foot3, 20:leg3,
        // 21:foot4, 22:leg4,
        // 23:leg2Shadow,
        // 24:body, 25:belly,
        // 26:bowtie
        return arr
    }
}
