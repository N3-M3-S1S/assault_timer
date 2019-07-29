package com.rektapps.assaulttimer.view.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.notifications.NotificationMode
import com.rektapps.assaulttimer.viewmodel.NotificationSettingsViewModel
import kotlinx.android.synthetic.main.noitification_mode_dialog.*
import kotlinx.android.synthetic.main.noitification_mode_dialog.view.*

class NotificationModeDialog : DialogFragment() {
    private lateinit var notificationViewModel: NotificationSettingsViewModel
    private val autoNotificationSettingItemViews = mutableMapOf<Region, AutomaticNotificationSettingsItemView>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        notificationViewModel = ViewModelProviders.of(targetFragment!!).get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.noitification_mode_dialog, container)

        initializeAutomaticNotificationPreferenceItems(root)

        root.notificationModeGroup.setOnCheckedChangeListener { _, checkedId ->
            root.autoNotificationsPreferencesContainer.isVisible = checkedId == R.id.notificationTypeAuto
        }

        val checkedNotificationMode = if(notificationViewModel.getLastSelectedNotificationMode() == NotificationMode.AUTO) R.id.notificationTypeAuto else R.id.notificationTypeManual
        root.notificationModeGroup.check(checkedNotificationMode)

        root.dialogOk.setOnClickListener {
            val notificationMode = if(notificationModeGroup.checkedRadioButtonId == R.id.notificationTypeAuto) NotificationMode.AUTO else NotificationMode.MANUAL
            notificationViewModel.setNotificationMode(notificationMode)
            if(notificationMode == NotificationMode.AUTO)
                notificationViewModel.setSelectedAutomaticNotificationPreferences(autoNotificationSettingItemViews.mapValues { mapEntry ->  mapEntry.value.getSelectedAssaultTypes() }.filter { it.value.isNotEmpty() })

            targetFragment!!.onActivityResult(targetRequestCode, notificationMode.getId(),null)
            dismiss()
        }

        root.dialogCancel.setOnClickListener { dismiss() }
        return root
    }

    private fun initializeAutomaticNotificationPreferenceItems(root: View) {
        val lastAutomaticNotificationModePreferences =
            notificationViewModel.getLastSelectedAutomaticNotificationPreferences()

        enumValues<Region>().forEach { region ->
            val preselectedAssaultTypes = lastAutomaticNotificationModePreferences.getOrElse(region) { null }
            val automaticNotificationPreferenceView =
                AutomaticNotificationSettingsItemView(
                    context!!,
                    region,
                    preselectedAssaultTypes
                )
            autoNotificationSettingItemViews[region] = automaticNotificationPreferenceView
            automaticNotificationPreferenceView.id = region.getId()
            root.autoNotificationsPreferencesContainer.addView(automaticNotificationPreferenceView)
        }
    }

    override fun onStart() {
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        super.onStart()
    }



}