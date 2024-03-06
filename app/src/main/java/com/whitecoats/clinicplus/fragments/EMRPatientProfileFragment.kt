package com.whitecoats.clinicplus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.EMRProfileTabAdapter
import com.whitecoats.clinicplus.apis.ApiUrls

/**
 * A simple [Fragment] subclass.
 * Use the [EMRPatientProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EMRPatientProfileFragment : Fragment(), OnTabSelectedListener {
    private var patientView: View? = null
    private var viewPager: ViewPager? = null
    private var patientId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            patientId = requireArguments().getInt("PatientID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        patientView = inflater.inflate(R.layout.fragment_e_m_r_patient_profile, container, false)
        initView()
        return patientView
    }

    private fun initView() {
        viewPager = patientView!!.findViewById(R.id.profileViewPager)
        val tabLayout = patientView!!.findViewById<TabLayout>(R.id.profileTabLayout)
        val profileTabAdapter =
            EMRProfileTabAdapter(requireActivity().supportFragmentManager, patientId)
        tabLayout.addTab(tabLayout.newTab().setText("Patient Profile"))
        tabLayout.addTab(tabLayout.newTab().setText("Family Profile"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.addOnTabSelectedListener(this)
        viewPager!!.adapter = profileTabAdapter
        viewPager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPager!!.currentItem = tab.position
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId,
            getString(R.string.PatientInfoFamilyBG),
            null
        )
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}
    override fun onTabReselected(tab: TabLayout.Tab) {}

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param patientId Parameter 1
         * @return A new instance of fragment EMRPatientProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(patientId: Int): EMRPatientProfileFragment {
            val fragment = EMRPatientProfileFragment()
            val args = Bundle()
            args.putInt("PatientID", patientId)
            fragment.arguments = args
            return fragment
        }
    }
}