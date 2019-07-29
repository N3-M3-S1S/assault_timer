package com.rektapps.assaulttimer.time.formatter

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.PeriodFormatterBuilder
import javax.inject.Inject

class AssaultDateTimeFormatterImpl @Inject constructor(isAMPMDateTimeFormatSelected: Boolean) :
    AssaultDateTimeFormatter, OnTimeFormatChangedListener {

    private val timerDurationFormatter = PeriodFormatterBuilder()
        .minimumPrintedDigits(2).printZeroAlways().appendHours().appendSeparator(":")
        .minimumPrintedDigits(2).printZeroAlways().appendMinutes().appendSeparator(":")
        .minimumPrintedDigits(2).printZeroAlways().appendSeconds()
        .toFormatter()

    override fun getTimerDurationString(duration: Duration): String  = timerDurationFormatter.print(duration.toPeriod())

    private lateinit var dateTimeFormatter: DateTimeFormatter

    init {
        createDateTimeFormatter(isAMPMDateTimeFormatSelected)
    }

    override fun onTimeFormatChanged(isAMPMDateTimeFormatSelected: Boolean) {
        createDateTimeFormatter(isAMPMDateTimeFormatSelected)
    }

    override fun getAssaultListItemDateTimeString(dateTime: DateTime): String = dateTimeFormatter.print(dateTime)

    private fun createDateTimeFormatter(isAMPMDateTimeFormatSelected: Boolean) {
        val timeFormat = if (isAMPMDateTimeFormatSelected) "KK:mm a" else "HH:mm"
        dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy $timeFormat")
    }

}