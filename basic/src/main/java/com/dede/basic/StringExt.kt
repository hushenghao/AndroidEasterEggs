package com.dede.basic

private val regexTrimZero = Regex("0+?$")
private val regexTrimDot = Regex("[.]$")

fun String.trimZeroAndDot(): String {
    var s = this
    if (s.indexOf(".") > 0) {
        s = s.replace(regexTrimZero, "")
        s = s.replace(regexTrimDot, "")
    }
    return s
}
