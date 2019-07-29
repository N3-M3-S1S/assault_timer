package com.rektapps.assaulttimer.service.impl

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.service.AssaultService
import com.rektapps.assaulttimer.storage.dao.AssaultDao
import com.rektapps.assaulttimer.service.AssaultsUpdater
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class AssaultServiceImpl @Inject constructor(
    private val assaultsUpdater: AssaultsUpdater,
    private val assaultDao: AssaultDao) : AssaultService {

    private lateinit var updateDisposable: Disposable

    override fun update(region: Region, assaultType: AssaultType) {
        if (::updateDisposable.isInitialized && !updateDisposable.isDisposed)
            updateDisposable.dispose()

        updateDisposable = assaultsUpdater.update(region, assaultType).subscribeOn(Schedulers.io()).subscribe()
    }

    override fun getAssaultById(assaultId: Long): Single<Assault> = assaultDao.getAssaultById(assaultId)

    override fun getAssaults(region: Region, assaultType: AssaultType): Flowable<List<Assault>> =
        assaultsUpdater.update(region, assaultType).andThen(assaultDao.getAssaults(region, assaultType))

}




