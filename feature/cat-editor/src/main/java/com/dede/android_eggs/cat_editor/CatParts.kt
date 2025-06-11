package com.dede.android_eggs.cat_editor

import android.graphics.Paint
import android.graphics.Region
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.toPath
import com.dede.android_eggs.cat_editor.Utilities.getRegion
import com.dede.basic.trimZeroAndDot
import android.graphics.Canvas as AndroidCanvas

internal object CatParts {

    const val VIEW_PORT_SIZE = 48f

    private const val MOUTH_STROKE_WIDTH = 1.2f
    private const val TAIL_STROKE_WIDTH = 5f

    private object SvgParts {
        val FORMAT_SVG = """
        <svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48' width='%d' height='%d'>
          %s
        </svg>
        """.trimIndent()
        const val FORMAT_PATH = "<path id='%s' d='%s' fill='%s' />"
        const val FORMAT_PATH_STROKE =
            "<path id='%s' d='%s' stroke-width='%s' stroke='%s' stroke-linecap='round' fill='none' />"

        const val HEAD = "M9,18.5c0,-8.3 6.8,-15 15,-15s15,6.7 15,15H9z"
        const val LEG1 = "M9,37h5v6h-5z"
        const val LEG2 = "M16,37h5v6h-5z"
        const val LEG3 = "M27,37h5v6h-5z"
        const val LEG4 = "M34,37h5v6h-5z"
        const val TAIL = "M35,35.5h5.9c2.1,0 3.8,-1.7 3.8,-3.8v-6.2"
        const val LEFT_EAR = "M15.4,1l5.1000004,5.3l-6.3,2.8000002z"
        const val RIGHT_EAR = "M32.6,1l-5.0999985,5.3l6.299999,2.8000002z"
        const val RIGHT_EAR_INSIDE = "M33.8,9.1l-4.7,-1.9l3.5,-6.2z"
        const val LEFT_EAR_INSIDE = "M15.4,1l3.5,6.2l-4.7,1.9z"
        const val FACE_SPOT = "M19.5,15.2a4.5,3.2 0,1 0,9 0a4.5,3.2 0,1 0,-9 0z"
        const val CAP =
            "M27.2,3.8c-1,-0.2 -2.1,-0.3 -3.2,-0.3s-2.1,0.1 -3.2,0.3c0.2,1.3 1.5,2.2 3.2,2.2C25.6,6.1 26.9,5.1 27.2,3.8z"
        const val MOUTH =
            "M29,14.3c-0.4,0.8 -1.3,1.4 -2.3,1.4c-1.4,0 -2.7,-1.3 -2.7,-2.7 M24,13c0,1.5 -1.2,2.7 -2.7,2.7c-1,0 -1.9,-0.5 -2.3,-1.4"
        const val FOOT4 = "M36.5,43m-2.5,0a2.5,2.5 0,1 1,5 0a2.5,2.5 0,1 1,-5 0"
        const val FOOT3 = "M29.5,43m-2.5,0a2.5,2.5 0,1 1,5 0a2.5,2.5 0,1 1,-5 0"
        const val FOOT1 = "M11.5,43m-2.5,0a2.5,2.5 0,1 1,5 0a2.5,2.5 0,1 1,-5 0"
        const val FOOT2 = "M18.5,43m-2.5,0a2.5,2.5 0,1 1,5 0a2.5,2.5 0,1 1,-5 0"
        const val LEG2_SHADOW = "M16,37h5v3h-5z"
        const val TAIL_SHADOW = "M40,38l0,-5l-1,0l0,5z"
        const val TAIL_CAP = "M42.2,25.5c0,-1.4 1.1,-2.5 2.5,-2.5s2.5,1.1 2.5,2.5H42.2z"
        const val BELLY = "M20.5,25c-3.6,0 -6.5,2.9 -6.5,6.5V38h13v-6.5C27,27.9 24.1,25 20.5,25z"
        const val BODY = "M9,20h30v18h-30z"
        const val RIGHT_EYE = "M30.5,11c0,1.7 -3,1.7 -3,0C27.5,9.3 30.5,9.3 30.5,11z"
        const val LEFT_EYE = "M20.5,11c0,1.7 -3,1.7 -3,0C17.5,9.3 20.5,9.3 20.5,11z"
        const val NOSE = "M25.2,13c0,1.3 -2.3,1.3 -2.3,0S25.2,11.7 25.2,13z"
        const val COLLAR = "M9,18.4h30v1.7h-30z"
        const val BOWTIE = "M29,16.8l-10,5l0,-5l10,5z"
    }

