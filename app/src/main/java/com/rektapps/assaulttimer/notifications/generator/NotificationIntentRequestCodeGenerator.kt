package com.rektapps.assaulttimer.notifications.generator

import com.rektapps.assaulttimer.model.entity.Assault

interface NotificationIntentRequestCodeGenerator {
    fun getRequestCodeForAssault(assault: Assault): Int
}