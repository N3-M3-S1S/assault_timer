package com.rektapps.assaulttimer.model.factory.impl

import com.rektapps.assaulttimer.model.factory.AssaultIntervalsFactory
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Interval

class AssaultIntervalsFactoryImpl(
    private val assaultDuration: Duration,
    private val breakBetweenAssaults: Duration
) : AssaultIntervalsFactory {

    override fun createAssaultIntervals(
        sinceDate: DateTime,
        lastAssaultEndDate: DateTime,
        count: Int
    ): List<Interval> {
        return if (count < 1)
            emptyList()
        else {
            val assaultIntervals = mutableListOf<Interval>()
            val firstAssaultIntervalSinceDate = createFirstIntervalSinceDate(lastAssaultEndDate, sinceDate)

            assaultIntervals.add(firstAssaultIntervalSinceDate)

            while (assaultIntervals.size != count)
                assaultIntervals.add(createAssaultIntervalByPreviousInterval(assaultIntervals.last()))

            return assaultIntervals
        }
    }

    private fun createFirstIntervalSinceDate(lastAssaultEndDateTime: DateTime, sinceDateTime: DateTime): Interval {
        val assaultStart = lastAssaultEndDateTime.plus(breakBetweenAssaults)
        val assaultEnd = assaultStart.plus(assaultDuration)

        var assaultInterval = Interval(assaultStart, assaultEnd)

        while (assaultInterval.end.isBefore(sinceDateTime))
            assaultInterval = createAssaultIntervalByPreviousInterval(assaultInterval)

        return assaultInterval
    }

    private fun createAssaultIntervalByPreviousInterval(previousAssaultInterval: Interval) =
        with(previousAssaultInterval) {
            val assaultStartDate = end.plus(breakBetweenAssaults)
            val assaultEndDate = assaultStartDate.plus(assaultDuration)
            Interval(assaultStartDate, assaultEndDate)
        }

}