package com.rektapps.assaulttimer.dagger.module

import com.rektapps.assaulttimer.view.MainActivity
import com.rektapps.assaulttimer.view.assaultsSchedule.AssaultScheduleFragment
import com.rektapps.assaulttimer.view.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface AndroidBindingModule{

    @ContributesAndroidInjector
    fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    fun assaultScheduleFragment(): AssaultScheduleFragment

    @ContributesAndroidInjector
    fun settingsFragment(): SettingsFragment

}