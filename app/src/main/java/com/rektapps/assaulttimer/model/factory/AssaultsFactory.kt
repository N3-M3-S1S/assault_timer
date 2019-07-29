package com.rektapps.assaulttimer.model.factory

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import io.reactivex.Single
import org.joda.time.DateTime

interface AssaultsFactory {
    fun createAssaults(since: DateTime, region: Region, type: AssaultType, count: Int): Single<List<Assault>>
}