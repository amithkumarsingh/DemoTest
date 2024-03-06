package com.whitecoats.clinicplus.casechannels

import android.view.View

interface CaseDoctorOrganisationClickListener {
    fun onItemClick(v: View, position: Int, selectState: String, sortByString: String)
    fun getFilters(
        v: View,
        position: Int,
        selectState: String,
        sortByString: String,
        statusPos: Int,
        sortPos: Int
    )
}