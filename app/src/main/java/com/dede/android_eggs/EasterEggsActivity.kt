package com.dede.android_eggs

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroupAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_easter_eggs.*
import kotlin.math.hypot

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity(), Runnable {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatusBar()
        setContentView(R.layout.activity_easter_eggs)
        setSupportActionBar(toolbar)
        postAnim()
    }

    private fun initStatusBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.setDecorFitsSystemWindows(false)
//        } else {
        val option =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        val decorView = window.decorView
        val visibility: Int = decorView.systemUiVisibility
        decorView.systemUiVisibility = visibility or option
//        }
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun postAnim() {
        content.visibility = View.INVISIBLE
        content.postDelayed(this, 200)
    }

    override fun run() {
        val cx = logo.x + logo.width / 2f
        val cy = logo.y + logo.height / 2f
        val startRadius = hypot(logo.width.toFloat(), logo.height.toFloat())
        val endRadius = hypot(content.width.toFloat(), content.height.toFloat())
        val circularAnim = ViewAnimationUtils
            .createCircularReveal(content, cx.toInt(), cy.toInt(), startRadius, endRadius)
            .setDuration(800)
        logo.animate()
            .alpha(0f)
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(600)
            .withEndAction {
                logo.visibility = View.GONE
            }
            .withStartAction {
                content.visibility = View.VISIBLE
                circularAnim.start()
            }
            .start()
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        companion object {
            const val KEY_COLLECTION = "key_collection"
        }

        private lateinit var eggCollection: EggCollection

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            eggCollection = checkNotNull(findPreference(KEY_COLLECTION))
            eggCollection.setOnPreferenceChangeListener { _, newValue ->
                val isGrid = newValue as? Boolean ?: false
                if (isGrid) {
                    Toast.makeText(requireContext(), "Wow!!!", Toast.LENGTH_SHORT).show()
                }
                listView?.layoutManager = createLayoutManager(isGrid)
                return@setOnPreferenceChangeListener true
            }
        }

        private inner class SpanSizeLookup : GridLayoutManager.SpanSizeLookup() {
            @SuppressLint("RestrictedApi")
            override fun getSpanSize(position: Int): Int {
                val adapter = listView.adapter as? PreferenceGroupAdapter
                val item = adapter?.getItem(position)
                if (item is EggPreference) {
                    return 1
                }
                return 2
            }
        }

        private fun createLayoutManager(isGrid: Boolean): RecyclerView.LayoutManager {
            return if (isGrid) {
                GridLayoutManager(requireContext(), 2).apply { spanSizeLookup = SpanSizeLookup() }
            } else {
                super.onCreateLayoutManager()
            }
        }

        override fun onCreateLayoutManager(): RecyclerView.LayoutManager {
            return createLayoutManager(eggCollection.isChecked())
        }
    }

}
