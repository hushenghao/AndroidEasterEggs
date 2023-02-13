package com.dede.android_eggs.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TintTypedArray
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.DefaultLifecycleObserver
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.LayoutEasterEggsContentBinding
import com.dede.android_eggs.databinding.LayoutNavigationHeaderBinding
import com.dede.android_eggs.ui.FontIconsDrawable
import com.dede.android_eggs.ui.ScaleTypeDrawable
import com.dede.android_eggs.util.ChromeTabsBrowser
import com.dede.android_eggs.util.NightModeManager
import com.dede.basic.dp
import com.dede.basic.requireDrawable
import com.dede.basic.string
import com.dede.basic.uiExecutor
import com.google.android.material.navigation.NavigationView
import com.google.android.material.resources.MaterialAttributes
import com.google.android.material.shape.MaterialShapeDrawable
import com.dede.android_eggs.R.layout.activity_easter_eggs as content_view
import com.dede.android_eggs.R.layout.activity_easter_eggs_land as content_view_land
import com.google.android.material.R as M3R

class NavigationViewController(private val activity: AppCompatActivity) : DefaultLifecycleObserver {

    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    private fun isWideSize(): Boolean {
        val configuration = activity.resources.configuration
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                configuration.smallestScreenWidthDp >= 600
    }

    fun setContentView() {
        activity.setContentView(if (isWideSize()) content_view_land else content_view)
        val binding =
            LayoutEasterEggsContentBinding.bind(activity.findViewById(R.id.layout_content))
        val navigationView: NavigationView = activity.findViewById(R.id.navigation_view)
        activity.setSupportActionBar(binding.toolbar)
        binding.appBar.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(activity)

        bindNavigationView(navigationView)

        val drawerLayout: DrawerLayout? = activity.findViewById(R.id.drawer_layout)
        if (drawerLayout != null) {
            actionBarDrawerToggle = ActionBarDrawerToggle(
                activity,
                drawerLayout,
                binding.toolbar,
                R.string.label_drawer_open,
                R.string.label_drawer_close
            ).apply { syncState() }

            DrawerBackPressedDispatcher(drawerLayout).bind(activity)
        }
    }

    private fun bindNavigationView(navigationView: NavigationView) {
        val headerBinding = LayoutNavigationHeaderBinding.bind(navigationView.getHeaderView(0))
        headerBinding.root.background = ScaleTypeDrawable(
            activity.requireDrawable(R.drawable.img_nav_header_bg),
            com.dede.android_eggs.ui.ScaleType.CENTER_CROP
        )
        val listeners = Listeners(activity, headerBinding)
        navigationView.setNavigationItemSelectedListener(listeners)
        bindMenuIcons(activity, navigationView.menu)
        ViewCompat.setOnApplyWindowInsetsListener(navigationView, listeners)

        val nightModeManager = NightModeManager(activity)
        headerBinding.tvVersion.text =
            activity.getString(
                R.string.label_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            )
        headerBinding.switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            nightModeManager.setNightMode(isChecked)
        }
        headerBinding.switchNightMode.setSwitchTypeface(FontIconsDrawable.ICONS_TYPEFACE)
        headerBinding.switchNightMode.isChecked = nightModeManager.isNightMode()
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
                    ChromeTabsBrowser.launchUrlByBrowser(
                        activity,
                        Uri.parse("mailto:dede.hu@qq.com")
                    )
                }
                R.id.menu_star -> {
                    ChromeTabsBrowser.launchUrlByBrowser(
                        activity, Uri.parse("market://details?id=" + activity.packageName)
                    )
                }
            }
            uiExecutor.execute {
                item.isChecked = false
            }
            return true
        }

        override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
            val edge = insets.getInsets(Type.displayCutout() or Type.systemBars())
            v.updatePadding(left = edge.left)
            headerBinding.root.updatePadding(top = edge.top)
            return insets
        }
    }
}