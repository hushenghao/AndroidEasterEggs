package com.dede.android_eggs.fake_test

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dede.android_eggs.fake_test.utils.EasterEggsServer
import com.dede.android_eggs.fake_test.utils.EasterEggsServer.Companion.registerHandler
import com.dede.android_eggs.fake_test.utils.ResponseUtils
import fi.iki.elonen.NanoHTTPD.Response
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generate Plat logo image
 */
@Ignore("Generate Plat Logo image")// remove this line to run test
@RunWith(AndroidJUnit4::class)
class PlatLogoUtil {

    companion object {
        private const val PLAT_LOGO_SIZE = 512
    }

    @Test
    fun platLogos() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        EasterEggsServer.start(context) {
            registerHandler("/platlogo_android_p.webp") {
                createDrawableResponse(context, com.android_p.egg.R.drawable.p_icon)
            }
            registerHandler("/platlogo_android_q.webp") {
                createDrawableResponse(context, com.android_q.egg.R.drawable.q_icon)
            }
            registerHandler("/platlogo_android_r.webp") {
                createDrawableResponse(context, com.android_r.egg.R.drawable.r_icon)
            }
        }
    }

    private fun createDrawableResponse(context: Context, drawable: Int): Response {
        return ResponseUtils.createDrawableResponse(
            context, drawable, PLAT_LOGO_SIZE, PLAT_LOGO_SIZE
        )
    }
}