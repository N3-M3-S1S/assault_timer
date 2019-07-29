package com.rektapps.assaulttimer.service.impl

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.model.provider.AssaultStateProvider
import com.rektapps.assaulttimer.service.AssaultsUpdater
import com.rektapps.assaulttimer.service.IncomingAssaultService
import com.rektapps.assaulttimer.storage.dao.AssaultDao
import com.rektapps.assaulttimer.storage.dao.ManualNotificationDao
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class IncomingAssaultServiceImpl @Inject constructor(
    private val assaultsUpdater: AssaultsUpdater,
    private val manualNotificationDao: ManualNotificationDao,
    private val assaultDao: AssaultDao,
    private val assaultStateProvider: AssaultStateProvider
) : IncomingAssaultService {

    override fun getFirstIncomingManualNotificationAssault(region: Region, type: AssaultType): Maybe<Assault> {
        return manualNotificationDao.getSelectedAssault(region, type)
            .flatMapMaybe { assaultsWithNotification ->
                Maybe.just(assaultsWithNotification.getFirstIncomingAssault(assaultStateProvider))
            }
            .onErrorComplete { it is NoSuchElementException }
    }

    override fun getFirstIncomingAssault(region: Region, type: AssaultType): Single<Assault> {
        return assaultsUpdater.update(region, type)
            .andThen(assaultDao.getAssaults(region, type))
            .firstOrError()
            .flatMap { assaults -> Single.just(assaults.getFirstIncomingAssault(assaultStateProvider)) }
    }

    private fun List<Assault>.getFirstIncomingAssault(assaultStateProvider: AssaultStateProvider) =
        first { assaultStateProvider.getAssaultState(it) == AssaultState.INCOMING }

}







