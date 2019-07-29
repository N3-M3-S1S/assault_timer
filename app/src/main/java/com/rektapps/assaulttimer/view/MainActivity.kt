package com.rektapps.assaulttimer.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.sender.NOTIFICATION_ASSAULT_TYPE_ID_KEY
import com.rektapps.assaulttimer.notifications.sender.NOTIFICATION_REGION_ID_KEY
import com.rektapps.assaulttimer.viewmodel.MainViewModel
import com.rektapps.assaulttimer.viewmodel.shared.EventsViewModel
import com.rektapps.assaulttimer.viewmodel.shared.UiSettingsViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.nav_header.view.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mainViewModel: MainViewModel
    private lateinit var eventsViewModel: EventsViewModel
    private lateinit var uiSettingsViewModel: UiSettingsViewModel

    private lateinit var navController: NavController

    private var onDrawerClosed: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViewModels()
        setDarkModeEnabled()
        setSupportActionBar(mainToolbar)
        setupNavigation()
        uiSettingsViewModel.getOnDarkModeToggledEvent().observe(this, Observer { recreate() })
        handleNotificationIntent(intent)
    }

    override fun onBackPressed() {
        if(!closeDrawerIfVisible()) { //if drawer wasn't open
            val isNavigationAtStartDestination = !navController.navigateUp()
            if(isNavigationAtStartDestination)
                finish()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent!!)
    }

    private fun handleNotificationIntent(intent: Intent) {
        val isIntentNotification =
            intent.hasExtra(NOTIFICATION_REGION_ID_KEY) && intent.hasExtra(NOTIFICATION_ASSAULT_TYPE_ID_KEY)

        if (isIntentNotification) {
            val region = Region.getById(intent.getIntExtra(NOTIFICATION_REGION_ID_KEY, -1))
            val assaultType = AssaultType.getById(intent.getIntExtra(NOTIFICATION_ASSAULT_TYPE_ID_KEY, -1))

            eventsViewModel.onNotificationClicked(region, assaultType)

            if (navController.currentDestination!!.id == R.id.settingsFragment)
                navController.navigate(R.id.fromSettingsToList)

            closeDrawerIfVisible()
        }
    }

    private fun initializeViewModels() {
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get()
        eventsViewModel = ViewModelProviders.of(this).get()
        uiSettingsViewModel = ViewModelProviders.of(this, viewModelFactory).get()
        lifecycle.addObserver(mainViewModel)
    }

    private fun setDarkModeEnabled() {
        val nightMode =
            if (mainViewModel.isDarkThemeSelected()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun setupNavigation() {
        navController = findNavController(navHost.id)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, mainToolbar, R.string.drawerOpen, R.string.drawerClose)
        toggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            onDrawerClosed = {
                val region = if (it.itemId == R.id.assaultRegionEU) Region.EU else Region.NA
                eventsViewModel.onRegionChanged(region)

                if (navController.currentDestination!!.id == R.id.settingsFragment)
                    navController.navigate(R.id.fromSettingsToList)
            }

            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

        drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {

            override fun onDrawerClosed(drawerView: View) {
                onDrawerClosed?.let {
                    it()
                    onDrawerClosed = null
                }
            }

            override fun onDrawerStateChanged(newState: Int) {
                /**unused*/
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                /**unused*/

            }

            override fun onDrawerOpened(drawerView: View) {
                /**unused*/

            }

        })

        navigationView.getHeaderView(0).settingsButton.setOnClickListener {
            onDrawerClosed = {
                if (navController.currentDestination!!.id == R.id.assaultsFragment) {
                    navController.navigate(R.id.fromListToSettings)
                }
            }
            drawer_layout.closeDrawers()
        }
    }

    private fun closeDrawerIfVisible(): Boolean {
        val isDrawerVisible = drawer_layout.isDrawerVisible(GravityCompat.START)
        if (isDrawerVisible)
            drawer_layout.closeDrawer(GravityCompat.START)
        return isDrawerVisible
    }

}
