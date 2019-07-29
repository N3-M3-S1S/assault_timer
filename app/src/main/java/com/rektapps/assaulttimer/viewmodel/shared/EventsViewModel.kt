package com.rektapps.assaulttimer.viewmodel.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.ClickedNotificationEntity
import com.rektapps.assaulttimer.utils.SingleLiveEvent

class EventsViewModel : ViewModel(){
    private val regionChangedEvent = SingleLiveEvent<Region>()
    private val notificationClickedEvent = SingleLiveEvent<ClickedNotificationEntity>()

    fun getRegionChangedEvent(): LiveData<Region> = regionChangedEvent
    fun getNotificationClickedEvent(): LiveData<ClickedNotificationEntity> = notificationClickedEvent

    fun onNotificationClicked(region: Region, assaultType: AssaultType){
        notificationClickedEvent.value = ClickedNotificationEntity(region, assaultType)
    }

    fun onRegionChanged(region: Region){
        regionChangedEvent.value = region
    }

}