package com.rektapps.assaulttimer.dagger.module

import android.app.AlarmManager
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import com.rektapps.assaulttimer.App
import com.rektapps.assaulttimer.dagger.component.DataBindingSubComponent
import dagger.Module
import dagger.Provides

@Module(subcomponents = [DataBindingSubComponent::class])

object      AppModule {

    @Provides
    @JvmStatic
    fun providesAppContext(app: App) = app.applicationContext

    @Provides
    @JvmStatic
    fun providesSharedPreferences(app: App) = PreferenceManager.getDefaultSharedPreferences(app)

    @Provides
    @JvmStatic
    fun providesAlarmManager(app: App) = app.getSystemService<AlarmManager>()!!

}
