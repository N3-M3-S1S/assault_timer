package com.rektapps.assaulttimer.dagger.module

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.factory.AssaultIntervalsFactory
import com.rektapps.assaulttimer.model.factory.AssaultsFactory
import com.rektapps.assaulttimer.model.factory.impl.AssaultIntervalsFactoryImpl
import com.rektapps.assaulttimer.model.factory.impl.AssaultsFactoryImpl
import com.rektapps.assaulttimer.storage.dao.AssaultDao
import dagger.Module
import dagger.Provides
import org.joda.time.Duration

@Module
object FactoryModule {

    @Provides
    @JvmStatic
    fun providesAssaultsFactory(assaultsDao: AssaultDao): AssaultsFactory =
        AssaultsFactoryImpl(assaultsDao, createIntervalFactories())

    private fun createIntervalFactories(): Map<AssaultType, AssaultIntervalsFactory> {
        val bfaAssaultDuration = Duration.standardHours(7)
        val bfaBreakBetweenAssaults = Duration.standardHours(12)
        val legionAssaultDuration = Duration.standardHours(6)
        val legionBreakBetweenAssaults = Duration.standardHours(12).plus(Duration.standardMinutes(30))
        val factories = mutableMapOf<AssaultType, AssaultIntervalsFactory>()

        factories[AssaultType.BFA] = AssaultIntervalsFactoryImpl(
            bfaAssaultDuration,
            bfaBreakBetweenAssaults
        )
        factories[AssaultType.LEGION] = AssaultIntervalsFactoryImpl(
            legionAssaultDuration,
            legionBreakBetweenAssaults
        )

        return factories
    }

}