package com.whitecoats.clinicplus

import android.view.View
import org.json.JSONObject

interface SettingBlockTimeClickListener {
    fun onItemClick(v: View?, position: Int, timeBlockStatus: JSONObject?, clickType: String)
}