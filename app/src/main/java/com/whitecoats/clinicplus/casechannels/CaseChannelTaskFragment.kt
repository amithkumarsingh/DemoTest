package com.whitecoats.clinicplus.casechannels

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.PatientRecordsApi
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import org.json.JSONObject
import java.util.*

class CaseChannelTaskFragment : Fragment() {
    private var taskListRecycleView: RecyclerView? = null
    private var taskListAdapter: TaskListAdapter? = null
    private var filterButton: Button? = null
    private var taskCreateNew: Button? = null
    private var taskCreateNew2: Button? = null
    private var caseChannelTaskListEmptyText: TextView? = null
    private var addTaskDateText: TextView? = null
    private var taskListFillterBottomsheet: TaskListFillterBottomsheet? = null
    private var filterCard: CardView? = null
    private var otpLoading: ProgressDialog? = null
    private var appUtilities: AppUtilities? = null
    private var apiCalls: PatientRecordsApi? = null
    private val taskListModelList: MutableList<TaskListModel> = ArrayList()
    private val assignedToArray: MutableList<String> = ArrayList()
    private val assignedIdToArray = ArrayList<Int>()
    private var selectStateID = ""
    private var sortByStrin = ""
    private var sortorder = ""
    private var assignedToFilter = 0
    private var assignedID = 0
    private var caseChannelId = 0
    private var pageNumber = 1
    private var statusFilterPos = 0
    private var sortFilterPos = 0
    private var assignedFilterPos = 0
    private var searchEt: EditText? = null
    private var searchStr = ""
    private var dataAdapter: ArrayAdapter<String>? = null
    var total = 0
    private var isSearcheApplied = false
    private var globalApiCall: ApiGetPostMethodCalls? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        val view = inflater.inflate(R.layout.fragment_case_channel_task, container, false)
        taskListRecycleView = view.findViewById(R.id.caseChannelTaskListRecycleView)
        caseChannelTaskListEmptyText = view.findViewById(R.id.caseChannelEmptyText)
        appUtilities = AppUtilities()
        apiCalls = PatientRecordsApi()
        globalApiCall = ApiGetPostMethodCalls()
        filterCard = view.findViewById(R.id.taskFilterCard)
        taskCreateNew2 = view.findViewById(R.id.taskCreateNew2)
        caseChannelId = requireArguments().getInt("caseChannelId")
        if (activity != null) {
            taskListAdapter = TaskListAdapter(
                requireActivity(),
                taskListModelList,
                this,
                object : CaseDoctorOrganisationClickListener {
                    override fun onItemClick(
                        v: View,
                        position: Int,
                        selectState: String,
                        sortByString: String
                    ) {
                        if (selectState.equals("LOAD_MORE", ignoreCase = true)) {
                            pageNumber++
                            getCaseChanneTasklsList(
                                caseChannelId,
                                assignedToFilter,
                                selectStateID,
                                sortByStrin,
                                searchStr,
                                sortorder,
                                "0",
                                pageNumber,
                                "10"
                            )
                        } else {
                            val popup = PopupMenu(
                                context!!, v
                            )
                            val inflater = popup.menuInflater
                            inflater.inflate(R.menu.case_channel_task_status_menu, popup.menu)
                            popup.show()
                            popup.setOnMenuItemClickListener { menuItem ->
                                changeTaskStatus(menuItem.itemId, position)
                                false
                            }
                        }
                    }

                    override fun getFilters(
                        v: View,
                        position: Int,
                        selectState: String,
                        sortByString: String,
                        statusPos: Int,
                        sortPos: Int
                    ) {
                    }
                })
            taskListRecycleView?.layoutManager = LinearLayoutManager(
                requireActivity().applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            taskListRecycleView?.itemAnimator = DefaultItemAnimator()
            taskListRecycleView?.adapter = taskListAdapter
        }
        taskListModelList.clear()
        pageNumber = 1
        getCaseChanneTasklsList(
            caseChannelId,
            assignedToFilter,
            "",
            "name",
            searchStr,
            "asc",
            "0",
            pageNumber,
            "60"
        )
        getAssignedFilterList(caseChannelId)
        filterButton = view.findViewById(R.id.filterButton)
        filterButton?.setOnClickListener(View.OnClickListener {
            taskListFillterBottomsheet = TaskListFillterBottomsheet()
            taskListFillterBottomsheet!!.setupConfig(
                requireActivity(),
                assignedToArray,
                assignedFilterPos,
                statusFilterPos,
                sortFilterPos,
                object : CaseDoctorOrganisationClickListener {
                    override fun onItemClick(
                        v: View,
                        position: Int,
                        selectState: String,
                        sortByString: String
                    ) {
                    }

                    override fun getFilters(
                        v: View,
                        position: Int,
                        selectState: String,
                        sortByString: String,
                        statusPos: Int,
                        sortPos: Int
                    ) {
                        assignedFilterPos = position
                        statusFilterPos = statusPos
                        sortFilterPos = sortPos
                        Log.d("Filter", "$selectState::$sortByString")
                        selectStateID = when (selectState) {
                            "Open" -> "1"
                            "On hold" -> "2"
                            "Cancelled" -> "3"
                            "Completed" -> "4"
                            else -> ""
                        }
                        when (sortByString) {
                            "Task(Ascending)" -> {
                                sortorder = "asc"
                                sortByStrin = "name"
                            }
                            "Task(Descending)" -> {
                                sortorder = "desc"
                                sortByStrin = "name"
                            }
                            "Due On(Ascending)" -> {
                                sortorder = "asc"
                                sortByStrin = "due_date"
                            }
                            "Due On(Descending)" -> {
                                sortorder = "desc"
                                sortByStrin = "due_date"
                            }
                            else -> {
                                sortorder = ""
                                sortByStrin = ""
                            }
                        }
                        assignedToFilter = if (position != 0) {
                            assignedIdToArray[position]
                        } else {
                            0
                        }
                        taskListModelList.clear()
                        pageNumber = 1
                        getCaseChanneTasklsList(
                            caseChannelId,
                            assignedToFilter,
                            selectStateID,
                            sortByStrin,
                            searchStr,
                            sortorder,
                            "0",
                            pageNumber,
                            "10"
                        )
                    }
                })
            taskListFillterBottomsheet!!.show(requireFragmentManager(), "Bottom Sheet Dialog Fragment")
        })
        taskCreateNew = view.findViewById(R.id.taskCreateNew)
        taskCreateNew?.setOnClickListener(View.OnClickListener { v -> openCreateDialog(v) })
        searchEt = view.findViewById(R.id.caseChannelTaskSearchEt)
        searchEt?.setOnEditorActionListener(OnEditorActionListener setOnEditorActionListener@{ v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchStr = searchEt!!.text.toString()
                isSearcheApplied = true
                taskListModelList.clear()
                pageNumber = 1
                getCaseChanneTasklsList(
                    caseChannelId,
                    assignedToFilter,
                    "",
                    "name",
                    searchStr,
                    "asc",
                    "0",
                    pageNumber,
                    "60"
                )
                return@setOnEditorActionListener true
            }
            false
        })
        searchEt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (searchEt?.text.toString()
                        .equals("", ignoreCase = true) && isSearcheApplied
                ) {
                    searchStr = ""
                    isSearcheApplied = false
                    taskListModelList.clear()
                    pageNumber = 1
                    getCaseChanneTasklsList(
                        caseChannelId,
                        assignedToFilter,
                        "",
                        "name",
                        searchStr,
                        "asc",
                        "0",
                        pageNumber,
                        "60"
                    )
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        taskCreateNew2?.setOnClickListener(View.OnClickListener { v: View -> openCreateDialog(v) })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
    }

