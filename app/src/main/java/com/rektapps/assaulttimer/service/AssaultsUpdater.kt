package com.rektapps.assaulttimer.service

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import io.reactivex.Completable

interface AssaultsUpdater {
    fun update(region: Region, assaultType: AssaultType): Completable
    fun recreateAllAssaults(): Completable
}