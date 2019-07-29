package com.rektapps.assaulttimer.utils

import androidx.lifecycle.LiveData

val <T> LiveData<T>.notNullValue
    get() = this.value!!




