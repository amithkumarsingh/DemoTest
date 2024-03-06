package com.whitecoats.clinicplus.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.whitecoats.clinicplus.fragments.*
import org.json.JSONArray

class ActiveConsultationPagerAdapter(
    fm: FragmentManager?,
    totalClinicCountData: Int,
    totalVideoCountData: Int,
    totalChatCountData: Int,
    totalInstantVideoCountData: Int,
    totalPagerCount: Int,
    clinicJSONData: JSONArray
) : FragmentStatePagerAdapter(
    fm!!
) {
    var serviceProductData: JSONArray
    var totalClinicCount: Int
    var totalVideoCount: Int
    var totalChatCount: Int
    var totalInstantVideoCount: Int

    init {
        totalPage = totalPagerCount
        serviceProductData = clinicJSONData
        totalClinicCount = totalClinicCountData
        totalVideoCount = totalVideoCountData
        totalChatCount = totalChatCountData
        totalInstantVideoCount = totalInstantVideoCountData
    }

    override fun getItem(position: Int): Fragment {
        var f = Fragment()
        if (totalClinicCount == 0 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            //if no any service
        } else if (totalClinicCount == 0 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            //if only video service
            when (position) {
                0 -> f = ActiveConsultationVideoFragment(serviceProductData, 0)
            }
        } else if (totalClinicCount == 0 && totalVideoCount == 1 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            //if only video,instantvideo service
            when (position) {
                0 -> f = ActiveConsultationVideoFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationChatFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 2)
            }
        } else if (totalClinicCount == 1 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if only clinic service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
            }
        } else if (totalClinicCount == 0 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 0) {
            // if only instantvideo service
            when (position) {
                0 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 0)
            }
        } else if (totalClinicCount == 0 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if only instantvideo service
            when (position) {
                0 -> f = ActiveConsultationChatFragment(serviceProductData, 0)
            }
        } else if (totalClinicCount == 1 && totalVideoCount == 1 && totalChatCount == 1 && totalInstantVideoCount == 1) {
            // if one clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationVideoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationChatFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 3)
            }
        } else if (totalClinicCount == 2 && totalVideoCount == 1 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            // if  2clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationVideoFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationChatFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 4)
            }
        } else if (totalClinicCount == 3 && totalVideoCount == 1 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            // if  3clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationVideoFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationChatFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 5)
            }
        } else if (totalClinicCount == 4 && totalVideoCount == 1 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            // if  4clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationVideoFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationChatFragment(serviceProductData, 5)
                6 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 6)
            }
        } else if (totalClinicCount == 5 && totalVideoCount == 1 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            // if  5clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationVideoFragment(serviceProductData, 5)
                6 -> f = ActiveConsultationChatFragment(serviceProductData, 6)
                7 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 7)
            }
        } else if (totalClinicCount == 6 && totalVideoCount == 1 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationClinicSixFragment(serviceProductData, 5)
                6 -> f = ActiveConsultationVideoFragment(serviceProductData, 6)
                7 -> f = ActiveConsultationChatFragment(serviceProductData, 7)
                8 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 8)
            }
        } else if (totalClinicCount == 2 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if  2clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
            }
        } else if (totalClinicCount == 3 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if  3clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
            }
        } else if (totalClinicCount == 4 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if  4clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
            }
        } else if (totalClinicCount == 5 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if  5clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
            }
        } else if (totalClinicCount == 6 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationClinicSixFragment(serviceProductData, 5)
            }
        } else if (totalClinicCount == 2 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if  2clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationVideoFragment(serviceProductData, 2)
            }
        } else if (totalClinicCount == 3 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if  3clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationVideoFragment(serviceProductData, 3)
            }
        } else if (totalClinicCount == 4 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if  4clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationVideoFragment(serviceProductData, 4)
            }
        } else if (totalClinicCount == 5 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if  5clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationVideoFragment(serviceProductData, 5)
            }
        } else if (totalClinicCount == 6 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 0) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationClinicSixFragment(serviceProductData, 5)
                6 -> f = ActiveConsultationVideoFragment(serviceProductData, 6)
            }
        } else if (totalClinicCount == 2 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 0) {
            // if  2clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 2)
            }
        } else if (totalClinicCount == 3 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 0) {
            // if  3clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 3)
            }
        } else if (totalClinicCount == 4 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 0) {
            // if  4clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 4)
            }
        } else if (totalClinicCount == 5 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 0) {
            // if  5clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 5)
            }
        } else if (totalClinicCount == 6 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 0) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationClinicSixFragment(serviceProductData, 5)
                6 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 6)
            }
        } else if (totalClinicCount == 2 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationChatFragment(serviceProductData, 2)
            }
        } else if (totalClinicCount == 3 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationChatFragment(serviceProductData, 3)
            }
        } else if (totalClinicCount == 4 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationChatFragment(serviceProductData, 4)
            }
        } else if (totalClinicCount == 5 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationChatFragment(serviceProductData, 5)
            }
        } else if (totalClinicCount == 6 && totalVideoCount == 0 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationClinicSixFragment(serviceProductData, 5)
                6 -> f = ActiveConsultationChatFragment(serviceProductData, 6)
            }
        } else if (totalClinicCount == 2 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationVideoFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationChatFragment(serviceProductData, 3)
            }
        } else if (totalClinicCount == 3 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationVideoFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationChatFragment(serviceProductData, 4)
            }
        } else if (totalClinicCount == 4 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationVideoFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationChatFragment(serviceProductData, 5)
            }
        } else if (totalClinicCount == 5 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationVideoFragment(serviceProductData, 5)
                6 -> f = ActiveConsultationChatFragment(serviceProductData, 6)
            }
        } else if (totalClinicCount == 6 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationClinicSixFragment(serviceProductData, 5)
                6 -> f = ActiveConsultationVideoFragment(serviceProductData, 6)
                7 -> f = ActiveConsultationChatFragment(serviceProductData, 7)
            }
        } else if (totalClinicCount == 2 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationChatFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 3)
            }
        } else if (totalClinicCount == 3 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationChatFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 4)
            }
        } else if (totalClinicCount == 4 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationChatFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 5)
            }
        } else if (totalClinicCount == 5 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationChatFragment(serviceProductData, 5)
                6 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 6)
            }
        } else if (totalClinicCount == 6 && totalVideoCount == 0 && totalInstantVideoCount == 1 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationClinicTwoFragment(serviceProductData, 1)
                2 -> f = ActiveConsultationClinicThreeFragment(serviceProductData, 2)
                3 -> f = ActiveConsultationClinicFourFragment(serviceProductData, 3)
                4 -> f = ActiveConsultationClinicFiveFragment(serviceProductData, 4)
                5 -> f = ActiveConsultationClinicSixFragment(serviceProductData, 5)
                6 -> f = ActiveConsultationChatFragment(serviceProductData, 6)
                7 -> f = ActiveConsultationInstantVideoFragment(serviceProductData, 7)
            }
        } else if (totalClinicCount == 1 && totalVideoCount == 1 && totalInstantVideoCount == 0 && totalChatCount == 1) {
            // if  6clinic,video,instantvide service
            when (position) {
                0 -> f = ActiveConsultationClinicFragment(serviceProductData, 0)
                1 -> f = ActiveConsultationVideoFragment(serviceProductData, 2)
                2 -> f = ActiveConsultationChatFragment(serviceProductData, 1)
            }
        }
        return f
    }

    override fun getCount(): Int {
        return totalPage
    }

    companion object {
        var totalPage = 5
    }
}