package com.rektapps.assaulttimer.storage.dao

import androidx.room.TypeConverter
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import org.joda.time.DateTime

class Converters{

    @TypeConverter
    fun dateTimeToLong(dateTime: DateTime) = dateTime.millis

    @TypeConverter
    fun dateTimeFromLong(millis:Long) = DateTime(millis)

    @TypeConverter
    fun regionToInt(region: Region) = region.getId()

    @TypeConverter
    fun regionFromInt(regionId:Int) = Region.values().find { it.getId() == regionId }!!

    @TypeConverter
    fun assaultTypeToInt(assaultType: AssaultType) = assaultType.getId()

    @TypeConverter
    fun assaultTypeFromInt(assaultTypeId:Int) = AssaultType.values().find { it.getId() == assaultTypeId }!!

}