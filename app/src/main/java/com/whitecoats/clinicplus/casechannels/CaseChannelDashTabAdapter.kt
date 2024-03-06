package com.whitecoats.clinicplus.casechannels

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.json.JSONArray

class CaseChannelDashTabAdapter(
        fm: FragmentManager?,
        var mNumOfTabs: Int,
        var caseChannelId: Int,
        var caseChannelPatientId: Int,
        var caseChannelEpisodeId: Int,
        var caseChannelDoctorId: Int,
        var doctorArray: JSONArray,
        var typeID: Int
) : FragmentStatePagerAdapter(
        fm!!
        ) {
        override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt("caseChannelId", caseChannelId)
        bundle.putInt("caseChannelPatientId", caseChannelPatientId)
        bundle.putInt("caseChannelEpisodeId", caseChannelEpisodeId)
        bundle.putInt("caseChannelDoctorId", caseChannelDoctorId)
        return if (typeID == 1) {
        when (position) {
        0 -> {
        val tab2 = CaseChannelTaskFragment()
        tab2.arguments = bundle
        tab2
        }
        1 -> {
        val tab3 = CaseChannelPostFragment()
        tab3.arguments = bundle
        tab3
        }
        else -> null!!
        }
        } else {
        when (position) {
        0 -> {
        val tab1 = CaseChannelRecordHandWrittenNotesFragment()
        tab1.arguments = bundle
        tab1
        }
        1 -> {
        val tab2 = CaseChannelRecordEvaluationFragment()
        tab2.arguments = bundle
        tab2
        }
        2 -> {
        val tab3 = TreatmentPlanFragment()
        tab3.arguments = bundle
        tab3
        }
        else -> null!!
        }
        }
        }

        override fun getCount(): Int {
        return mNumOfTabs
        }
        }