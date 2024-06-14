package com.dede.android_eggs.api.upgrade

import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.api.upgrade.impl.GithubUpgradeCheckerImpl
import com.dede.android_eggs.util.compareStringVersion
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

interface UpgradeChecker {

    data class Version(val versionName: String? = null, val versionCode: Int = -1) {
        constructor(versionName: String?, versionCodeStr: String?)
                : this(versionName, versionCodeStr?.toIntOrNull() ?: -1)

        var upgradeUrl: String? = null
    }

    suspend fun getLatestVersion(): Version?

    fun getAppVersion(): Version {
        return Version(
            versionName = BuildConfig.VERSION_NAME,
            versionCode = BuildConfig.VERSION_CODE
        )
    }

    fun haveUpgrade(latestVersion: Version?): Boolean {
        if (latestVersion == null) {
            return false
        }
        val appVersion = getAppVersion()
        if (latestVersion.versionCode > appVersion.versionCode) {
            return true
        }
        val latestVersionName = latestVersion.versionName
        val appVersionName = appVersion.versionName
        if (latestVersionName.isNullOrBlank() || appVersionName.isNullOrBlank()) {
            return false
        }
        return compareStringVersion(latestVersionName, appVersionName) > 0
    }

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Github

@Module
@InstallIn(SingletonComponent::class)
abstract class UpgradeCheckerImplProviders {

    @Github
    @Binds
    @Singleton
    abstract fun providerGithubImpl(impl: GithubUpgradeCheckerImpl): UpgradeChecker

}
