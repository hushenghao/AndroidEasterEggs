package com.android_b.egg

import android.content.Context
import android.content.Intent
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.SnapshotProvider
import com.dede.basic.provider.toRange

class BaseEasterEgg(
    iconRes: Int,
    nameRes: Int,
    nicknameRes: Int,
    apiLevelRange: IntRange,
) : EasterEgg(
    iconRes = iconRes,
    nameRes = nameRes,
    nicknameRes = nicknameRes,
    apiLevelRange = apiLevelRange,
) {

    constructor(
        iconRes: Int,
        nameRes: Int,
        nicknameRes: Int,
        apiLevel: Int,
    ) : this(iconRes, nameRes, nicknameRes, apiLevel.toRange())

    override fun onEasterEggAction(context: Context): Boolean {
        context.startActivity(
            Intent(context, PlatLogoActivity::class.java).apply {
                putExtra(PlatLogoActivity.EXTRA_ICON_RES, iconRes)
                putExtra(PlatLogoActivity.EXTRA_NICKNAME_RES, nicknameRes)
                putExtra(PlatLogoActivity.EXTRA_API_LEVEL, apiLevelRange.first)
            },
        )
        return true
    }

    override fun provideSnapshotProvider(): SnapshotProvider {
        return BaseSnapshotProvider(iconRes)
    }
}
