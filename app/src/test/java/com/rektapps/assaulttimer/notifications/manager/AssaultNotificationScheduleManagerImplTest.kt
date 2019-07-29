package com.rektapps.assaulttimer.notifications.manager

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.notifications.scheduler.AssaultNotificationScheduler
import com.rektapps.assaulttimer.notifications.scheduler.NextIncomingAssaultNotificationScheduler
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import com.rektapps.assaulttimer.utils.doForEachRegionAndAssaultType
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Completable
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class AssaultNotificationScheduleManagerImplTest {
    companion object {
        @MockK(relaxUnitFun = true)
        private lateinit var assaultNotificationScheduler: AssaultNotificationScheduler
        @MockK
        private lateinit var notificationsConfig: NotificationsConfig
        @MockK
        private lateinit var nextIncomingAssaultNotificationScheduler: NextIncomingAssaultNotificationScheduler
        @InjectMockKs
        private lateinit var assaultNotificationScheduleManager: AssaultNotificationScheduleManagerImpl

        @BeforeClass
        @JvmStatic
        fun init() {
            MockKAnnotations.init(this)
            every { nextIncomingAssaultNotificationScheduler.schedule(any(), any()) } returns Completable.complete()
        }

    }

    @Before
    fun resetMocks() = clearAllMocks(answers = false)


    @Test
    fun scheduleManualNotifications() {
        every { notificationsConfig.notificationMode } returns NotificationMode.MANUAL

        assaultNotificationScheduleManager.scheduleNotificationsWithLastConfiguration().blockingAwait()

        verify {
            assaultNotificationScheduler.cancelAll()
            doForEachRegionAndAssaultType { region, assaultType ->
                nextIncomingAssaultNotificationScheduler.schedule(region, assaultType)
            }
        }


    }


    @Test
    fun scheduleAutoNotifications() {
        val testMap =
            mapOf(Region.EU to listOf(AssaultType.BFA), Region.NA to listOf(AssaultType.LEGION, AssaultType.BFA))
        every { notificationsConfig.notificationMode } returns NotificationMode.AUTO
        every { notificationsConfig.automaticNotificationModePreferences } returns testMap
        assaultNotificationScheduleManager.scheduleNotificationsWithLastConfiguration().blockingAwait()

        verify { assaultNotificationScheduler.cancelAll() }

        verify {
            testMap.forEach { (region, types) ->
                types.forEach { type ->
                    nextIncomingAssaultNotificationScheduler.schedule(region, type)
                }
            }
        }

    }


}