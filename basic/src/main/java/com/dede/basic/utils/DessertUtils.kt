package com.dede.basic.utils

import android.os.Build

object DessertUtils {

    /**
     * 获取Android版本的甜点代码
     */
    fun getDessertCode(): String =
        when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.LOLLIPOP -> "LMP"
            Build.VERSION_CODES.LOLLIPOP_MR1 -> "LM1"
            Build.VERSION_CODES.M -> "MNC"
            Build.VERSION_CODES.N -> "NYC"
            Build.VERSION_CODES.N_MR1 -> "NM1"
            Build.VERSION_CODES.O -> "OC"
            Build.VERSION_CODES.O_MR1 -> "OM1"//
            Build.VERSION_CODES.P -> "PIE"
            Build.VERSION_CODES.Q -> "QT"
            Build.VERSION_CODES.R -> "RVC"
            Build.VERSION_CODES.S -> "SC"
            Build.VERSION_CODES.S_V2 -> "SC2"
            Build.VERSION_CODES.TIRAMISU -> "TM"
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> "UDC"
            Build.VERSION_CODES.VANILLA_ICE_CREAM -> "VIC"
            Build.VERSION_CODES.BAKLAVA -> "BKL"//
            else -> Build.VERSION.RELEASE_OR_CODENAME.replace(Regex("[a-z]*"), "")
        }
}
