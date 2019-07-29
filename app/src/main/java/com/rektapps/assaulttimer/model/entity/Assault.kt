package com.rektapps.assaulttimer.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import org.joda.time.DateTime

@Entity
data class Assault(
    val start: DateTime,
    val end: DateTime,
    val region: Region,
    val type: AssaultType,
    @PrimaryKey val id: Long? = null
)