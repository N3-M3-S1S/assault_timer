package com.rektapps.assaulttimer.service

import com.rektapps.assaulttimer.model.entity.Assault
import io.reactivex.Single

interface ManualNotificationsService {
    fun toggleNotificationForAssault(assaultEntity: Assault): Single<Boolean>
    fun isAssaultSelectedForNotification(assaultEntity: Assault): Single<Boolean>
}