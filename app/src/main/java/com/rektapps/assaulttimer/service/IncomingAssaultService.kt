package com.rektapps.assaulttimer.service

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import io.reactivex.Maybe
import io.reactivex.Single

interface IncomingAssaultService {
    fun getFirstIncomingAssault(region: Region, type: AssaultType): Single<Assault>
    fun getFirstIncomingManualNotificationAssault(region: Region, type: AssaultType): Maybe<Assault>
}