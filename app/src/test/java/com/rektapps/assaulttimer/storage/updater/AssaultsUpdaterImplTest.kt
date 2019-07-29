package com.rektapps.assaulttimer.storage.updater

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.model.factory.AssaultsFactory
import com.rektapps.assaulttimer.service.impl.AssaultsUpdaterImpl
import com.rektapps.assaulttimer.storage.dao.AssaultDao
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class AssaultsUpdaterImplTest {

    companion object {
        @MockK(relaxUnitFun = true)
        private lateinit var mockedAssaultDao: AssaultDao
        @MockK
        private lateinit var mockedAssaultsFactory: AssaultsFactory
        @InjectMockKs
        private lateinit var assaultsUpdaterImpl: AssaultsUpdaterImpl

        @BeforeClass
        @JvmStatic
        fun setUp() {
            MockKAnnotations.init(this)
            every { mockedAssaultsFactory.createAssaults(any(), any(), any(), any()) } returns Single.just(emptyList())
            every { mockedAssaultDao.addAll(any()) } returns Completable.complete()
        }
    }

    private val testRegion = Region.EU
    private val testType = AssaultType.LEGION

    @Before
    fun resetMocks() = clearAllMocks(answers = false)

    @Test
    fun updateWhenNoAssaults() {
        every { mockedAssaultDao.hasAssaults(any(), any()) } returns Single.just(false)
        assaultsUpdaterImpl.update(testRegion, testType).blockingAwait()
        verifyOrder {
            mockedAssaultDao.hasAssaults(testRegion, testType)
            mockedAssaultsFactory.createAssaults(any(), testRegion, testType, any())
            mockedAssaultDao.addAll(any())
        }
    }


    @Test
    fun updateWhenNoEndedAssaults() {
        every { mockedAssaultDao.hasAssaults(any(), any()) } returns Single.just(true)
        every { mockedAssaultDao.getAssaultsWithLessOrEqualEndDateTime(any(), any(), any()) } returns Single.just(
            emptyList()
        )

        assaultsUpdaterImpl.update(testRegion, testType).blockingAwait()

        verify(inverse = true) {
            mockedAssaultsFactory.createAssaults(any(), any(), any(), any())
            mockedAssaultDao.deleteAndAddTransaction(any(), any())
        }
    }


    @Test
    fun updateEndedAssaults() {
        val testEndedAssaults = listOf(mockk<Assault>())
        every { mockedAssaultDao.hasAssaults(any(), any()) } returns Single.just(true)
        every { mockedAssaultDao.getAssaultsWithLessOrEqualEndDateTime(any(), any(), any()) } returns Single.just(
            testEndedAssaults
        )

        assaultsUpdaterImpl.update(testRegion, testType).blockingAwait()

        verifyOrder {
            mockedAssaultDao.hasAssaults(testRegion, testType)
            mockedAssaultDao.getAssaultsWithLessOrEqualEndDateTime(any(), testRegion, testType)
            mockedAssaultsFactory.createAssaults(any(), testRegion, testType, testEndedAssaults.count())
            mockedAssaultDao.deleteAndAddTransaction(testEndedAssaults, any())
        }


    }


}