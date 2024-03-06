package com.whitecoats.clinicplus

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import android.view.View.OnLayoutChangeListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opentok.android.Connection
import com.opentok.android.Session
import com.opentok.android.Session.SignalListener
import com.whitecoats.adapter.ChatRoomAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.model.ChatRoomModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomActivity : AppCompatActivity(), SignalListener {
    private var chatRoomRecycleView: RecyclerView? = null
    private var chatRoomModelList: MutableList<ChatRoomModel>? = null
    private var chatRoomAdapter: ChatRoomAdapter? = null
    private var chatId = 0
    private var recipientId = 0
    private var onGoingProgressBar: RelativeLayout? = null
    private var appUtilities: AppUtilities? = null
    private var appointmentApiRequests: AppointmentApiRequests? = null
    private var chatEditText: EditText? = null
    private var chatSendImageButton: ImageButton? = null
    private var lastMessage = ""
    private var emptyText: TextView? = null
    private var CHAT_TOKEN = ""
    private var CHAT_SESSION = ""
    private var API_KEY = ""
    private var mSession: Session? = null
    private var activeBottomLayout: RelativeLayout? = null
    private var pastBottomLayout: RelativeLayout? = null
    private var bookNow: Button? = null
    private var apiCall: PatientRecordsApi? = null
    private var loader: ProgressDialog? = null
    private val toolbar: Toolbar? = null
    private var selectedPaymentDuration = false
    private var invoiceNumber: String? = null

    //chat service data
    private var productId = 0
    private var serviceId = 0
    private var dateAndTimeFormat: String? = null
    private var chatListBack: ImageButton? = null
    private var chat_patient_name: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        chatListBack = findViewById(R.id.chat_add_back)
        chat_patient_name = findViewById(R.id.chat_patient_name)
        chat_patient_name?.text = intent.getStringExtra("patientName")
        chatListBack?.setOnClickListener(View.OnClickListener {
            if (mSession != null) {
                mSession!!.disconnect()
            }
            val intent = Intent()
            intent.putExtra("last_message", lastMessage)
            intent.putExtra("chat_id", chatId)
            setResult(RESULT_OK, intent)
            finish()
        })
        chatId = intent.getIntExtra("chatId", 0)
        recipientId = intent.getIntExtra("recipientId", 0)
        val chatType = intent.getStringExtra("ChatType")
        onGoingProgressBar = findViewById(R.id.onGoingProgressBar)
        appointmentApiRequests = AppointmentApiRequests()
        appUtilities = AppUtilities()
        apiCall = PatientRecordsApi()
        chatClickFlag = 1
        ApiUrls.bottomNaviType = 3
        ZohoSalesIQ.Tracking.setPageTitle("ChatRoom Page")
        chatRoomRecycleView = findViewById(R.id.chatRoomRecycleView)
        chatRoomModelList = ArrayList()
        chatRoomAdapter = ChatRoomAdapter(chatRoomModelList as ArrayList<ChatRoomModel>)
        chatEditText = findViewById(R.id.chatEditText)
        chatSendImageButton = findViewById(R.id.chatSendImageButton)
        activeBottomLayout = findViewById(R.id.chatRoomActiveBottomLayout)
        pastBottomLayout = findViewById(R.id.chatRoomPastBottomLayout)
        bookNow = findViewById(R.id.chatRoomBookNow)
        emptyText = findViewById(R.id.chatRoomEmptyText)
        dateAndTimeFormat = if (appUtilities!!.timeFormatPreferences(applicationContext) == 12) {
            "dd MMM yy hh:mm aa"
        } else {
            "dd MMM yy HH:mm"
        }
        if (chatType.equals("Active", ignoreCase = true)) {
            activeBottomLayout?.visibility = View.VISIBLE
            pastBottomLayout?.visibility = View.GONE
        } else {
            activeBottomLayout?.visibility = View.GONE
            pastBottomLayout?.visibility = View.VISIBLE
        }
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        (mLayoutManager as LinearLayoutManager).stackFromEnd = true
        chatRoomRecycleView?.layoutManager = mLayoutManager
        chatRoomRecycleView?.itemAnimator = DefaultItemAnimator()
        chatRoomRecycleView?.adapter = chatRoomAdapter


        //dummyData();
        onGoingChat
        docDetails
        patientDetails
        chatSendImageButton?.setOnClickListener(View.OnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ChatRoom - Send New Msg")
            try {
                if (chatEditText?.text.toString() != "") {
                    val msgObj = JSONObject()
                    msgObj.put("chat_id", chatId)
                    msgObj.put("recipient_id", recipientId)
                    msgObj.put("message", chatEditText?.text.toString())

                    val chatMessageModel = ChatRoomModel()
                    chatMessageModel.chatMsg = msgObj.getString("message")

                    val destDf = SimpleDateFormat(dateAndTimeFormat)
                    val symbols = DateFormatSymbols(Locale.US)
                    destDf.dateFormatSymbols = symbols
                    val dateStr = destDf.format(Date())
                    chatMessageModel.chatTime = dateStr
                    chatMessageModel.type = 0
                    (chatRoomModelList as ArrayList<ChatRoomModel>).add(chatMessageModel)
                    chatRoomAdapter!!.notifyDataSetChanged()
                    chatRoomRecycleView?.addOnLayoutChangeListener(OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                        if (bottom == oldBottom) {
                            chatRoomRecycleView!!.post(Runnable {
                                chatRoomRecycleView!!.scrollToPosition(
                                    chatRoomRecycleView!!.adapter!!.itemCount - 1
                                )
                            })
                        }
                    })



                    lastMessage = chatEditText?.text.toString()
                    mSession!!.sendSignal("TextChat", chatEditText?.text.toString())
                    chatEditText?.setText("")
                    chatEditText?.hint = resources.getString(R.string.type_a_message)
                    if (emptyText?.visibility == View.VISIBLE) {
                        emptyText!!.visibility = View.GONE
                    }
                    sendChatMessage(msgObj)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        bookNow?.setOnClickListener(View.OnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ChatRoom - Book New Chat")
            val text =
                "Do you want to book a chat session with " + intent.getStringExtra("patientName")
            showCustomDialog(text)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Do something that differs the Activity's menu here
        menuInflater.inflate(R.menu.chat_room_menu, menu)
        val icon = menu.findItem(R.id.chatRoomMenuHistory).icon
        icon!!.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        menu.findItem(R.id.chatRoomMenuHistory).icon = icon
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.chatRoomMenuHistory -> {
                val intenti = Intent(this, HistoryActivity::class.java)
                startActivity(intenti)
                true
            }
            android.R.id.home -> {
                if (mSession != null) {
                    mSession!!.disconnect()
                }
                val intent = Intent()
                intent.putExtra("last_message", lastMessage)
                intent.putExtra("chat_id", chatId)
                setResult(RESULT_OK, intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //        Log.d("Chat Url", url);
    private val onGoingChat: Unit
        private get() {
            onGoingProgressBar!!.visibility = View.VISIBLE
            chatRoomModelList!!.clear()
            emptyText!!.visibility = View.VISIBLE
            emptyText!!.text = "Getting your messages..."
            val url = ApiUrls.getOngoingChatList + "?chat_id=" + chatId
            //        Log.d("Chat Url", url);
            appointmentApiRequests!!.getApptApiData(
                url,
                "",
                this@ChatRoomActivity,
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        try {
                            onGoingProgressBar!!.visibility = View.GONE
                            val resObj = JSONObject(result)
                            val resObject = resObj.getJSONObject("response")
                            val resArr = resObject.getJSONArray("chatMsgs")
                            //totalActiveChat = resArr.length();
                            if (resArr.length() > 0) {
                                emptyText!!.visibility = View.GONE
                                for (i in 0 until resArr.length()) {
                                    val temp = resArr.getJSONObject(i)
                                    val chatSenderMessageObject = temp.getJSONObject("chat_sender")
                                    val chatMessageModel = ChatRoomModel()
                                    chatMessageModel.chatMsg = temp.getString("message")
                                    val date = appUtilities!!.changeDateFormat(
                                        "yyyy-MM-dd HH:mm:ss",
                                        dateAndTimeFormat,
                                        temp.getString("tstamp")
                                    )

                                    chatMessageModel.chatTime = date
                                    if (temp.getInt("sender_id") == ApiUrls.doctorId) {
                                        chatMessageModel.type = 0
                                    } else {
                                        chatMessageModel.type = 1
                                    }
                                    chatRoomModelList!!.add(chatMessageModel)

                                    chatRoomAdapter!!.notifyDataSetChanged()
                                    lastMessage = temp.getString("message")
                                }
                                if (resObject.has("token")) {
                                    CHAT_TOKEN = resObject.getString("token")
                                    CHAT_SESSION = resObject.getString("chatSession")
                                    API_KEY = resObject.getString("apiKey")
                                    mSession = Session.Builder(
                                        this@ChatRoomActivity,
                                        API_KEY,
                                        CHAT_SESSION
                                    ).build()
                                    mSession?.setSignalListener(this@ChatRoomActivity)
                                    mSession?.connect(CHAT_TOKEN)
                                }
                            } else {
                                if (resObject.has("token")) {
                                    CHAT_TOKEN = resObject.getString("token")
                                    CHAT_SESSION = resObject.getString("chatSession")
                                    API_KEY = resObject.getString("apiKey")
                                    mSession = Session.Builder(
                                        this@ChatRoomActivity,
                                        API_KEY,
                                        CHAT_SESSION
                                    ).build()
                                    mSession?.setSignalListener(this@ChatRoomActivity)
                                    mSession?.connect(CHAT_TOKEN)
                                }
                                emptyText!!.visibility = View.VISIBLE
                                emptyText!!.text = "No message sent or received"
                            }
                        } catch (e: JSONException) {
                            e.message
                            onGoingProgressBar!!.visibility = View.GONE
                            emptyText!!.visibility = View.VISIBLE
                            emptyText!!.text = "Error Occurred. Try again later"
                        }
                    }

                    override fun onError(err: String) {
                        err.lowercase(Locale.getDefault())
                        onGoingProgressBar!!.visibility = View.GONE
                        emptyText!!.visibility = View.VISIBLE
                        emptyText!!.text = "Error Occurred. Try again later"
                    }
                })
        }

    private fun sendChatMessage(reqObj: JSONObject) {


        appointmentApiRequests!!.postApptApiData(
            ApiUrls.saveChatMessage,
            reqObj,
            this@ChatRoomActivity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
//                Log.d("Update Status", result);
                    try {
                        val resObj = JSONObject(result)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@ChatRoomActivity, err)
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val b = data!!.extras
                if (b != null) {
                    try {
                        val shared = JSONArray(b.getString("share_data"))
                        var msg = "Shared Data:\n"
                        for (i in 0 until shared.length()) {
                            msg += """
                                ${shared.getString(i)}
                                
                                """.trimIndent()
                        }


                        val chatMessageModel = ChatRoomModel()
                        chatMessageModel.chatMsg = msg
                        val destDf: DateFormat = SimpleDateFormat(dateAndTimeFormat)
                        val dateStr = destDf.format(Date())
                        // String date = appUtilities.changeDateFormat("yyyy-MM-dd hh:mm:ss", "hh:mm", temp.getString("tstamp"));
                        chatMessageModel.chatTime = dateStr
                        chatMessageModel.type = 1
                        chatRoomModelList!!.add(chatMessageModel)
                        chatRoomAdapter!!.notifyDataSetChanged()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (resultCode == 0) {
                println("RESULT CANCELLED")
            }
        }
    }

    override fun onSignalReceived(
        session: Session,
        type: String,
        data: String,
        connection: Connection
    ) {
        val remote = connection != mSession!!.connection
        //        Log.d("Remote Connection", remote + "");
        if (remote) {
            if (type == "TextChat") {

                val chatMessageModel = ChatRoomModel()
                chatMessageModel.chatMsg = data
                val destDf: DateFormat = SimpleDateFormat(dateAndTimeFormat)
                val dateStr = destDf.format(Date())
                // String date = appUtilities.changeDateFormat("yyyy-MM-dd hh:mm:ss", "hh:mm", temp.getString("tstamp"));
                chatMessageModel.chatTime = dateStr
                lastMessage = data
                chatMessageModel.type = 1
                chatRoomModelList!!.add(chatMessageModel)
                chatRoomAdapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onBackPressed() {
        if (mSession != null) {
            mSession!!.disconnect()
        }
        val intent = Intent()
        intent.putExtra("last_message", lastMessage)
        intent.putExtra("chat_id", chatId)
        setResult(RESULT_OK, intent)
        finish()
    }


    private fun showCustomDialog(dialogTxt: String) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_chat_booking, viewGroup, false)
        val yes = dialogView.findViewById<TextView>(R.id.yes)
        val no = dialogView.findViewById<TextView>(R.id.no)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialogText)
        val paymentReminderCheckBox =
            dialogView.findViewById<View>(R.id.paymentReminderCheckBox) as CheckBox
        dialogText.text = dialogTxt

        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(this)

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)

        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.show()
        yes.setOnClickListener {
            alertDialog.dismiss()
            ZohoSalesIQ.Tracking.setCustomAction("ChatRoom - Book New Chat - Yes")
            bookChatAppt()
        }
        no.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ChatRoom - Book New Chat - No")
            alertDialog.dismiss()
        }
        paymentReminderCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            selectedPaymentDuration = if (isChecked) {
                true
            } else {
                false
            }
        }
    }

    private val docDetails: Unit
        private get() {
            val url = ApiUrls.getDoctorDetail + "?id=" + ApiUrls.doctorId
            apiCall!!.getRecordPref(url, "", this, object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        val chatObj = resObj.getJSONObject("response").getJSONObject("chat_product")
                        productId = chatObj.getInt("id")
                        serviceId = chatObj.getInt("dr_service_id")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@ChatRoomActivity, err)
                }
            })
        }

    private fun bookChatAppt() {
        loader = ProgressDialog(this)
        loader!!.setMessage("Please wait while....")
        loader!!.setTitle("Book Appointment")
        loader!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        loader!!.setCancelable(false)
        loader!!.show()
        invoiceNumber = java.lang.Long.toString(System.currentTimeMillis())
        val chatObj = JSONObject()
        try {
            chatObj.put("endSlot", "")
            chatObj.put("startSlot", "")
            chatObj.put("patientId", recipientId)
            chatObj.put("prodId", productId)
            chatObj.put("servId", serviceId)
            chatObj.put("invoice_no", invoiceNumber)
            chatObj.put("send_payment_notification", selectedPaymentDuration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        apiCall!!.postRecords(ApiUrls.bookAppointment, chatObj, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
//                Log.d("Chat Booking result", result);
                loader!!.dismiss()
                try {
                    val resObj = JSONObject(result)
                    if (resObj.getString("response").equals("success", ignoreCase = true)) {
                        val alertDialog = AlertDialog.Builder(this@ChatRoomActivity).create()
                        alertDialog.setTitle("Chat booked")
                        alertDialog.setMessage("Chat booked successfully. You can now chat with the patient.")
                        alertDialog.setButton(
                            AlertDialog.BUTTON_NEUTRAL, "OK"
                        ) { dialog, which ->
                            dialog.dismiss()
                            finish()
                        }
                        alertDialog.show()
                    } else {
                        Toast.makeText(
                            this@ChatRoomActivity,
                            "Error while booking chat. Try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@ChatRoomActivity,
                        "Error while booking chat. Try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onError(err: String) {
                loader!!.dismiss()
                errorHandler(this@ChatRoomActivity, err)
            }
        })
    }

    private val patientDetails: Unit
        private get() {
            val url = ApiUrls.getPatientBackground + "?patient_id=" + recipientId
            apiCall!!.getRecordPref(url, "", this, object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        val patientObj = resObj.getJSONObject("response")
                        toolbar!!.title = patientObj.getString("name")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@ChatRoomActivity, err)
                }
            })
        }

    companion object {
        @JvmField
        var chatClickFlag = 0
    }
}