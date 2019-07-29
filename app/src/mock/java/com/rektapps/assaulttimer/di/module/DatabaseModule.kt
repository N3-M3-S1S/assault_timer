package com.rektapps.assaulttimer.di.module

import android.content.Context
import androidx.room.Room
import com.rektapps.assaulttimer.storage.AssaultDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(appContext: Context) = Room.databaseBuilder(appContext, AssaultDb::class.java, "db").build()

    @Provides
    @Singleton
    fun providesAssaultDao(assaultDb: AssaultDb) = assaultDb.getAssaultDao()

}