package com.rektapps.assaulttimer.viewmodel.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rektapps.assaulttimer.time.formatter.OnTimeFormatChangedListener
import com.rektapps.assaulttimer.utils.SingleLiveEvent
import javax.inject.Inject

class UiSettingsViewModel @Inject constructor(private val onTimeFormatChangedListener: OnTimeFormatChangedListener) :
    ViewModel() {
    private val onDarkModeToggled = SingleLiveEvent<Unit>()

    fun getOnDarkModeToggledEvent(): LiveData<Unit> = onDarkModeToggled

    fun onDarkModeToggled() {
        onDarkModeToggled.call()
    }

    fun onTimeFormatToggled(isAMPMSelected: Boolean) {
        onTimeFormatChangedListener.onTimeFormatChanged(isAMPMSelected)
    }

}