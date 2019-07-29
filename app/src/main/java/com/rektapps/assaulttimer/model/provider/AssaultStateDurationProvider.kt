package com.rektapps.assaulttimer.model.provider

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState
import org.joda.time.Duration

interface AssaultStateDurationProvider {
    fun getAssaultStateDuration(assaultEntity: Assault, assaultState: AssaultState): Duration
}