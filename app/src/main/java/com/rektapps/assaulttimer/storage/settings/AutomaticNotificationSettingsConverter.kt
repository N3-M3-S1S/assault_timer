package com.rektapps.assaulttimer.storage.settings

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region

interface AutomaticNotificationSettingsConverter {
    fun settingsToString(settings: Map<Region, List<AssaultType>>): String
    fun stringToSettings(settingsString: String): Map<Region, List<AssaultType>>
}