package com.rektapps.assaulttimer.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
    entity = Assault::class,
    parentColumns = ["id"],
    childColumns = ["assaultId"],
    onDelete = ForeignKey.CASCADE)] )
data class ManualNotification(@PrimaryKey val assaultId: Long)