package com.dede.basic

import android.os.Bundle

fun bundleBuilder(builder: Bundle.() -> Unit): Bundle {
    return Bundle().apply(builder)
}
