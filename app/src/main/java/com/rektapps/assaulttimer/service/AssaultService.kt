package com.rektapps.assaulttimer.service

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import io.reactivex.Flowable
import io.reactivex.Single


interface AssaultService {
    fun getAssaults(region: Region, assaultType: AssaultType): Flowable<List<Assault>>
    fun getAssaultById(assaultId: Long): Single<Assault>
    fun update(region: Region, assaultType: AssaultType)
}