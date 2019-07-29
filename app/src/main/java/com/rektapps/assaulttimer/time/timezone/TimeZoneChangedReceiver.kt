package com.rektapps.assaulttimer.time.timezone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rektapps.assaulttimer.App
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TimeZoneChangedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var timeZoneUpdater: TimeZoneUpdater

    init {
        App.instance.appComponent.inject(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        timeZoneUpdater.updateTimeZone()
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

}