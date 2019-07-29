package com.rektapps.assaulttimer.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rektapps.assaulttimer.dagger.key.ViewModelKey
import com.rektapps.assaulttimer.viewmodel.AssaultScheduleListViewModel
import com.rektapps.assaulttimer.viewmodel.MainViewModel
import com.rektapps.assaulttimer.viewmodel.NotificationSettingsViewModel
import com.rektapps.assaulttimer.viewmodel.ViewModelFactory
import com.rektapps.assaulttimer.viewmodel.shared.UiSettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton


@Module
interface ViewModelModule{

    @Binds
    @IntoMap
    @ViewModelKey(AssaultScheduleListViewModel::class)
    fun providesAssaultListViewModel(assaultListViewModel: AssaultScheduleListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun providesMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationSettingsViewModel::class)
    fun providesSettingsViewModel(notificationSettingsViewModel: NotificationSettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UiSettingsViewModel::class)
    fun providesUISettingsViewModel(uiSettingsViewModel: UiSettingsViewModel): ViewModel

    @Binds
    @Singleton
    fun providesViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

}