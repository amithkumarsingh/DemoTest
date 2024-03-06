package com.whitecoats.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request.Method
import com.whitecoats.adapter.HomeCaseChannelsListAdapter
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.PatientRecordsApi
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.casechannels.*
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.model.CaseChannelModel
import org.json.JSONObject

class CaseChannelTabFragment : Fragment() {
    //channel list section
    private lateinit var caseChannelsViewAll: TextView
    private lateinit var caseChannelsEmptyMsg: TextView
    private lateinit var caseChannelsRv: RecyclerView
    private var caseChannelModelsList: MutableList<CaseChannelModel>? = null
    private var homeCaseChannelsListAdapter: HomeCaseChannelsListAdapter? = null
    private lateinit var progressDialog: ProgressBar

    //channel task section
    private lateinit var taskEmptyMsg: TextView
    private lateinit var taskRv: RecyclerView
    private lateinit var taskModelsList: MutableList<CaseChannelModel>
    private var taskListAdapter: HomeCaseChannelsTaskListAdapter? = null
    private lateinit var taskProgressDialog: ProgressBar

    //channel discussion section
    private lateinit var postEmptyMsg: TextView
    private lateinit var postRv: RecyclerView
    private lateinit var postModelsList: MutableList<CaseChannelModel>
    private var postListAdapter: HomeCaseChannelsPostListAdapter? = null
    private lateinit var postProgressDialog: ProgressBar
    private var viewGroup: ViewGroup? = null
    private var apiCall: PatientRecordsApi? = null
    private lateinit var dialog: AlertDialog
    private lateinit var appUtilities: AppUtilities
    private lateinit var caseChannelsViewModel: CaseChannelsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val caseChannelTabView =
            inflater.inflate(R.layout.tab_fragment_case_channels, container, false)
        caseChannelsRv = caseChannelTabView.findViewById(R.id.caseChannelsTabRv)
        caseChannelsViewAll = caseChannelTabView.findViewById(R.id.caseChannelsTabViewAll)
        caseChannelsEmptyMsg = caseChannelTabView.findViewById(R.id.caseChannelTabEmptyText)
        progressDialog = caseChannelTabView.findViewById(R.id.caseChannelTabLoader)
        caseChannelModelsList = ArrayList()
        appUtilities = AppUtilities()
        caseChannelsViewModel =
            ViewModelProvider(requireActivity())[CaseChannelsViewModel::class.java]
        taskRv = caseChannelTabView.findViewById(R.id.caseChannelsTaskRv)
        taskEmptyMsg = caseChannelTabView.findViewById(R.id.caseChannelTaskEmptyText)
        taskProgressDialog = caseChannelTabView.findViewById(R.id.caseChannelTaskLoader)
        taskModelsList = ArrayList()
        postRv = caseChannelTabView.findViewById(R.id.caseChannelsPostRv)
        postEmptyMsg = caseChannelTabView.findViewById(R.id.caseChannelPostEmptyText)
        postProgressDialog = caseChannelTabView.findViewById(R.id.caseChannelPostLoader)
        postModelsList = ArrayList()
        homeCaseChannelsListAdapter =
            HomeCaseChannelsListAdapter(requireActivity(), caseChannelModelsList)
        caseChannelsRv.layoutManager = LinearLayoutManager(
            requireActivity().applicationContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        caseChannelsRv.itemAnimator = DefaultItemAnimator()
        caseChannelsRv.isNestedScrollingEnabled = false
        caseChannelsRv.adapter = homeCaseChannelsListAdapter
        viewGroup = caseChannelTabView.findViewById(android.R.id.content)
        apiCall = PatientRecordsApi()
        postListAdapter = HomeCaseChannelsPostListAdapter(
            requireActivity(),
            postModelsList, object : HomeCaseChannelPostDiscussionInterface {
                override fun onButtonClick(v: View, message: String) {
                    openNewPostForm(message)
                }
            }
        )
        postRv.layoutManager = LinearLayoutManager(
            requireActivity().applicationContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        postRv.itemAnimator = DefaultItemAnimator()
        postRv.isNestedScrollingEnabled = false
        postRv.adapter = postListAdapter
        taskListAdapter =
            HomeCaseChannelsTaskListAdapter(
                requireActivity(),
                taskModelsList,
                object : HomeCaseChannelPostDiscussionInterface {
                    override fun onButtonClick(v: View, message: String) {
                        val popup = PopupMenu(
                            requireContext(), v
                        )
                        val inflater1 = popup.menuInflater
                        inflater1.inflate(R.menu.case_channel_task_status_menu, popup.menu)
                        popup.show()
                        popup.setOnMenuItemClickListener { menuItem ->
                            changeTaskStatus(menuItem.itemId, message)
                            false
                        }
                    }
                })
        taskRv.layoutManager = LinearLayoutManager(
            requireActivity().applicationContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        taskRv.itemAnimator = DefaultItemAnimator()
        taskRv.isNestedScrollingEnabled = false
        taskRv.adapter = taskListAdapter
        caseChannelsViewAll.setOnClickListener {
            val intent = Intent(activity, CaseChannelListActivity::class.java)
            intent.putExtra("PatientId", "")
            startActivity(intent)
        }
        val data = HashMap<String, Any>()
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId, getString(R.string.HomeSupportScreen),
            data
        )
        return caseChannelTabView
    }//temp code

    //discussion
    val summary:

    //task

    //post
            Unit
        @SuppressLint("SetTextI18n")
        get() {
            postProgressDialog.visibility = View.VISIBLE
            taskProgressDialog.visibility = View.VISIBLE
            postEmptyMsg.visibility = View.GONE
            taskEmptyMsg.visibility = View.GONE
            progressDialog.visibility = View.VISIBLE
            caseChannelsEmptyMsg.visibility = View.GONE
            val url =
                ApiUrls.caseDiscussionSummary + "?disc_patient_id&disc_status&disc_search&disc_sortby=created_at&disc_sortorder=desc&disc_per_page=5&disc_page=1&task_discussion_id&task_status&task_assigned_to&task_sortby&task_sortorder&task_per_page=5&task_page=1&chat_sender_id"


            caseChannelsViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
                requireActivity()
            ) { result ->
                try {
                    val resRes = JSONObject(result)
                    if (resRes.getInt("status_code") == 200) {
                        postProgressDialog.visibility = View.GONE
                        taskProgressDialog.visibility = View.GONE
                        progressDialog.visibility = View.GONE
                        Log.d("caseDashBoardSummary", "caseDashBoardSummary$result")
                        //discussion
                        val resObj = resRes.getJSONObject("response")
                        val discussionObj =
                            resObj.getJSONObject("response").getJSONObject("discussions")
                        val discusArr = discussionObj.getJSONArray("data")
                        if (discusArr.length() > 0) {
                            for (i in 0 until discusArr.length()) {
                                val tempobj = discusArr.getJSONObject(i)
                                val temp = CaseChannelModel()
                                temp.caseChannelName = tempobj.getString("name")
                                temp.caseId = tempobj.getInt("id")
                                temp.ownerName = tempobj.getString("owner_name")
                                temp.createdAt = tempobj.getString("created_at")
                                temp.patientId = tempobj.getJSONObject("patients").getInt("id")
                                temp.episodeId = tempobj.getInt("episode_id")
                                temp.ownerId = tempobj.getInt("owner_by_id")
                                temp.patientName =
                                    tempobj.getJSONObject("patients").getString("fname")
                                temp.status = tempobj.getInt("status")
                                temp.taskCount = tempobj.getInt("total_task_count")
                                temp.recordsCount = tempobj.getInt("total_records_count")
                                temp.messageCounts = tempobj.getInt("total_chat_count")
                                caseChannelModelsList!!.add(temp)
                            }
                            homeCaseChannelsListAdapter!!.notifyDataSetChanged()
                        } else {
                            caseChannelsEmptyMsg.visibility = View.VISIBLE
                            caseChannelsViewAll.visibility = View.GONE
                            caseChannelsEmptyMsg.text =
                                buildString {
                                    append("You have not created any case channels or part of any case channel yet.")
                                }
                        }

                        //task
                        val taskObj = resObj.getJSONObject("response").getJSONObject("tasks")
                        val taskArr = taskObj.getJSONArray("data")
                        if (taskArr.length() > 0) {
                            for (i in 0 until taskArr.length()) {
                                val temp = CaseChannelModel()
                                val tempObj = taskArr.getJSONObject(i)
                                temp.taskName = tempObj.getString("name")
                                temp.taskAssignedTo =
                                    tempObj.getJSONObject("assigned_to_doctor").getString("fname")
                                temp.taskStatus = tempObj.getInt("status")
                                temp.taskDueOn = tempObj.getString("due_date")
                                temp.caseId = tempObj.getInt("group_id")
                                temp.taskId = tempObj.getInt("id")
                                taskModelsList.add(temp)
                            }
                            taskListAdapter!!.notifyDataSetChanged()
                        } else {
                            taskEmptyMsg.visibility = View.VISIBLE
                            taskEmptyMsg.text = "No task assigned to you yet"
                        }

                        //post
                        val postArr = resObj.getJSONObject("response").getJSONArray("chatMsgs")
                        if (postArr.length() > 0) {
                            for (i in 0 until postArr.length()) {
                                val temp = CaseChannelModel()
                                val tempObj = postArr.getJSONObject(i)

                                //temp code
                                temp.postSenderName =
                                    tempObj.getJSONObject("chat_sender").getString("fname")
                                temp.postMessage = tempObj.getString("message")
                                temp.postSenderId = tempObj.getInt("sender_id")
                                temp.postDate = tempObj.getString("tstamp")
                                temp.caseId =
                                    tempObj.getJSONObject("chat_session").getInt("discussion_id")
                                postModelsList.add(temp)
                            }
                            postListAdapter!!.notifyDataSetChanged()
                        } else {
                            postEmptyMsg.visibility = View.VISIBLE
                            postEmptyMsg.text = "No post has been created till now"
                        }
                    } else {
                        errorHandler(requireActivity(), result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    private fun openNewPostForm(msg: String) {


        //then we will inflate the custom alert dialog xml that we created
        val dialogView = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_case_channel_new_post, viewGroup, false)
        val postMsg = dialogView.findViewById<EditText>(R.id.newPostEt)
        val newPostBtn = dialogView.findViewById<Button>(R.id.newPostBtn)

        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(activity)

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)

        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.show()
        newPostBtn.setOnClickListener {
            addNewPost(postMsg.text.toString(), msg)
            alertDialog.dismiss()
        }
    }

    private fun changeTaskStatus(statusVal: Int, taskId: String) {
        var status = statusVal
        when (status) {
            R.id.taskOpen -> status = 1
            R.id.taskOnHold -> status = 2
            R.id.taskCompleted -> status = 4
            R.id.taskCancelled -> status = 3
        }
        showCustomProgressAlertDialog(
            "Updating Status",
            "Please wait while we update task status..."
        )
        val temp = taskId.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val reqBody = JSONObject()
        try {
            reqBody.put("task_id", temp[0])
            reqBody.put("status", status)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("Status", reqBody.toString())

        caseChannelsViewModel.commonViewModelCall(ApiUrls.updateTaskStatus, reqBody, Method.POST)
            .observe(
                requireActivity()
            ) { result ->
                try {
                    dialog.dismiss()
                    Log.d("Success", result)
                    val objRes = JSONObject(result)
                    if (objRes.getInt("status_code") == 200) {
                        val obj = objRes.getJSONObject("response")
                        if (obj.getInt("response") == 1) {
                            Toast.makeText(
                                context,
                                "Status Updated Successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            taskModelsList.clear()
                            summary
                        } else {
                            Toast.makeText(
                                context,
                                "Couldn't update the status. Try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        errorHandler(requireActivity(), result)
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    Toast.makeText(
                        context,
                        "Couldn't update the status. Try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun addNewPost(msg: String, discussionID: String) {
        showCustomProgressAlertDialog(
            "Posting Your Discussion",
            "Please wait while we post your discussion..."

        )
        val reqBody = JSONObject()
        try {
            reqBody.put("discussion_id", discussionID.toInt())
            reqBody.put("message", msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        caseChannelsViewModel.addNewPost(reqBody).observe(
            requireActivity()
        ) { result ->
            Log.d("Success", result)
            dialog.dismiss()
            try {
                val objRes = JSONObject(result)
                if (objRes.getInt("status_code") == 200) {
                    val obj = objRes.getJSONObject("response")
                    if (obj.getInt("response") == 1) {
                        Toast.makeText(context, "Post Added Successfully", Toast.LENGTH_SHORT)
                            .show()
                        postModelsList.clear()
                        summary
                    } else {
                        Toast.makeText(
                            context,
                            "Couldn't update your post. Try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.d("Error", result)
                    Toast.makeText(
                        context,
                        "Couldn't update your post. Try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Couldn't update your post. Try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (caseChannelModelsList!!.size != 0) {
            caseChannelModelsList!!.clear()
        }
        if (postModelsList.size != 0) {
            postModelsList.clear()
        }
        if (taskModelsList.size != 0) {
            taskModelsList.clear()
        }
        summary
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(activity).inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}