package com.dede.android_eggs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.TintTypedArray
import androidx.core.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.DefaultLifecycleObserver
import com.dede.android_eggs.databinding.ActivityEasterEggsBinding
import com.dede.android_eggs.databinding.LayoutNavigationHeaderBinding
import com.dede.basic.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.resources.MaterialAttributes
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.R as M3R

class NavigationViewController(private val activity: AppCompatActivity) : DefaultLifecycleObserver {

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

        DrawerBackPressedDispatcher(binding.drawerLayout).bind(activity)

        val headerBinding = LayoutNavigationHeaderBinding.bind(
            binding.navigationView.getHeaderView(0)
        )

        val listeners = Listeners(activity, headerBinding)
        binding.navigationView.setNavigationItemSelectedListener(listeners)
        bindMenuIcons(activity, binding.navigationView.menu)
        ViewCompat.setOnApplyWindowInsetsListener(binding.navigationView, listeners)

        headerBinding.tvVersion.text =
            activity.getString(
                R.string.summary_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            )
        headerBinding.switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            val nightMode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            if (nightMode == AppCompatDelegate.getDefaultNightMode()) {
                return@setOnCheckedChangeListener
            }
            AppCompatDelegate.setDefaultNightMode(nightMode)
            activity.putBoolean("key_night_mode", isChecked)
        }
        headerBinding.switchNightMode.setSwitchTypeface(FontIconsDrawable.ICONS_TYPEFACE)
        headerBinding.switchNightMode.isChecked = activity.getBoolean("key_night_mode", false)
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        actionBarDrawerToggle?.onConfigurationChanged(newConfig)
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return actionBarDrawerToggle?.onOptionsItemSelected(item) ?: false
    }

    @SuppressLint("RestrictedApi", "PrivateResource")
    private fun bindMenuIcons(context: Context, menu: Menu) {
        val parts = listOf(
            R.id.menu_github to "\ue88a",           // home
            R.id.menu_source to "\ue859",           // android
            R.id.menu_privacy_agreement to "\uea17",// policy
            R.id.menu_beta to "\uf090",             // download
            R.id.menu_star to "\ue838",             // star
            R.id.menu_email to "\ue0be",            // email
        )
        var colorStateList: ColorStateList? = null
        // getColorStateList(R.styleable.NavigationView_itemIconTint)
        // default: R.color.m3_navigation_item_icon_tint
        val typeValue = MaterialAttributes.resolve(context, M3R.attr.navigationViewStyle)
        if (typeValue != null) {
            val typedArray = TintTypedArray.obtainStyledAttributes(
                context, typeValue.resourceId, intArrayOf(M3R.attr.itemIconTint)
            )
            colorStateList = typedArray.getColorStateList(0)
            typedArray.recycle()
        }
        for (pair in parts) {
            menu.findItem(pair.first).icon = FontIconsDrawable(context, pair.second).apply {
                setPadding(.5f.dp)
                if (colorStateList != null) {
                    setColorStateList(colorStateList)
                }
            }
        }
    }

    private class DrawerBackPressedDispatcher(private val drawerLayout: DrawerLayout) :
        OnBackPressedCallback(false), Runnable, DrawerLayout.DrawerListener {

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

    private class Listeners(
        val activity: Activity,
        val headerBinding: LayoutNavigationHeaderBinding,
    ) : OnApplyWindowInsetsListener, NavigationView.OnNavigationItemSelectedListener {

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
                        activity, Uri.parse(R.string.url_privacy_agreement.string)
                    )
                }
                R.id.menu_beta -> {
                    ChromeTabsBrowser.launchUrl(activity, Uri.parse(R.string.url_beta.string))
                }
                R.id.menu_email -> {
                    ChromeTabsBrowser.launchUrlByBrowser(activity, Uri.parse("mailto:dede.hu@qq.com"))
                }
                R.id.menu_star -> {
                    ChromeTabsBrowser.launchUrlByBrowser(
                        activity, Uri.parse("market://details?id=" + activity.packageName)
                    )
                }
                R.id.menu_dino -> {
                    activity.startActivity(Intent(activity, DinoEggActivity::class.java))
                }
            }
            uiExecutor.execute {
                item.isChecked = false
            }
            return true
        }

        override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            headerBinding.spaceTop.updateLayoutParams<MarginLayoutParams> {
                topMargin = systemBars.top
            }
            return insets
        }
    }
}