package com.rektapps.assaulttimer.view.assaultsSchedule

import com.rektapps.assaulttimer.model.entity.Assault

data class AssaultListItem(val assault: Assault, var isNotificationEnabled: Boolean = false)