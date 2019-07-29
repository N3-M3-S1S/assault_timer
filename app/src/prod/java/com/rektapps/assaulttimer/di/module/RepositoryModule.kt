package com.rektapps.assaulttimer.di.module


import com.rektapps.assaulttimer.di.key.AssaultTypeKey
import com.rektapps.assaulttimer.model.AssaultIntervalsFactory
import com.rektapps.assaulttimer.model.AssaultType
import com.rektapps.assaulttimer.model.impl.AssaultIntervalsFactoryImpl
import com.rektapps.assaulttimer.storage.AssaultRepository
import com.rektapps.assaulttimer.storage.AssaultRepositoryImpl
import com.rektapps.assaulttimer.storage.AssaultRepositoryInitializer
import com.rektapps.assaulttimer.storage.RepositoryInitializerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import org.joda.time.Duration

import javax.inject.Singleton

@Module
abstract class RepositoryModule{
    @Module
    companion object {
        @JvmStatic
        @Provides
        @IntoMap
        @AssaultTypeKey(AssaultType.LEGION)
        @Singleton
        fun providesLegionAssaultIntervalFactory(): AssaultIntervalsFactory = AssaultIntervalsFactoryImpl(Duration.standardHours(1), Duration.standardHours(2))

        @JvmStatic
        @Provides
        @IntoMap
        @AssaultTypeKey(AssaultType.BFA)
        @Singleton
        fun providesBfa(): AssaultIntervalsFactory = AssaultIntervalsFactoryImpl(Duration.standardHours(1), Duration.standardHours(2))
    }

    @Binds
    @Singleton
    abstract fun providesAssaultRepository(assaultRepositoryImpl: AssaultRepositoryImpl) : AssaultRepository




    @Binds
    abstract fun providesRepositoryInitializer(repositoryInitializer: RepositoryInitializerImpl): AssaultRepositoryInitializer

}