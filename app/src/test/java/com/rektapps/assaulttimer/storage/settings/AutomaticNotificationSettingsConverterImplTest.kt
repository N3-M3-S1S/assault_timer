package com.rektapps.assaulttimer.storage.settings

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.storage.settings.impl.AutomaticNotificationSettingsConverterImpl
import junit.framework.Assert.assertTrue
import org.junit.Test

class AutomaticNotificationSettingsConverterImplTest {
    private val automaticNotificationsSettingsConverterImpl =
        AutomaticNotificationSettingsConverterImpl()
    private val testSettings =
        mapOf(Region.EU to listOf(AssaultType.BFA), Region.NA to listOf(AssaultType.BFA, AssaultType.LEGION))
    private val testString =
        "{\"${Region.EU.getId()}\":[${AssaultType.BFA.getId()}],\"${Region.NA.getId()}\":[${AssaultType.BFA.getId()},${AssaultType.LEGION.getId()}]}"


    @Test
    fun settingsToString() {
        val result = automaticNotificationsSettingsConverterImpl.settingsToString(testSettings)
        assertTrue(result == testString)
    }

    @Test
    fun convertStringToAutomaticNotificationPreferences() {
        val result = automaticNotificationsSettingsConverterImpl.stringToSettings(testString)
        assertTrue(result == testSettings)
    }

    @Test
    fun noSettingsSelected() {
        val result = automaticNotificationsSettingsConverterImpl.stringToSettings("")
        assert(result.isEmpty())
    }
}