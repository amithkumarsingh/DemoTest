package com.whitecoats.clinicplus.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import org.json.JSONObject

class DashboardTabFragment : Fragment() {
    private var dashboardViewModel: DashboardViewModel? = null
    private lateinit var firstLogin: RelativeLayout
    private lateinit var fullMode: RelativeLayout
    private lateinit var mainView: View
    private lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainView = inflater.inflate(R.layout.fragment_dashboard_tab, container, false)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        progressBar.visibility = View.VISIBLE
        dashboardViewModel!!.getStatusString(activity)
            .observe(viewLifecycleOwner) { s ->
                Log.d("API Response ", s)
                progressBar.visibility = View.GONE
                setView(s)
            }
    }

    private fun initView() {
        firstLogin = mainView.findViewById(R.id.firsLoginLayout)
        fullMode = mainView.findViewById(R.id.fullModeLayout)
        progressBar = mainView.findViewById(R.id.dashboard_tab_progress)
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        dashboardViewModel!!.init()
        fullMode.visibility = View.GONE
        firstLogin.visibility = View.GONE
    }

    private fun setView(statusStr: String) {
        Log.i("status response", statusStr)
        try {
            val response = JSONObject(statusStr)
            val statusObj = response.getJSONObject("response").getJSONObject("response")
                .getJSONObject("general_info")
            practiceAdded = statusObj.getInt("is_practice_added")
            if (statusObj.getInt("is_practice_added") == 1
                && statusObj.getInt("is_patient_added") == 1
            ) {
                fullMode.visibility = View.VISIBLE
                firstLogin.visibility = View.GONE
                DashboardFragment.progressBarDasBoardFragment!!.visibility = View.GONE
            } else {
                fullMode.visibility = View.GONE
                firstLogin.visibility = View.VISIBLE
                DashboardFragment.progressBarDasBoardFragment!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        var practiceAdded = 0
    }
}