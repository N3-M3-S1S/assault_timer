package com.rektapps.assaulttimer.di.module


import com.rektapps.assaulttimer.di.key.AssaultTypeKey
import com.rektapps.assaulttimer.model.AssaultIntervalsFactory
import com.rektapps.assaulttimer.model.AssaultType
import com.rektapps.assaulttimer.model.Region
import com.rektapps.assaulttimer.model.impl.AssaultIntervalsFactoryImpl
import com.rektapps.assaulttimer.storage.AssaultEntity
import com.rektapps.assaulttimer.storage.AssaultRepository
import com.rektapps.assaulttimer.storage.AssaultRepositoryInitializer
import com.rektapps.assaulttimer.storage.RepositoryInitializerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Completable
import io.reactivex.Single
import org.joda.time.DateTime
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

        @Provides
        @Singleton
        @JvmStatic
        fun providesAssaultRepository() : AssaultRepository{
            val testDate = DateTime.now().minusSeconds(5)
            val testEntity = AssaultEntity(0, testDate,testDate, Region.EU, AssaultType.LEGION)

            val timeSlot = slot<DateTime>()
            val mockedAssaultRepository = mockk<AssaultRepository>(relaxed = true)

            every { mockedAssaultRepository.getLastAssault(any(),any())
                    mockedAssaultRepository.getLastAssault(any(),any())
            } returns Single.just(testEntity)
            every { mockedAssaultRepository.getAssaultsBeforeDate(any(),any(),any()) } returns Single.just(listOf(testEntity))
            every { mockedAssaultRepository.deleteAndCreateAssaults(any(),any(),any(),any(),any()) } answers  {

                Completable.complete()
            }


            return mockedAssaultRepository
        }
    }







    @Binds
    abstract fun providesRepositoryInitializer(repositoryInitializer: RepositoryInitializerImpl): AssaultRepositoryInitializer

}