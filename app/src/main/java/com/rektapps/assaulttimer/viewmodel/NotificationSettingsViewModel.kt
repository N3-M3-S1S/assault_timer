package com.rektapps.assaulttimer.viewmodel

import androidx.lifecycle.ViewModel
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.notifications.manager.AssaultNotificationScheduleManager
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NotificationSettingsViewModel @Inject constructor(
    private val notificationsConfig: NotificationsConfig,
    private val notificationScheduleManager: AssaultNotificationScheduleManager
) : ViewModel() {

    private lateinit var notificationDisposable: Disposable

    fun setNotificationsEnabled(enabled: Boolean) {
        val notificationEnabledCompletable = if (enabled)
            notificationScheduleManager.scheduleNotificationsWithLastConfiguration()
        else
            notificationScheduleManager.cancelNotifications()

        subscribeToNotificationCompletable(notificationEnabledCompletable)
    }

    fun setNotificationMode(notificationMode: NotificationMode) {
        if(notificationMode != notificationsConfig.notificationMode) {
            notificationsConfig.notificationMode = notificationMode
            subscribeToNotificationCompletable(notificationScheduleManager.scheduleNotificationsWithLastConfiguration())
        }
    }

    fun getLastSelectedAutomaticNotificationPreferences() = notificationsConfig.automaticNotificationModePreferences

    fun getLastSelectedNotificationMode() = notificationsConfig.notificationMode

    fun setSelectedAutomaticNotificationPreferences(newPreferences: Map<Region, List<AssaultType>>) {
        if(newPreferences != notificationsConfig.automaticNotificationModePreferences) {
            notificationsConfig.automaticNotificationModePreferences = newPreferences
            if (notificationsConfig.notificationMode == NotificationMode.AUTO)
                subscribeToNotificationCompletable(notificationScheduleManager.scheduleNotificationsWithLastConfiguration())
        }
    }

    private fun subscribeToNotificationCompletable(completable: Completable) {
        if (::notificationDisposable.isInitialized && !notificationDisposable.isDisposed)
            notificationDisposable.dispose()

        notificationDisposable = completable
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

}