package com.rektapps.assaulttimer.notifications

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region

data class ClickedNotificationEntity(val region: Region, val assaultType: AssaultType)