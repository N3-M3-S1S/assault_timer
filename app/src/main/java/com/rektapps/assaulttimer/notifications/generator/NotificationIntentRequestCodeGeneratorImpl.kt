package com.rektapps.assaulttimer.notifications.generator

import com.rektapps.assaulttimer.model.entity.Assault
import javax.inject.Inject

class NotificationIntentRequestCodeGeneratorImpl @Inject constructor() : NotificationIntentRequestCodeGenerator {

    override fun getRequestCodeForAssault(assault: Assault): Int {
        val regionId = assault.region.getId()
        val assaultTypeId = assault.type.getId()
        return (0.5 * (regionId + assaultTypeId) * (regionId + assaultTypeId + 1) + assaultTypeId).toInt() //see Cantor pairing function
    }
}