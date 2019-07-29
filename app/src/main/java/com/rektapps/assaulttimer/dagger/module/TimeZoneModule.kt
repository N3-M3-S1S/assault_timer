package com.rektapps.assaulttimer.dagger.module

import com.rektapps.assaulttimer.time.timezone.TimeZoneIDProvider
import com.rektapps.assaulttimer.time.timezone.TimeZoneUpdater
import com.rektapps.assaulttimer.time.timezone.impl.TimeZoneIDProviderImpl
import com.rektapps.assaulttimer.time.timezone.impl.TimeZoneUpdaterImpl
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface TimeZoneModule {

    @Binds
    fun providesTimeZoneUpdater(timeZoneChangedListenerImpl: TimeZoneUpdaterImpl): TimeZoneUpdater

    @Binds
    @Reusable
    fun providesTimeZoneIDProvider(timeZoneIDProviderImpl: TimeZoneIDProviderImpl): TimeZoneIDProvider

}