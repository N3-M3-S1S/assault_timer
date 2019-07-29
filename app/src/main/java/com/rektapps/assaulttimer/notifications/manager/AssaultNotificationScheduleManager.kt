package com.rektapps.assaulttimer.notifications.manager

import io.reactivex.Completable

interface AssaultNotificationScheduleManager{
    fun scheduleNotificationsWithLastConfiguration(): Completable
    fun cancelNotifications(): Completable
}



