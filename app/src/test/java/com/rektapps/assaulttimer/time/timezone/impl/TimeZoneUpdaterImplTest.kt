package com.rektapps.assaulttimer.time.timezone.impl

import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.notifications.manager.AssaultNotificationScheduleManager
import com.rektapps.assaulttimer.storage.settings.AppConfig
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import com.rektapps.assaulttimer.service.AssaultsUpdater
import com.rektapps.assaulttimer.time.timezone.TimeZoneIDProvider
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.mockk.verifyOrder
import io.reactivex.Completable
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.TimeUnit

class TimeZoneUpdaterImplTest {
    companion object {
        @MockK
        private lateinit var assaultsUpdater: AssaultsUpdater
        @MockK
        private lateinit var assaultNotificationScheduleManager: AssaultNotificationScheduleManager
        @MockK
        private lateinit var appConfig: AppConfig
        @MockK
        private lateinit var timeZoneIDProvider: TimeZoneIDProvider
        @MockK
        private lateinit var notificationsConfig: NotificationsConfig
        @InjectMockKs
        private lateinit var timeZoneUpdaterImpl: TimeZoneUpdaterImpl

        @BeforeClass
        @JvmStatic
        fun setUp() {
            MockKAnnotations.init(this, relaxed = true)
            every {
                assaultsUpdater.update(any(), any())
                assaultNotificationScheduleManager.cancelNotifications()
                assaultNotificationScheduleManager.scheduleNotificationsWithLastConfiguration()
            } returns Completable.complete()
        }
    }


    @Test
    fun updateWhenAutoNotificationsEnabled() {
        every { appConfig.areNotificationsEnabled } returns true
        every { notificationsConfig.notificationMode } returns NotificationMode.AUTO
        timeZoneUpdaterImpl.updateTimeZone().blockingAwait(1, TimeUnit.SECONDS)
        verifyOrder {
            assaultNotificationScheduleManager.cancelNotifications()
            assaultsUpdater.recreateAllAssaults()
            assaultNotificationScheduleManager.scheduleNotificationsWithLastConfiguration()
        }
    }


    @Test
    fun updateWhenManualNotificationsEnabled() {
        every { appConfig.areNotificationsEnabled } returns true
        every { notificationsConfig.notificationMode } returns NotificationMode.MANUAL
        timeZoneUpdaterImpl.updateTimeZone().blockingAwait(1, TimeUnit.SECONDS)
        verifyOrder {
            assaultNotificationScheduleManager.cancelNotifications()
            assaultsUpdater.recreateAllAssaults()
        }
        verify(inverse = true) { assaultNotificationScheduleManager.scheduleNotificationsWithLastConfiguration() }
    }

    @Test
    fun updateWhenNotificationsDisabled() {
        every { appConfig.areNotificationsEnabled } returns false
        timeZoneUpdaterImpl.updateTimeZone().blockingAwait(1, TimeUnit.SECONDS)
        verify {
            assaultsUpdater.recreateAllAssaults()
        }
        verify(inverse = true) {
            assaultNotificationScheduleManager.cancelNotifications()
            assaultNotificationScheduleManager.scheduleNotificationsWithLastConfiguration()
        }
    }
}