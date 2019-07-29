package com.rektapps.assaulttimer.storage.settings

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region


interface AppConfig {
    var lastSelectedRegion: Region
    var lastSelectedAssaultType: AssaultType
    var lastTimeZoneID: String?
    val isDarkThemeSelected: Boolean
    val isAmPmTimeSelected: Boolean
    val areNotificationsEnabled: Boolean
}