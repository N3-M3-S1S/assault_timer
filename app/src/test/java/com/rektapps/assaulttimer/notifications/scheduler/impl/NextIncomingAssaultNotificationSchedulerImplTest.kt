package com.rektapps.assaulttimer.notifications.scheduler.impl

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.notifications.scheduler.AssaultNotificationScheduler
import com.rektapps.assaulttimer.service.IncomingAssaultService
import com.rektapps.assaulttimer.storage.settings.NotificationsConfig
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class NextIncomingAssaultNotificationSchedulerImplTest {
    companion object {
        @MockK
        private lateinit var mockedIncomingAssaultService: IncomingAssaultService
        @MockK(relaxUnitFun = true)
        private lateinit var mockedAssaultNotificationScheduler: AssaultNotificationScheduler
        @MockK
        private lateinit var mockedNotificationConfig: NotificationsConfig
        @MockK
        private lateinit var mockedAssaultClass: Assault
        @InjectMockKs
        private lateinit var nextAssaultNotificationScheduler: NextIncomingAssaultNotificationSchedulerImpl

        @BeforeClass
        @JvmStatic
        fun setUp() = MockKAnnotations.init(this)
    }

    @Before
    fun resetMocks() = clearMocks(mockedIncomingAssaultService, mockedAssaultNotificationScheduler)

    @Test
    fun scheduleNextAutoNotification() {
        every { mockedNotificationConfig.notificationMode } returns NotificationMode.AUTO
        every { mockedIncomingAssaultService.getFirstIncomingAssault(any(), any()) } returns Single.just(
            mockedAssaultClass
        )

        nextAssaultNotificationScheduler.schedule(Region.EU, AssaultType.LEGION).blockingAwait()

        verifyAll {
            mockedIncomingAssaultService.getFirstIncomingAssault(any(), any())
            mockedAssaultNotificationScheduler.schedule(any())
        }
    }

    @Test
    fun scheduleNextManualNotification() {
        every { mockedNotificationConfig.notificationMode } returns NotificationMode.MANUAL
        every {
            mockedIncomingAssaultService.getFirstIncomingManualNotificationAssault(
                any(),
                any()
            )
        } returns Maybe.just(
            mockedAssaultClass
        )

        nextAssaultNotificationScheduler.schedule(Region.NA, AssaultType.BFA).blockingAwait()

        verifyAll {
            mockedIncomingAssaultService.getFirstIncomingManualNotificationAssault(any(), any())
            mockedAssaultNotificationScheduler.schedule(any())
        }
    }

    @Test
    fun scheduleManualNotificationWhenNoSelectedAssaults() {
        every { mockedNotificationConfig.notificationMode } returns NotificationMode.MANUAL
        every {
            mockedIncomingAssaultService.getFirstIncomingManualNotificationAssault(
                any(),
                any()
            )
        } returns Maybe.empty()

        nextAssaultNotificationScheduler.schedule(Region.NA, AssaultType.BFA).blockingAwait()

        verify(exactly = 1) { mockedIncomingAssaultService.getFirstIncomingManualNotificationAssault(any(), any()) }
        verify(exactly = 0) { mockedAssaultNotificationScheduler.schedule(any()) }
    }

}