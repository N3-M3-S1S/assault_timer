package com.rektapps.assaulttimer.notifications.composer

import android.content.Context
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.time.formatter.AssaultDateTimeFormatter
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.joda.time.DateTime
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test

class AssaultNotificationComposerImplTest {
    companion object {
        @MockK
        private lateinit var mockedContext: Context
        @MockK
        private lateinit var mockedAssaultDateTimeFormatter: AssaultDateTimeFormatter
        @MockK
        private lateinit var mockedAssault: Assault
        @InjectMockKs
        private lateinit var assaultNotificationComposerImpl: AssaultNotificationComposerImpl

        @BeforeClass
        @JvmStatic
        fun setUp() = MockKAnnotations.init(this)
    }


    @Test
    fun testCompose() {
        val testStartDate = DateTime(2019, 1, 1, 12, 0)
        val testEndDate = DateTime(2019, 1, 2, 13, 0)

        every { mockedAssault.region } returns Region.NA
        every { mockedAssault.type } returns AssaultType.BFA
        every { mockedAssault.start } returns testStartDate
        every { mockedAssault.end } returns testEndDate
        every { mockedAssaultDateTimeFormatter.getAssaultListItemDateTimeString(any()) } answers { arg<DateTime>(0).toString() }

        every { mockedContext.getString(any()) } answers {
            if (arg<Int>(0) == R.string.BFATitle)
                AssaultType.BFA.name
            else
                Region.NA.name
        }

        every { mockedContext.getString(any(), *varargAll { nArgs == 2 }) } answers {
            val varargs = arg<Array<Any>>(1)
            "${varargs[0]} ${varargs[1]}"
        }

        val message = assaultNotificationComposerImpl.compose(mockedAssault)

        verify {
            mockedContext.getString(R.string.BFATitle)
            mockedContext.getString(R.string.NATitle)
            mockedContext.getString(R.string.assaultNotificationTitle, AssaultType.BFA.name, Region.NA.name)
            mockedContext.getString(
                R.string.assaultNotificationMessage,
                testStartDate.toString(),
                testEndDate.toString()
            )
            mockedAssaultDateTimeFormatter.getAssaultListItemDateTimeString(testStartDate)
            mockedAssaultDateTimeFormatter.getAssaultListItemDateTimeString(testEndDate)
        }

        assertTrue(message.title == "${AssaultType.BFA.name} ${Region.NA.name}")
        assertTrue(message.message == "$testStartDate $testEndDate")
    }


}