    private fun getCaseChanneTasklsList(
        group_id: Int,
        assigned_to: Int,
        status: String,
        sortBy: String,
        searchStr: String,
        sortorder: String,
        length: String,
        pageNum: Int,
        perPage: String
    ) {
        otpLoading = ProgressDialog(activity)
        otpLoading!!.setMessage("Fetching your tasks...")
        otpLoading!!.setTitle("Getting Tasks")
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        val URL: String = if (assigned_to == 0) {
            ApiUrls.getCaseTasks + "?group_id=" + group_id + "&assigned_to=" + "" + "&status=" + status + "&sortby=" + sortBy + "&search=" + searchStr + "&sortorder=" + sortorder + "&length=" + length + "&page=" + pageNum + "&per_page=" + perPage //"?group_id=&status=&search="+ "" +"&sortby=name&sortorder&assigned_to=&sortorder=asc&length=0&page=1&per_page=10";
        } else {
            ApiUrls.getCaseTasks + "?group_id=" + group_id + "&assigned_to=" + assigned_to + "&status=" + status + "&sortby=" + sortBy + "&search=" + searchStr + "&sortorder=" + sortorder + "&length=" + length + "&page=" + pageNum + "&per_page=" + perPage //"?group_id=&status=&search="+ "" +"&sortby=name&sortorder&assigned_to=&sortorder=asc&length=0&page=1&per_page=10";
        }
        Log.d("Task URl", URL)
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.GET,
            null,
            requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        //Process os success response
                        Log.d("Tasks", response.toString())
                        otpLoading!!.dismiss()
                        val responseObject = response.getJSONObject("response")
                        val taskArray = responseObject.getJSONArray("data")

                        if (taskArray.length() > 0) {
                            caseChannelTaskListEmptyText!!.visibility = View.GONE
                            filterCard!!.visibility = View.VISIBLE
                            taskCreateNew!!.visibility = View.VISIBLE
                            taskCreateNew2!!.visibility = View.GONE
                            total = responseObject.getInt("total")
                            for (i in 0 until taskArray.length()) {
                                val channelObj = taskArray.getJSONObject(i)
                                val taskListModel = TaskListModel()
                                taskListModel.name = channelObj.getString("name")
                                taskListModel.status = channelObj.getInt("status")
                                taskListModel.id = channelObj.getInt("id")
                                taskListModel.created_by_name =
                                    channelObj.getString("created_by_name")
                                taskListModel.fname = channelObj.getJSONObject("assigned_to_doctor")
                                    .getString("fname")
                                taskListModel.due_date = appUtilities!!.changeDateFormat(
                                    "yyyy-MM-dd",
                                    "dd MMM, yy",
                                    channelObj.getString("due_date")
                                )
                                taskListModelList.add(taskListModel)
                            }
                            taskListAdapter!!.notifyDataSetChanged()
                        } else {
                            if (taskListModelList.size == 0) {
                                if (!searchStr.equals("", ignoreCase = true)) {
                                    caseChannelTaskListEmptyText!!.visibility = View.VISIBLE
                                    caseChannelTaskListEmptyText!!.text =
                                        "No task found in the name of $searchStr"
                                } else if (assignedFilterPos != 0 || statusFilterPos != 0 || sortFilterPos != 0) {
                                    caseChannelTaskListEmptyText!!.visibility = View.VISIBLE
                                    caseChannelTaskListEmptyText!!.text =
                                        "No task found from the applied filter"
                                } else {
                                    caseChannelTaskListEmptyText!!.visibility = View.VISIBLE
                                    caseChannelTaskListEmptyText!!.text =
                                        "No task created or assigned to you yet"
                                    filterCard!!.visibility = View.GONE
                                    taskCreateNew!!.visibility = View.GONE
                                    taskCreateNew2!!.visibility = View.VISIBLE
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading!!.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(requireContext(), err)
                }
            })
    }

