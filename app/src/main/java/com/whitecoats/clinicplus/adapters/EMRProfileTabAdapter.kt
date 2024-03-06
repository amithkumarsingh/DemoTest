package com.whitecoats.clinicplus.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.whitecoats.clinicplus.fragments.EMRFamilybgTabFragment
import com.whitecoats.clinicplus.fragments.EMRProfileTabFragment

class EMRProfileTabAdapter(fm: FragmentManager, private val patientID: Int) :
    FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = EMRProfileTabFragment.newInstance(patientID)
        } else if (position == 1) {
            fragment = EMRFamilybgTabFragment.newInstance(patientID)
        }
        assert(fragment != null)
        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }
}