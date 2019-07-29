package com.rektapps.assaulttimer.notifications.composer

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.notifications.AssaultStartNotificationMessage

interface AssaultNotificationComposer {
    fun compose(assaultEntity: Assault): AssaultStartNotificationMessage
}