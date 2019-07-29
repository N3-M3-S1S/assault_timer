package com.rektapps.assaulttimer.storage.settings

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.NotificationMode

interface NotificationsConfig {
    var notificationMode: NotificationMode
    var automaticNotificationModePreferences: Map<Region, List<AssaultType>>
}