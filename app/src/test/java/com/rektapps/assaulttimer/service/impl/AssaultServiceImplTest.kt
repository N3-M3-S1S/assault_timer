package com.rektapps.assaulttimer.service.impl

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.storage.dao.AssaultDao
import com.rektapps.assaulttimer.service.AssaultsUpdater
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verifyOrder
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.BeforeClass
import org.junit.Test

class AssaultServiceImplTest {

    companion object {
        @MockK
        private lateinit var mockedAssaultsUpdater: AssaultsUpdater
        @MockK
        private lateinit var mockedAssaultDao: AssaultDao

        @InjectMockKs
        private lateinit var assaultRepositoryImpl: AssaultServiceImpl


        @BeforeClass
        @JvmStatic
        fun setUp() = MockKAnnotations.init(this)

    }


    @Test
    fun getAssaults() {
        val testRegion = Region.EU
        val testAssaultType = AssaultType.LEGION

        every { mockedAssaultDao.getAssaults(any(), any()) } returns Flowable.just(emptyList())
        every { mockedAssaultsUpdater.update(any(), any()) } returns Completable.complete()

        assaultRepositoryImpl.getAssaults(testRegion, testAssaultType).blockingFirst()
        verifyOrder {
            mockedAssaultsUpdater.update(testRegion, testAssaultType)
            mockedAssaultDao.getAssaults(testRegion, testAssaultType)
        }

    }


}