package com.rektapps.assaulttimer.notifications.composer

import android.content.Context
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.notifications.AssaultStartNotificationMessage
import com.rektapps.assaulttimer.time.formatter.AssaultDateTimeFormatter
import javax.inject.Inject

class AssaultNotificationComposerImpl @Inject constructor(
    private val context: Context,
    private val assaultDateTimeFormatter: AssaultDateTimeFormatter
) :
    AssaultNotificationComposer {

    override fun compose(assaultEntity: Assault): AssaultStartNotificationMessage {
        val title = getNotificationTitle(assaultEntity)
        val message = getNotificationMessage(assaultEntity)
        return AssaultStartNotificationMessage(title, message)
    }


    private fun getNotificationTitle(assault: Assault): String {
        val assaultTypeString = context.getString(
            when (assault.type) {
                AssaultType.BFA -> R.string.BFATitle
                AssaultType.LEGION -> R.string.LegionTitle
            }
        )

        val regionString = context.getString(
            when (assault.region) {
                Region.EU -> R.string.EUTitle
                Region.NA -> R.string.NATitle
            }
        )


        return context.getString(R.string.assaultNotificationTitle, assaultTypeString, regionString)


    }




    private fun getNotificationMessage(assault: Assault): String {
        val assaultStartDateTimeString = assaultDateTimeFormatter.getAssaultListItemDateTimeString(assault.start)
        val assaultEndDateTimeString = assaultDateTimeFormatter.getAssaultListItemDateTimeString(assault.end)
        return context.getString(
            R.string.assaultNotificationMessage,
            assaultStartDateTimeString,
            assaultEndDateTimeString
        )
    }


}