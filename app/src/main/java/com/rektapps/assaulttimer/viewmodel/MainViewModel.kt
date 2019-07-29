package com.rektapps.assaulttimer.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.rektapps.assaulttimer.notifications.manager.AssaultNotificationScheduleManager
import com.rektapps.assaulttimer.storage.settings.AppConfig
import com.rektapps.assaulttimer.time.timezone.TimeZoneIDProvider
import com.rektapps.assaulttimer.time.timezone.TimeZoneUpdater
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val appConfig: AppConfig,
    private val timeZoneIDProvider: TimeZoneIDProvider,
    timeZoneUpdater: TimeZoneUpdater,
    assaultNotificationScheduleManager: AssaultNotificationScheduleManager
) : ViewModel(), LifecycleObserver {

    init {
        if (appConfig.lastTimeZoneID.isNullOrEmpty())
            appConfig.lastTimeZoneID = timeZoneIDProvider.getCurrentTimeZoneID()
        else {
            var initializationCompletable: Completable? = null

            if (isTimeZoneChanged())
                initializationCompletable = timeZoneUpdater.updateTimeZone()
            else
                if (appConfig.areNotificationsEnabled)
                    initializationCompletable =
                        assaultNotificationScheduleManager.scheduleNotificationsWithLastConfiguration()

            initializationCompletable?.subscribeOn(Schedulers.io())?.subscribe()
        }
    }

    fun isDarkThemeSelected() = appConfig.isDarkThemeSelected

    private fun isTimeZoneChanged(): Boolean {
        return appConfig.lastTimeZoneID != timeZoneIDProvider.getCurrentTimeZoneID()
    }

}