    private fun getAssignedFilterList(group_id: Int) {
        val URL =
            ApiUrls.getCaseTaskAssignedFilter + "?group_id=" + group_id //"?group_id=&status=&search="+ "" +"&sortby=name&sortorder&assigned_to=&sortorder=asc&length=0&page=1&per_page=10";
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.GET,
            null,
            requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        Log.d("Case Channels", response.toString())
                        val responseObject = response.getJSONObject("response")
                        val allDoctorsArray = responseObject.getJSONArray("allDoctors")
                        assignedToArray.add("Select")
                        assignedIdToArray.add(0)
                        if (allDoctorsArray.length() > 0) {
                            for (i in 0 until allDoctorsArray.length()) {
                                val reobject = allDoctorsArray.getJSONObject(i)
                                assignedToArray.add(reobject.getString("fname"))
                                assignedIdToArray.add(reobject.getInt("id"))
                            }
                        } else {

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(requireContext(), err)
                }
            })
    }

    private fun openCreateDialog(taskView: View) {
        dataAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assignedToArray)
        dataAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = taskView.findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_create_new_task_list, viewGroup, false)
        val dismiss = dialogView.findViewById<Button>(R.id.newChannelDismissBtn)
        val createNew = dialogView.findViewById<Button>(R.id.newTaskCreateBtn)
        val channelName = dialogView.findViewById<EditText>(R.id.newTaskNameEditText)
        val episodes = dialogView.findViewById<Spinner>(R.id.addNewTaskSpinner)
        val taskDateLayout = dialogView.findViewById<RelativeLayout>(R.id.taskDateLayout)
        addTaskDateText = dialogView.findViewById(R.id.addTaskDateText)
        episodes.adapter = dataAdapter
        episodes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                assignedID = assignedIdToArray[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()
        dismiss.setOnClickListener { view: View? -> alertDialog.dismiss() }
        createNew.setOnClickListener { view: View? ->
            if (channelName.text.toString().equals("", ignoreCase = true)) {
                Toast.makeText(context, "Please enter task name.", Toast.LENGTH_SHORT).show()
            } else if (assignedID == 0) {
                Toast.makeText(context, "Please select one Specialists", Toast.LENGTH_SHORT).show()
            } else {
                alertDialog.dismiss()
                createNewTask(channelName.text.toString())
            }
        }

        //getting current date and setting it
        val c = Calendar.getInstance()
        var tempDate: String? =
            c[Calendar.DAY_OF_MONTH].toString() + "-" + (c[Calendar.MONTH] + 1) + "-" + c[Calendar.YEAR]
        tempDate = appUtilities!!.changeDateFormat("dd-MM-yyyy", "dd MMM, yy", tempDate)
        addTaskDateText?.setText(tempDate)
        taskDateLayout.setOnClickListener {
            val date = appUtilities!!.changeDateFormat(
                "dd MMM, yy",
                "dd-MM-yyyy",
                addTaskDateText!!.getText().toString()
            ).split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            openDatePicker(date[0].toInt(), date[1].toInt() - 1, date[2].toInt())
        }
    }

    private fun openDatePicker(day: Int, month: Int, year: Int) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                var tempDate: String? = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                tempDate = appUtilities!!.changeDateFormat("dd-MM-yyyy", "dd MMM, yy", tempDate)
                addTaskDateText!!.text = tempDate
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun createNewTask(channelName: String) {
        otpLoading = ProgressDialog(context)
        otpLoading!!.setMessage("Please wait while we set up the Task...")
        otpLoading!!.setTitle("Creating Task")
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        val params = JSONObject()
        try {
            val tempdate = appUtilities!!.changeDateFormat(
                "dd MMM, yy",
                "yyyy-MM-dd",
                addTaskDateText!!.text.toString()
            )
            params.put("due_date", tempdate)
            params.put("name", channelName)
            params.put("assigned_to", assignedID)
            params.put("group_id", caseChannelId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        globalApiCall!!.volleyApiRequestData(
            ApiUrls.saveCaseTask,
            Request.Method.POST,
            params,
            requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        otpLoading!!.dismiss()
                        Log.d("Response", response.toString())
                        taskListModelList.clear()
                        getCaseChanneTasklsList(
                            caseChannelId,
                            assignedToFilter,
                            "",
                            "name",
                            searchStr,
                            "asc",
                            "0",
                            pageNumber,
                            "10"
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading!!.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(requireContext(), err)
                }
            })
    }

    private fun changeTaskStatus(status: Int, pos: Int) {
        var status = status
        when (status) {
            R.id.taskOpen -> status = 1
            R.id.taskOnHold -> status = 2
            R.id.taskCompleted -> status = 4
            R.id.taskCancelled -> status = 3
        }
        otpLoading!!.setTitle("Updating Status")
        otpLoading!!.setMessage("Please wait while we update task status...")
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        val reqBody = JSONObject()
        try {
            reqBody.put("task_id", taskListModelList[pos].id)
            reqBody.put("status", status)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("Status", reqBody.toString())
        val finalStatus = status
        apiCalls!!.postRecords(
            ApiUrls.updateTaskStatus,
            reqBody,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    otpLoading!!.dismiss()
                    Log.d("Success", result)
                    try {
                        val obj = JSONObject(result)
                        if (obj.getInt("response") == 1) {
                            Toast.makeText(
                                context,
                                "Status Updated Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val model = taskListModelList[pos]
                            model.status = finalStatus
                            taskListModelList.removeAt(pos)
                            taskListModelList.add(pos, model)
                            taskListAdapter!!.notifyDataSetChanged()
                        } else {
                            Toast.makeText(
                                context,
                                "Couldn't update the status. Try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            context,
                            "Couldn't update the status. Try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(requireActivity(), err)
                }
            })
    }
}