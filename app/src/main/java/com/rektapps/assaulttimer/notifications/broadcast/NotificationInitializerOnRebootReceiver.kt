package com.rektapps.assaulttimer.notifications.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rektapps.assaulttimer.App
import com.rektapps.assaulttimer.notifications.manager.AssaultNotificationScheduleManager
import com.rektapps.assaulttimer.storage.settings.AppConfig
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NotificationInitializerOnRebootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var assaultNotificationScheduleManager: AssaultNotificationScheduleManager

    @Inject
    lateinit var appConfig: AppConfig

    init {
        App.instance.appComponent.inject(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (appConfig.areNotificationsEnabled)
            assaultNotificationScheduleManager
                .scheduleNotificationsWithLastConfiguration()
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

}