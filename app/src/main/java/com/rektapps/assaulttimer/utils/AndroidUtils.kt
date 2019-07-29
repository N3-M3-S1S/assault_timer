package com.rektapps.assaulttimer.utils

import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.NotificationMode

fun getTitleIDForRegion(region: Region) = when(region){
    Region.EU -> R.string.EUTitle
    Region.NA -> R.string.NATitle
}

fun getTitleIDForAssaultType(assaultType: AssaultType) = when(assaultType){
    AssaultType.BFA -> R.string.BFATitle
    AssaultType.LEGION -> R.string.LegionTitle
}

fun getTitleIDForNotificationMode(notificationMode: NotificationMode) = when(notificationMode){
    NotificationMode.AUTO -> R.string.notificationModeAutoTitle
    NotificationMode.MANUAL -> R.string.notificationModeManualTitle
}