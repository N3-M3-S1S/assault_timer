package com.rektapps.assaulttimer.model.timer

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState
import org.joda.time.Duration

interface AssaultDurationTimer {
    fun start(assault: Assault, onTick: (remainingDuration: Duration) -> Unit, onAssaultStateChanged: (AssaultState) -> Unit)
    fun stop()
}