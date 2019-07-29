package com.rektapps.assaulttimer.dagger.module

import com.rektapps.assaulttimer.storage.settings.AppConfig
import com.rektapps.assaulttimer.time.formatter.AssaultDateTimeFormatter
import com.rektapps.assaulttimer.time.formatter.AssaultDateTimeFormatterImpl
import com.rektapps.assaulttimer.time.formatter.OnTimeFormatChangedListener
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object UtilsModule {

    @Provides
    @JvmStatic
    fun providesAssaultDateTimeFormatter(assaultDateTimeFormatterImpl: AssaultDateTimeFormatterImpl) = assaultDateTimeFormatterImpl as AssaultDateTimeFormatter

    @Provides
    @JvmStatic
    fun providesOnTimeFormatChangedListener(assaultDateTimeFormatterImpl: AssaultDateTimeFormatterImpl) = assaultDateTimeFormatterImpl as OnTimeFormatChangedListener

    @Provides
    @Singleton
    @JvmStatic
    fun providesAssaultDateTimeFormatterImpl(appConfig: AppConfig) = AssaultDateTimeFormatterImpl(appConfig.isAmPmTimeSelected)

}