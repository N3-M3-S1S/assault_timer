package com.rektapps.assaulttimer.model.provider.impl

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.model.provider.AssaultStateProvider
import com.rektapps.assaulttimer.utils.getNowDateTimeWithRoundedSeconds
import org.joda.time.Duration
import org.joda.time.Interval
import javax.inject.Inject

class AssaultStateProviderImpl @Inject constructor() : AssaultStateProvider {

    override fun getAssaultState(assaultEntity: Assault): AssaultState {
        val roundedNow = getNowDateTimeWithRoundedSeconds()
        val assaultInterval = Interval(assaultEntity.start, assaultEntity.end)

        if (assaultInterval.isAfter(roundedNow))
            return AssaultState.INCOMING

        if (assaultInterval.isBefore(roundedNow))
            return AssaultState.ENDED

        val remainingDuration = assaultInterval.withStart(roundedNow).toDuration()

        return if (remainingDuration.isShorterThan(Duration.standardHours(1)))
            AssaultState.ENDING
        else
            AssaultState.ACTIVE
    }

}