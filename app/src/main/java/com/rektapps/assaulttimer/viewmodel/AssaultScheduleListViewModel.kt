package com.rektapps.assaulttimer.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.model.provider.AssaultStateProvider
import com.rektapps.assaulttimer.model.timer.AssaultDurationTimer
import com.rektapps.assaulttimer.notifications.ClickedNotificationEntity
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.service.AssaultService
import com.rektapps.assaulttimer.service.ManualNotificationsService
import com.rektapps.assaulttimer.storage.settings.AppConfig
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import com.rektapps.assaulttimer.utils.SingleLiveEvent
import com.rektapps.assaulttimer.utils.notNullValue
import com.rektapps.assaulttimer.utils.subscribeIOObserveMainSingle
import com.rektapps.assaulttimer.view.assaultsSchedule.AssaultListItem
import com.rektapps.assaulttimer.view.assaultsSchedule.mapper.AssaultListItemMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.joda.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AssaultScheduleListViewModel @Inject constructor(
    private val assaultService: AssaultService,
    private val assaultStateProvider: AssaultStateProvider,
    private val assaultListItemMapper: AssaultListItemMapper,
    private val manualNotificationsService: ManualNotificationsService,
    private val assaultDurationTimer: AssaultDurationTimer,
    private val appConfig: AppConfig,
    private val notificationsConfig: NotificationsConfig
) : ViewModel(), LifecycleObserver {

    private val assaultListItems = MutableLiveData<List<AssaultListItem>>()

    private val firstAssaultInListState = MutableLiveData<AssaultState>()
    private val firstAssaultInListStateDuration = MutableLiveData<Duration>().apply { value = Duration.ZERO }

    private val isListLoading = MutableLiveData<Boolean>()

    private val updateAssaultListItemOnPositionEvent = SingleLiveEvent<Int>()
    private val scheduleNotificationForActiveAssaultErrorEvent = SingleLiveEvent<Unit>()

    private val restoreSelectedAssaultTypeEvent = SingleLiveEvent<AssaultType>()
    private val restoreRegionTitleEvent = SingleLiveEvent<Region>()

    private var assaultsSourceDisposable: Disposable? = null
    private var assaultListItemClickDisposable: Disposable? = null

    private var selectedAssaultType = appConfig.lastSelectedAssaultType
    private var selectedRegion = appConfig.lastSelectedRegion

    private var onTimerTick =
        { remainingDuration: Duration -> firstAssaultInListStateDuration.value = remainingDuration }

    private var onAssaultStateChanged = { assaultState: AssaultState ->
        firstAssaultInListState.value = assaultState

        if (assaultState == AssaultState.ENDED) {
            assaultService.update(selectedRegion, selectedAssaultType)
        } else if (assaultState == AssaultState.ACTIVE && appConfig.areNotificationsEnabled) {

            val firstAssaultListItemInList = assaultListItems.value!!.first()

            if (firstAssaultListItemInList.isNotificationEnabled) {

                firstAssaultListItemInList.isNotificationEnabled = false
                updateAssaultListItemOnPositionEvent.value = 0

                if (notificationsConfig.notificationMode == NotificationMode.AUTO) {
                    assaultListItems.value!![1].isNotificationEnabled = true
                    updateAssaultListItemOnPositionEvent.value = 1
                }
            }
        }
    }

    fun getAssaultListItems(): LiveData<List<AssaultListItem>> = assaultListItems
    fun getActiveAssaultState(): LiveData<AssaultState> = firstAssaultInListState
    fun getUpdateItemOnPositionEvent(): LiveData<Int> = updateAssaultListItemOnPositionEvent
    fun getFirstAssaultInListStateDuration(): LiveData<Duration> = firstAssaultInListStateDuration
    fun getNotificationForActiveAssaultErrorEvent(): LiveData<Unit> = scheduleNotificationForActiveAssaultErrorEvent
    fun getIsListLoadingLiveData(): LiveData<Boolean> = isListLoading
    fun getRestoreSelectedAssaultTypeEvent(): LiveData<AssaultType> = restoreSelectedAssaultTypeEvent
    fun getRestoreRegionTitleEvent(): LiveData<Region> = restoreRegionTitleEvent

    fun onRegionChanged(region: Region) {
        if (region != selectedRegion) {
            selectedRegion = region
            subscribeToAssaultsSource()
        }
    }

    fun onAssaultTypeChanged(assaultType: AssaultType) {
        if (selectedAssaultType != assaultType) {
            selectedAssaultType = assaultType
            subscribeToAssaultsSource()
        }
    }

    //after a user clicks a notification, onPause and then onResume will be called so no need to subscribe to assaults source here
    fun onNotificationClicked(clickedNotificationEntity: ClickedNotificationEntity) {
        selectedRegion = clickedNotificationEntity.region
        selectedAssaultType = clickedNotificationEntity.assaultType
    }

    fun onAssaultItemClicked(clickedAssaultListItem: AssaultListItem) {
        val manualNotificationsEnabled =
            appConfig.areNotificationsEnabled && notificationsConfig.notificationMode == NotificationMode.MANUAL

        if (manualNotificationsEnabled) {
            val assault = clickedAssaultListItem.assault
            if (assaultStateProvider.getAssaultState(assault) == AssaultState.INCOMING) {
                assaultListItemClickDisposable?.dispose()
                assaultListItemClickDisposable = manualNotificationsService
                    .toggleNotificationForAssault(assault)
                    .compose(subscribeIOObserveMainSingle())
                    .subscribeBy(onSuccess = { isNotificationEnabled ->
                        val clickedAssaultListItemPosition =
                            assaultListItems.notNullValue.indexOf(clickedAssaultListItem)
                        assaultListItems.notNullValue[clickedAssaultListItemPosition].isNotificationEnabled = isNotificationEnabled
                        updateAssaultListItemOnPositionEvent.value = clickedAssaultListItemPosition
                    }
                        , onError = {
                            Log.e("Schedule viewmodel", it.localizedMessage)
                        })
            } else {
                scheduleNotificationForActiveAssaultErrorEvent.call()
            }
        }
    }


    private fun subscribeToAssaultsSource() {
        assaultsSourceDisposable?.dispose()
        assaultsSourceDisposable = assaultService.getAssaults(selectedRegion, selectedAssaultType)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { isListLoading.postValue(true) }
            .delay(
                200,
                TimeUnit.MILLISECONDS
            ) //add a delay to prevent showing empty list if assaults are being updated and to prevent tab's change animation lag caused by updating recycler view's items
            .flatMapSingle { assaults -> assaultListItemMapper.mapAssaultsToAssaultListItems(assaults) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { mappedAssaultListItems ->
                    assaultListItems.value = mappedAssaultListItems
                    assaultDurationTimer.start(mappedAssaultListItems.first().assault, onTimerTick, onAssaultStateChanged)
                    isListLoading.value = false
                },
                onError = {
                    Log.e("Schedule viewmodel", it.localizedMessage)
                })
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        assaultDurationTimer.stop()
        assaultsSourceDisposable?.dispose()
        appConfig.lastSelectedRegion = selectedRegion
        appConfig.lastSelectedAssaultType = selectedAssaultType
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (assaultsSourceDisposable == null || assaultsSourceDisposable!!.isDisposed)
            subscribeToAssaultsSource()

        restoreSelectedAssaultTypeEvent.value = selectedAssaultType
        restoreRegionTitleEvent.value = selectedRegion
    }

}



