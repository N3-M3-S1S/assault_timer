package com.rektapps.assaulttimer.notifications.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.rektapps.assaulttimer.App
import com.rektapps.assaulttimer.notifications.scheduler.NextIncomingAssaultNotificationScheduler
import com.rektapps.assaulttimer.notifications.sender.NotificationSender
import com.rektapps.assaulttimer.service.AssaultService
import com.rektapps.assaulttimer.storage.settings.NotificationIntentRequestCodeStorage
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

const val ASSAULT_ID_KEY = "assaultId"
const val NOTIFICATION_INTENT_ID_KEY = "notificationId"

class NotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var assaultService: AssaultService

    @Inject
    lateinit var notificationSender: NotificationSender

    @Inject
    lateinit var nextIncomingAssaultNotificationScheduler: NextIncomingAssaultNotificationScheduler

    @Inject
    lateinit var notificationIntentRequestCodeStorage: NotificationIntentRequestCodeStorage


    init {
        App.instance.appComponent.inject(this)
    }

    @SuppressLint("CheckResult")
    override fun onReceive(context: Context?, intent: Intent?) {
        val assaultId = intent!!.getLongExtra(ASSAULT_ID_KEY, -1)
        assaultService.getAssaultById(assaultId)
            .flatMap { assault ->
                nextIncomingAssaultNotificationScheduler
                    .schedule(assault.region, assault.type)
                    .doOnComplete {
                        val notificationIntentId = intent.getIntExtra(NOTIFICATION_INTENT_ID_KEY, -1)
                        notificationIntentRequestCodeStorage.delete(notificationIntentId)
                    }
                    .andThen(Single.just(assault))
            }
            .subscribeOn(Schedulers.io())

            .subscribeBy(
                onError = { Log.e("Notification", it.localizedMessage) },
                onSuccess = { notificationSender.sendAssaultStartedNotification(it) }
            )
    }


}