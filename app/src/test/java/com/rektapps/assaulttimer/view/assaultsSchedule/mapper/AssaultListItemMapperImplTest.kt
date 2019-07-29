package com.rektapps.assaulttimer.view.assaultsSchedule.mapper

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.model.provider.AssaultStateProvider
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.service.ManualNotificationsService
import com.rektapps.assaulttimer.storage.settings.AppConfig
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import com.rektapps.assaulttimer.view.assaultsSchedule.AssaultListItem
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import org.joda.time.DateTime
import org.junit.BeforeClass
import org.junit.Test

class AssaultListItemMapperImplTest {
    companion object {
        @MockK
        private lateinit var mockAppConfig: AppConfig
        @MockK
        private lateinit var mockNotificationConfig: NotificationsConfig
        @MockK
        private lateinit var mockAssaultStateProvider: AssaultStateProvider
        @MockK
        private lateinit var mockManualNotificationsService: ManualNotificationsService
        @InjectMockKs
        private lateinit var assaultListItemMapper: AssaultListItemMapperImpl

        @BeforeClass
        @JvmStatic
        fun setUp() = MockKAnnotations.init(this)

    }

    private val allListItemsHaveNotificationDisabled =
        { assaultListItems: List<AssaultListItem> -> assaultListItems.all { !it.isNotificationEnabled } }


    @Test
    fun createAssaultListItemsWithAutomaticNotifications() {
        every { mockAppConfig.areNotificationsEnabled } returns true
        every { mockNotificationConfig.notificationMode } returns NotificationMode.AUTO
        every { mockAssaultStateProvider.getAssaultState(any()) } answers {
            val assault = arg<Assault>(0)
            if (assault.id == 0L)
                AssaultState.ACTIVE
            else
                AssaultState.INCOMING
        }

        every { mockNotificationConfig.automaticNotificationModePreferences } answers {
            val mockedSettings = mutableMapOf<Region, List<AssaultType>>()
            mockedSettings[Region.NA] = listOf(AssaultType.BFA, AssaultType.LEGION)
            mockedSettings
        }


        var testObserver = assaultListItemMapper.mapAssaultsToAssaultListItems(
            createTestAssaults(
                Region.EU,
                AssaultType.LEGION,
                2
            )
        ).test()
        testObserver.assertValue(allListItemsHaveNotificationDisabled)


        val onlyFirstAssaultItemHasNotificationEnabledFunc = { assaultListItems: List<AssaultListItem> ->
            var isOnlyFirstListItemHasEnabledNotification = false

            assaultListItems.forEachIndexed { index, assaultListItem ->
                isOnlyFirstListItemHasEnabledNotification = if (index == 0)
                    assaultListItem.isNotificationEnabled
                else
                    !assaultListItem.isNotificationEnabled
            }
            isOnlyFirstListItemHasEnabledNotification
        }
        enumValues<AssaultType>().forEach {
            testObserver =
                assaultListItemMapper.mapAssaultsToAssaultListItems(createTestAssaults(Region.NA, it, 2)).test()
            testObserver.assertValue(onlyFirstAssaultItemHasNotificationEnabledFunc)

        }

    }


    @Test
    fun createAssaultListItemsWithManualNotifications() {
        every { mockAppConfig.areNotificationsEnabled } returns true
        every { mockNotificationConfig.notificationMode } returns NotificationMode.MANUAL
        every { mockManualNotificationsService.isAssaultSelectedForNotification(any()) } answers {
            val assault = arg(0) as Assault
            Single.just(assault.id!! % 2 == 0L)
        }

        val testObserver =
            assaultListItemMapper.mapAssaultsToAssaultListItems(createTestAssaults(count = 7)).test()
        testObserver.assertValue { assaultListItems ->
            val allEvenListItemsHaveSelectedNotification =
                assaultListItems.filter { it.assault.id!! % 2 == 0L }.all { it.isNotificationEnabled }

            val allOddListItemsHaveNotSelectedNotification =
                assaultListItems.filter { it.assault.id!! % 2 != 0L }.all { !it.isNotificationEnabled }

            allEvenListItemsHaveSelectedNotification and allOddListItemsHaveNotSelectedNotification
        }
    }


    @Test
    fun createAssaultListItemsWhenNotificationsDisabled() {
        every { mockAppConfig.areNotificationsEnabled } returns false
        val testObserver =
            assaultListItemMapper.mapAssaultsToAssaultListItems(createTestAssaults(count = 2)).test()
        testObserver.assertValue(allListItemsHaveNotificationDisabled)
    }

    private fun createTestAssaults(
        region: Region = Region.EU,
        assaultType: AssaultType = AssaultType.LEGION,
        count: Int
    ): List<Assault> {
        val testAssaults = mutableListOf<Assault>()
        for (i in 0..count.toLong()) {
            testAssaults.add(Assault(DateTime.now(), DateTime.now(), region, assaultType, i))
        }
        return testAssaults
    }

}