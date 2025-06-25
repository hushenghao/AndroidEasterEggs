package com.dede.android_eggs.views.main

import android.app.assist.AssistContent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.dede.android_eggs.R
import com.dede.android_eggs.inject.FlavorFeatures
import com.dede.android_eggs.util.setupSplashScreen
import com.dede.android_eggs.views.main.util.EasterEggShortcutsHelp
import com.dede.android_eggs.views.main.util.IntentHandler
import com.dede.android_eggs.views.theme.EasterEggsTheme
import com.dede.basic.provider.EasterEgg
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@AndroidEntryPoint
class EasterEggsActivity : AppCompatActivity() {

    @Inject
    lateinit var pureEasterEggs: List<@JvmSuppressWildcards EasterEgg>

    @Inject
    @ActivityScoped
    lateinit var intentHandler: IntentHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
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
        FlavorFeatures.get().call(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentHandler.handleIntent(intent)
    }

    override fun onProvideAssistContent(outContent: AssistContent?) {
        super.onProvideAssistContent(outContent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && outContent != null) {
            outContent.webUri = getString(R.string.url_github).toUri()
        }
    }
}
