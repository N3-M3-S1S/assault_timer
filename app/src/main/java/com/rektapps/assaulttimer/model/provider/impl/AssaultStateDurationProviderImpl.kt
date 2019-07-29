package com.rektapps.assaulttimer.model.provider.impl

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.model.provider.AssaultStateDurationProvider
import com.rektapps.assaulttimer.utils.getNowDateTimeWithRoundedSeconds
import org.joda.time.Duration
import javax.inject.Inject

class AssaultStateDurationProviderImpl @Inject constructor() : AssaultStateDurationProvider {

    override fun getAssaultStateDuration(assaultEntity: Assault, assaultState: AssaultState): Duration {
        return when (assaultState) {
            AssaultState.INCOMING -> {
                Duration(getNowDateTimeWithRoundedSeconds(), assaultEntity.start)
            }

            AssaultState.ACTIVE, AssaultState.ENDING -> {
                Duration(getNowDateTimeWithRoundedSeconds(), assaultEntity.end)
            }

            AssaultState.ENDED -> {
                Duration.ZERO
            }
        }
    }
}