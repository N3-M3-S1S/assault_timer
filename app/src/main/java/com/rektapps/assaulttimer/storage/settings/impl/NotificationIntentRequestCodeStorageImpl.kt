package com.rektapps.assaulttimer.storage.settings.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.storage.settings.NotificationIntentRequestCodeStorage
import javax.inject.Inject

class NotificationIntentRequestCodeStorageImpl @Inject constructor(private val sharedPreferences: SharedPreferences, appContext:Context) :
    NotificationIntentRequestCodeStorage {

    private val notificationRequestCodesKey = appContext.getString(R.string.notificationRequestCodesKey)

    override fun save(requestCode: Int) {
        sharedPreferences.edit {
            val set = getIdSet()
            set.add(requestCode.toString())
            putStringSet(notificationRequestCodesKey, set)
        }
    }

    override fun delete(requestCode: Int) {
        sharedPreferences.edit {
            val set = getIdSet()
            if (set.isEmpty())
                remove(notificationRequestCodesKey)
            else
                putStringSet(notificationRequestCodesKey, set)
        }
    }

    override fun deleteAll() {
        sharedPreferences.edit { remove(notificationRequestCodesKey) }
    }

    override fun getAll() = getIdSet().map { it.toInt() }.toSet()

    private fun getIdSet() = sharedPreferences.getStringSet(notificationRequestCodesKey, mutableSetOf())!!

}