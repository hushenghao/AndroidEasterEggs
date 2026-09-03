@file:Suppress("CustomSplashScreen")

package com.dede.android_eggs.views.main

import android.app.HandoffActivityData
import android.app.HandoffActivityDataRequestInfo
import android.app.assist.AssistContent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dede.android_eggs.R
import com.dede.android_eggs.flavor.FlavorFeatures
import com.dede.android_eggs.views.main.util.EasterEggShortcutsHelp
import com.dede.android_eggs.views.main.util.IntentHandler
import com.dede.android_eggs.views.theme.EasterEggsTheme
import com.dede.basic.provider.EasterEgg
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class Android16SplashActivity : EasterEggsActivity()

class Android15SplashActivity : EasterEggsActivity()

@AndroidEntryPoint
open class EasterEggsActivity : AppCompatActivity() {

    @Inject
    lateinit var pureEasterEggs: List<@JvmSuppressWildcards EasterEgg>

    @Inject
    @ActivityScoped
    lateinit var intentHandler: IntentHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        // Applies postSplashScreenTheme; the splash icon is static below API 31,
        // the animated icon only plays on the API 31+ system splash screen.
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            EasterEggsTheme {
                EasterEggsNavHost()
            }
        }

        intentHandler.handleIntent(intent)
        EasterEggShortcutsHelp.updateShortcuts(this, pureEasterEggs)

        // call flavor features
        FlavorFeatures.get().launchReview(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
            setHandoffEnabled(true, null)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentHandler.handleIntent(intent)
    }

    override fun onProvideAssistContent(outContent: AssistContent?) {
        super.onProvideAssistContent(outContent)
        if (outContent != null) {
            outContent.webUri = getString(R.string.url_github).toUri()
        }
    }

    @RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
    override fun onHandoffActivityDataRequested(requestInfo: HandoffActivityDataRequestInfo): HandoffActivityData? {
        return HandoffActivityData.Builder(componentName)
            .setFallbackUri(getString(R.string.url_github).toUri())
            .build()
    }
}
