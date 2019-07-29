package com.rektapps.assaulttimer.utils

import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime


fun doForEachRegionAndAssaultType(func: (region: Region, assaultType: AssaultType) -> Unit) {
    enumValues<Region>().forEach { region ->
        enumValues<AssaultType>().forEach { assaultType ->
            func(region, assaultType)
        }
    }
}

fun getNowDateTimeWithRoundedSeconds(): DateTime = DateTime.now().secondOfMinute().roundHalfCeilingCopy()

fun <T> subscribeIOObserveMainSingle(): SingleTransformer<T, T> = SingleTransformer { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
