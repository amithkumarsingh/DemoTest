package com.whitecoats.clinicplus

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.whitecoats.clinicplus.fragments.DashboardFirstLogin
import com.whitecoats.clinicplus.fragments.DashboardFullMode
import com.whitecoats.clinicplus.fragments.DashboardTabFragment
import com.whitecoats.fragments.AssistantTabFragment
import com.whitecoats.fragments.CaseChannelTabFragment
import com.whitecoats.fragments.GetHelpTabFragment
import org.json.JSONException
import org.json.JSONObject

class MainTabPagerAdapter(
    fm: FragmentManager,
    var mNumOfTabs: Int,
    var caseTabPresent: Boolean,
    private val statusResponse: String
) : FragmentStatePagerAdapter(
    fm
) {
    override fun getItem(position: Int): Fragment {
        Log.d("Position", position.toString() + "")
        Log.d("Tab Count", mNumOfTabs.toString() + "")
        return when (position) {
            0 -> {
                if (statusResponse.isNotEmpty()) {
                    val response: JSONObject?
                    try {
                        response = JSONObject(statusResponse)
                        val statusObj = response.getJSONObject("response").getJSONObject("response")
                            .getJSONObject("general_info")
                        DashboardTabFragment.practiceAdded = statusObj.getInt("is_practice_added")
                        return if (statusObj.getInt("is_practice_added") == 1
                            && statusObj.getInt("is_patient_added") == 1
                        ) {
                            val fullMode = DashboardFullMode()
                            isSupportTabSelected = false
                            fullMode
                        } else {
                            val firstLogin = DashboardFirstLogin()
                            val bundle = Bundle()
                            bundle.putString("response_string", statusResponse)
                            firstLogin.arguments = bundle
                            isSupportTabSelected = false
                            firstLogin
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    val tab1 = DashboardTabFragment()
                    isSupportTabSelected = false
                    return tab1
                }
                val tab2 = AssistantTabFragment()
                isSupportTabSelected = false
                tab2
            }
            1 -> {
                val tab2 = AssistantTabFragment()
                isSupportTabSelected = false
                tab2
            }
            2 -> if (caseTabPresent) {
                val tab3 = CaseChannelTabFragment()
                isSupportTabSelected = false
                tab3
            } else {
                val tab3 = GetHelpTabFragment()
                isSupportTabSelected = true
                tab3
            }
            3 -> {
                val tab4 = GetHelpTabFragment()
                isSupportTabSelected = true
                tab4
            }
            else -> {
                return null!!
            }
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }

    companion object {
        var isSupportTabSelected = false
    }
}