    private val head = vectorPath {
        // M9,18.5c0,-8.3 6.8,-15 15,-15s15,6.7 15,15H9z
        moveTo(9f, 18.5f)
        curveToRelative(0f, -8.3f, 6.8f, -15f, 15f, -15f)
        reflectiveCurveToRelative(15f, 6.7f, 15f, 15f)
        horizontalLineTo(9f)
        close()
    }

    private val leg1 = vectorPath {
        // M9,37h5v6h-5z
        moveTo(9f, 37f)
        horizontalLineToRelative(5f)
        verticalLineToRelative(6f)
        horizontalLineToRelative(-5f)
        close()
    }

    private val leg2 = vectorPath {
        // M16,37h5v6h-5z
        moveTo(16f, 37f)
        horizontalLineToRelative(5f)
        verticalLineToRelative(6f)
        horizontalLineToRelative(-5f)
        close()
    }

    private val leg3 = vectorPath {
        // M27,37h5v6h-5z
        moveTo(27f, 37f)
        horizontalLineToRelative(5f)
        verticalLineToRelative(6f)
        horizontalLineToRelative(-5f)
        close()
    }

    private val leg4 = vectorPath {
        // M34,37h5v6h-5z
        moveTo(34f, 37f)
        horizontalLineToRelative(5f)
        verticalLineToRelative(6f)
        horizontalLineToRelative(-5f)
        close()
    }

    private val tail = vectorPath {
        // android:strokeWidth="5"
        // android:strokeLineCap="round"
        // M35,35.5h5.9c2.1,0 3.8,-1.7 3.8,-3.8v-6.2
        moveTo(35f, 35.5f)
        horizontalLineToRelative(5.9f)
        curveToRelative(2.1f, 0f, 3.8f, -1.7f, 3.8f, -3.8f)
        verticalLineToRelative(-6.2f)
    }

    private val leftEar = vectorPath {
        // M15.4,1l5.1000004,5.3l-6.3,2.8000002z
        moveTo(15.4f, 1f)
        lineToRelative(5.1000004f, 5.3f)
        lineToRelative(-6.3f, 2.8000002f)
        close()
    }

    private val rightEar = vectorPath {
        // M32.6,1l-5.0999985,5.3l6.299999,2.8000002z
        moveTo(32.6f, 1f)
        lineToRelative(-5.0999985f, 5.3f)
        lineToRelative(6.299999f, 2.8000002f)
        close()
    }

    private val rightEarInside = vectorPath {
        // M33.8,9.1l-4.7,-1.9l3.5,-6.2z
        moveTo(33.8f, 9.1f)
        lineToRelative(-4.7f, -1.9f)
        lineToRelative(3.5f, -6.2f)
        close()
    }

    private val leftEarInside = vectorPath {
        // M15.4,1l3.5,6.2l-4.7,1.9z
        moveTo(15.4f, 1f)
        lineToRelative(3.5f, 6.2f)
        lineToRelative(-4.7f, 1.9f)
        close()
    }

    private val faceSpot = vectorPath {
        // M19.5,15.2a4.5,3.2 0,1 0,9 0a4.5,3.2 0,1 0,-9 0z
        moveTo(19.5f, 15.2f)
        arcToRelative(
            4.5f, 3.2f, 0f,
            isMoreThanHalf = true,
            isPositiveArc = false,
            dx1 = 9f,
            dy1 = 0f
        )
        arcToRelative(
            4.5f, 3.2f, 0f,
            isMoreThanHalf = true,
            isPositiveArc = false,
            dx1 = -9f,
            dy1 = 0f
        )
        close()
    }

