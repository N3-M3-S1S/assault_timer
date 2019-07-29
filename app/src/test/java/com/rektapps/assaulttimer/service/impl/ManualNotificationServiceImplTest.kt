package com.rektapps.assaulttimer.service.impl

import androidx.room.EmptyResultSetException
import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.entity.ManualNotification
import com.rektapps.assaulttimer.notifications.scheduler.AssaultNotificationScheduler
import com.rektapps.assaulttimer.storage.dao.ManualNotificationDao
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test

class ManualNotificationServiceImplTest {

    companion object {
        @MockK
        private lateinit var mockedManualNotificationDao: ManualNotificationDao
        @MockK
        private lateinit var mockedAssaultNotificationScheduler: AssaultNotificationScheduler
        @MockK
        private lateinit var mockedAssault: Assault
        @InjectMockKs
        private lateinit var selectedAssaultNotificationService: ManualNotificationsServiceImpl
        private const val testAssaultId = 1L


        @BeforeClass
        @JvmStatic
        fun setUp() {
            MockKAnnotations.init(this, relaxUnitFun = true)
            every { mockedAssault.id!! } returns testAssaultId
            every { mockedManualNotificationDao.create(any()) } returns Completable.complete()
            every { mockedManualNotificationDao.deleteByAssaultId(any()) } returns Completable.complete()

        }

    }


    private val singleError = Single.error<ManualNotification>(EmptyResultSetException(""))
    private val singleWithMockedResult = Single.just<ManualNotification>(mockk())


    @Test
    fun enableNotificationForAssault() {
        every { mockedManualNotificationDao.getByAssaultId(any()) } returns singleError
        val isNotificationForAssaultEnabled = selectedAssaultNotificationService.toggleNotificationForAssault(
            mockedAssault
        ).blockingGet()
        verify {
            mockedAssaultNotificationScheduler.schedule(
                mockedAssault
            )
            mockedManualNotificationDao.create(any())
        }
        assertTrue(isNotificationForAssaultEnabled)
    }


    @Test
    fun disableNotificationForAssault() {
        every { mockedManualNotificationDao.getByAssaultId(any()) } returns singleWithMockedResult
        val isNotificationForAssaultEnabled = selectedAssaultNotificationService.toggleNotificationForAssault(
            mockedAssault
        ).blockingGet()
        verify {
            mockedAssaultNotificationScheduler.cancel(
                mockedAssault
            )
            mockedManualNotificationDao.deleteByAssaultId(
                testAssaultId
            )
        }
        assertFalse(isNotificationForAssaultEnabled)
    }


    @Test
    fun assaultSelectedForNotification() {
        every { mockedManualNotificationDao.getByAssaultId(any()) } returns singleWithMockedResult
        selectedAssaultNotificationService.isAssaultSelectedForNotification(
            mockedAssault
        ).test().assertValue(true)
    }


    @Test
    fun assaultNotSelectedForNotification() {
        every { mockedManualNotificationDao.getByAssaultId(any()) } returns singleError
        selectedAssaultNotificationService.isAssaultSelectedForNotification(
            mockedAssault
        ).test().assertValue(false)
    }
}