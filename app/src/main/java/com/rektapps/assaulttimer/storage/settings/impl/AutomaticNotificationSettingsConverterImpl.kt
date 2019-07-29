package com.rektapps.assaulttimer.storage.settings.impl

import android.util.Log
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import com.rektapps.assaulttimer.storage.settings.AutomaticNotificationSettingsConverter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class AutomaticNotificationSettingsConverterImpl @Inject constructor():
    AutomaticNotificationSettingsConverter {

    override fun settingsToString(settings: Map<Region, List<AssaultType>>): String {
        val root = JSONObject()
        settings.filter { it.value.isNotEmpty() }.forEach { (selectedRegion, selectedAssaultTypes) ->
            val selectedAssaultTypesIdsJsonArray = JSONArray()
            selectedAssaultTypes.forEach { selectedAssaultTypesIdsJsonArray.put(it.getId()) }
            root.put(selectedRegion.getId().toString(), selectedAssaultTypesIdsJsonArray)
        }
        return root.toString()
    }

    override fun stringToSettings(settingsString: String): Map<Region, List<AssaultType>> {
        val automaticNotificationPreferences = mutableMapOf<Region, List<AssaultType>>()
        if(settingsString.isEmpty())
            return automaticNotificationPreferences

        val preferencesJsonObject = JSONObject(settingsString)

        enumValues<Region>().forEach { region ->
            try {
                val selectedAssaultTypesIdsJsonArray = preferencesJsonObject.getJSONArray(region.getId().toString())
                val selectedAssaultTypesList = mutableListOf<AssaultType>()

                for(i in 0 until selectedAssaultTypesIdsJsonArray.length()){
                    val assaultType = AssaultType.values().find { assaultType ->  assaultType.getId() == selectedAssaultTypesIdsJsonArray.getInt(i) }!!
                    selectedAssaultTypesList.add(assaultType)
                }

                automaticNotificationPreferences[region] = selectedAssaultTypesList
            }

            catch (jsonException: JSONException){
                Log.d("Preference converter", "Didn't find selected assault type for region ${region.name}")
            }
        }
        return automaticNotificationPreferences
    }
}