package com.rektapps.assaulttimer.model.enums

enum class AssaultType {
    BFA, LEGION;

    companion object {
        fun getById(id: Int) = enumValues<AssaultType>().first { it.getId() == id }
    }


    fun getId() = when (this) {
        LEGION -> 1
        BFA -> 2
    }
}