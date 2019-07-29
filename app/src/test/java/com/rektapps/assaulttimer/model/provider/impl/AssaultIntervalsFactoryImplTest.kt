package com.rektapps.assaulttimer.model.provider.impl

import com.rektapps.assaulttimer.model.factory.impl.AssaultIntervalsFactoryImpl
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Assert.assertTrue
import org.junit.Test


class AssaultIntervalsFactoryImplTest {

    @Test
    fun createAssaultIntervals() {
        val date = DateTime(2019, 1, 8, 10, 0)
        val assaultDuration = Duration.standardSeconds(5)
        val assaultBreak = Duration.standardSeconds(10)
        val assaultsCount = 10

        val factory =
            AssaultIntervalsFactoryImpl(assaultDuration, assaultBreak)

        val assaults = factory.createAssaultIntervals(date, date, assaultsCount)

        assertTrue(assaults.size == assaultsCount)

        assaults.forEachIndexed { index, interval ->

            if (index == 0) {
                assertTrue(interval.start.isEqual(date.plus(assaultBreak)))
            } else if (index > 0) {
                val previousInterval = assaults[index - 1]
                assertTrue(interval.start.isEqual(previousInterval.start.plus(assaultDuration).plus(assaultBreak)))
                assertTrue(interval.end.isEqual(previousInterval.end.plus(assaultBreak).plus(assaultDuration)))
            }
        }
    }


}