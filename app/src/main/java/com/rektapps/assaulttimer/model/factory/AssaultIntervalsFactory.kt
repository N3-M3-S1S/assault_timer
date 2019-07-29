package com.rektapps.assaulttimer.model.factory

import org.joda.time.DateTime
import org.joda.time.Interval

interface AssaultIntervalsFactory {
    fun createAssaultIntervals(sinceDate: DateTime, lastAssaultEndDate: DateTime, count: Int): List<Interval>
}