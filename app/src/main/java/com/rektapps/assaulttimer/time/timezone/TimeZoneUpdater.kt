package com.rektapps.assaulttimer.time.timezone

import io.reactivex.Completable

interface TimeZoneUpdater {
    fun updateTimeZone():Completable
}