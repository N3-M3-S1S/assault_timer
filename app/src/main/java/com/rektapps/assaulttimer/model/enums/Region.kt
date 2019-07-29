package com.rektapps.assaulttimer.model.enums

enum class Region {
    EU, NA;

    companion object {
        fun getById(id: Int) = enumValues<Region>().first { it.getId() == id }
    }

    fun getId() = when (this) {
        EU -> 1
        NA -> 2
    }
}