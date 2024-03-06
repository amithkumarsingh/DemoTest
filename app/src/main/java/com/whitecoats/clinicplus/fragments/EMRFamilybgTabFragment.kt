package com.whitecoats.clinicplus.fragments

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.activities.EMRAddFamilyActivity
import com.whitecoats.clinicplus.adapters.EMRFamilyRecyclerAdapter
import com.whitecoats.clinicplus.adapters.EMRFamilyRecyclerAdapter.FamilyInterface
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.models.EMRFamilyModel
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.viewmodels.EMRPatientProfileViewModel

class EMRFamilybgTabFragment : Fragment(), FamilyInterface {
    private var mainView: View? = null
    private var familyListView: RelativeLayout? = null
    private var noDataView: RelativeLayout? = null
    private var familyRecyclerAdapter: EMRFamilyRecyclerAdapter? = null
    private var familyModelList: MutableList<EMRFamilyModel>? = null
    private var progressDialog: ProgressDialog? = null
    private var patientProfileViewModel: EMRPatientProfileViewModel? = null
    private var patientID = 0
    private var receiver: BroadcastReceiver? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.fragment_emr_familybg_tab, container, false)
        if (arguments != null) {
            patientID = requireArguments().getInt("PatientID")
        }
        initView()
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView() {
        familyListView = mainView!!.findViewById(R.id.familyListLayout)
        val addFamilyBtn = mainView!!.findViewById<Button>(R.id.addFamilyBtn)
        val addFamilyFab = mainView!!.findViewById<FloatingActionButton>(R.id.addFamilyFab)
        noDataView = mainView!!.findViewById(R.id.emptyView)
        val familyRV = mainView!!.findViewById<RecyclerView>(R.id.familyListRecycleView)
        registerGetFamDataReceiver()
        familyModelList = ArrayList()
        familyRecyclerAdapter = EMRFamilyRecyclerAdapter(familyModelList as ArrayList<EMRFamilyModel>, this)
        progressDialog = ProgressDialog(context)
        val layoutManager = LinearLayoutManager(context)
        familyRV.layoutManager = layoutManager
        familyRV.adapter = familyRecyclerAdapter
        val addIcon = AppUtilities.changeIconColor(
            addFamilyFab.drawable,
            requireActivity(),
            R.color.colorWhite
        )
        addFamilyFab.setImageDrawable(addIcon)
        patientProfileViewModel = ViewModelProvider(this).get(
            EMRPatientProfileViewModel::class.java
        )
        patientProfileViewModel!!.init()
        addFamilyFab.setOnClickListener { view: View? ->
            val intent = Intent(activity, EMRAddFamilyActivity::class.java)
            intent.putExtra("PatientID", patientID)
            if (activity != null) {
                requireActivity().startActivity(intent)
            }
        }
        addFamilyBtn.setOnClickListener { view: View? ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.PatientInfoFamilyBGAdd),
                null
            )
            val intent = Intent(activity, EMRAddFamilyActivity::class.java)
            intent.putExtra("PatientID", patientID)
            if (activity != null) {
                requireActivity().startActivity(intent)
            }
        }
        familyData
    }

    private val familyData: Unit
        private get() {
            progressDialog!!.setMessage("Fetching Data...")
            progressDialog!!.show()
            patientProfileViewModel!!.getFamilyData(
                activity,
                "doctor_id=" + ApiUrls.doctorId + "&page=1&patient_id=" + patientID + "&per_page=200"
            ).observe(
                viewLifecycleOwner
            ) { familyModels: List<EMRFamilyModel>? ->
                progressDialog!!.dismiss()
                familyModelList!!.clear()
                familyRecyclerAdapter!!.notifyDataSetChanged()
                familyModelList!!.addAll(familyModels!!)
                if (familyModelList!!.size > 0) {
                    familyListView!!.visibility = View.VISIBLE
                    noDataView!!.visibility = View.GONE
                    familyRecyclerAdapter!!.notifyDataSetChanged()
                } else {
                    familyListView!!.visibility = View.GONE
                    noDataView!!.visibility = View.VISIBLE
                }
            }
        }

    override fun onCardClick(familyModel: EMRFamilyModel?) {
        if (activity != null) {
            val builder = AlertDialog.Builder(
                requireActivity()
            )
            val viewGroup = mainView!!.findViewById<ViewGroup>(android.R.id.content)
            val dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_emr_family_details, viewGroup, false)
            val famName = dialogView.findViewById<TextView>(R.id.famNameText)
            val famAge = dialogView.findViewById<TextView>(R.id.fameAgeText)
            val famProblems = dialogView.findViewById<TextView>(R.id.fameProblemText)
            val close = dialogView.findViewById<ImageView>(R.id.closeDialog)
            if (familyModel != null) {
                famName.text = familyModel.relative_name
            }
            famAge.text = String.format("%s %s", familyModel!!.relative_age_str, familyModel.age_type)
            var temp = StringBuilder()
            if (familyModel!!.problems.size > 0) {
                for (i in familyModel.problems.indices) {
                    temp.append(familyModel.problems[i].problem.condition).append(", ")
                }
                temp = StringBuilder(temp.substring(0, temp.length - 2))
                famProblems.text = temp.toString()
            } else {
                val noProb = "No Problem Conditions"
                famProblems.text = noProb
            }
            builder.setView(dialogView)
            val alertDialog = builder.create()
            alertDialog.show()
            close.setOnClickListener { view: View? -> alertDialog.dismiss() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiver != null) {
            requireActivity().unregisterReceiver(receiver)
        }
    }

    private fun registerGetFamDataReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent != null && intent.action.equals(
                        "Get_Family_Updated_Data",
                        ignoreCase = true
                    )
                ) {
                    familyData
                }
            }
        }
        val filter = IntentFilter()
        filter.addAction("Get_Family_Updated_Data")
        requireActivity().registerReceiver(receiver, filter)
    }

    companion object {
        @JvmStatic
        fun newInstance(patientId: Int): EMRFamilybgTabFragment {
            val fragment = EMRFamilybgTabFragment()
            val args = Bundle()
            args.putInt("PatientID", patientId)
            fragment.arguments = args
            return fragment
        }
    }
}