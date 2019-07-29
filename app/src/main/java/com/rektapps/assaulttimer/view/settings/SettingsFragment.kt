package com.rektapps.assaulttimer.view.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.utils.getTitleIDForNotificationMode
import com.rektapps.assaulttimer.viewmodel.NotificationSettingsViewModel
import com.rektapps.assaulttimer.viewmodel.shared.UiSettingsViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    private lateinit var uiSettingsViewModel: UiSettingsViewModel
    private lateinit var notificationSettingsViewModel: NotificationSettingsViewModel

    private val handler = Handler()
    private val darkModeToggleCallback = Runnable { uiSettingsViewModel.onDarkModeToggled() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
        initializeViewModels()
        activity!!.setTitle(R.string.settingsTitle)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        findPreference<Preference>(getString(R.string.notificationModeKey))!!.onPreferenceClickListener =
           Preference.OnPreferenceClickListener {
                val notificationModeDialog = NotificationModeDialog()
                notificationModeDialog.setTargetFragment(this, 0)
                notificationModeDialog.show(fragmentManager!!, "")
                true
            }

        setNotificationModeSummaryText(notificationSettingsViewModel.getLastSelectedNotificationMode())

        onSharedPreferenceChangeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                when (key) {
                    getString(R.string.darkThemeKey) -> {
                        handler.removeCallbacks(darkModeToggleCallback)
                        handler.postDelayed(
                            darkModeToggleCallback,
                            250
                        ) //prevent the switch's thumb from a lag when main activity is being recreated by setting the delay to the thumb's animation duration
                    }

                    getString(R.string.isAMPMSelectedKey) -> {
                        uiSettingsViewModel.onTimeFormatToggled(findPreference<SwitchPreference>(key)!!.isChecked)
                    }


                    getString(R.string.notificationsEnabledKey) -> {
                        notificationSettingsViewModel.setNotificationsEnabled(findPreference<SwitchPreference>(key)!!.isChecked)
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    private fun initializeViewModels() {
        notificationSettingsViewModel = ViewModelProviders.of(this, viewModelFactory).get()
        uiSettingsViewModel = ViewModelProviders.of(activity!!).get()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val selectedNotificationMode = NotificationMode.getById(resultCode)
        setNotificationModeSummaryText(selectedNotificationMode)
    }

    private fun setNotificationModeSummaryText(notificationMode: NotificationMode){
        val notificationModeTitleID = getTitleIDForNotificationMode(notificationMode)
        findPreference<Preference>(getString(R.string.notificationModeKey))!!.setSummary(notificationModeTitleID)
    }

}