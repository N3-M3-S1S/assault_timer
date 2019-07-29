package com.rektapps.assaulttimer.notifications.scheduler.impl

import android.app.AlarmManager
import androidx.core.app.AlarmManagerCompat
import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.notifications.broadcast.NotificationPendingIntentCreator
import com.rektapps.assaulttimer.notifications.generator.NotificationIntentRequestCodeGenerator
import com.rektapps.assaulttimer.notifications.scheduler.AssaultNotificationScheduler
import com.rektapps.assaulttimer.storage.settings.NotificationIntentRequestCodeStorage
import org.joda.time.DateTime
import org.joda.time.Duration
import javax.inject.Inject

class AlarmManagerNotificationScheduler @Inject constructor(
    private val alarmManager: AlarmManager,
    private val notificationIntentRequestCodeStorage: NotificationIntentRequestCodeStorage,
    private val notificationPendingIntentCreator: NotificationPendingIntentCreator,
    private val notificationIntentRequestCodeGenerator: NotificationIntentRequestCodeGenerator
) : AssaultNotificationScheduler {


    override fun schedule(assaultEntity: Assault) {
        val notificationIntentRequestCode = notificationIntentRequestCodeGenerator.getRequestCodeForAssault(assaultEntity)
        val notificationPendingIntent = notificationPendingIntentCreator.createNotificationPendingIntent(notificationIntentRequestCode, assaultEntity.id!!)
        val delay = Duration(DateTime.now(), assaultEntity.start).plus(System.currentTimeMillis()).millis

        notificationIntentRequestCodeStorage.save(notificationIntentRequestCode)

        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, delay, notificationPendingIntent)
    }


    override fun cancel(assaultEntity: Assault) {
        val notificationPendingIntentRequestCode = notificationIntentRequestCodeGenerator.getRequestCodeForAssault(assaultEntity)
        val notificationPendingIntent = notificationPendingIntentCreator.createNotificationPendingIntent(notificationPendingIntentRequestCode)

        notificationPendingIntent.cancel()
        alarmManager.cancel(notificationPendingIntent)
        notificationIntentRequestCodeStorage.delete(notificationPendingIntentRequestCode)
    }

    override fun cancelAll() {
        for (notificationId in notificationIntentRequestCodeStorage.getAll()) {
            val pendingIntent = notificationPendingIntentCreator.createNotificationPendingIntent(notificationId)
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
        }
        notificationIntentRequestCodeStorage.deleteAll()
    }

}