package com.rektapps.assaulttimer.model.provider.impl

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.utils.getNowDateTimeWithRoundedSeconds
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Assert.assertTrue
import org.junit.Test

class AssaultStateProviderImplTest {
    private val mockedAssault = mockkClass(Assault::class)
    private val assaultStateProvider = AssaultStateProviderImpl()

    @Test
    fun incomingAssault() {
        every { mockedAssault.start } returns getNowDateTimeWithRoundedSeconds().plusMinutes(1)
        every { mockedAssault.end } returns getNowDateTimeWithRoundedSeconds().plusMinutes(2)
        assertTrue(assaultStateProvider.getAssaultState(mockedAssault) == AssaultState.INCOMING)
    }

    @Test
    fun activeAssault() {
        every { mockedAssault.start } returns getNowDateTimeWithRoundedSeconds()
        every { mockedAssault.end } returns getNowDateTimeWithRoundedSeconds().plusHours(2)
        assertTrue(assaultStateProvider.getAssaultState(mockedAssault) == AssaultState.ACTIVE)
    }


    @Test
    fun endingAssault() {
        every { mockedAssault.start } returns getNowDateTimeWithRoundedSeconds()
        every { mockedAssault.end } returns getNowDateTimeWithRoundedSeconds().plusMinutes(30)
        assertTrue(assaultStateProvider.getAssaultState(mockedAssault) == AssaultState.ENDING)
    }

    @Test
    fun endedAssault() {
        every { mockedAssault.start } returns getNowDateTimeWithRoundedSeconds().minusMinutes(2)
        every { mockedAssault.end } returns getNowDateTimeWithRoundedSeconds().minusMinutes(1)
        assertTrue(assaultStateProvider.getAssaultState(mockedAssault) == AssaultState.ENDED)
    }

}
