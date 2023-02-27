package com.dede.android_eggs.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.PathParser
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.setPadding
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import coil.decode.DecodeUtils
import coil.load
import coil.size.*
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import com.dede.android_eggs.R
import com.dede.android_eggs.util.IconShapeOverride
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.roundToInt
import com.google.android.material.R as M3R

/**
 * Easter Egg Preference
 *
 * @author hsh
 * @since 2020/10/29 10:29 AM
 */
open class EggPreference : Preference {

    companion object {
        private const val MODE_DEFAULT = 0
        private const val MODE_CORNERS = 1
        private const val MODE_OVAL = 2

        var showSuffix = true
        private val suffixRegex = Regex("\\s*\\(.+\\)")
        private val versionRegex = Regex("Android\\s*\\d+(.\\d)*(.\\*)?")

        private const val ACTIVITY_TASK_FLAGS =
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS
    }

    private val supportAdaptiveIconMode: Int
    private var iconRadius: Float = 0f
    private val iconPadding: Int

    private val finalTitle: CharSequence?
    private val finalSummary: CharSequence?
    private val versionComment: String?

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val arrays = context.obtainStyledAttributes(attrs, R.styleable.EggPreference)
        supportAdaptiveIconMode =
            arrays.getInt(R.styleable.EggPreference_supportAdaptiveIcon, MODE_DEFAULT)
        if (supportAdaptiveIconMode == MODE_CORNERS) {
            iconRadius = arrays.getDimension(R.styleable.EggPreference_iconRadius, 0f)
        }
        val className = arrays.getString(R.styleable.EggPreference_android_targetClass)
        if (className != null) {
            val intent = Intent()
                .setClassName(context, className)
            setIntent(intent)
        }
        iconPadding =
            arrays.getDimensionPixelSize(R.styleable.EggPreference_iconPadding, 0)
        versionComment = arrays.getString(R.styleable.EggPreference_versionComment)
        arrays.recycle()

        finalTitle = title
        finalSummary = summary
        isPersistent = false
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val title = holder.findViewById(android.R.id.title) as? TextView
        if (title != null) {
            if (!showSuffix && finalTitle != null) {
                title.text = finalTitle.replace(suffixRegex, "")
            }

            if (versionComment != null) {
                val text = title.text.toString()
                val result = versionRegex.find(text)
                if (result != null) {
                    val range = result.range
                    title.movementMethod = LinkMovementMethod.getInstance()
                    title.highlightColor = Color.TRANSPARENT
                    val span = text.toSpannable()
                    if (range.first > 0) {
                        span[0, range.first] = DefaultClickSpan(this)
                    }
                    if (range.last + 1 < text.length) {
                        span[range.last + 1, text.length] = DefaultClickSpan(this)
                    }
                    span[range.first, range.last + 1] =
                        CommentClickSpan(icon, finalTitle, versionComment)
                    title.text = span
                }
            }
        }

        if (!showSuffix && finalSummary != null) {
            val summary = holder.findViewById(android.R.id.summary) as? TextView
            summary?.text = finalSummary.replace(suffixRegex, "")
        }

        val icon = holder.findViewById(android.R.id.icon) as? ImageView ?: return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && supportAdaptiveIconMode != MODE_DEFAULT) {
            // support adaptive-icon
            icon.load(icon.drawable) {
                val shapePath = IconShapeOverride.getAppliedValue(context)
                if (!TextUtils.isEmpty(shapePath)) {
                    transformations(SupportAdaptiveIconTransformation(shapePath))
                } else {
                    transformations(CircleCropTransformation())
                }
            }
        }
        icon.setPadding(iconPadding)
    }

    private class SupportAdaptiveIconTransformation(val maskPathStr: String) : Transformation {

        override val cacheKey: String = "${javaClass.name}-$maskPathStr"

        override suspend fun transform(input: Bitmap, size: Size): Bitmap {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

            val (outputWidth, outputHeight) = calculateOutputSize(input, size)

            val safeConfig = input.config ?: Bitmap.Config.ARGB_8888
            val output = createBitmap(outputWidth, outputHeight, safeConfig)
            output.applyCanvas {
                val matrix = Matrix()
                val path = PathParser.createPathFromPathData(maskPathStr)
                val pathRectF = RectF()
                path.computeBounds(pathRectF, true)

                val multiplier = DecodeUtils.computeSizeMultiplier(
                    srcWidth = pathRectF.width().toInt(),
                    srcHeight = pathRectF.height().toInt(),
                    dstWidth = outputWidth,
                    dstHeight = outputHeight,
                    scale = Scale.FILL
                ).toFloat()
                val dx = (outputWidth - multiplier * pathRectF.width().toInt()) / 2
                val dy = (outputHeight - multiplier * pathRectF.height().toInt()) / 2
                matrix.setTranslate(dx, dy)
                matrix.preScale(multiplier, multiplier)
                path.transform(matrix)
                drawPath(path, paint)

                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                drawBitmap(input, 0f, 0f, paint)
            }
            return output
        }

        private fun calculateOutputSize(input: Bitmap, size: Size): Pair<Int, Int> {
            if (size.isOriginal) {
                return input.width to input.height
            }

            val (dstWidth, dstHeight) = size
            if (dstWidth is Dimension.Pixels && dstHeight is Dimension.Pixels) {
                return dstWidth.px to dstHeight.px
            }

            val multiplier = DecodeUtils.computeSizeMultiplier(
                srcWidth = input.width,
                srcHeight = input.height,
                dstWidth = size.width.pxOrElse { Int.MIN_VALUE },
                dstHeight = size.height.pxOrElse { Int.MIN_VALUE },
                scale = Scale.FILL
            )
            val outputWidth = (multiplier * input.width).roundToInt()
            val outputHeight = (multiplier * input.height).roundToInt()
            return outputWidth to outputHeight
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is SupportAdaptiveIconTransformation && this.maskPathStr == other.maskPathStr
        }

        override fun hashCode(): Int {
            return this.maskPathStr.hashCode()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun performClick() {
        val intent = this.intent
        if (intent != null) {
            intent.addFlags(ACTIVITY_TASK_FLAGS)
            super.performClick()
            return
        }

        MaterialAlertDialogBuilder(
            context,
            M3R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        ).setTitle(finalTitle)
            .setMessage(finalSummary)
            .setIcon(icon)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private class DefaultClickSpan(val preference: Preference) : ClickableSpan() {
        @SuppressLint("RestrictedApi")
        override fun onClick(widget: View) {
            preference.performClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }

    private class CommentClickSpan(
        val icon: Drawable?,
        val title: CharSequence?,
        val message: CharSequence?,
    ) : ClickableSpan() {
        override fun onClick(widget: View) {
            MaterialAlertDialogBuilder(widget.context)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }

        override fun updateDrawState(ds: TextPaint) {
            //super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

}