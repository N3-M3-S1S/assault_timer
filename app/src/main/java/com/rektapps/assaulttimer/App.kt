package com.rektapps.assaulttimer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import com.rektapps.assaulttimer.dagger.component.AppComponent
import com.rektapps.assaulttimer.dagger.component.DaggerAppComponent
import com.rektapps.assaulttimer.time.timezone.TimeZoneChangedReceiver
import dagger.android.support.DaggerApplication
import net.danlew.android.joda.JodaTimeAndroid
import javax.inject.Inject

class App: DaggerApplication() {

    companion object {
        lateinit var instance:App
    }

    @Inject lateinit var appComponent:AppComponent

    override fun applicationInjector() = appComponent

    override fun onCreate() {
        DaggerAppComponent.factory().create(this).inject(this)
        instance = this
        JodaTimeAndroid.init(this)
        DataBindingUtil.setDefaultComponent(appComponent.getDataBindingSubComponentBuilder().create())

        val timeZoneChangedReceiver = TimeZoneChangedReceiver()
        val intentFilter = IntentFilter().apply { addAction(Intent.ACTION_TIMEZONE_CHANGED) }
        registerReceiver(timeZoneChangedReceiver, intentFilter)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            registerAssaultNotificationChannel()

        super.onCreate()
    }

    @SuppressLint("NewApi")
    private fun registerAssaultNotificationChannel(){
        val name = getString(R.string.notificationChannelName)
        val id = getString(R.string.notificationChannelId)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(id, name, importance)
        val notificationManager = getSystemService<NotificationManager>()!!
        notificationManager.createNotificationChannel(mChannel)
    }

}