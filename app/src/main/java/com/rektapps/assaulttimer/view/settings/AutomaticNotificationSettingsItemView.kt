package com.rektapps.assaulttimer.view.settings

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.view.forEach
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.utils.getTitleIDForAssaultType
import com.rektapps.assaulttimer.utils.getTitleIDForRegion
import kotlinx.android.synthetic.main.automatic_notifications_settings_item.view.*

@SuppressLint("ViewConstructor")
class AutomaticNotificationSettingsItemView @JvmOverloads constructor(
    context: Context,
    region: Region,
    preselectedAssaultTypes: List<AssaultType>? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val assaultTypeCheckboxesMap = mutableMapOf<AssaultType, CheckBox>()
    private val bundleKey = "state"

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.automatic_notifications_settings_item, this)
        notificationRegionName.setText(getTitleIDForRegion(region))
        createAssaultTypeCheckBoxes(preselectedAssaultTypes)
    }

    private fun createAssaultTypeCheckBoxes(preselectedAssaultTypes: List<AssaultType>?) {
        enumValues<AssaultType>().forEach { assaultType ->
            val checkBox = CheckBox(context)
            checkBox.setText(getTitleIDForAssaultType(assaultType))
            checkBox.id = assaultType.getId()
            checkBox.isChecked = preselectedAssaultTypes?.contains(assaultType) ?: false
            checkBox.isSaveFromParentEnabled = false
            checkBox.isSaveEnabled = false
            assaultTypeCheckboxesMap[assaultType] = checkBox
            assaultTypeCheckBoxesContainer.addView(checkBox)
        }
    }

    fun getSelectedAssaultTypes(): List<AssaultType> =
        assaultTypeCheckboxesMap.filter { it.value.isChecked }.map { it.key }


    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(bundleKey, super.onSaveInstanceState())
        assaultTypeCheckBoxesContainer.forEach {
            val checkBox = it as CheckBox
            bundle.putBoolean(checkBox.id.toString(), checkBox.isChecked)
        }
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(bundleKey))
            assaultTypeCheckBoxesContainer.forEach {
                val checkBox = it as CheckBox
                checkBox.isChecked = state.getBoolean(checkBox.id.toString())
            }
        }
    }

}