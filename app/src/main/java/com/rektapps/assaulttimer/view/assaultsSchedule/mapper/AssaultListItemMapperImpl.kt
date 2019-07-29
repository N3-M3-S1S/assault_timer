package com.rektapps.assaulttimer.view.assaultsSchedule.mapper

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.model.provider.AssaultStateProvider
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.service.ManualNotificationsService
import com.rektapps.assaulttimer.storage.settings.AppConfig
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import com.rektapps.assaulttimer.view.assaultsSchedule.AssaultListItem
import io.reactivex.Single
import javax.inject.Inject

class AssaultListItemMapperImpl @Inject constructor(
    private val appConfig: AppConfig,
    private val notificationsConfig: NotificationsConfig,
    private val assaultStateProvider: AssaultStateProvider,
    private val manualNotificationsService: ManualNotificationsService
) : AssaultListItemMapper {

    override fun mapAssaultsToAssaultListItems(assaults: List<Assault>): Single<List<AssaultListItem>> {
        if (!appConfig.areNotificationsEnabled)
            return Single.just(assaults.map { AssaultListItem(it) })
        else {
            when (notificationsConfig.notificationMode) {

                NotificationMode.AUTO -> {
                    return Single.just(mapAssaultListItemForAutomaticNotificationMode(assaults))
                }

                NotificationMode.MANUAL -> {
                    val singles = mutableListOf<Single<AssaultListItem>>()
                    assaults.forEach { assaultEntity ->
                        val single = manualNotificationsService.isAssaultSelectedForNotification(assaultEntity)
                            .map { isNotificationSelected -> AssaultListItem(assaultEntity, isNotificationSelected) }
                        singles.add(single)
                    }
                    return Single.concat(singles).toList()
                }
            }
        }
    }

    private fun mapAssaultListItemForAutomaticNotificationMode(assaultEntities: List<Assault>): List<AssaultListItem> {
        val automaticNotificationsSettings = notificationsConfig.automaticNotificationModePreferences
        val firstIncomingAssault =
            assaultEntities.first { assaultStateProvider.getAssaultState(it) == AssaultState.INCOMING }
        val areNotificationsEnabledForRegionAndAssaultType =
            automaticNotificationsSettings[firstIncomingAssault.region]?.contains(firstIncomingAssault.type) == true

        return assaultEntities.map {
            AssaultListItem(
                it,
                areNotificationsEnabledForRegionAndAssaultType && it == firstIncomingAssault
            )
        }
    }

}