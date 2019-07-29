package com.rektapps.assaulttimer.view.assaultsSchedule.mapper

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.view.assaultsSchedule.AssaultListItem
import io.reactivex.Single

interface AssaultListItemMapper {
    fun mapAssaultsToAssaultListItems(assaults: List<Assault>): Single<List<AssaultListItem>>
}