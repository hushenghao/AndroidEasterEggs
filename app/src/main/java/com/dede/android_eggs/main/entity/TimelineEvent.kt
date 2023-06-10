package com.dede.android_eggs.main.entity

import androidx.annotation.DrawableRes

data class TimelineEvent(
    val year: String,
    val month: String,
    @DrawableRes val logoRes: Int,
    val event: CharSequence,
    val showYear: Boolean = true
)
