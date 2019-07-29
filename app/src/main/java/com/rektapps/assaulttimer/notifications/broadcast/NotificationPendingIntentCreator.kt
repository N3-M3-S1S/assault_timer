package com.rektapps.assaulttimer.notifications.broadcast

import android.app.PendingIntent

interface NotificationPendingIntentCreator {
    fun createNotificationPendingIntent(requestCode: Int, assaultID: Long? = null): PendingIntent
}