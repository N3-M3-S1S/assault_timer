package com.rektapps.assaulttimer.time.timezone.impl

import com.rektapps.assaulttimer.time.timezone.TimeZoneIDProvider
import org.joda.time.DateTimeZone
import javax.inject.Inject

class TimeZoneIDProviderImpl @Inject constructor(): TimeZoneIDProvider {
    override fun getCurrentTimeZoneID(): String  = DateTimeZone.getDefault().id
}