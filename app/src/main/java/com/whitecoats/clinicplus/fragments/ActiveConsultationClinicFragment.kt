package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.whitecoats.clinicplus.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ActiveConsultationClinicFragment(clinicDataObj: JSONArray, productArrayPosition: Int) :
    Fragment() {
    private lateinit var serviceNameText: TextView
    private lateinit var pendingLeftRupeeText: TextView
    private lateinit var receivedRupeeText: TextView
    var productObject: JSONObject? = null

    init {
        clinicData=clinicDataObj
        productArrayPos=productArrayPosition
        try {
            productObject = clinicData!!.getJSONObject(
                productArrayPos
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.list_row_active_con_clinic, container, false)
        serviceNameText = view.findViewById(R.id.serviceNameText)
        pendingLeftRupeeText = view.findViewById(R.id.pendingLeftRupeeText)
        receivedRupeeText = view.findViewById(R.id.receivedRupeeText)
        try {
            serviceNameText.text = "" + productObject!!.getString("name").trim { it <= ' ' }
            pendingLeftRupeeText.text = "" + productObject!!.getDouble("received")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return view
    }

    companion object {
        var clinicData: JSONArray? = null
        var productArrayPos = 0
        fun newInstance(): ActiveConsultationClinicFragment {
            val fragment =
                ActiveConsultationClinicFragment(clinicData!!, productArrayPos)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}