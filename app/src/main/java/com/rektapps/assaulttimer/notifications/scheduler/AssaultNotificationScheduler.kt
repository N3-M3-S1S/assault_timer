package com.rektapps.assaulttimer.notifications.scheduler

import com.rektapps.assaulttimer.model.entity.Assault

interface AssaultNotificationScheduler {
    fun schedule(assaultEntity: Assault)
    fun cancel(assaultEntity: Assault)
    fun cancelAll()
}


