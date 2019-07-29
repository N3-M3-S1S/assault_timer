package com.rektapps.assaulttimer.time.formatter

import org.joda.time.DateTime
import org.joda.time.Duration

interface AssaultDateTimeFormatter {
    fun getAssaultListItemDateTimeString(dateTime: DateTime): String
    fun getTimerDurationString(duration: Duration): String

}