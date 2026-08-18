package com.android_next.egg

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScopeMarker
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import kotlin.math.max


@DrawScopeMarker
private class ArtistDrawScope(private val delegate: DrawScope) : DrawScope by delegate {

    val width: Float = size.width
    val height: Float = size.height

    val fullStroke: Float = 30.dp.toPx()
    val innerStroke: Float = 14.dp.toPx()
    val lineStroke: Float = 1.5.dp.toPx()

    val labelSpace: Float = 6.dp.toPx()

    val linePointRadius: Float = 2.dp.toPx()
    val pointRadius = 11.dp.toPx()

    val rect: Rect = Rect(
        top = height * 0.2f,
        left = fullStroke / 2f,
        right = width - fullStroke / 2f,
        bottom = height - fullStroke / 2f
    )
    val endOffsetX: Float = rect.left + rect.width * 0.7f
    val arcRadius: Float = rect.height / 2f / 2f

    // beta 与 release 两个弧度之间距离的角度差
    val arcGapDegrees: Float = (innerStroke * 0.8f / arcRadius).toDegrees() / 2f

    val pathLabelSize = 12.sp
    val bubbleLabelSize = 14.sp

    val bubbleAnchorSpace = 28.dp
}

private fun ArtistDrawScope.drawFullPath() {
    val fullPath = Path().apply {
        var y = rect.top
        moveTo(rect.left, y)
        y += arcRadius
        arcTo(
            Rect(
                center = Offset(rect.right - arcRadius, y),
                radius = arcRadius
            ),
            -90f,
            180f,
            false
        )

        y += arcRadius * 2
        arcTo(
            Rect(
                center = Offset(rect.left + arcRadius, y),
                radius = arcRadius
            ),
            -90f,
            -180f,
            false
        )
        y += arcRadius
        lineTo(endOffsetX, y)
    }
    drawPath(
        path = fullPath,
        color = Color(0x22_2E9B49),
        style = Stroke(width = fullStroke, cap = StrokeCap.Round)
    )
}

private fun ArtistDrawScope.drawPlatformStabilityPath(
    textMeasurer: TextMeasurer,
    labelPlatformStability: String,
    platformStabilityMonth: Int
) {
    val platformStabilityPath = Path().apply {
        var y = rect.top + arcRadius * 2
        moveTo(rect.left + rect.width * 0.6f, y)
        y += arcRadius
        arcTo(
            Rect(
                center = Offset(rect.left + arcRadius, y),
                radius = arcRadius
            ),
            -90f,
            -180f,
            false
        )
        y += arcRadius
        lineTo(endOffsetX, y)
    }
    drawPath(
        path = platformStabilityPath,
        color = Color(0x20_2E9B49),
        style = Stroke(width = fullStroke, cap = StrokeCap.Round)
    )

    drawBubbleLabel(
        textMeasurer = textMeasurer,
        label = getLocaleFormatMonth(platformStabilityMonth),
        textColor = Color(0xFF_575B5E),
        bubbleColor = Color(0xFF_D3D3D3),
        anchorOffset = rect.center
    )

    drawCircle(
        color = Color(0xFF_AFCFFF),
        radius = pointRadius,
        center = rect.center
    )

    val layoutResultPlatformStability = textMeasurer.measure(
        text = labelPlatformStability,
        style = TextStyle(fontSize = pathLabelSize),
        constraints = Constraints(
            maxWidth = (rect.width - fullStroke - arcRadius - labelSpace * 2).toInt(),
            maxHeight = arcRadius.toInt()
        )
    )
    val layoutResultSize = layoutResultPlatformStability.getFixedSize()
    val labelOffsetX = rect.center.x - layoutResultSize.width / 2f
    drawText(
        textLayoutResult = layoutResultPlatformStability,
        color = Color(0xFF_2E9B49),
        topLeft = Offset(labelOffsetX, rect.center.y + labelSpace + pointRadius)
    )

    val firstLineHeight = layoutResultPlatformStability.getLineBottom(0)
    val platformStabilityLineArcRadius = arcRadius - firstLineHeight / 2 - fullStroke / 2
    val platformStabilityLinePath = Path().apply {
        var y = rect.top + arcRadius * 2 + fullStroke / 2f + firstLineHeight / 2f
        moveTo(max(labelOffsetX - labelSpace, rect.left + arcRadius), y)
        y += platformStabilityLineArcRadius
        arcTo(
            Rect(
                center = Offset(rect.left + arcRadius, y),
                radius = platformStabilityLineArcRadius
            ),
            -90f,
            -180f,
            false
        )
        y += platformStabilityLineArcRadius
        lineTo(endOffsetX - labelSpace, y)

        drawCircle(
            color = Color(0xFF_2E9B49),
            radius = linePointRadius,
            center = Offset(endOffsetX - labelSpace, y)
        )
    }
    drawPath(
        path = platformStabilityLinePath,
        color = Color(0xFF_2E9B49),
        style = Stroke(width = lineStroke, cap = StrokeCap.Round)
    )
}

