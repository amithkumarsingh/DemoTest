package com.whitecoats.clinicplus.casechannels

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opentok.android.Connection
import com.opentok.android.Session
import com.opentok.android.Session.SignalListener
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.PatientRecordsApi
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
class CaseChannelPostFragment : Fragment(), SignalListener {
    private var postRv: RecyclerView? = null
    private var postDiscussionModels: MutableList<PostDiscussionModel>? = null
    private var postFragmentAdapter: PostFragmentAdapter? = null
    private var apiCall: PatientRecordsApi? = null
    private var progressBarLayout: RelativeLayout? = null
    private var appUtilities: AppUtilities? = null
    private var emptyText: TextView? = null
    private var newPost: Button? = null
    private var viewGroup: ViewGroup? = null
    private var caseChannelId = 0
    private var loader: ProgressDialog? = null
    private var docName = ""
    private var mSession: Session? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_case_channel_post, container, false)
        postRv = view.findViewById(R.id.caseChannelPostRecycleView)
        progressBarLayout = view.findViewById(R.id.onGoingProgressBar)
        emptyText = view.findViewById(R.id.caseChannelPostEmptyText)
        newPost = view.findViewById(R.id.caseChannelNewPostBtn)
        viewGroup = view.findViewById(android.R.id.content)
        postDiscussionModels = ArrayList()
        postFragmentAdapter = PostFragmentAdapter(postDiscussionModels as ArrayList<PostDiscussionModel>)
        apiCall = PatientRecordsApi()
        appUtilities = AppUtilities()
        loader = ProgressDialog(activity)
        val horizontalLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        horizontalLayoutManager.reverseLayout = true
        postRv?.layoutManager = horizontalLayoutManager
        postRv?.adapter = postFragmentAdapter
        if (arguments != null) {
            caseChannelId = requireArguments().getInt("caseChannelId")
        }
        docDetails
        discussionPost
        newPost?.setOnClickListener(View.OnClickListener { view1: View? -> openNewPostForm() })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
    }

    private val discussionPost: Unit
        private get() {
            progressBarLayout!!.visibility = View.VISIBLE
            emptyText!!.visibility = View.GONE
            val url = ApiUrls.getChannelDiscussions + "?discussion_id=" + caseChannelId
            apiCall!!.getRecordPref(url, "", activity, object : VolleyCallback {
                override fun onSuccess(result: String) {
                    progressBarLayout!!.visibility = View.GONE
                    try {
                        Log.d("Post Data", result)
                        var resObj = JSONObject(result)
                        resObj = resObj.getJSONObject("response")
                        val postArr = resObj.getJSONArray("chatMsgs")
                        mSession = Session.Builder(
                            context,
                            resObj.getString("apiKey"),
                            resObj.getString("chatSession")
                        ).build()
                        mSession?.setSignalListener(SignalListener { session: Session, type: String, data: String, connection: Connection ->
                            onSignalReceived(
                                session,
                                type,
                                data,
                                connection
                            )
                        })
                        mSession?.connect(resObj.getString("token"))
                        if (postArr.length() > 0) {
                            for (i in 0 until postArr.length()) {
                                val temp = postArr.getJSONObject(i)
                                val postDiscussionModel = PostDiscussionModel()
                                postDiscussionModel.docName =
                                    temp.getJSONObject("chat_sender").getString("fname")
                                postDiscussionModel.msgText = temp.getString("message")
                                postDiscussionModel.msgTime = appUtilities!!.changeDateFormat(
                                    "yyyy-dd-MM HH:mm:ss",
                                    "dd MMM, yy HH:mm",
                                    temp.getString("tstamp")
                                )
                                if (temp.getInt("sender_id") == ApiUrls.doctorId) {
                                    postDiscussionModel.msgType = 1
                                } else {
                                    postDiscussionModel.msgType = 0
                                }
                                postDiscussionModels!!.add(postDiscussionModel)
                            }
                            postFragmentAdapter!!.notifyDataSetChanged()
                        } else {
                            emptyText!!.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    progressBarLayout!!.visibility = View.GONE
                    errorHandler(requireActivity(), err)
                }
            })
        }

    private fun openNewPostForm() {


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
        newPostBtn.setOnClickListener { view: View? ->
            addNewPost(postMsg.text.toString())
            alertDialog.dismiss()
        }
    }

    private fun addNewPost(msg: String) {
        loader!!.setTitle("Posting Your Discussion")
        loader!!.setMessage("Please wait while we post your discussion...")
        loader!!.setCancelable(false)
        loader!!.show()
        val reqBody = JSONObject()
        try {
            reqBody.put("discussion_id", caseChannelId)
            reqBody.put("message", msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        apiCall!!.postRecords(
            ApiUrls.postNewDiscussion,
            reqBody,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    Log.d("Success", result)
                    loader!!.dismiss()
                    mSession!!.sendSignal("msg", msg)
                    try {
                        val resObj = JSONObject(result)
                        if (resObj.getInt("response") == 1) {
                            emptyText!!.visibility = View.GONE
                            Toast.makeText(context, "Post added successfully", Toast.LENGTH_SHORT)
                                .show()
                            val postDiscussionModel = PostDiscussionModel()
                            postDiscussionModel.docName = docName
                            postDiscussionModel.msgText = msg
                            val sdf = SimpleDateFormat("yyyy-dd-MM HH:mm:ss", Locale.getDefault())
                            val currentDateandTime = sdf.format(Date())
                            postDiscussionModel.msgTime = appUtilities!!.changeDateFormat(
                                "yyyy-dd-MM HH:mm:ss",
                                "dd MMM, yy HH:mm",
                                currentDateandTime
                            )
                            postDiscussionModel.msgType = 1
                            postDiscussionModels!!.add(0, postDiscussionModel)
                            postFragmentAdapter!!.notifyDataSetChanged()
                        } else {
                            Toast.makeText(
                                context,
                                "Unable to post your message. Try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    Log.d("Error", err)
                    loader!!.dismiss()
                    errorHandler(requireActivity(), err)
                }
            })
    }

    private val docDetails: Unit
        private get() {
            apiCall!!.getRecordPref(ApiUrls.authMe, "", activity, object : VolleyCallback {
                override fun onSuccess(result: String) {
                    Log.d("Success", result)
                    try {
                        val resObj = JSONObject(result)
                        docName = resObj.getJSONObject("user").getString("fname")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(requireActivity(), err)
                }
            })
        }

    override fun onSignalReceived(
        session: Session,
        type: String,
        data: String,
        connection: Connection
    ) {
        val remote = connection != mSession!!.connection
        Log.d("Remote Connection", remote.toString() + "")
        if (remote) {
            if (type == "msg") {
                try {
                    val temp = JSONObject(data)
                    val postDiscussionModel = PostDiscussionModel()
                    postDiscussionModel.msgText = temp.getString("message")
                    postDiscussionModel.docName = temp.getString("docName")
                    val sdf = SimpleDateFormat("yyyy-dd-MM HH:mm:ss", Locale.getDefault())
                    val currentDateandTime = sdf.format(Date())
                    postDiscussionModel.msgTime = appUtilities!!.changeDateFormat(
                        "yyyy-dd-MM HH:mm:ss",
                        "dd MMM, yy HH:mm",
                        currentDateandTime
                    )
                    postDiscussionModel.msgType = 2
                    postDiscussionModels!!.add(0, postDiscussionModel)
                    postFragmentAdapter!!.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}