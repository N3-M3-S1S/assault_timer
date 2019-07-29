package com.rektapps.assaulttimer.time.timezone.impl

import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.notifications.manager.AssaultNotificationScheduleManager
import com.rektapps.assaulttimer.service.AssaultsUpdater
import com.rektapps.assaulttimer.storage.settings.AppConfig
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import com.rektapps.assaulttimer.time.timezone.TimeZoneIDProvider
import com.rektapps.assaulttimer.time.timezone.TimeZoneUpdater
import io.reactivex.Completable
import javax.inject.Inject

class TimeZoneUpdaterImpl @Inject constructor(
    private val assaultsUpdater: AssaultsUpdater,
    private val assaultNotificationScheduleManager: AssaultNotificationScheduleManager,
    private val appConfig: AppConfig,
    private val timeZoneIDProvider: TimeZoneIDProvider,
    private val notificationsConfig: NotificationsConfig
) : TimeZoneUpdater {

    override fun updateTimeZone(): Completable {

        return if (appConfig.areNotificationsEnabled) {
            val completableList = mutableListOf<Completable>()
            completableList.add(assaultNotificationScheduleManager.cancelNotifications())
            completableList.add(assaultsUpdater.recreateAllAssaults())

            if (notificationsConfig.notificationMode == NotificationMode.AUTO)
                completableList.add(assaultNotificationScheduleManager.scheduleNotificationsWithLastConfiguration())
            Completable.concat(completableList)
        } else {
            assaultsUpdater.recreateAllAssaults()
        }
            .doFinally { appConfig.lastTimeZoneID = timeZoneIDProvider.getCurrentTimeZoneID() }
    }

}