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
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.content.res.TypedArrayUtils
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.PathParser
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.setPadding
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import coil.decode.DecodeUtils
import coil.dispose
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

        var showSuffix = true
        private val suffixRegex = Regex("\\s*\\(.+\\)")
        private val versionRegex = Regex("Android\\s*\\d+(.\\d)*(.\\*)?")

        private const val ACTIVITY_TASK_FLAGS =
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS
    }

    private val supportAdaptiveIcon: Boolean
    private val iconPadding: Int
    private var iconResId: Int = 0

    private val finalTitle: CharSequence?
    private val finalSummary: CharSequence?
    private val versionComment: String?

    private lateinit var onCreateShortcutListener: OnCreateShortcutListener

    constructor(context: Context) : this(context, null)

    @SuppressLint("RestrictedApi", "PrivateResource")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        context.withStyledAttributes(attrs, androidx.preference.R.styleable.Preference) {
            iconResId = TypedArrayUtils.getResourceId(
                this, androidx.preference.R.styleable.Preference_icon,
                androidx.preference.R.styleable.Preference_android_icon, 0
            )
        }

        val arrays = context.obtainStyledAttributes(attrs, R.styleable.EggPreference)
        supportAdaptiveIcon =
            arrays.getBoolean(R.styleable.EggPreference_supportAdaptiveIcon, false)
        val className = arrays.getString(R.styleable.EggPreference_android_targetClass)
        if (className != null) {
            val intent = Intent(Intent.ACTION_VIEW)
                .setClassName(context, className)
                .addFlags(ACTIVITY_TASK_FLAGS)
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

        configureTitle(holder, finalTitle)

        configureSummary(holder, finalSummary)

        configureIcon(holder)

        configureLongPress(holder)
    }

    private fun configureTitle(holder: PreferenceViewHolder, finalTitle: CharSequence?) {
        val title = holder.findViewById(android.R.id.title) as? TextView ?: return
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

    private fun configureSummary(holder: PreferenceViewHolder, finalSummary: CharSequence?) {
        if (!showSuffix && finalSummary != null) {
            val summary = holder.findViewById(android.R.id.summary) as? TextView
            summary?.text = finalSummary.replace(suffixRegex, "")
        }
    }

    private fun configureIcon(holder: PreferenceViewHolder) {
        val icon = holder.findViewById(android.R.id.icon) as? ImageView ?: return
        icon.dispose()
        if (supportAdaptiveIcon && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // support adaptive-icon
            icon.load(iconResId) {
                val shapePath = IconShapeOverride.getAppliedValue(context)
                if (!IconShapeOverride.isSquareShape(context, shapePath)) {
                    if (!TextUtils.isEmpty(shapePath)) {
                        transformations(SupportAdaptiveIconTransformation(shapePath))
                    } else {
                        transformations(CircleCropTransformation())
                    }
                    Region()
                }
            }
        }
        icon.setPadding(iconPadding)
    }

    private fun configureLongPress(holder: PreferenceViewHolder) {
        var enableLongPress = intent != null && key != null
        if (enableLongPress && !::onCreateShortcutListener.isInitialized) {
            onCreateShortcutListener = OnCreateShortcutListener(this)
        }
        enableLongPress = enableLongPress && onCreateShortcutListener.isSupport()
        holder.itemView.setOnCreateContextMenuListener(if (enableLongPress) onCreateShortcutListener else null)
        holder.itemView.isLongClickable = enableLongPress
    }

    private class OnCreateShortcutListener(private val preference: EggPreference) :
        OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        fun isSupport(): Boolean {
            return ShortcutManagerCompat.isRequestPinShortcutSupported(preference.context)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?,
        ) {
            menu.setHeaderTitle(preference.finalTitle)
            menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.label_add_shortcut)
                .setOnMenuItemClickListener(this)
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            val intent = preference.intent
            val key = preference.key
            val label = preference.finalSummary
            if (intent == null || key == null || TextUtils.isEmpty(label) || !isSupport()) return false

            val context = preference.context
            val shortcut = ShortcutInfoCompat.Builder(context, key)
                .setIcon(IconCompat.createWithResource(context, preference.iconResId))
                .setIntent(intent)
                .setShortLabel(label!!)
                .setLongLabel(label)
                .build()
            ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
            return true
        }
    }

    private class SupportAdaptiveIconTransformation(val maskPathStr: String) : Transformation {

        companion object {
            private const val MASK_SIZE = 100f
        }

        override val cacheKey: String = "${javaClass.name}-$maskPathStr"

        override suspend fun transform(input: Bitmap, size: Size): Bitmap {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

            val (outputWidth, outputHeight) = calculateOutputSize(input, size)

            val safeConfig = input.config ?: Bitmap.Config.ARGB_8888
            val output = createBitmap(outputWidth, outputHeight, safeConfig)
            return output.applyCanvas {
                drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                paint.shader = getBitmapShader(input, outputWidth, outputHeight)
                val path = getMaskPath(maskPathStr, outputWidth, outputHeight)
                drawPath(path, paint)

                setBitmap(null)
            }
        }

        private fun getMaskPath(pathStr: String, outputWidth: Int, outputHeight: Int): Path {
            val matrix = Matrix()
            val path = PathParser.createPathFromPathData(pathStr)
            matrix.setScale(outputWidth / MASK_SIZE, outputHeight / MASK_SIZE)
            path.transform(matrix)
            return path
        }

        private fun getBitmapShader(
            input: Bitmap,
            outputWidth: Int,
            outputHeight: Int,
        ): BitmapShader {
            val matrix = Matrix()
            val multiplier = DecodeUtils.computeSizeMultiplier(
                srcWidth = input.width,
                srcHeight = input.height,
                dstWidth = outputWidth,
                dstHeight = outputHeight,
                scale = Scale.FILL
            ).toFloat()
            val dx = (outputWidth - multiplier * input.width) / 2
            val dy = (outputHeight - multiplier * input.height) / 2
            matrix.setTranslate(dx, dy)
            matrix.preScale(multiplier, multiplier)

            val shader = BitmapShader(input, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            shader.setLocalMatrix(matrix)
            return shader
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