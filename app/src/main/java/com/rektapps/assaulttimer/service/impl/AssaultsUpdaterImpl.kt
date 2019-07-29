package com.rektapps.assaulttimer.service.impl

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.model.factory.AssaultsFactory
import com.rektapps.assaulttimer.service.AssaultsUpdater
import com.rektapps.assaulttimer.storage.dao.AssaultDao
import com.rektapps.assaulttimer.utils.doForEachRegionAndAssaultType
import com.rektapps.assaulttimer.utils.getNowDateTimeWithRoundedSeconds
import io.reactivex.Completable
import org.joda.time.DateTime
import javax.inject.Inject

class AssaultsUpdaterImpl @Inject constructor(
    private val assaultDao: AssaultDao,
    private val assaultsFactory: AssaultsFactory
) : AssaultsUpdater {

    private val assaultsCountInSchedule = 10
    private val cachedUpdates = mutableMapOf<Pair<Region, AssaultType>, Completable>()

    override fun update(region: Region, assaultType: AssaultType): Completable {
        val key = region to assaultType

        return cachedUpdates.getOrElse(key) {
            val updateCompletable = assaultDao.hasAssaults(region, assaultType)
                .flatMapCompletable { hasAssaults ->
                    if (hasAssaults)
                        deleteEndedAssaultsAndCreateNew(region, assaultType)
                    else
                        createAndAddAssaultsFromNow(region, assaultType)
                }
                .doFinally { cachedUpdates.remove(key) }
                .cache()

            cachedUpdates[key] = updateCompletable
            updateCompletable
        }
    }


    override fun recreateAllAssaults(): Completable {
        val completableList = mutableListOf<Completable>()
        doForEachRegionAndAssaultType { region, assaultType ->
            val key = region to assaultType
            val hasAssaultsSingle = assaultDao.hasAssaults(region, assaultType)
            val recreateIfHasAssaults = hasAssaultsSingle.flatMapCompletable { hasAssaults ->
                if (hasAssaults)
                    assaultDao.dropTable(region, assaultType).andThen(createAndAddAssaultsFromNow(region, assaultType))
                else
                    Completable.complete()

            }
                .doFinally { cachedUpdates.remove(key) }
                .cache()

            cachedUpdates[key] = recreateIfHasAssaults
            completableList.add(recreateIfHasAssaults)
        }

        return Completable.merge(completableList)
    }

    private fun deleteEndedAssaultsAndCreateNew(region: Region, type: AssaultType): Completable {
        val now = getNowDateTimeWithRoundedSeconds()
        return assaultDao.getAssaultsWithLessOrEqualEndDateTime(now, region, type)

            .flatMapCompletable { endedAssaults ->
                if (endedAssaults.isEmpty())
                    Completable.complete()
                else
                    assaultsFactory
                        .createAssaults(now, region, type, endedAssaults.size)
                        .flatMapCompletable {
                            Completable.fromAction {
                                assaultDao.deleteAndAddTransaction(
                                    endedAssaults,
                                    it
                                )
                            }
                        }
            }
    }

    private fun createAndAddAssaultsFromNow(region: Region, type: AssaultType) =
        assaultsFactory
            .createAssaults(DateTime.now(), region, type, assaultsCountInSchedule)
            .flatMapCompletable { assaultDao.addAll(it) }

}


