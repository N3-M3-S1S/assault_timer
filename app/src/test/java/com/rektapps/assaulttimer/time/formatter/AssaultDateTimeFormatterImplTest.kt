package com.rektapps.assaulttimer.time.formatter

import org.joda.time.DateTime
import org.junit.Assert.assertTrue
import org.junit.Test


class AssaultDateTimeFormatterImplTest {

    @Test
    fun testDateTimeFormat() {
        val dateTimeFormatterImpl = AssaultDateTimeFormatterImpl(true)
        val testDateTime = DateTime(2011, 12, 13, 14, 15)
        val expectedAMPMDateTimeString = "13-12-2011 02:15 PM"
        val expected24HourDateTimeString = "13-12-2011 14:15"

        assertTrue(dateTimeFormatterImpl.getAssaultListItemDateTimeString(testDateTime) == expectedAMPMDateTimeString)

        dateTimeFormatterImpl.onTimeFormatChanged(false)

        assertTrue(dateTimeFormatterImpl.getAssaultListItemDateTimeString(testDateTime) == expected24HourDateTimeString)


    }

}