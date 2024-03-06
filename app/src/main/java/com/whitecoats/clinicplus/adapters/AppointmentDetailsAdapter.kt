package com.whitecoats.clinicplus.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.whitecoats.clinicplus.fragments.ApptDetailsFragment

class AppointmentDetailsAdapter(fm: FragmentManager?, private val noOfTabs: Int) :
    FragmentStatePagerAdapter(
        fm!!
    ) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            ApptDetailsFragment()
        } else null!!
    }

    override fun getCount(): Int {
        return noOfTabs
    }
}