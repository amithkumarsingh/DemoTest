package com.whitecoats.clinicplus.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class OnBoardingTabAdapter(fm: FragmentManager?, private var mNumOfTabs: Int) : FragmentStatePagerAdapter(
    fm!!
) {
    private lateinit var args:Bundle
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                val tab1 = OnBoardingScreenFragment()
                args = Bundle()
                args.putInt("PageNumber", 1)
                tab1.arguments = args
                tab1
            }
            1 -> {
                val tab2 = OnBoardingScreenFragment()
                args = Bundle()
                args.putInt("PageNumber", 2)
                tab2.arguments = args
                tab2
            }
            2 -> {
                val tab3 = OnBoardingScreenFragment()
                args = Bundle()
                args.putInt("PageNumber", 3)
                tab3.arguments = args
                tab3
            }
            3 -> {
                val tab4 = OnBoardingScreenFragment()
                args = Bundle()
                args.putInt("PageNumber", 4)
                tab4.arguments = args
                tab4
            }
            4 -> {
                val tab5 = OnBoardingScreenFragment()
                args = Bundle()
                args.putInt("PageNumber", 5)
                tab5.arguments = args
                tab5
            }
            5 -> {
                val tab6 = OnBoardingScreenFragment()
                args = Bundle()
                args.putInt("PageNumber", 6)
                tab6.arguments = args
                tab6
            }
            else -> null!!
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }
}