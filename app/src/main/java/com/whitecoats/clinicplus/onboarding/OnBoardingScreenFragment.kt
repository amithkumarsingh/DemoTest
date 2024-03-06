package com.whitecoats.clinicplus.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.whitecoats.clinicplus.R

class OnBoardingScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_onboarding, container, false)
        val viewOne = view.findViewById<RelativeLayout>(R.id.onBoardingViewOne)
        val viewTwo = view.findViewById<RelativeLayout>(R.id.onBoardingViewTwo)
        val viewThree = view.findViewById<RelativeLayout>(R.id.onBoardingViewThree)
        val viewFour = view.findViewById<RelativeLayout>(R.id.onBoardingViewFour)
        val viewFive = view.findViewById<RelativeLayout>(R.id.onBoardingViewFive)
        var position = 0
        if (arguments != null) {
            position = requireArguments().getInt("PageNumber")
        }
        when (position) {
            2 -> {
                viewTwo.visibility = View.VISIBLE
                viewOne.visibility = View.GONE
            }
            3 -> {
                viewOne.visibility = View.VISIBLE
                viewTwo.visibility = View.GONE
            }
            4 -> {
                viewThree.visibility = View.VISIBLE
            }
            5 -> {
                viewFour.visibility = View.VISIBLE
            }
            6 -> {
                viewFive.visibility = View.VISIBLE
            }
        }
        return view
    }
}