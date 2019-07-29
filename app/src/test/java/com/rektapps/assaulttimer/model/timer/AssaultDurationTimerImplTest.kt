package com.rektapps.assaulttimer.model.timer

import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.model.provider.AssaultStateDurationProvider
import com.rektapps.assaulttimer.model.provider.AssaultStateProvider
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers.trampoline
import io.reactivex.schedulers.TestScheduler
import org.joda.time.Duration
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.TimeUnit

class AssaultDurationTimerImplTest {
    companion object {
        @MockK
        private lateinit var assaultStateDurationProvider: AssaultStateDurationProvider
        @MockK
        private lateinit var assaultStateProvider: AssaultStateProvider
        @InjectMockKs
        private lateinit var assaultDurationTimerImpl: AssaultDurationTimerImpl

        private val testScheduler = TestScheduler()


        @BeforeClass
        @JvmStatic
        fun setUp() {
            MockKAnnotations.init(this)
            RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { trampoline() }
        }

    }

    @Before
    fun resetMocks() = clearAllMocks()

    @Test
    fun timerTicks() {
        var testDuration = Duration.standardSeconds(5)
        val onTick: (Duration) -> Unit = { testDuration = testDuration.minus(Duration.standardSeconds(1)) }
        val spyOnTick = spyk(onTick)

        every { assaultStateDurationProvider.getAssaultStateDuration(any(), any()) } returns testDuration
        every { assaultStateProvider.getAssaultState(any()) } answers {
            if (testDuration.standardSeconds > 0)
                AssaultState.ACTIVE
            else
                AssaultState.ENDED
        }

        assaultDurationTimerImpl.start(mockk(), spyOnTick, mockk(relaxed = true))

        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        assertTrue(testDuration.isEqual(Duration.ZERO))

        verifySequence {
            spyOnTick(Duration.standardSeconds(4))
            spyOnTick(Duration.standardSeconds(3))
            spyOnTick(Duration.standardSeconds(2))
            spyOnTick(Duration.standardSeconds(1))
            spyOnTick(Duration.standardSeconds(0))
        }
    }


    @Test
    fun restartIfNotEnded() {
        val onTick = mockk<(Duration) -> Unit>(relaxed = true)
        every { assaultStateProvider.getAssaultState(any()) } returns AssaultState.INCOMING
        every { assaultStateDurationProvider.getAssaultStateDuration(any(), any()) } returns Duration.standardSeconds(5)

        assaultDurationTimerImpl.start(mockk(), onTick, mockk(relaxed = true))
        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS)
        verifyOrder {
            onTick(Duration.standardSeconds(4))
            onTick(Duration.standardSeconds(3))
            onTick(Duration.standardSeconds(2))
            onTick(Duration.standardSeconds(1))
            onTick(Duration.standardSeconds(0))
        }


        clearMocks(onTick)
        every { assaultStateProvider.getAssaultState(any()) } returns AssaultState.ACTIVE
        every { assaultStateDurationProvider.getAssaultStateDuration(any(), any()) } returns Duration.standardSeconds(3)
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        verifyOrder {
            onTick(Duration.standardSeconds(2))
            onTick(Duration.standardSeconds(1))
            onTick(Duration.standardSeconds(0))
        }

        clearMocks(onTick)
        every { assaultStateProvider.getAssaultState(any()) } returns AssaultState.ENDED
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        verify(inverse = true) { onTick(any()) }

    }


    @Test
    fun fromActiveToEnding() {
        var testDuration = Duration.standardSeconds(6)
        val onTick: (Duration) -> Unit = { testDuration = testDuration.minus(Duration.standardSeconds(1))}
        val spyOnTick = spyk(onTick)
        val onAssaultStateChanged = mockk<(AssaultState) -> Unit>(relaxed = true)

        every { assaultStateDurationProvider.getAssaultStateDuration(any(), any()) } returns testDuration
        every { assaultStateProvider.getAssaultState(any()) } answers {
            when {
                testDuration == Duration.ZERO -> AssaultState.ENDED
                testDuration.standardSeconds < 3 -> AssaultState.ENDING
                else -> AssaultState.ACTIVE
            }
        }

        assaultDurationTimerImpl.start(mockk(), spyOnTick, onAssaultStateChanged)

        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)
        assertTrue(testDuration == Duration.ZERO)
        verifySequence {
            onAssaultStateChanged(AssaultState.ACTIVE)
            spyOnTick(Duration.standardSeconds(5))
            spyOnTick(Duration.standardSeconds(4))
            spyOnTick(Duration.standardSeconds(3))
            spyOnTick(Duration.standardSeconds(2))
            onAssaultStateChanged(AssaultState.ENDING)
            spyOnTick(Duration.standardSeconds(1))
            spyOnTick(Duration.standardSeconds(0))
            onAssaultStateChanged(AssaultState.ENDED)
        }
    }

    @Test
    fun noTicksIfAssaultEnded() {
        val onTick = mockk<(Duration) -> Unit>()

        every { assaultStateDurationProvider.getAssaultStateDuration(any(), any()) } returns Duration.standardSeconds(10)
        every { assaultStateProvider.getAssaultState(any()) } returns AssaultState.ENDED

        assaultDurationTimerImpl.start(mockk(), onTick, mockk(relaxed = true))
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify(inverse = true) {
            onTick(any())
        }

    }
}