    private val cap = vectorPath {
        // M27.2,3.8c-1,-0.2 -2.1,-0.3 -3.2,-0.3s-2.1,0.1 -3.2,0.3c0.2,1.3 1.5,2.2 3.2,2.2C25.6,6.1 26.9,5.1 27.2,3.8z
        moveTo(27.2f, 3.8f)
        curveToRelative(-1f, -0.2f, -2.1f, -0.3f, -3.2f, -0.3f)
        reflectiveCurveToRelative(-2.1f, 0.1f, -3.2f, 0.3f)
        curveToRelative(0.2f, 1.3f, 1.5f, 2.2f, 3.2f, 2.2f)
        curveTo(25.6f, 6.1f, 26.9f, 5.1f, 27.2f, 3.8f)
        close()
    }

    private val mouth = vectorPath {
        // android:strokeWidth="1.2"
        // android:strokeLineCap="round"
        // M29,14.3c-0.4,0.8 -1.3,1.4 -2.3,1.4c-1.4,0 -2.7,-1.3 -2.7,-2.7
        // M24,13c0,1.5 -1.2,2.7 -2.7,2.7c-1,0 -1.9,-0.5 -2.3,-1.4
        moveTo(29f, 14.3f)
        curveToRelative(-0.4f, 0.8f, -1.3f, 1.4f, -2.3f, 1.4f)
        curveToRelative(-1.4f, 0f, -2.7f, -1.3f, -2.7f, -2.7f)

        moveTo(24f, 13f)
        curveToRelative(0f, 1.5f, -1.2f, 2.7f, -2.7f, 2.7f)
        curveToRelative(-1f, 0f, -1.9f, -0.5f, -2.3f, -1.4f)
    }

    private val foot4 = vectorPath {
        // M36.5,43m-2.5,0a2.5,2.5 0,1 1,5 0a2.5,2.5 0,1 1,-5 0
        moveTo(36.5f, 43f)
        moveToRelative(-2.5f, 0f)
        arcToRelative(
            2.5f, 2.5f, 0f,
            isMoreThanHalf = true,
            isPositiveArc = true,
            dx1 = 5f,
            dy1 = 0f
        )
        arcToRelative(
            2.5f, 2.5f, 0f,
            isMoreThanHalf = true,
            isPositiveArc = true,
            dx1 = -5f,
            dy1 = 0f
        )
    }

    private val foot3 = vectorPath {
        // M29.5,43m-2.5,0a2.5,2.5 0,1 1,5 0a2.5,2.5 0,1 1,-5 0
        moveTo(29.5f, 43f)
        moveToRelative(-2.5f, 0f)
        arcToRelative(
            2.5f, 2.5f, 0f,
            isMoreThanHalf = true,
            isPositiveArc = true,
            dx1 = 5f,
            dy1 = 0f
        )
        arcToRelative(
            2.5f, 2.5f, 0f,
            isMoreThanHalf = true,
            isPositiveArc = true,
            dx1 = -5f,
            dy1 = 0f
        )
    }

    private val foot1 = vectorPath {
        // M11.5,43m-2.5,0a2.5,2.5 0,1 1,5 0a2.5,2.5 0,1 1,-5 0
        moveTo(11.5f, 43f)
        moveToRelative(-2.5f, 0f)
        arcToRelative(
            2.5f, 2.5f, 0f,
            isMoreThanHalf = true,
            isPositiveArc = true,
            dx1 = 5f,
            dy1 = 0f
        )
        arcToRelative(
            2.5f, 2.5f, 0f,
            isMoreThanHalf = true,
            isPositiveArc = true,
            dx1 = -5f,
            dy1 = 0f
        )
        close()
    }

    private val foot2 = vectorPath {
        // M18.5,43m-2.5,0a2.5,2.5 0,1 1,5 0a2.5,2.5 0,1 1,-5 0
        moveTo(18.5f, 43f)
        moveToRelative(-2.5f, 0f)
        arcToRelative(
            2.5f, 2.5f, 0f,
            isMoreThanHalf = true,
            isPositiveArc = true,
            dx1 = 5f,
            dy1 = 0f
        )
        arcToRelative(
            2.5f, 2.5f, 0f,
            isMoreThanHalf = true,
            isPositiveArc = true,
            dx1 = -5f,
            dy1 = 0f
        )
    }

