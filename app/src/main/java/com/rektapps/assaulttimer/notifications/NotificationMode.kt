package com.rektapps.assaulttimer.notifications

enum class NotificationMode {
    AUTO, MANUAL;

    companion object {
        fun getById(id: Int) = enumValues<NotificationMode>().first { it.getId() == id }
    }

    fun getId() = when (this) {
        AUTO -> 0
        MANUAL -> 1
    }

}