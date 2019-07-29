package com.rektapps.assaulttimer.notifications.sender

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.notifications.composer.AssaultNotificationComposer
import com.rektapps.assaulttimer.view.MainActivity
import javax.inject.Inject

const val NOTIFICATION_REGION_ID_KEY = "notificationRegion"
const val NOTIFICATION_ASSAULT_TYPE_ID_KEY = "notificationAssaultType"

class NotificationSenderImpl @Inject constructor(private val context: Context,
                                                 private val notificationComposer: AssaultNotificationComposer): NotificationSender {

    override fun sendAssaultStartedNotification(startedAssault: Assault) {
        val notificationMessage = notificationComposer.compose(startedAssault)

        val pendingIntent = PendingIntent.getActivity(context, startedAssault.id!!.toInt(), createNotificationIntent(startedAssault), PendingIntent.FLAG_ONE_SHOT)

        val notification =  with(NotificationCompat.Builder(context, context.getString(R.string.notificationChannelId))){
            setSmallIcon(R.drawable.notification_icon)
            setContentTitle(notificationMessage.title)
            setContentText(notificationMessage.message)
            setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage.message))
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            setDefaults(Notification.DEFAULT_ALL)
            priority = NotificationCompat.PRIORITY_DEFAULT
            build()
        }

        NotificationManagerCompat.from(context).notify(startedAssault.id.toInt(), notification)
    }


    private fun createNotificationIntent(startedAssault: Assault) = Intent(context, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        putExtra(NOTIFICATION_REGION_ID_KEY, startedAssault.region.getId())
        putExtra(NOTIFICATION_ASSAULT_TYPE_ID_KEY, startedAssault.type.getId())
    }
}