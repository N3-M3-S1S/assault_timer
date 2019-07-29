package com.rektapps.assaulttimer.notifications.sender

import com.rektapps.assaulttimer.model.entity.Assault

interface NotificationSender {
    fun sendAssaultStartedNotification(startedAssault: Assault)
}