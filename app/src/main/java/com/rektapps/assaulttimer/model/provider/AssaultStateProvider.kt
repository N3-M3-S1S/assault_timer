package com.rektapps.assaulttimer.model.provider

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState

interface AssaultStateProvider {
    fun getAssaultState(assaultEntity: Assault): AssaultState
}