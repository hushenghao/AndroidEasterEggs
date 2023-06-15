package com.dede.android_eggs.ui.preferences

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.preference.ListPreference
import com.dede.android_eggs.settings.IconShapePref
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.updateCompoundDrawablesRelative
import com.dede.basic.dp
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.R as M3R

open class MaterialListPreferenceDialog(open val pref: ListPreference) :
    MaterialAlertDialogBuilder(pref.context), DialogInterface.OnClickListener {

    companion object {
        fun newInstance(pref: ListPreference): MaterialAlertDialogBuilder {
            return MaterialListPreferenceDialog(pref)
        }
    }

    init {
        @Suppress("LeakingThis")
        setupDialog()
    }

    open fun setupDialog() {
        setTitle(pref.title)
            .setIcon(pref.dialogIcon)
            .setNegativeButton(pref.negativeButtonText, null)
            .setSingleChoiceItems(pref.entries, pref.findIndexOfValue(pref.value), this)
    }


    override fun onClick(dialog: DialogInterface, which: Int) {
        if (which >= 0) {
            val value: String = pref.entryValues[which].toString()
            if (pref.callChangeListener(value)) {
                pref.value = value
            }
        }
        dialog.dismiss()
    }

    class IconShape(pref: ListPreference) : MaterialListPreferenceDialog(pref) {

        companion object {
            fun newInstance(pref: ListPreference): MaterialAlertDialogBuilder {
                return IconShape(pref)
            }
        }

        override fun setupDialog() {
            super.setupDialog()
            var itemLayout = 0
            context.withStyledAttributes(
                null,
                M3R.styleable.AlertDialog,
                M3R.attr.alertDialogStyle,
                0
            ) {
                // R.layout.mtrl_alert_select_dialog_singlechoice
                itemLayout = getResourceId(M3R.styleable.AlertDialog_singleChoiceItemLayout, 0)
            }
            val adapter =
                CheckedItemAdapter(pref.context, itemLayout, android.R.id.text1, pref.entries)
            setSingleChoiceItems(adapter, pref.findIndexOfValue(pref.value), this)
        }

        private class CheckedItemAdapter(
            context: Context, resource: Int, textViewResourceId: Int,
            objects: Array<CharSequence?>,
        ) : ArrayAdapter<CharSequence?>(context, resource, textViewResourceId, objects) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, convertView, parent).apply {
                    val textView = this as TextView
                    val bitmap = createBitmap(24.dp, 24.dp, Bitmap.Config.ARGB_8888)
                    val pathStr = IconShapePref.getMaskPathByIndex(context, position)
                    val shapePath = AlterableAdaptiveIconDrawable.getMaskPath(
                        pathStr, bitmap.width, bitmap.height
                    )
                    bitmap.applyCanvas {
                        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                        paint.color = MaterialColors.getColor(textView, M3R.attr.colorSecondary)
                        drawPath(shapePath, paint)

                        setBitmap(null)
                    }
                    val drawable = BitmapDrawable(context.resources, bitmap).apply {
                        setBounds(0, 0, bitmap.width, bitmap.height)
                    }
                    textView.updateCompoundDrawablesRelative(end = drawable)
                }
            }

            override fun hasStableIds(): Boolean {
                return true
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }
        }
    }
}