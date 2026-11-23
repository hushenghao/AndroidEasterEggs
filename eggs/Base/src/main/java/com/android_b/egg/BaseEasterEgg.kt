package com.android_b.egg

import com.dede.basic.provider.AndroidVersion
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.SnapshotProvider

class BaseEasterEgg(
    iconRes: Int,
    nameRes: Int,
    nicknameRes: Int,
    version: AndroidVersion,
    additionalVersions: List<AndroidVersion> = emptyList(),
    actionClass: Class<out PlatLogoActivity>,
) : EasterEgg(
    iconRes = iconRes,
    nameRes = nameRes,
    nicknameRes = nicknameRes,
    version = version,
    actionClass = actionClass,
    additionalVersions = additionalVersions,
) {

    override fun provideSnapshotProvider(): SnapshotProvider {
        return BaseSnapshotProvider(iconRes)
    }
}