private fun ArtistDrawScope.drawBetaPath(
    textMeasurer: TextMeasurer,
    labelBetaRelease: String,
    betaReleaseMonth: Int
) {
    val betaPath = Path().apply {
        var y = rect.top
        moveTo(rect.left, y)
        y += arcRadius
        arcTo(
            Rect(
                center = Offset(rect.right - arcRadius, y),
                radius = arcRadius
            ),
            -90f,
            180f,
            false
        )

        y += arcRadius * 2
        val arcRect = Rect(
            center = Offset(rect.left + arcRadius, y),
            radius = arcRadius
        )
        arcTo(
            arcRect,
            -90f,
            -(90f - arcGapDegrees),
            false
        )
    }
    drawPath(
        path = betaPath,
        color = Color(0xFF_3B78EF),
        style = Stroke(width = innerStroke, cap = StrokeCap.Round)
    )

    val layoutResultBeta = textMeasurer.measure(
        labelBetaRelease,
        TextStyle(fontSize = pathLabelSize),
    )
    val labelOffsetX = rect.left + pointRadius + labelSpace
    drawText(
        layoutResultBeta,
        color = Color(0xFF_F6FEFF),
        topLeft = Offset(
            labelOffsetX,
            rect.top - layoutResultBeta.size.height / 2f
        )
    )

    val betaInnerPath = Path().apply {
        var y = rect.top
        moveTo(labelOffsetX + layoutResultBeta.size.width + labelSpace, y)
        y += arcRadius
        arcTo(
            Rect(
                center = Offset(rect.right - arcRadius, y),
                radius = arcRadius
            ),
            -90f,
            180f,
            false
        )

        y += arcRadius * 2
        val arcRect = Rect(
            center = Offset(rect.left + arcRadius, y),
            radius = arcRadius
        )
        arcTo(
            arcRect,
            -90f,
            -(90f - arcGapDegrees),
            false
        )
        // 获取圆弧终点坐标
        val betaArcEndPoint = getArcEndPoint(arcRect, -90f, -(90f - arcGapDegrees))
        drawCircle(
            color = Color(0xEE_FFFFFF),
            radius = linePointRadius,
            center = betaArcEndPoint,
        )
    }
    drawPath(
        path = betaInnerPath,
        color = Color(0xFF_F6FEFF),
        style = Stroke(width = lineStroke, cap = StrokeCap.Round)
    )

    drawBubbleLabel(
        textMeasurer = textMeasurer,
        label = getLocaleFormatMonth(betaReleaseMonth),
        textColor = Color(0xFF_00397E),
        bubbleColor = Color(0xFF_72A9FE),
        anchorOffset = Offset(rect.left, rect.top)
    )

    drawCircle(
        color = Color(0xFF_AFCFFF),
        radius = pointRadius,
        center = Offset(rect.left, rect.top)
    )
}

