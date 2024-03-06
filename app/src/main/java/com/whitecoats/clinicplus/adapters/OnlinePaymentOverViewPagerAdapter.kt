package com.whitecoats.clinicplus.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.whitecoats.clinicplus.fragments.OnlinePaymentActiveAndSettlementFragment
import com.whitecoats.clinicplus.fragments.OnlinePaymentRefundFragment
import org.json.JSONObject

class OnlinePaymentOverViewPagerAdapter(
    fm: FragmentManager?,
    private val onlinePaymentOverviewObjectData: JSONObject
) : FragmentStatePagerAdapter(
    fm!!
) {
    override fun getItem(position: Int): Fragment {
        var f = Fragment()
        when (position) {
            0 -> f = OnlinePaymentActiveAndSettlementFragment(onlinePaymentOverviewObjectData)
            1 -> f = OnlinePaymentRefundFragment(onlinePaymentOverviewObjectData)
        }
        return f
    }

    override fun getCount(): Int {
        return totalPage
    }

    companion object {
        var totalPage = 2
    }
}