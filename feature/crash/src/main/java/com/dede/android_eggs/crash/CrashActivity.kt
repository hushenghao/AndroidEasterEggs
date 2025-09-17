package com.dede.android_eggs.crash

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Scaffold
import com.dede.android_eggs.views.theme.EasterEggsTheme

/**
 * App crash report
 */
class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val throwable: Throwable? = Utilities.getUncaughtException(intent)
        if (throwable == null) {
            finish()
            return
        }

        if (Utilities.hasPostNotificationPermission(this)) {
            Utilities.tryPostCrashNotification(this, throwable)
        } else {
            val request =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                    Utilities.tryPostCrashNotification(this, throwable)
                }
            request.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val screenshotPath = intent.getStringExtra(Utilities.EXTRA_SCREENSHOT_PATH)
        setContent {
            EasterEggsTheme {
                Scaffold {
                    CrashScreen(it, throwable, screenshotPath)
                }
            }
        }
    }
}
