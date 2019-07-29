package com.rektapps.assaulttimer.model.provider.impl

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState
import io.mockk.every
import io.mockk.mockkClass
import junit.framework.Assert.assertTrue
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Test

class AssaultStateDurationProviderImplTest {
    private val mockedAssault = mockkClass(Assault::class)
    private val assaultStateDurationProvider = AssaultStateDurationProviderImpl()
    private val testDuration = Duration.standardSeconds(10)
    private val resultRangeMatcher = allOf(greaterThanOrEqualTo(testDuration.minus(Duration.standardSeconds(1))), lessThanOrEqualTo(testDuration.plus(Duration.standardSeconds(1))))

    @Test
    fun incomingAssault() {
        every { mockedAssault.start } returns DateTime.now().plus(testDuration)
        assertThat(  assaultStateDurationProvider.getAssaultStateDuration(mockedAssault, AssaultState.INCOMING), resultRangeMatcher)
    }

    @Test
    fun activeOrEndingAssault() {
        every { mockedAssault.start } returns DateTime.now()
        every { mockedAssault.end } returns DateTime.now().plus(testDuration)
        val activeDuration =
            assaultStateDurationProvider.getAssaultStateDuration(mockedAssault, AssaultState.ACTIVE)
        val endingDuration =
            assaultStateDurationProvider.getAssaultStateDuration(mockedAssault, AssaultState.ENDING)
        assertTrue(activeDuration == endingDuration)
        assertThat(activeDuration, resultRangeMatcher)
    }

    @Test
    fun endedAssault() {
        assertTrue(assaultStateDurationProvider.getAssaultStateDuration(mockedAssault, AssaultState.ENDED) == Duration.ZERO)
    }

}

