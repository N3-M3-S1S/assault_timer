package com.rektapps.assaulttimer.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.entity.ManualNotification
import com.rektapps.assaulttimer.storage.dao.AssaultDao
import com.rektapps.assaulttimer.storage.dao.Converters
import com.rektapps.assaulttimer.storage.dao.ManualNotificationDao

@Database(entities = [Assault::class, ManualNotification::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database: RoomDatabase(){
   abstract fun getAssaultDao(): AssaultDao
   abstract fun getNotificationDao(): ManualNotificationDao
}