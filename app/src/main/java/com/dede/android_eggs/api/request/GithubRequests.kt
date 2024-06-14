package com.dede.android_eggs.api.request

import com.dede.android_eggs.api.ApiManager
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import javax.inject.Singleton

interface GithubRequests {

    companion object {
        private const val GITHUB_OWNER = "hushenghao"
        private const val GITHUB_REPO = "AndroidEasterEggs"

        private const val GITHUB_RELEASE_URL = "https://github.com/${GITHUB_OWNER}/${GITHUB_REPO}/releases"
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object Provider {
        @Provides
        @Singleton
        fun providerGithubRequests(): GithubRequests {
            return ApiManager.create<GithubRequests>()
        }
    }

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path(value = "owner") owner: String = GITHUB_OWNER,
        @Path(value = "repo") repo: String = GITHUB_REPO,
    ): LatestRelease?

    @JsonClass(generateAdapter = true)
    data class LatestRelease(
        val id: Long,
        @Json(name = "tag_name")
        val tagName: String,
        val assets: List<Assets>
    ) {
        @JsonClass(generateAdapter = true)
        data class Assets(
            val id: Long,
            val name: String,
            @Json(name = "content_type")
            val contentType: String,
            val size: Long,
            @Json(name = "browser_download_url")
            val browserDownloadUrl: String = GITHUB_RELEASE_URL
        )
    }
}