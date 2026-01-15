package com.dede.android_eggs

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import com.dede.android_eggs.flavor.FlavorFeatures
import com.dede.android_eggs.flavor.LatestVersion
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders.Accept
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class FlavorFeaturesImpl : FlavorFeatures {
    override fun launchReview(activity: ComponentActivity) {
    }

    override suspend fun checkUpdate(activity: Activity): LatestVersion? {
        val client = HttpClient {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorClient", message)
                    }
                }
                level = LogLevel.HEADERS
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
        try {
            val response = client.get {
                url("https://api.github.com/repos/hushenghao/AndroidEasterEggs/releases/latest")
                headers {
                    append(Accept, "application/vnd.github+json")
                    append("X-GitHub-Api-Version", "2022-11-28")
                }
            }
            if (response.status != OK) {
                return null
            }
            return response.body<GitHubLatestRelease>().toLatestRelease()
        } catch (e: NoTransformationFoundException) {
            return null
        } finally {
            client.close()
        }
    }
}

@Serializable
private data class GitHubLatestRelease(
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("tag_name")
    val tagName: String,
    val assets: List<Asset>,
    @SerialName("body")
    val changelog: String,
) {
    @Serializable
    data class Asset(
        @SerialName("browser_download_url")
        val downloadUrl: String,
        val name: String,
        @SerialName("content_type")
        val contentType: String,
    )

    fun toLatestRelease(): LatestVersion? {
        val apkAsset =
            assets.firstOrNull { it.contentType == "application/vnd.android.package-archive" }
        if (apkAsset == null) {
            return null
        }
        return LatestVersion(
            versionName = tagName,
            pageUrl = htmlUrl,
            downloadUrl = apkAsset.downloadUrl,
            changelog = changelog,
        )
    }
}
