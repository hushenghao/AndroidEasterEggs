package com.dede.android_eggs.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun <T : Fragment> FragmentActivity.findFragmentById(id: Int): T? {
    return supportFragmentManager.findFragmentById(id) as? T
}
