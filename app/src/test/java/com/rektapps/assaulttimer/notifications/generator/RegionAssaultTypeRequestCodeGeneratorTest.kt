package com.rektapps.assaulttimer.notifications.generator

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationIntentRequestCodeGeneratorImplTest {

    @Test
    fun getRequestCodeForAssault() {
        val notificationIntentRequestCodeGenerator = NotificationIntentRequestCodeGeneratorImpl()
        val mockedAssault = mockkClass(Assault::class)
        every { mockedAssault.region } returns Region.EU
        every { mockedAssault.type } returns AssaultType.LEGION
        val generatedRequestCode = notificationIntentRequestCodeGenerator.getRequestCodeForAssault(mockedAssault)
        assertTrue(generatedRequestCode == 4)

    }
}