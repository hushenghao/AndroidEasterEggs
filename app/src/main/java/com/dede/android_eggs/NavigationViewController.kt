package com.dede.android_eggs

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Typeface
import android.net.Uri
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import com.dede.android_eggs.databinding.ActivityEasterEggsBinding
import com.dede.android_eggs.databinding.LayoutNavigationHeaderBinding
import com.dede.basic.getBoolean
import com.dede.basic.putBoolean
import com.dede.basic.string
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.MaterialShapeDrawable

class NavigationViewController(private val activity: AppCompatActivity) {

    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    fun bind(binding: ActivityEasterEggsBinding) {
        activity.setContentView(binding.root)
        activity.setSupportActionBar(binding.toolbar)

        binding.appBar.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(activity)

        actionBarDrawerToggle = ActionBarDrawerToggle(
            activity,
            binding.drawerLayout,
            binding.toolbar,
            R.string.label_drawer_open,
            R.string.label_drawer_close
        ).apply { syncState() }

        DrawerLayoutBackPressedDispatcher(binding.drawerLayout).bind(activity)

        val listeners = Listeners(activity)
        binding.navigationView.setNavigationItemSelectedListener(listeners)
        val headerView = binding.navigationView.getHeaderView(0)
        val headerBinding = LayoutNavigationHeaderBinding.bind(headerView)
        ViewCompat.setOnApplyWindowInsetsListener(headerView, listeners)
        headerBinding.tvVersion.text =
            activity.getString(
                R.string.summary_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            )
        val switchNightMode = headerBinding.switchNightMode
        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            val nightMode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            if (nightMode == AppCompatDelegate.getDefaultNightMode()) {
                return@setOnCheckedChangeListener
            }
            AppCompatDelegate.setDefaultNightMode(nightMode)
            activity.putBoolean("key_night_mode", isChecked)
        }
        switchNightMode.setSwitchTypeface(Typeface.createFromAsset(activity.assets, "icons.otf"))
        switchNightMode.isChecked = activity.getBoolean("key_night_mode", false)
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        actionBarDrawerToggle?.onConfigurationChanged(newConfig)
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return actionBarDrawerToggle?.onOptionsItemSelected(item) ?: false
    }

    private class DrawerLayoutBackPressedDispatcher(
        private val drawerLayout: DrawerLayout,
    ) : OnBackPressedCallback(false), Runnable, DrawerLayout.DrawerListener {

        fun bind(activity: AppCompatActivity) {
            drawerLayout.addDrawerListener(this)
            drawerLayout.post(this)
            activity.onBackPressedDispatcher.addCallback(activity, this)
        }

        override fun handleOnBackPressed() {
            drawerLayout.close()
        }

        override fun run() {
            isEnabled = drawerLayout.isOpen
        }

        override fun onDrawerOpened(drawerView: View) {
            isEnabled = true
        }

        override fun onDrawerClosed(drawerView: View) {
            isEnabled = false
        }

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        }

        override fun onDrawerStateChanged(newState: Int) {
        }
    }

    private class Listeners(val activity: Activity) : OnApplyWindowInsetsListener,
        NavigationView.OnNavigationItemSelectedListener {

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.menu_github -> {
                    ChromeTabsBrowser.launchUrl(activity, Uri.parse(R.string.url_github.string))
                }
                R.id.menu_source -> {
                    ChromeTabsBrowser.launchUrl(activity, Uri.parse(R.string.url_source.string))
                }
                R.id.menu_privacy_agreement -> {
                    ChromeTabsBrowser.launchUrl(
                        activity,
                        Uri.parse(R.string.url_privacy_agreement.string)
                    )
                }
                R.id.menu_beta -> {
                    ChromeTabsBrowser.launchUrl(activity, Uri.parse(R.string.url_beta.string))
                }
            }
            return true
        }

        override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(top = systemBars.top)
            return insets
        }
    }
}