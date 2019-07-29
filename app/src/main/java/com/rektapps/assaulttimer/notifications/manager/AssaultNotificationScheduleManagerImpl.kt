package com.rektapps.assaulttimer.notifications.manager

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.notifications.scheduler.AssaultNotificationScheduler
import com.rektapps.assaulttimer.notifications.scheduler.NextIncomingAssaultNotificationScheduler
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import com.rektapps.assaulttimer.utils.doForEachRegionAndAssaultType
import io.reactivex.Completable
import javax.inject.Inject

class AssaultNotificationScheduleManagerImpl @Inject constructor(
    private val assaultNotificationScheduler: AssaultNotificationScheduler,
    private val notificationsConfig: NotificationsConfig,
    private val nextIncomingAssaultNotificationScheduler: NextIncomingAssaultNotificationScheduler
) : AssaultNotificationScheduleManager {


    override fun scheduleNotificationsWithLastConfiguration(): Completable {
        val completableList = mutableListOf<Completable>()

        when (notificationsConfig.notificationMode) {
            NotificationMode.AUTO -> {
                val automaticNotificationConfig = notificationsConfig.automaticNotificationModePreferences

                automaticNotificationConfig.forEach { entry: Map.Entry<Region, List<AssaultType>> ->
                    val (region, types) = entry
                    types.forEach { type ->
                        completableList.add(nextIncomingAssaultNotificationScheduler.schedule(region, type))
                    }
                }
            }

            NotificationMode.MANUAL -> {
                doForEachRegionAndAssaultType { region, type ->
                    completableList.add(nextIncomingAssaultNotificationScheduler.schedule(region, type))
                }
            }

        }
        return cancelNotifications().andThen(Completable.merge(completableList))
    }

    override fun cancelNotifications(): Completable =
        Completable.fromAction { assaultNotificationScheduler.cancelAll() }

}