    private val leg2Shadow = vectorPath {
        // M16,37h5v3h-5z
        moveTo(16f, 37f)
        horizontalLineToRelative(5f)
        verticalLineToRelative(3f)
        horizontalLineToRelative(-5f)
        close()
    }

    private val tailShadow = vectorPath {
        // M40,38l0,-5l-1,0l0,5z
        moveTo(40f, 38f)
        lineToRelative(0f, -5f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, 5f)
        close()
    }

    private val tailCap = vectorPath {
        // M42.2,25.5c0,-1.4 1.1,-2.5 2.5,-2.5s2.5,1.1 2.5,2.5H42.2z
        moveTo(42.2f, 25.5f)
        curveToRelative(0f, -1.4f, 1.1f, -2.5f, 2.5f, -2.5f)
        reflectiveCurveToRelative(2.5f, 1.1f, 2.5f, 2.5f)
        horizontalLineTo(42.2f)
        close()
    }

    private val belly = vectorPath {
        // M20.5,25c-3.6,0 -6.5,2.9 -6.5,6.5V38h13v-6.5C27,27.9 24.1,25 20.5,25z
        moveTo(20.5f, 25f)
        curveToRelative(-3.6f, 0f, -6.5f, 2.9f, -6.5f, 6.5f)
        verticalLineTo(38f)
        horizontalLineToRelative(13f)
        verticalLineToRelative(-6.5f)
        curveTo(27f, 27.9f, 24.1f, 25f, 20.5f, 25f)
        close()
    }

    private val body = vectorPath {
        // M9,20h30v18h-30z
        moveTo(9f, 20f)
        horizontalLineToRelative(30f)
        verticalLineToRelative(18f)
        horizontalLineToRelative(-30f)
        close()
    }

    private val rightEye = vectorPath {
        // M30.5,11c0,1.7 -3,1.7 -3,0C27.5,9.3 30.5,9.3 30.5,11z
        moveTo(30.5f, 11f)
        curveToRelative(0f, 1.7f, -3f, 1.7f, -3f, 0f)
        curveTo(27.5f, 9.3f, 30.5f, 9.3f, 30.5f, 11f)
        close()
    }

    private val leftEye = vectorPath {
        // M20.5,11c0,1.7 -3,1.7 -3,0C17.5,9.3 20.5,9.3 20.5,11z
        moveTo(20.5f, 11f)
        curveToRelative(0f, 1.7f, -3f, 1.7f, -3f, 0f)
        curveTo(17.5f, 9.3f, 20.5f, 9.3f, 20.5f, 11f)
        close()
    }

    private val nose = vectorPath {
        // M25.2,13c0,1.3 -2.3,1.3 -2.3,0S25.2,11.7 25.2,13z
        moveTo(25.2f, 13f)
        curveToRelative(0f, 1.3f, -2.3f, 1.3f, -2.3f, 0f)
        reflectiveCurveTo(25.2f, 11.7f, 25.2f, 13f)
        close()
    }

    private val collar = vectorPath {
        // M9,18.4h30v1.7h-30z
        moveTo(9f, 18.4f)
        horizontalLineToRelative(30f)
        verticalLineToRelative(1.7f)
        horizontalLineToRelative(-30f)
        close()
    }

    private val bowtie = vectorPath {
        // M29,16.8l-10,5l0,-5l10,5z
        moveTo(29f, 16.8f)
        lineToRelative(-10f, 5f)
        lineToRelative(0f, -5f)
        lineToRelative(10f, 5f)
        close()
    }

    private inline fun vectorPath(pathBuilder: PathBuilder.() -> Unit) =
        with(PathBuilder()) {
            pathBuilder()
            nodes.toPath()
        }

