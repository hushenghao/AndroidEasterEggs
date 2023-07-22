package com.dede.android_eggs.ui.views.text

import android.graphics.Canvas
import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ReplacementSpan

class SpaceSpan(private val space: Int) : ReplacementSpan() {

    companion object {
        const val SPACE_CHAR = " "

        fun SpannableStringBuilder.appendSpace(space: Int): SpannableStringBuilder {
            return append(SPACE_CHAR, SpaceSpan(space), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

    }

    private fun trimSpaceChars(text: CharSequence, start: Int, end: Int): CharSequence {
//        return text.subSequence(start, end)
        return text.subSequence(start, end).trim(SPACE_CHAR[0])
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence, start: Int, end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val chars = trimSpaceChars(text, start, end)
        if (chars.isEmpty()) {
            return space
        }
        return (paint.measureText(chars, 0, chars.length) + space).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int,
        paint: Paint
    ) {
        val chars = trimSpaceChars(text, start, end)
        if (chars.isEmpty()) return
        canvas.drawText(chars, 0, chars.length, x + space, y.toFloat(), paint)
    }
}