package com.rektapps.assaulttimer.storage.settings.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.storage.settings.AppConfig
import javax.inject.Inject

class AppConfigImpl @Inject constructor(appContext: Context, private val sharedPreferences: SharedPreferences): AppConfig {


    private val lastSelectedRegionKey = appContext.getString(R.string.lastSelectedRegionKey)
    private val lastSelectedAssaultTypeKey = appContext.getString(R.string.lastSelectedAssaultTypeKey)
    private val isDarkThemeSelectedKey = appContext.getString(R.string.darkThemeKey)
    private val isAmPmSelectedKey = appContext.getString(R.string.isAMPMSelectedKey)
    private val areNotificationEnabledKey = appContext.getString(R.string.notificationsEnabledKey)
    private val lastTimeZoneIDKey = appContext.getString(R.string.timeZoneIdKey)


    override var lastSelectedRegion: Region
        get() {
            val lastSelectedRegionId = sharedPreferences.getInt(lastSelectedRegionKey, Region.EU.getId())
            return Region.values().find { it.getId() == lastSelectedRegionId }!!
        }
        set(value) { sharedPreferences.edit {  putInt(lastSelectedRegionKey, value.getId())} }

    override var lastSelectedAssaultType: AssaultType
        get(){
            val lastSelectedAssaultTypeId = sharedPreferences.getInt(lastSelectedAssaultTypeKey, AssaultType.values()[0].getId())
            return AssaultType.values().find { it.getId() == lastSelectedAssaultTypeId }!!
        }
        set(value) { sharedPreferences.edit { putInt(lastSelectedAssaultTypeKey, value.getId()) } }


    override var lastTimeZoneID: String?
        get() = sharedPreferences.getString(lastTimeZoneIDKey, null)
        set(value) { sharedPreferences.edit { putString(lastTimeZoneIDKey, value) } }


    override val isDarkThemeSelected: Boolean
        get() = sharedPreferences.getBoolean(isDarkThemeSelectedKey, false)

    override val isAmPmTimeSelected: Boolean
        get() =  sharedPreferences.getBoolean(isAmPmSelectedKey, false)

    override val areNotificationsEnabled: Boolean
        get() = sharedPreferences.getBoolean(areNotificationEnabledKey, false)

}