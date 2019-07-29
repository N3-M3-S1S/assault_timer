package com.rektapps.assaulttimer.service.impl

import androidx.room.EmptyResultSetException
import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.model.provider.AssaultStateProvider
import com.rektapps.assaulttimer.storage.dao.AssaultDao
import com.rektapps.assaulttimer.storage.dao.ManualNotificationDao
import com.rektapps.assaulttimer.service.AssaultsUpdater
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verifyOrder
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.BeforeClass
import org.junit.Test

class IncomingAssaultServiceImplTest {

    companion object {
        @MockK
        private lateinit var assaultUpdater: AssaultsUpdater
        @MockK
        private lateinit var manualNotificationDao: ManualNotificationDao
        @MockK
        private lateinit var assaultDao: AssaultDao
        @MockK
        private lateinit var assaultStateProvider: AssaultStateProvider
        @InjectMockKs
        private lateinit var incomingAssaultServiceImpl: IncomingAssaultServiceImpl

        @BeforeClass
        @JvmStatic
        fun setUp() {
            MockKAnnotations.init(this, relaxUnitFun = true)
            every { assaultUpdater.update(any(), any()) } returns Completable.complete()
        }
    }

    private val testRegion = Region.EU
    private val testType = AssaultType.LEGION

    @Test
    fun getFirstIncomingManualNotificationAssault() {
        every { manualNotificationDao.getSelectedAssault(any(), any()) } returns Single.just(
            mutableListOf(mockk())
        )
        every { assaultStateProvider.getAssaultState(any()) } returns AssaultState.INCOMING
        incomingAssaultServiceImpl.getFirstIncomingManualNotificationAssault(testRegion, testType)
            .test()
            .assertValueCount(1)
    }


    @Test
    fun noSelectedAssaults() {
        every { manualNotificationDao.getSelectedAssault(any(), any()) } returns Single.error(
            EmptyResultSetException("timerTicks")
        )
        incomingAssaultServiceImpl.getFirstIncomingManualNotificationAssault(testRegion, testType)
            .test()
            .assertNoValues()
    }


    @Test
    fun noSelectedIncomingAssaults() {
        every { assaultStateProvider.getAssaultState(any()) } returns AssaultState.ACTIVE
        incomingAssaultServiceImpl.getFirstIncomingManualNotificationAssault(testRegion, testType)
            .test()
            .assertNoValues()
    }

    @Test
    fun getFirstIncomingAssault() {
        every { assaultDao.getAssaults(any(), any()) } returns Flowable.just(mutableListOf(mockk()))
        every { assaultStateProvider.getAssaultState(any()) } returns AssaultState.INCOMING
        incomingAssaultServiceImpl.getFirstIncomingAssault(testRegion, testType).test().assertValueCount(1)
        verifyOrder {
            assaultUpdater.update(testRegion, testType)
            assaultDao.getAssaults(testRegion, testType)
        }
    }
}