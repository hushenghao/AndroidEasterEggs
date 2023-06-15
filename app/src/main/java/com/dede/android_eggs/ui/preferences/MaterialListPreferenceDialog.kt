package com.dede.android_eggs.ui.preferences

import android.content.DialogInterface
import androidx.preference.ListPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

}