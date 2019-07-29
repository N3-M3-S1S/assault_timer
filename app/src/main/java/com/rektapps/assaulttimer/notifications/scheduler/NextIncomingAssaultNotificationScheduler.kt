package com.rektapps.assaulttimer.notifications.scheduler

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import io.reactivex.Completable

interface NextIncomingAssaultNotificationScheduler {
    fun schedule(region: Region, assaultType: AssaultType): Completable
}