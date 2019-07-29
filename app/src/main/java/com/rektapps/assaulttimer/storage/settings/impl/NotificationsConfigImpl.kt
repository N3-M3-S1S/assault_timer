package com.rektapps.assaulttimer.storage.settings.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.storage.settings.AutomaticNotificationSettingsConverter
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import javax.inject.Inject

class NotificationsConfigImpl @Inject constructor(private val appContext: Context,
                                                  private val automaticNotificationSettingsConverter: AutomaticNotificationSettingsConverter,
                                                  private val sharedPreferences: SharedPreferences): NotificationsConfig {

    private val automaticNotificationsPreferenceKey: String = appContext.getString(R.string.automaticNotificationsSettingsKey)

    override var notificationMode: NotificationMode
        get()  {
            val notificationModeId = sharedPreferences.getString(appContext.getString(R.string.notificationModeKey), NotificationMode.MANUAL.getId().toString()).toInt()
            return NotificationMode.values().find { it.getId() == notificationModeId }!!
        }

    set(value){ sharedPreferences.edit{ putString(appContext.getString(R.string.notificationModeKey), value.getId().toString()) } }

    override var automaticNotificationModePreferences: Map<Region, List<AssaultType>>
        set(value) { sharedPreferences.edit { putString(automaticNotificationsPreferenceKey, automaticNotificationSettingsConverter.settingsToString(value)) } }
        get() = automaticNotificationSettingsConverter.stringToSettings(sharedPreferences.getString(automaticNotificationsPreferenceKey, ""))

}