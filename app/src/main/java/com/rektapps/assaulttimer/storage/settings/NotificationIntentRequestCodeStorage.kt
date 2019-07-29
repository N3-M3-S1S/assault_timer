package com.rektapps.assaulttimer.storage.settings

interface NotificationIntentRequestCodeStorage {
    fun save(requestCode: Int)
    fun delete(requestCode: Int)
    fun deleteAll()
    fun getAll(): Set<Int>
}