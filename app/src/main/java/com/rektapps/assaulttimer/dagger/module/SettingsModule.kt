package com.rektapps.assaulttimer.dagger.module

import com.rektapps.assaulttimer.storage.settings.AppConfig
import com.rektapps.assaulttimer.storage.settings.AutomaticNotificationSettingsConverter
import com.rektapps.assaulttimer.storage.settings.NotificationIntentRequestCodeStorage
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import com.rektapps.assaulttimer.storage.settings.impl.AppConfigImpl
import com.rektapps.assaulttimer.storage.settings.impl.AutomaticNotificationSettingsConverterImpl
import com.rektapps.assaulttimer.storage.settings.impl.NotificationIntentRequestCodeStorageImpl
import com.rektapps.assaulttimer.storage.settings.impl.NotificationsConfigImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SettingsModule {

    @Binds
    @Singleton
    fun providesAppConfig(appSettingsStorageImpl: AppConfigImpl): AppConfig

    @Binds
    @Singleton
    fun providesNotificationsConfig(notificationsConfigImpl: NotificationsConfigImpl): NotificationsConfig

    @Binds
    fun providesAutomaticNoificationSettingsConverter(automaticNotificationSettingsConverterImpl: AutomaticNotificationSettingsConverterImpl): AutomaticNotificationSettingsConverter

    @Binds
    fun providesNotificationIntentRequestCodeStorage(notificationIntentRequestCodeStorage: NotificationIntentRequestCodeStorageImpl): NotificationIntentRequestCodeStorage

}