package com.rektapps.assaulttimer.notifications.broadcast.impl

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rektapps.assaulttimer.notifications.broadcast.ASSAULT_ID_KEY
import com.rektapps.assaulttimer.notifications.broadcast.NOTIFICATION_INTENT_ID_KEY
import com.rektapps.assaulttimer.notifications.broadcast.NotificationBroadcastReceiver
import com.rektapps.assaulttimer.notifications.broadcast.NotificationPendingIntentCreator
import javax.inject.Inject

class NotificationPendingIntentCreatorImpl @Inject constructor(private val context: Context) :
    NotificationPendingIntentCreator {

    override fun createNotificationPendingIntent(requestCode: Int, assaultID: Long?): PendingIntent {
        val notificationBroadcastReceiverIntent = Intent(context, NotificationBroadcastReceiver::class.java)
        assaultID?.let { notificationBroadcastReceiverIntent.putExtra(ASSAULT_ID_KEY, it) }
        notificationBroadcastReceiverIntent.putExtra(NOTIFICATION_INTENT_ID_KEY, requestCode)
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            notificationBroadcastReceiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

}