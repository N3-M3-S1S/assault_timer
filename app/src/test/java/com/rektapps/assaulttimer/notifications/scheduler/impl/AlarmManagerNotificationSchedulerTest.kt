package com.rektapps.assaulttimer.notifications.scheduler.impl

import android.app.AlarmManager
import android.app.PendingIntent
import androidx.core.app.AlarmManagerCompat
import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.notifications.broadcast.NotificationPendingIntentCreator
import com.rektapps.assaulttimer.notifications.generator.NotificationIntentRequestCodeGenerator
import com.rektapps.assaulttimer.storage.settings.NotificationIntentRequestCodeStorage
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class AlarmManagerNotificationSchedulerTest {
    companion object {
        @MockK
        private lateinit var mockedAlarmManager: AlarmManager
        @MockK
        private lateinit var mockedAssault: Assault
        @MockK
        lateinit var mockedPendingIntent: PendingIntent
        @MockK
        private lateinit var mockedNotificationIntentRequestCodeStorage: NotificationIntentRequestCodeStorage
        @MockK
        private lateinit var mockedNotificationPendingIntentCreator: NotificationPendingIntentCreator
        @MockK
        private lateinit var mockedNotificationIntentRequestCodeGenerator: NotificationIntentRequestCodeGenerator
        @InjectMockKs
        private lateinit var alarmManagerNotificationScheduler: AlarmManagerNotificationScheduler

        private const val testAssaultId = 2L
        private const val testRequestCode = 1


        @BeforeClass
        @JvmStatic
        fun init() {
            MockKAnnotations.init(this, relaxUnitFun = true)
            every { mockedAssault.id } returns testAssaultId
            every { mockedNotificationIntentRequestCodeGenerator.getRequestCodeForAssault(any()) } returns testRequestCode
            every {
                mockedNotificationPendingIntentCreator.createNotificationPendingIntent(
                    any(),
                    any()
                )
            } returns mockedPendingIntent
        }
    }


    @Before
    fun resetMocks() = clearAllMocks(answers = false)

    @Test
    fun schedule() {
        mockkStatic(AlarmManagerCompat::class)

        every { mockedAssault.start } returns DateTime.now().plusMillis(100)

        alarmManagerNotificationScheduler.schedule(mockedAssault)

        val expectedDelay = Duration(DateTime.now(), mockedAssault.start).plus(System.currentTimeMillis()).millis

        verifyOrder {
            mockedNotificationIntentRequestCodeGenerator.getRequestCodeForAssault(mockedAssault)
            mockedNotificationPendingIntentCreator.createNotificationPendingIntent(testRequestCode, testAssaultId)
            mockedNotificationIntentRequestCodeStorage.save(testRequestCode)
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                any(),
                AlarmManager.RTC_WAKEUP,
                range(expectedDelay - 3, expectedDelay + 3),
                any()
            )
        }
    }

    @Test
    fun cancel() {
        alarmManagerNotificationScheduler.cancel(mockedAssault)
        verifyOrder {
            mockedNotificationIntentRequestCodeGenerator.getRequestCodeForAssault(mockedAssault)
            mockedNotificationPendingIntentCreator.createNotificationPendingIntent(testRequestCode)
            mockedPendingIntent.cancel()
            mockedAlarmManager.cancel(mockedPendingIntent)
            mockedNotificationIntentRequestCodeStorage.delete(testRequestCode)
        }
    }

    @Test
    fun cancelAll() {
        val testRequestIdSet = setOf(1, 2, 3)
        val capturedIds = mutableListOf<Int>()
        every { mockedNotificationIntentRequestCodeStorage.getAll() } returns testRequestIdSet
        every {
            mockedNotificationPendingIntentCreator.createNotificationPendingIntent(
                capture(capturedIds),
                null
            )
        } returns mockedPendingIntent

        alarmManagerNotificationScheduler.cancelAll()

        assertTrue(capturedIds.toSet() == testRequestIdSet)
        verify(exactly = testRequestIdSet.size) { mockedPendingIntent.cancel() }
        verify(exactly = testRequestIdSet.size) { mockedAlarmManager.cancel(mockedPendingIntent) }
        verify { mockedNotificationIntentRequestCodeStorage.deleteAll() }
    }
}