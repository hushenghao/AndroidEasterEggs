package com.dede.basic.provider

import android.content.Context
import android.view.View

abstract class SnapshotProvider {

    open val includeBackground: Boolean = false

    open val insertPadding: Boolean = false

    abstract fun create(context: Context): View
}