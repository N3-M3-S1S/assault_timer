package com.rektapps.assaulttimer.service.impl

import androidx.room.EmptyResultSetException
import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.entity.ManualNotification
import com.rektapps.assaulttimer.notifications.scheduler.AssaultNotificationScheduler
import com.rektapps.assaulttimer.service.ManualNotificationsService
import com.rektapps.assaulttimer.storage.dao.ManualNotificationDao
import io.reactivex.Single
import javax.inject.Inject

class ManualNotificationsServiceImpl @Inject constructor(
    private val manualNotificationDao: ManualNotificationDao,
    private val assaultNotificationScheduler: AssaultNotificationScheduler
) : ManualNotificationsService {


    override fun toggleNotificationForAssault(assaultEntity: Assault): Single<Boolean> {
        return isAssaultSelectedForNotification(assaultEntity).flatMap {
            val assaultId = assaultEntity.id!!
            if (it) {
                assaultNotificationScheduler.cancel(assaultEntity)
                manualNotificationDao.deleteByAssaultId(assaultId)
            } else {
                assaultNotificationScheduler.schedule(assaultEntity)
                manualNotificationDao.create(ManualNotification(assaultId))
            }
                .andThen(Single.just(!it))
        }
    }

    override fun isAssaultSelectedForNotification(assaultEntity: Assault): Single<Boolean> {
        return manualNotificationDao.getByAssaultId(assaultEntity.id!!)
            .flatMap { Single.just(true) }
            .onErrorReturn {
                if (it is EmptyResultSetException)
                    false
                else
                    throw (it)
            }
    }


}


