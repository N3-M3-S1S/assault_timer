package com.rektapps.assaulttimer.dagger.component

import com.rektapps.assaulttimer.App
import com.rektapps.assaulttimer.dagger.module.*
import com.rektapps.assaulttimer.notifications.broadcast.NotificationBroadcastReceiver
import com.rektapps.assaulttimer.notifications.broadcast.NotificationInitializerOnRebootReceiver
import com.rektapps.assaulttimer.time.timezone.TimeZoneChangedReceiver
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        AndroidSupportInjectionModule::class,
        AndroidBindingModule::class,
        ViewModelModule::class,
        AssaultModule::class,
        ServiceModule::class,
        NotificationModule::class,
        SettingsModule::class,
        UtilsModule::class,
        TimeZoneModule::class
    ]
)

interface AppComponent : AndroidInjector<App> {
    override fun inject(app: App)

    fun inject(notificationBroadcastReceiver: NotificationBroadcastReceiver)
    fun inject(notificationInitializerOnRebootReceiver: NotificationInitializerOnRebootReceiver)
    fun inject(timeZoneChangedReceiver: TimeZoneChangedReceiver)

    fun getDataBindingSubComponentBuilder(): DataBindingSubComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app:App): AppComponent
    }
}