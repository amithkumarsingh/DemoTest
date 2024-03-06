package com.whitecoats.clinicplus.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.activities.EMRActivity
import com.whitecoats.clinicplus.adapters.EMRSharedRecordsAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.models.EMRSharedRecordModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.EMRSharedRecordsViewModel
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [EMRSharedRecordsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EMRSharedRecordsFragment : Fragment() {
    private var sharedRecordsRecycler: RecyclerView? = null
    private val recordFilterItems =
        arrayOf("Today", "Last 7 days", "Last 30 days", "Last 60 days", "All Records")
    private var emrSharedRecordsViewModel: EMRSharedRecordsViewModel? = null
    private var myClinicGlobalClass: MyClinicGlobalClass? = null
    private var emrSharedRecordModelList: MutableList<EMRSharedRecordModel>? = null
    private var noRecordLayout: LinearLayout? = null
    private var recordLayout: NestedScrollView? = null
    private var recordGroup: MutableList<Int>? = null
    private var index = 0
    private var noOfRecords: MutableMap<String, Int>? = null
    private var noRecordText: TextView? = null
    private var fieldDirectory: JSONObject? = null
    private var emrSharedRecordsAdapter: EMRSharedRecordsAdapter? = null
    private var dateFormat: SimpleDateFormat? = null
    private var inputFornat: SimpleDateFormat? = null
    private var progressBar: ProgressBar? = null
    private var floatingActionButton: FloatingActionButton? = null
    private var appUtilities: AppUtilities? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_e_m_r_shared_records, container, false)
        val recordsFilter = view.findViewById<Spinner>(R.id.record_filter)
        sharedRecordsRecycler = view.findViewById(R.id.shared_records_list)
        noRecordLayout = view.findViewById(R.id.noRecordLayout)
        recordLayout = view.findViewById(R.id.recordLayout)
        noRecordText = view.findViewById(R.id.noRecordText)
        progressBar = view.findViewById(R.id.emr_shared_progress)
        floatingActionButton = view.findViewById(R.id.go_up)
        floatingActionButton!!.visibility = View.GONE
        progressBar!!.visibility = View.GONE
        myClinicGlobalClass = requireActivity().applicationContext as MyClinicGlobalClass
        emrSharedRecordsViewModel = ViewModelProvider(this).get(
            EMRSharedRecordsViewModel::class.java
        )
        emrSharedRecordsViewModel!!.init()
        val recordsFilterAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            recordFilterItems
        )
        recordsFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        recordsFilter.adapter = recordsFilterAdapter
        recordsFilter.setSelection(ApiUrls.spinnerSelection)
        emrSharedRecordModelList = ArrayList()
        recordGroup = ArrayList()
        noOfRecords = HashMap()
        fieldDirectory = JSONObject()
        appUtilities = AppUtilities()
        dateFormat = SimpleDateFormat("MMM, yyyy", Locale.getDefault())
        inputFornat = SimpleDateFormat("yyyy-MM-dd mm:HH:ss", Locale.getDefault())
        noRecordLayout!!.visibility = View.GONE
        emrSharedRecordsAdapter = context?.let {
            EMRSharedRecordsAdapter(
                it,
                emrSharedRecordModelList as ArrayList<EMRSharedRecordModel>,
                recordGroup as ArrayList<Int>,
                noOfRecords as HashMap<String, Int>,
                fieldDirectory!!
            )
        }
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        sharedRecordsRecycler?.layoutManager = layoutManager
        sharedRecordsRecycler?.itemAnimator = DefaultItemAnimator()
        sharedRecordsRecycler?.adapter = emrSharedRecordsAdapter
        recordLayout?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY > oldScrollY) {
                floatingActionButton!!.visibility = View.VISIBLE
            }
            if (scrollY == 0) {
                floatingActionButton!!.visibility = View.GONE
            }
        })
        floatingActionButton!!.setOnClickListener(View.OnClickListener { v: View? ->
            recordLayout!!.smoothScrollTo(
                0,
                0
            )
        })
        recordsFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val duration: String
                ApiUrls.spinnerSelection = position
                duration = when (recordFilterItems[position]) {
                    "Today" -> "1"
                    "Last 7 days" -> "7"
                    "Last 30 days" -> "30"
                    "Last 60 days" -> "60"
                    "All Records" -> ""
                    else -> "30"
                }
                if (myClinicGlobalClass!!.isOnline) {
                    getEMRSharedRecords(
                        activity,
                        duration,
                        "month",
                        (requireActivity() as EMRActivity).patientId
                    )
                } else {
                    myClinicGlobalClass!!.noInternetConnection.showDialog(activity)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        return view
    }

    fun getEMRSharedRecords(
        activity: Activity?,
        duration: String?,
        groupby: String?,
        patientId: Int
    ) {
        progressBar!!.visibility = View.VISIBLE
        noRecordLayout!!.visibility = View.GONE
        emrSharedRecordModelList!!.clear()
        recordGroup!!.clear()
        noOfRecords!!.clear()
        fieldDirectory = JSONObject()
        emrSharedRecordsAdapter!!.updateAdapter(
            emrSharedRecordModelList!!,
            recordGroup!!,
            noOfRecords!!,
            fieldDirectory!!
        )
        index = 0
        emrSharedRecordsViewModel!!.getEMRSharedRecords(activity, duration, groupby, patientId)
            .observe(
                viewLifecycleOwner
            ) { s: String? ->
                Log.i("EMR res", s!!)
                progressBar!!.visibility = View.GONE
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val resObject = response.getJSONObject("response").getJSONObject("response")
                        if (resObject.getJSONArray("recordsinfo").length() == 0) {
                            noRecordLayout!!.visibility = View.VISIBLE
                            sharedRecordsRecycler!!.visibility = View.GONE
                            noRecordText!!.text = String.format(
                                "%s has not shared any records with you yet",
                                (activity as EMRActivity?)!!.patientName
                            )
                        } else {
                            noRecordLayout!!.visibility = View.GONE
                            sharedRecordsRecycler!!.visibility = View.VISIBLE
                            val records = resObject.getJSONArray("recordsinfo")
                            fieldDirectory = resObject.getJSONObject("field-dictionary")
                            Log.i("fd", fieldDirectory.toString())
                            for (i in 0 until records.length()) {
                                val recordInfo = records.getJSONObject(i)
                                val record = recordInfo.getJSONArray("records")
                                noOfRecords!![dateFormat!!.format(
                                    Objects.requireNonNull(
                                        inputFornat!!.parse(recordInfo.getString("date"))
                                    )
                                )] = record.length()
                                for (j in 0 until record.length()) {
                                    recordGroup!!.add(index, j)
                                    index++
                                    val recordItem =
                                        record.getJSONObject(j).getJSONObject("records")
                                    val date = appUtilities!!.changeDateFormat(
                                        "yyyy-MM-dd hh:mm:ss",
                                        "MMM, yyyy",
                                        recordInfo.getString("date")
                                    )
                                    val currentStringStart =
                                        recordItem.getJSONObject("share_details")
                                            .getString("created_at")
                                    val separatedDate = currentStringStart.split(" ".toRegex())
                                        .dropLastWhile { it.isEmpty() }
                                        .toTypedArray()
                                    val createdDate = separatedDate[0]
                                    val createDateFormat = appUtilities!!.changeDateFormat(
                                        "yyyy-MM-dd",
                                        "dd MMM, yyyy",
                                        createdDate
                                    )
                                    val createdTime =
                                        separatedDate[1].substring(0, separatedDate[1].length - 3)
                                    val createdRecordDate = "$createDateFormat $createdTime"
                                    emrSharedRecordModelList!!.add(
                                        EMRSharedRecordModel(
                                            date,
                                            createdRecordDate,
                                            recordItem.getJSONObject("category").getInt("id"),
                                            recordItem.getJSONObject("category")
                                                .getString("category"),
                                            recordItem,
                                            recordItem.getInt("record_id"),
                                            recordItem.getJSONObject("share_details")
                                                .getString("created_at")
                                        )
                                    )
                                    //                                    emrSharedRecordModelList.add(new EMRSharedRecordModel(dateFormat.format(inputFornat.parse(recordInfo.getString("date"))),recordDateFormat.format(inputFornat.parse(recordItem.getJSONObject("share_details").getString("created_at"))),recordItem.getJSONObject("category").getInt("id"),recordItem.getJSONObject("category").getString("category"),recordItem,recordItem.getInt("record_id")));
                                }
                            }
                            emrSharedRecordsAdapter!!.updateAdapter(
                                emrSharedRecordModelList!!,
                                recordGroup!!,
                                noOfRecords!!,
                                fieldDirectory!!
                            )
                        }
                    } else {
                        noRecordLayout!!.visibility = View.VISIBLE
                        sharedRecordsRecycler!!.visibility = View.GONE
                        noRecordText!!.text = String.format(
                            "%s has not shared any records with you yet",
                            (activity as EMRActivity?)!!.patientName
                        )
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                } catch (e: ParseException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EMRSharedRecordsFragment.
         */
        fun newInstance(param1: String?, param2: String?): EMRSharedRecordsFragment {
            val fragment = EMRSharedRecordsFragment()
            val args = Bundle()
            args.putString("param1", param1)
            args.putString("param2", param2)
            fragment.arguments = args
            return fragment
        }
    }
}