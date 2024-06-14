package com.dede.android_eggs.api.upgrade.impl

import com.dede.android_eggs.api.request.GithubRequests
import com.dede.android_eggs.api.upgrade.UpgradeChecker
import javax.inject.Inject

class GithubUpgradeCheckerImpl @Inject constructor() : UpgradeChecker {

    @Inject
    lateinit var githubRequests: GithubRequests

    override suspend fun getLatestVersion(): UpgradeChecker.Version? {
        return githubRequests.getLatestRelease()?.convertToVersion()
    }

    private fun GithubRequests.LatestRelease?.convertToVersion(): UpgradeChecker.Version? {
        val regex = Regex("^\\S*_([\\d.]+)_(\\d+)\\S*.apk$")
        // example: easter_eggs_2.5.2_42-release.apk
        val apkAssets = this?.assets?.find { regex.matches(it.name) } ?: return null
        val matchGroups = regex.matchEntire(apkAssets.name)?.groups ?: return null
        val versionName = matchGroups[1]?.value ?: return null
        val versionCode = matchGroups[2]?.value ?: return null
        return UpgradeChecker.Version(
            versionName = versionName,
            versionCodeStr = versionCode
        ).apply {
            upgradeUrl = apkAssets.browserDownloadUrl
        }
    }
}
