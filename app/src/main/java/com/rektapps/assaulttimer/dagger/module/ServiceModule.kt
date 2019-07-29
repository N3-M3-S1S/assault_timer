package com.rektapps.assaulttimer.dagger.module


import com.rektapps.assaulttimer.service.AssaultService
import com.rektapps.assaulttimer.service.AssaultsUpdater
import com.rektapps.assaulttimer.service.IncomingAssaultService
import com.rektapps.assaulttimer.service.ManualNotificationsService
import com.rektapps.assaulttimer.service.impl.AssaultServiceImpl
import com.rektapps.assaulttimer.service.impl.AssaultsUpdaterImpl
import com.rektapps.assaulttimer.service.impl.IncomingAssaultServiceImpl
import com.rektapps.assaulttimer.service.impl.ManualNotificationsServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Reusable
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class, FactoryModule::class])
interface ServiceModule{

    @Binds
    @Singleton
    fun providesAssaultService(assaultRepositoryImpl: AssaultServiceImpl) : AssaultService

    @Binds
    @Reusable
    fun providesManualNotificationService(notificationRepositoryImpl: ManualNotificationsServiceImpl): ManualNotificationsService

    @Binds
    @Reusable
    fun providesAssaultsUpdater(assaultsUpdaterImpl: AssaultsUpdaterImpl): AssaultsUpdater

    @Binds
    fun providesIncomingAssaultService(incomingAssaultServiceImpl: IncomingAssaultServiceImpl): IncomingAssaultService
}