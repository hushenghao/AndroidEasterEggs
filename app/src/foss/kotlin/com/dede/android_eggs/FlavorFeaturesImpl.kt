package com.dede.android_eggs

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import com.dede.android_eggs.flavor.FlavorFeatures
import com.dede.android_eggs.flavor.LatestVersion
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders.Accept
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class FlavorFeaturesImpl : FlavorFeatures {

    companion object {

        private val KtorClient = HttpClient {
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
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
            }
        }

        private suspend inline fun <reified T> HttpClient.simpleRequest(block: HttpRequestBuilder.() -> Unit): Result<T> {
            try {
                val response = request {
                    expectSuccess = false
                    block()
                }
                if (response.status != OK) {
                    return Result.failure(IOException("Unexpected response status: ${response.status}"))
                }
                return Result.success(response.body<T>())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
    }

    override fun launchReview(activity: ComponentActivity) {
    }

    override suspend fun checkUpdate(activity: Activity): Result<LatestVersion> {
        val result = KtorClient.simpleRequest<GitHubLatestRelease> {
            method = HttpMethod.Get
            url("https://api.github.com/repos/hushenghao/AndroidEasterEggs/releases/latest")
            headers {
                append(Accept, "application/vnd.github+json")
                append("X-GitHub-Api-Version", "2022-11-28")
            }
        }
        return result.mapCatching {
            it.toLatestVersion() ?: throw IOException("No APK asset found in the latest release")
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

    fun toLatestVersion(): LatestVersion? {
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
