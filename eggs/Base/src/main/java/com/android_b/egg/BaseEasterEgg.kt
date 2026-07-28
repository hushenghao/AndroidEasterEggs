package com.android_b.egg

import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.SnapshotProvider
import com.dede.basic.provider.toRange

class BaseEasterEgg(
    iconRes: Int,
    nameRes: Int,
    nicknameRes: Int,
    fullApiLevelRange: IntRange,
    actionClass: Class<out PlatLogoActivity>,
) : EasterEgg(
    iconRes = iconRes,
    nameRes = nameRes,
    nicknameRes = nicknameRes,
    fullApiLevelRange = fullApiLevelRange,
    actionClass = actionClass,
) {

    constructor(
        iconRes: Int,
        nameRes: Int,
        nicknameRes: Int,
        fullApiLevel: Int,
        actionClass: Class<out PlatLogoActivity>,
    ) : this(iconRes, nameRes, nicknameRes, fullApiLevel.toRange(), actionClass)

    override fun provideSnapshotProvider(): SnapshotProvider {
        return BaseSnapshotProvider(iconRes)
    }
}
