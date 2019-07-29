package com.rektapps.assaulttimer.model.factory.impl

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.model.factory.AssaultIntervalsFactory
import com.rektapps.assaulttimer.model.factory.AssaultsFactory
import com.rektapps.assaulttimer.storage.dao.AssaultDao
import io.reactivex.Single
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import javax.inject.Inject

class AssaultsFactoryImpl @Inject constructor(private val assaultDao: AssaultDao,
                                              private val intervalsFactories: Map<AssaultType, @JvmSuppressWildcards AssaultIntervalsFactory>
): AssaultsFactory {

    override fun createAssaults(since: DateTime, region: Region, type: AssaultType, count: Int): Single<List<Assault>> {
        return getLastAssaultEndDateTime(region, type)
            .map { endDateTime ->
                intervalsFactories.getValue(type).createAssaultIntervals(
                    since,
                    endDateTime,
                    count
                )
            }
            .map { intervals ->
                intervals.map {
                    Assault(
                        it.start,
                        it.end,
                        region,
                        type
                    )
                }
            }
    }


    private fun getLastAssaultEndDateTime(region: Region, assaultType: AssaultType) =
        assaultDao.getLastAssaultEndDateTime(region, assaultType)
            .onErrorReturn { getDefaultAssaultEndDateTime(region, assaultType) }



    private fun getDefaultAssaultEndDateTime(region: Region, assaultType: AssaultType): DateTime {
        val moscowTimeZone = DateTimeZone.forID("Europe/Moscow")

        val dateTime = when (region) {
            Region.EU -> when (assaultType) {
                AssaultType.LEGION -> DateTime(2019, 4, 24, 3, 30,moscowTimeZone)
                AssaultType.BFA -> DateTime(2019, 4, 24, 12, 0, moscowTimeZone)
            }

            Region.NA -> when (assaultType) {
                AssaultType.LEGION -> DateTime(2019, 4, 24, 11, 30, moscowTimeZone)
                AssaultType.BFA -> DateTime(2019, 4, 24, 2, 0, moscowTimeZone)
            }
        }
        return dateTime.withZone(DateTimeZone.getDefault())
    }


}