    val drawOrders = arrayOf(
        ClosedPD(collar, SvgParts.COLLAR, "collar"),
        ClosedPD(leftEar, SvgParts.LEFT_EAR, "left_ear"),
        ClosedPD(leftEarInside, SvgParts.LEFT_EAR_INSIDE, "left_ear_inside"),
        ClosedPD(rightEar, SvgParts.RIGHT_EAR, "right_ear"),
        ClosedPD(rightEarInside, SvgParts.RIGHT_EAR_INSIDE, "right_ear_inside"),
        ClosedPD(head, SvgParts.HEAD, "head"),
        ClosedPD(faceSpot, SvgParts.FACE_SPOT, "face_spot"),
        ClosedPD(cap, SvgParts.CAP, "cap"),
        ClosedPD(leftEye, SvgParts.LEFT_EYE, "left_eye"),
        ClosedPD(rightEye, SvgParts.RIGHT_EYE, "right_eye"),
        RoundStrokePD(mouth, SvgParts.MOUTH, MOUTH_STROKE_WIDTH, "mouth"),
        ClosedPD(nose, SvgParts.NOSE, "nose"),
        RoundStrokePD(tail, SvgParts.TAIL, TAIL_STROKE_WIDTH, "tail"),
        ClosedPD(tailCap, SvgParts.TAIL_CAP, "tail_cap"),
        ClosedPD(tailShadow, SvgParts.TAIL_SHADOW, "tail_shadow", false),
        ClosedPD(foot1, SvgParts.FOOT1, "foot1"),
        ClosedPD(leg1, SvgParts.LEG1, "leg1"),
        ClosedPD(foot2, SvgParts.FOOT2, "foot2"),
        ClosedPD(leg2, SvgParts.LEG2, "leg2"),
        ClosedPD(foot3, SvgParts.FOOT3, "foot3"),
        ClosedPD(leg3, SvgParts.LEG3, "leg3"),
        ClosedPD(foot4, SvgParts.FOOT4, "foot4"),
        ClosedPD(leg4, SvgParts.LEG4, "leg4"),
        ClosedPD(leg2Shadow, SvgParts.LEG2_SHADOW, "leg2_shadow", false),
        ClosedPD(body, SvgParts.BODY, "body"), ClosedPD(belly, SvgParts.BELLY, "belly"),
        ClosedPD(bowtie, SvgParts.BOWTIE, "bowtie"),
    )

    fun toSvg(colors: List<Color>, size: Int = 512): String {
        val nodes = drawOrders.mapIndexed { i, draw ->
            draw.toSvgNode(colors[i])
        }.joinToString(separator = "\n  ")
        return SvgParts.FORMAT_SVG.format(size, size, nodes)
    }

    private class RoundStrokePD(
        path: Path,
        pathData: String,
        val strokeWidth: Float,
        name: String,
    ) : PathDraw(path, pathData, name) {
        override fun DrawScope.draw(color: Color) {
            drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        }

        override fun AndroidCanvas.androidDraw(paint: Paint) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            paint.strokeCap = Paint.Cap.ROUND
            drawPath(path.asAndroidPath(), paint)
        }

        override fun toSvgNode(color: Color): String {
            return SvgParts.FORMAT_PATH_STROKE.format(
                name, pathData, strokeWidth.toString().trimZeroAndDot(),
                Utilities.getHexColor(color, color.alpha < 1f)
            )
        }
    }

    private class ClosedPD(
        path: Path,
        pathData: String,
        name: String,
        touchable: Boolean = true,
    ) : PathDraw(path, pathData, name, touchable) {
        override fun DrawScope.draw(color: Color) {
            drawPath(path, color, style = Fill)
        }

        override fun AndroidCanvas.androidDraw(paint: Paint) {
            paint.style = Paint.Style.FILL
            drawPath(path.asAndroidPath(), paint)
        }

        override fun toSvgNode(color: Color): String {
            return SvgParts.FORMAT_PATH.format(
                name, pathData,
                Utilities.getHexColor(color, color.alpha < 1f)
            )
        }
    }

    abstract class PathDraw(
        val path: Path,
        val pathData: String,
        val name: String,
        val touchable: Boolean = true
    ) {

        val drawLambda: DrawScope.(color: Color) -> Unit = { draw(it) }

        val androidDrawLambda: AndroidCanvas.(color: Color, paint: Paint) -> Unit = { c, p ->
            p.setColor(c.toArgb())
            androidDraw(p)
        }

        val regin: Region = path.getRegion(this.javaClass == ClosedPD::class.java)

        protected abstract fun DrawScope.draw(color: Color)

        protected abstract fun AndroidCanvas.androidDraw(paint: Paint)

        abstract fun toSvgNode(color: Color): String
    }

}