private fun ArtistDrawScope.drawReleasePath(
    textMeasurer: TextMeasurer,
    labelFinalRelease: String
) {
    val releasePath = Path().apply {
        moveTo(endOffsetX, rect.bottom)
        arcTo(
            Rect(
                center = Offset(rect.left + arcRadius, rect.bottom - arcRadius),
                radius = arcRadius
            ),
            90f,
            80f - arcGapDegrees,
            false
        )
    }
    drawPath(
        path = releasePath,
        color = Color(0xFF_2E9B49),
        style = Stroke(width = innerStroke, cap = StrokeCap.Round)
    )

    drawBubbleLabel(
        textMeasurer = textMeasurer,
        label = labelFinalRelease,
        textColor = Color(0xFF_095A16),
        bubbleColor = Color(0xFF_76D386),
        labelConstraints = Constraints(
            maxWidth = (rect.right - endOffsetX).toInt(),
        ),
        anchorOffset = Offset(endOffsetX, rect.bottom)
    )

    drawCircle(
        color = Color(0xFF_76D28A),
        radius = pointRadius,
        center = Offset(endOffsetX, rect.bottom)
    )
}

private fun ArtistDrawScope.drawBubbleLabel(
    textMeasurer: TextMeasurer,
    label: String,
    textColor: Color,
    bubbleColor: Color,
    anchorOffset: Offset,
    labelConstraints: Constraints = Constraints(),
    anchorSpace: Dp = bubbleAnchorSpace
) {
    val paddingHorizontal = 12.dp.toPx()
    val paddingVertical = 4.dp.toPx()
    val lineLength = anchorSpace.toPx()

    val layoutResultMonth = textMeasurer.measure(
        text = label,
        style = TextStyle(fontSize = bubbleLabelSize),
        constraints = labelConstraints,
    )
    val layoutResultSize = layoutResultMonth.getFixedSize()
    val roundRectSize = Size(
        layoutResultSize.width + paddingHorizontal * 2f,
        layoutResultSize.height + paddingVertical * 2f
    )

    var offsetY = anchorOffset.y - lineLength - roundRectSize.height / 2f
    drawLine(
        color = bubbleColor,
        start = Offset(anchorOffset.x, anchorOffset.y),
        end = Offset(anchorOffset.x, offsetY),
        strokeWidth = lineStroke
    )

    offsetY -= roundRectSize.height / 2f
    val rectOffset = Offset(anchorOffset.x - paddingHorizontal, offsetY)
    drawRoundRect(
        color = bubbleColor,
        size = roundRectSize,
        topLeft = rectOffset,
        cornerRadius = CornerRadius(roundRectSize.height, roundRectSize.height)
    )
    drawText(
        layoutResultMonth,
        color = textColor,
        topLeft = Offset(rectOffset.x + paddingHorizontal, rectOffset.y + paddingVertical)
    )

}

@Preview
@Composable
internal fun AndroidScheduleArtist(
    modifier: Modifier = Modifier,
    betaReleaseMonth: Int = Calendar.FEBRUARY,
    platformStabilityMonth: Int = Calendar.MARCH,
) {
    val labelBetaRelease = stringResource(R.string.label_timeline_beta_release)
    val labelPlatformStability = stringResource(R.string.label_timeline_platform_stability)
    val labelFinalRelease = stringResource(R.string.label_timeline_final_release)
    val textMeasurer = rememberTextMeasurer(cacheSize = 5)

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Ltr,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .then(modifier)
        ) {
            val drawScope = ArtistDrawScope(this)
            drawScope.drawFullPath()
            drawScope.drawBetaPath(textMeasurer, labelBetaRelease, betaReleaseMonth)
            drawScope.drawPlatformStabilityPath(
                textMeasurer,
                labelPlatformStability,
                platformStabilityMonth
            )
            drawScope.drawReleasePath(textMeasurer, labelFinalRelease)
        }
    }
}