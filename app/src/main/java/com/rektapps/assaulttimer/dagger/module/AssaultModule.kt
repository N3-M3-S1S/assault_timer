package com.rektapps.assaulttimer.dagger.module

import com.rektapps.assaulttimer.view.assaultsSchedule.mapper.AssaultListItemMapper
import com.rektapps.assaulttimer.view.assaultsSchedule.mapper.AssaultListItemMapperImpl
import com.rektapps.assaulttimer.model.provider.AssaultStateDurationProvider
import com.rektapps.assaulttimer.model.provider.AssaultStateProvider
import com.rektapps.assaulttimer.model.provider.impl.AssaultStateDurationProviderImpl
import com.rektapps.assaulttimer.model.provider.impl.AssaultStateProviderImpl
import com.rektapps.assaulttimer.model.timer.AssaultDurationTimer
import com.rektapps.assaulttimer.model.timer.AssaultDurationTimerImpl
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface AssaultModule {

    @Binds
    @Reusable
    fun providesAssaultStateProvider(assaultStateProviderImpl: AssaultStateProviderImpl): AssaultStateProvider

    @Binds
    fun providesAssaultStateDurationProvider(assaultStateDurationProviderImpl: AssaultStateDurationProviderImpl): AssaultStateDurationProvider

    @Binds
    fun providesAssaultDurationTimer(assaultDurationTimerImpl: AssaultDurationTimerImpl): AssaultDurationTimer

    @Binds
    fun providesAssaultListItemMapper(assaultListItemMapperImpl: AssaultListItemMapperImpl): AssaultListItemMapper

}