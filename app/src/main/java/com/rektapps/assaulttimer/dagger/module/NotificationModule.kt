package com.rektapps.assaulttimer.dagger.module

import com.rektapps.assaulttimer.notifications.broadcast.NotificationPendingIntentCreator
import com.rektapps.assaulttimer.notifications.broadcast.impl.NotificationPendingIntentCreatorImpl
import com.rektapps.assaulttimer.notifications.composer.AssaultNotificationComposer
import com.rektapps.assaulttimer.notifications.composer.AssaultNotificationComposerImpl
import com.rektapps.assaulttimer.notifications.generator.NotificationIntentRequestCodeGenerator
import com.rektapps.assaulttimer.notifications.generator.NotificationIntentRequestCodeGeneratorImpl
import com.rektapps.assaulttimer.notifications.manager.AssaultNotificationScheduleManager
import com.rektapps.assaulttimer.notifications.manager.AssaultNotificationScheduleManagerImpl
import com.rektapps.assaulttimer.notifications.scheduler.AssaultNotificationScheduler
import com.rektapps.assaulttimer.notifications.scheduler.NextIncomingAssaultNotificationScheduler
import com.rektapps.assaulttimer.notifications.scheduler.impl.AlarmManagerNotificationScheduler
import com.rektapps.assaulttimer.notifications.scheduler.impl.NextIncomingAssaultNotificationSchedulerImpl
import com.rektapps.assaulttimer.notifications.sender.NotificationSender
import com.rektapps.assaulttimer.notifications.sender.NotificationSenderImpl
import dagger.Binds
import dagger.Module
import dagger.Reusable


@Module
interface NotificationModule {

    @Binds
    @Reusable
    fun providesAssaultNotificationScheduleManager(assaultNotificationScheduleManagerImpl: AssaultNotificationScheduleManagerImpl): AssaultNotificationScheduleManager

    @Binds
    @Reusable
    fun providesNotificationScheduler(assaultStartNotificationNotificationSchedulerImpl: AlarmManagerNotificationScheduler): AssaultNotificationScheduler

    @Binds
    fun providesNotificationIntentRequestCodeGenerator(notificationIntentRequestCodeGeneratorImpl: NotificationIntentRequestCodeGeneratorImpl): NotificationIntentRequestCodeGenerator

    @Binds
    fun providesNotificationMessageComposer(notificationMessageComposer: AssaultNotificationComposerImpl): AssaultNotificationComposer

    @Binds
    fun providesNotificationPendingIntentCreator(notificationPendingIntentCreatorImpl: NotificationPendingIntentCreatorImpl): NotificationPendingIntentCreator

    @Binds
    fun providesNotificationSender(notificationSender: NotificationSenderImpl): NotificationSender

    @Binds
    fun providesFirstIncomingAssaultStartNotificationScheduler(nextAssaultStartNotificationSchedulerImpl: NextIncomingAssaultNotificationSchedulerImpl): NextIncomingAssaultNotificationScheduler

}