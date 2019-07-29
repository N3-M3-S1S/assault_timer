package com.rektapps.assaulttimer.dagger.module

import android.content.Context
import androidx.room.Room
import com.rektapps.assaulttimer.storage.Database
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    @JvmStatic
    fun providesDatabase(appContext: Context) = Room.databaseBuilder(appContext, Database::class.java, "db").build()

    @Provides
    @Singleton
    @JvmStatic
    fun providesAssaultDao(database: Database) = database.getAssaultDao()

    @Provides
    @Singleton
    @JvmStatic
    fun providesNotificationDao(database: Database) = database.getNotificationDao()

}