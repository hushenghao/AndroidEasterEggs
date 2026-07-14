package com.android_b.egg

import android.content.Context
import android.content.Intent
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEgg.VERSION_CODES_FULL.toApiLevel
import com.dede.basic.provider.SnapshotProvider
import com.dede.basic.provider.toRange

class BaseEasterEgg(
    iconRes: Int,
    nameRes: Int,
    nicknameRes: Int,
    fullApiLevelRange: IntRange,
) : EasterEgg(
    iconRes = iconRes,
    nameRes = nameRes,
    nicknameRes = nicknameRes,
    fullApiLevelRange = fullApiLevelRange,
) {

    constructor(
        iconRes: Int,
        nameRes: Int,
        nicknameRes: Int,
        fullApiLevel: Int,
    ) : this(iconRes, nameRes, nicknameRes, fullApiLevel.toRange())

    override fun onEasterEggAction(context: Context): Boolean {
        context.startActivity(
            Intent(context, PlatLogoActivity::class.java).apply {
                putExtra(PlatLogoActivity.EXTRA_ICON_RES, iconRes)
                putExtra(PlatLogoActivity.EXTRA_NICKNAME_RES, nicknameRes)
                putExtra(PlatLogoActivity.EXTRA_API_LEVEL, fullApiLevelRange.first.toApiLevel())
            },
        )
        return true
    }

    override fun provideSnapshotProvider(): SnapshotProvider {
        return BaseSnapshotProvider(iconRes)
    }
}
