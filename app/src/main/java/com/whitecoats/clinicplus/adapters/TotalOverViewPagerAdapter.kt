package com.whitecoats.clinicplus.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.whitecoats.clinicplus.fragments.TotalPaymentAndApptCollectionFragment
import com.whitecoats.clinicplus.fragments.TotalSettlementAndRefundCollectionFragment
import org.json.JSONObject

class TotalOverViewPagerAdapter(
    fm: FragmentManager?,
    private val totalOverviewObjectData: JSONObject
) : FragmentStatePagerAdapter(
    fm!!
) {
    override fun getItem(position: Int): Fragment {
        var f = Fragment()
        when (position) {
            0 -> f = TotalPaymentAndApptCollectionFragment(totalOverviewObjectData)
            1 -> f = TotalSettlementAndRefundCollectionFragment(totalOverviewObjectData)
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