package com.rektapps.assaulttimer.di.module

import androidx.work.WorkManager
import com.rektapps.assaulttimer.App
import com.rektapps.assaulttimer.di.component.WorkerSubComponent
import com.rektapps.assaulttimer.di.key.AssaultTypeKey
import com.rektapps.assaulttimer.model.AssaultIntervalsFactory
import com.rektapps.assaulttimer.model.AssaultType
import com.rektapps.assaulttimer.model.impl.AssaultIntervalsFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import org.joda.time.Duration
import javax.inject.Singleton

@Module(subcomponents = [WorkerSubComponent::class])
class AppModule {

    @Provides
    @Singleton
    fun providesWorkManager() = WorkManager.getInstance()

    @Provides
    @Singleton
    fun providesAppContext(app:App) = app.applicationContext


}