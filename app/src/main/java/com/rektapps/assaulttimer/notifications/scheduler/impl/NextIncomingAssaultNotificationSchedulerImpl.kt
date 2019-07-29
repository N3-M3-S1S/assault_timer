package com.rektapps.assaulttimer.notifications.scheduler.impl

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.notifications.scheduler.AssaultNotificationScheduler
import com.rektapps.assaulttimer.notifications.scheduler.NextIncomingAssaultNotificationScheduler
import com.rektapps.assaulttimer.service.IncomingAssaultService
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import io.reactivex.Completable
import javax.inject.Inject

class NextIncomingAssaultNotificationSchedulerImpl @Inject constructor(
    private val incomingAssaultService: IncomingAssaultService,
    private val assaultNotificationScheduler: AssaultNotificationScheduler,
    private val notificationsConfig: NotificationsConfig
) : NextIncomingAssaultNotificationScheduler {

    override fun schedule(region: Region, assaultType: AssaultType): Completable {
        return when(notificationsConfig.notificationMode){
            NotificationMode.AUTO -> {
                incomingAssaultService.getFirstIncomingAssault(region, assaultType)
                    .flatMapCompletable { scheduleAssault(it) } }

            NotificationMode.MANUAL -> {
                incomingAssaultService.getFirstIncomingManualNotificationAssault(region, assaultType)
                    .flatMapCompletable { scheduleAssault(it) }
            }
        }
    }

    private fun scheduleAssault(assault: Assault) = Completable.fromAction { assaultNotificationScheduler.schedule(assault) }
}