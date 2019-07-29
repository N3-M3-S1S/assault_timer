package com.rektapps.assaulttimer.model.timer

import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.model.provider.AssaultStateDurationProvider
import com.rektapps.assaulttimer.model.provider.AssaultStateProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import org.joda.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AssaultDurationTimerImpl @Inject constructor(
    private val assaultStateDurationProvider: AssaultStateDurationProvider,
    private val assaultStateProvider: AssaultStateProvider
) : AssaultDurationTimer {

    private lateinit var timerDisposable: Disposable


    override fun start(
        assault: Assault,
        onTick: (remainingDuration: Duration) -> Unit,
        onAssaultStateChanged: (AssaultState) -> Unit
    ) {
        stop()

        var assaultState = assaultStateProvider.getAssaultState(assault)

        if (assaultState == AssaultState.ENDED) {
            onAssaultStateChanged(assaultState)
        }
        else {
            var assaultStateDuration = assaultStateDurationProvider.getAssaultStateDuration(assault, assaultState)

            timerDisposable = Observable.intervalRange(0, assaultStateDuration.standardSeconds + 1, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onAssaultStateChanged(assaultState) }
                .doOnComplete {
                    assaultState = assaultStateProvider.getAssaultState(assault)
                    onAssaultStateChanged(assaultState)
                    if (assaultState != AssaultState.ENDED)
                        start(assault, onTick, onAssaultStateChanged)
                }

                .subscribeBy {

                    assaultStateDuration = assaultStateDuration.minus(1000)

                    if (assaultStateDuration.standardSeconds >= 0)
                        onTick(assaultStateDuration)

                    if (assaultState == AssaultState.ACTIVE) {
                        val newAssaultState = assaultStateProvider.getAssaultState(assault)

                        if (newAssaultState == AssaultState.ENDING) {
                            assaultState = newAssaultState
                            onAssaultStateChanged(assaultState)
                        }
                    }
                }
        }
    }


    override fun stop() {
        if (::timerDisposable.isInitialized)
            timerDisposable.dispose()
    }

}