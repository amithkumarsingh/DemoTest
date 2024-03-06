package com.whitecoats.adapter

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.text.Html
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.whitecoats.clinicplus.ChatRoomActivity
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import com.whitecoats.model.ChatListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ChatListAdapter(
    private val chatListModelList: List<ChatListModel>,
    activity: Activity,
    type: Int
) : RecyclerView.Adapter<ChatListAdapter.MyViewHolder>() {
    private var type = 0 //0 = active, 1 = past
    private val activity: Activity
    var globalClass: MyClinicGlobalClass
    private var otpLoading: ProgressDialog? = null
    private var jsonValue: JSONObject? = null
    private val dashboardViewModel: DashboardViewModel
    private var receiptUrl: String? = null
    private val apiGetPostMethodCalls: ApiGetPostMethodCalls

    init {
        this.type = type
        this.activity = activity
        globalClass = activity.applicationContext as MyClinicGlobalClass
        dashboardViewModel = ViewModelProvider((activity as ViewModelStoreOwner)).get(
            DashboardViewModel::class.java
        )
        dashboardViewModel.init()
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_chat_list, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val chatListModel = chatListModelList[i]
        if (type == 0) {
            myViewHolder.patientName.text = chatListModel.patientName
            myViewHolder.dateTime.text = ": " + chatListModel.chatDateTime
            myViewHolder.bookAgain.visibility = View.GONE
            myViewHolder.dateTimeLable.text = activity.resources.getString(R.string.valid_till)
            myViewHolder.paymentStatusCreateReceipt.paintFlags =
                myViewHolder.paymentStatusCreateReceipt.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            if (chatListModel.paymentStatus.equals("Pending", ignoreCase = true)) {
                myViewHolder.receivedLayout.visibility = View.GONE
                myViewHolder.paymentStatus.visibility = View.VISIBLE
                if (chatListModel.sendPaymentReminder == 1) {
                    myViewHolder.apptListSendPaymentLink.visibility = View.VISIBLE
                    myViewHolder.paymentStatus.isEnabled = false
                    val resendPaymentLink = "Resend payment link"
                    val resendPaymentLinkContent = SpannableString(resendPaymentLink)
                    resendPaymentLinkContent.setSpan(
                        UnderlineSpan(),
                        0,
                        resendPaymentLink.length,
                        0
                    )
                    myViewHolder.apptListSendPaymentLink.text = resendPaymentLinkContent
                } else {
                    myViewHolder.apptListSendPaymentLink.visibility = View.VISIBLE
                    myViewHolder.paymentStatus.isEnabled = false
                    val sendPaymentLink = "Send payment link"
                    val sendPaymentLinkContent = SpannableString(sendPaymentLink)
                    sendPaymentLinkContent.setSpan(UnderlineSpan(), 0, sendPaymentLink.length, 0)
                    myViewHolder.apptListSendPaymentLink.text = sendPaymentLinkContent
                }
            } else {
                myViewHolder.receivedLayout.visibility = View.VISIBLE
                myViewHolder.paymentStatus.visibility = View.GONE
                myViewHolder.apptListSendPaymentLink.visibility = View.GONE
                if (chatListModel.receiptUrl.equals("", ignoreCase = true)) {
                    val createReceipt = "Create Receipt"
                    val content = SpannableString(createReceipt)
                    content.setSpan(UnderlineSpan(), 0, createReceipt.length, 0)
                    myViewHolder.paymentStatusCreateReceipt.text = content
                } else {
                    val showReceipt = "Show Receipt"
                    val content = SpannableString(showReceipt)
                    content.setSpan(UnderlineSpan(), 0, showReceipt.length, 0)
                    myViewHolder.paymentStatusCreateReceipt.text = content
                }
            }
            myViewHolder.paymentStatusCreateReceipt.setOnClickListener {
                if (globalClass.isOnline) {
                    if (myViewHolder.paymentStatusCreateReceipt.text.toString()
                            .equals("Show Receipt", ignoreCase = true)
                    ) {
                        getReceiptUrl(chatListModel.receiptUrl)
                    } else {
                        createReceipt(chatListModel.orderId, chatListModel, myViewHolder)
                    }
                } else {
                    globalClass.noInternetConnection.showDialog(activity)
                }
            }
            myViewHolder.apptListSendPaymentLink.setOnClickListener {
                if (globalClass.isOnline) {
                    dashboardViewModel.sendPaymentReminderDetails(
                        activity,
                        chatListModel.appointmentId
                    ).observe(
                        (activity as LifecycleOwner)
                    ) { value ->
                        try {
                            val jsonObject = JSONObject(value)
                            if (jsonObject.getInt("status_code") == 200) {
                                val response = JSONObject(value).getJSONObject("response")
                                val responseValue = response.getInt("response")
                                if (responseValue == 1) {
                                    Toast.makeText(
                                        activity,
                                        "Patient has been notified successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    if (myViewHolder.apptListSendPaymentLink.text.toString()
                                            .equals("Send payment link", ignoreCase = true)
                                    ) {
                                        myViewHolder.apptListSendPaymentLink.visibility =
                                            View.VISIBLE
                                        myViewHolder.paymentStatus.isEnabled = false
                                        val resendPaymentLink = "Resend payment link"
                                        val resendPaymentLinkContent =
                                            SpannableString(resendPaymentLink)
                                        resendPaymentLinkContent.setSpan(
                                            UnderlineSpan(),
                                            0,
                                            resendPaymentLink.length,
                                            0
                                        )
                                        myViewHolder.apptListSendPaymentLink.text =
                                            resendPaymentLinkContent
                                    }
                                } else {
                                }
                            } else {
                                if (value != null) {
                                    errorHandler(activity, value)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    globalClass.noInternetConnection.showDialog(activity)
                }
            }
            myViewHolder.paymentStatus.setOnClickListener { v ->
                val dialog = Dialog(activity)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dailog_apointment_payment_status_new)
                val spinner = dialog.findViewById<View>(R.id.paymentModeSpinner) as Spinner
                val userNameText = dialog.findViewById<View>(R.id.amountPaid) as EditText
                val dailogArticleCancel =
                    dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
                val sendReminderText = dialog.findViewById<View>(R.id.sendReminderText) as TextView
                val sendSMSReminderButtonLayout =
                    dialog.findViewById<View>(R.id.sendSMSReminderButtonLayout) as RelativeLayout
                val received = dialog.findViewById<View>(R.id.received) as RelativeLayout
                val receivedCreateReceipt =
                    dialog.findViewById<View>(R.id.receivedCreateReceipt) as RelativeLayout
                if (chatListModel.netAmount == 0) {
                    userNameText.setText("0")
                } else {
                    val netAmount = chatListModel.orderAmount - chatListModel.discount
                    userNameText.setText(netAmount.toString())
                }
                val str =
                    "<a><font color='#000000'> Send an SMS reminder to </font> " + chatListModel.patientName + " with payment link </a>"
                sendReminderText.text = Html.fromHtml(str)
                val adapter = ArrayAdapter(
                    v.context,
                    android.R.layout.simple_spinner_item, activity.resources
                        .getStringArray(R.array.paymentTypelistArray)
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                dailogArticleCancel.setOnClickListener { dialog.dismiss() }
                received.setOnClickListener {
                    if (globalClass.isOnline) {
                        if (spinner.selectedItemPosition == 0) {
                            Toast.makeText(
                                activity,
                                "Please select payment mode",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            dialog.dismiss()
                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Payment Card - Only Pay Done")
                            val paymentModespin = spinner.selectedItem.toString()
                            var paymentMode = ""
                            if (paymentModespin.equals("Cash", ignoreCase = true)) {
                                paymentMode = "Cash"
                            } else if (paymentModespin.equals("Credit Card", ignoreCase = true)) {
                                paymentMode = "CC"
                            }
                            if (paymentModespin.equals("Debit Card", ignoreCase = true)) {
                                paymentMode = "DC"
                            }
                            if (paymentModespin.equals("Net Banking", ignoreCase = true)) {
                                paymentMode = "Net Banking"
                            }
                            val amountPaid = userNameText.text.toString()
                            updatePaymentStatus(
                                amountPaid,
                                chatListModel.orderId,
                                paymentMode,
                                myViewHolder,
                                false,
                                chatListModel
                            )
                        }
                    } else {
                        globalClass.noInternetConnection.showDialog(activity)
                    }
                }
                receivedCreateReceipt.setOnClickListener {
                    if (globalClass.isOnline) {
                        if (spinner.selectedItemPosition == 0) {
                            Toast.makeText(
                                activity,
                                "Please select payment mode",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            dialog.dismiss()
                            val paymentModespin = spinner.selectedItem.toString()
                            var paymentMode = ""
                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Payment Card - Pay & Create Receipt")
                            if (paymentModespin.equals("Cash", ignoreCase = true)) {
                                paymentMode = "Cash"
                            } else if (paymentModespin.equals("Credit Card", ignoreCase = true)) {
                                paymentMode = "CC"
                            }
                            if (paymentModespin.equals("Debit Card", ignoreCase = true)) {
                                paymentMode = "DC"
                            }
                            if (paymentModespin.equals("Net Banking", ignoreCase = true)) {
                                paymentMode = "Net Banking"
                            }
                            val amountPaid = userNameText.text.toString()
                            updatePaymentStatus(
                                amountPaid,
                                chatListModel.orderId,
                                paymentMode,
                                myViewHolder,
                                true,
                                chatListModel
                            )
                        }
                    } else {
                        globalClass.noInternetConnection.showDialog(activity)
                    }
                }
                sendSMSReminderButtonLayout.setOnClickListener {
                    dialog.dismiss()
                    dashboardViewModel.sendPaymentReminderDetails(activity, chatListModel.chatId)
                        .observe(
                            (activity as LifecycleOwner)
                        ) { value ->
                            try {
                                val jsonObject = JSONObject(value)
                                if (jsonObject.getInt("status_code") == 200) {
                                    val response = JSONObject(value).getJSONObject("response")
                                    val responseValue = response.getInt("response")
                                    if (responseValue == 1) {
                                        Toast.makeText(
                                            activity,
                                            "Payment reminder send successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                    }
                                } else {
                                    if (value != null) {
                                        errorHandler(activity, value)
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                }
                dialog.show()
            }
            if (!chatListModel.readCount.equals("0", ignoreCase = true)) {
                myViewHolder.chatListUnreadCountLayout.visibility = View.VISIBLE
                myViewHolder.unreadCount.text = chatListModel.readCount
            } else {
                myViewHolder.chatListUnreadCountLayout.visibility = View.GONE
            }


            myViewHolder.imageText.text =
                if (chatListModel.patientName != null) chatListModel.patientName[0].toString() + "" else ""

            if (i == chatListModelList.size - 1) {
                myViewHolder.bottomLine.visibility = View.GONE
            }
            myViewHolder.mainLayout.setOnClickListener {
                val intent = Intent(activity, ChatRoomActivity::class.java)
                intent.putExtra("chatId", chatListModel.chatId)
                intent.putExtra("recipientId", chatListModel.recipientId)
                intent.putExtra("patientName", chatListModel.patientName)
                intent.putExtra("ChatType", "Active")
                activity.startActivity(intent)
            }
        } else {
            myViewHolder.patientName.text = chatListModel.patientName
            myViewHolder.dateTime.text = ": " + chatListModel.chatDateTime
            myViewHolder.dateTimeLable.text = activity.resources.getString(R.string.expired_on)
            if (chatListModel.paymentStatus.equals("Pending", ignoreCase = true)) {
                myViewHolder.receivedLayout.visibility = View.GONE
                myViewHolder.paymentStatus.visibility = View.VISIBLE
                if (chatListModel.sendPaymentReminder == 1) {
                    myViewHolder.apptListSendPaymentLink.visibility = View.GONE
                    myViewHolder.paymentStatus.isEnabled = false
                    val resendPaymentLink = "Resend payment link"
                    val resendPaymentLinkContent = SpannableString(resendPaymentLink)
                    resendPaymentLinkContent.setSpan(
                        UnderlineSpan(),
                        0,
                        resendPaymentLink.length,
                        0
                    )
                    myViewHolder.apptListSendPaymentLink.text = resendPaymentLinkContent
                } else {
                    myViewHolder.apptListSendPaymentLink.visibility = View.GONE
                    myViewHolder.paymentStatus.isEnabled = false
                    val sendPaymentLink = "Send payment link"
                    val sendPaymentLinkContent = SpannableString(sendPaymentLink)
                    sendPaymentLinkContent.setSpan(UnderlineSpan(), 0, sendPaymentLink.length, 0)
                    myViewHolder.apptListSendPaymentLink.text = sendPaymentLinkContent
                }
            } else {
                myViewHolder.receivedLayout.visibility = View.VISIBLE
                myViewHolder.paymentStatus.visibility = View.GONE
                myViewHolder.apptListSendPaymentLink.visibility = View.GONE
                if (chatListModel.receiptUrl.equals("", ignoreCase = true)) {
                    val createReceipt = "Create Receipt"
                    val content = SpannableString(createReceipt)
                    content.setSpan(UnderlineSpan(), 0, createReceipt.length, 0)
                    myViewHolder.paymentStatusCreateReceipt.text = content
                } else {
                    val showReceipt = "Show Receipt"
                    val content = SpannableString(showReceipt)
                    content.setSpan(UnderlineSpan(), 0, showReceipt.length, 0)
                    myViewHolder.paymentStatusCreateReceipt.text = content
                }
            }
            myViewHolder.paymentStatusCreateReceipt.paintFlags =
                myViewHolder.paymentStatusCreateReceipt.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            myViewHolder.paymentStatusCreateReceipt.setOnClickListener {
                if (globalClass.isOnline) {
                    if (myViewHolder.paymentStatusCreateReceipt.text.toString()
                            .equals("Show Receipt", ignoreCase = true)
                    ) {
                        getReceiptUrl(chatListModel.receiptUrl)
                    } else {
                        createReceipt(chatListModel.orderId, chatListModel, myViewHolder)
                    }
                } else {
                    globalClass.noInternetConnection.showDialog(activity)
                }
            }
            myViewHolder.apptListSendPaymentLink.setOnClickListener {
                if (globalClass.isOnline) {
                    dashboardViewModel.sendPaymentReminderDetails(
                        activity,
                        chatListModel.appointmentId
                    ).observe(
                        (activity as LifecycleOwner), object : Observer<String?> {
                            override fun onChanged(value: String?) {
                                try {
                                    val jsonObject = JSONObject(value)
                                    if (jsonObject.getInt("status_code") == 200) {
                                        val response = JSONObject(value).getJSONObject("response")
                                        val responseValue = response.getInt("response")
                                        if (responseValue == 1) {
                                            Toast.makeText(
                                                activity,
                                                "Patient has been notified successfully.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            if (myViewHolder.apptListSendPaymentLink.text.toString()
                                                    .equals("Send payment link", ignoreCase = true)
                                            ) {
                                                myViewHolder.apptListSendPaymentLink.visibility =
                                                    View.VISIBLE
                                                myViewHolder.paymentStatus.isEnabled = false
                                                val resendPaymentLink = "Resend payment link"
                                                val resendPaymentLinkContent =
                                                    SpannableString(resendPaymentLink)
                                                resendPaymentLinkContent.setSpan(
                                                    UnderlineSpan(),
                                                    0,
                                                    resendPaymentLink.length,
                                                    0
                                                )
                                                myViewHolder.apptListSendPaymentLink.text =
                                                    resendPaymentLinkContent
                                            }
                                        }
                                    } else {
                                        if (value != null) {
                                            errorHandler(activity, value)
                                        }
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        })
                } else {
                    globalClass.noInternetConnection.showDialog(activity)
                }
            }
            myViewHolder.paymentStatus.setOnClickListener { v ->
                val dialog = Dialog(activity)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dailog_apointment_payment_status_new)
                val spinner = dialog.findViewById<View>(R.id.paymentModeSpinner) as Spinner
                val userNameText = dialog.findViewById<View>(R.id.amountPaid) as EditText
                val dailogArticleCancel =
                    dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
                val sendReminderText = dialog.findViewById<View>(R.id.sendReminderText) as TextView
                val sendSMSReminderButtonLayout =
                    dialog.findViewById<View>(R.id.sendSMSReminderButtonLayout) as RelativeLayout
                val received = dialog.findViewById<View>(R.id.received) as RelativeLayout
                val receivedCreateReceipt =
                    dialog.findViewById<View>(R.id.receivedCreateReceipt) as RelativeLayout
                if (chatListModel.netAmount == 0) {
                    userNameText.setText("0")
                } else {
                    val netAmount = chatListModel.orderAmount - chatListModel.discount
                    userNameText.setText(netAmount.toString())
                }
                val str =
                    "<a><font color='#000000'> Send an SMS reminder to </font> " + chatListModel.patientName + " with payment link </a>"
                sendReminderText.text = Html.fromHtml(str)
                val adapter = ArrayAdapter(
                    v.context,
                    android.R.layout.simple_spinner_item, activity.resources
                        .getStringArray(R.array.paymentTypelistArray)
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                dailogArticleCancel.setOnClickListener { dialog.dismiss() }
                received.setOnClickListener {
                    if (globalClass.isOnline) {
                        if (spinner.selectedItemPosition == 0) {
                            Toast.makeText(
                                activity,
                                "Please select payment mode",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            dialog.dismiss()
                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Payment Card - Only Pay Done")
                            val paymentModespin = spinner.selectedItem.toString()
                            var paymentMode = ""
                            if (paymentModespin.equals("Cash", ignoreCase = true)) {
                                paymentMode = "Cash"
                            } else if (paymentModespin.equals("Credit Card", ignoreCase = true)) {
                                paymentMode = "CC"
                            }
                            if (paymentModespin.equals("Debit Card", ignoreCase = true)) {
                                paymentMode = "DC"
                            }
                            if (paymentModespin.equals("Net Banking", ignoreCase = true)) {
                                paymentMode = "Net Banking"
                            }
                            val amountPaid = userNameText.text.toString()
                            updatePaymentStatus(
                                amountPaid,
                                chatListModel.orderId,
                                paymentMode,
                                myViewHolder,
                                false,
                                chatListModel
                            )
                        }
                    } else {
                        globalClass.noInternetConnection.showDialog(activity)
                    }
                }
                receivedCreateReceipt.setOnClickListener {
                    if (globalClass.isOnline) {
                        if (spinner.selectedItemPosition == 0) {
                            Toast.makeText(
                                activity,
                                "Please select payment mode",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            dialog.dismiss()
                            val paymentModespin = spinner.selectedItem.toString()
                            var paymentMode = ""
                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Payment Card - Pay & Create Receipt")
                            if (paymentModespin.equals("Cash", ignoreCase = true)) {
                                paymentMode = "Cash"
                            } else if (paymentModespin.equals("Credit Card", ignoreCase = true)) {
                                paymentMode = "CC"
                            }
                            if (paymentModespin.equals("Debit Card", ignoreCase = true)) {
                                paymentMode = "DC"
                            }
                            if (paymentModespin.equals("Net Banking", ignoreCase = true)) {
                                paymentMode = "Net Banking"
                            }
                            val amountPaid = userNameText.text.toString()
                            updatePaymentStatus(
                                amountPaid,
                                chatListModel.orderId,
                                paymentMode,
                                myViewHolder,
                                true,
                                chatListModel
                            )
                        }
                    } else {
                        globalClass.noInternetConnection.showDialog(activity)
                    }
                }
                sendSMSReminderButtonLayout.setOnClickListener {
                    dialog.dismiss()
                    dashboardViewModel.sendPaymentReminderDetails(
                        activity,
                        chatListModel.appointmentId
                    ).observe(
                        (activity as LifecycleOwner), object : Observer<String?> {
                            override fun onChanged(value: String?) {
                                try {
                                    val jsonObject = JSONObject(value)
                                    if (jsonObject.getInt("status_code") == 200) {
                                        val response = JSONObject(value).getJSONObject("response")
                                        val responseValue = response.getInt("response")
                                        if (responseValue == 1) {
                                            Toast.makeText(
                                                activity,
                                                "Payment reminder send successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        if (value != null) {
                                            errorHandler(activity, value)
                                        }
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        })
                }
                dialog.show()
            }
            if (!chatListModel.readCount.equals("0", ignoreCase = true)) {
                myViewHolder.chatListUnreadCountLayout.visibility = View.VISIBLE
                myViewHolder.unreadCount.text = chatListModel.readCount
            } else {
                myViewHolder.chatListUnreadCountLayout.visibility = View.GONE
            }


           myViewHolder.imageText.text =
                if (chatListModel.patientName != null) chatListModel.patientName[0].toString() + "" else ""
            //            if (nameSplit.length > 1) {
//                myViewHolder.imageText.setText(nameSplit[0].charAt(0) + "" + nameSplit[1].charAt(0));
//            }
            if (i == chatListModelList.size - 1) {
                myViewHolder.bottomLine.visibility = View.GONE
            }
            myViewHolder.mainLayout.setOnClickListener {
                val intent = Intent(activity, ChatRoomActivity::class.java)
                intent.putExtra("chatId", chatListModel.chatId)
                intent.putExtra("recipientId", chatListModel.recipientId)
                intent.putExtra("patientName", chatListModel.patientName)
                intent.putExtra("ChatType", "Past")
                activity.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return chatListModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var patientName: TextView
        var dateTime: TextView
        var unreadCount: TextView
        var bookAgain: TextView
        var imageText: TextView
        var dateTimeLable: TextView
        var paymentStatusCreateReceipt: TextView
        var paymentStatus: TextView
        var bottomLine: View
        var mainLayout: RelativeLayout
        var chatListUnreadCountLayout: RelativeLayout
        var receivedLayout //newMessageLayout
                : RelativeLayout

        //        public ImageView newMessageIcon;
        var paymentStatusReceivedLabel: TextView
        var apptListSendPaymentLink: TextView

        init {
            patientName = itemView.findViewById(R.id.chatListPatientName)
            dateTime = itemView.findViewById(R.id.chatListDateTime)
            chatListUnreadCountLayout = itemView.findViewById(R.id.chatListUnreadCountLayout)
            unreadCount = itemView.findViewById(R.id.chatListUnreadCount)
            bookAgain = itemView.findViewById(R.id.chatListBookAgain)
            bottomLine = itemView.findViewById(R.id.chatListBottomLine)
            imageText = itemView.findViewById(R.id.chatListImageText)
            dateTimeLable = itemView.findViewById(R.id.chatListDateTimeLabel)
            mainLayout = itemView.findViewById(R.id.chatListMainLayout)
            apptListSendPaymentLink = itemView.findViewById(R.id.apptListSendPaymentLink)
            paymentStatus = itemView.findViewById(R.id.paymentStatus)
            receivedLayout = itemView.findViewById(R.id.receivedLayout)
            paymentStatusCreateReceipt = itemView.findViewById(R.id.paymentStatusCreateReceipt)
            paymentStatusReceivedLabel = itemView.findViewById(R.id.paymentStatusReceivedLabel)
            //            newMessageLayout = itemView.findViewById(R.id.newMessageLayout);
//            newMessageIcon = itemView.findViewById(R.id.newMessageIcon);
        }
    }

    fun updatePaymentStatus(
        paidAmount: String,
        orderId: Int,
        paymentMode: String?,
        itemViewHolder: MyViewHolder,
        isGeneratedReceipt: Boolean,
        chatListModel: ChatListModel
    ) {
        otpLoading = ProgressDialog(activity)
        otpLoading!!.setMessage(activity.resources.getString(R.string.wait_while_we_updating))
        otpLoading!!.setTitle(activity.resources.getString(R.string.updating))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        val URL = ApiUrls.updatePaymentStatus
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("order_net_amount", paidAmount.toInt())
            jsonValue!!.put("order_id", orderId)
            jsonValue!!.put("order_payment_mode", paymentMode)
            jsonValue!!.put("isGenerateReport", isGeneratedReceipt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        apiGetPostMethodCalls.volleyApiRequestData(
            URL,
            Request.Method.POST,
            jsonValue,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        //Process os success response
                        otpLoading!!.dismiss()
                        val response = JSONObject(result)
                        val rootObj = response.getJSONObject("response")
                        chatListModel.paymentStatus = rootObj.getString("payment_status")
                        itemViewHolder.receivedLayout.visibility = View.VISIBLE
                        itemViewHolder.paymentStatus.visibility = View.GONE
                        if (isGeneratedReceipt) {
                            val intervention = rootObj["receipt"]
                            if (intervention is JSONArray) {
                            } else if (intervention is JSONObject) {
                                // It's an object
                                val receiptObject = rootObj.getJSONObject("receipt")
                                chatListModel.receiptUrl = receiptObject.getString("public_url")
                            } else {
                                chatListModel.receiptUrl = ""
                                // It's something else, like a string or number
                            }
                            val showReceipt = "Show Receipt"
                            val content = SpannableString(showReceipt)
                            content.setSpan(UnderlineSpan(), 0, showReceipt.length, 0)
                            itemViewHolder.paymentStatusCreateReceipt.text = content
                        } else {
                            val createReceipt = "Create Receipt"
                            val content = SpannableString(createReceipt)
                            content.setSpan(UnderlineSpan(), 0, createReceipt.length, 0)
                            itemViewHolder.paymentStatusCreateReceipt.text = content
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading!!.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(activity, err)
                }
            })
    }

    private fun getReceiptUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        activity.startActivity(i)
    }

    private fun createReceipt(
        orderId: Int,
        chatListModel: ChatListModel,
        itemViewHolder: MyViewHolder
    ) {
        val url = ApiUrls.createReceipt + "?order_id=" + orderId
        otpLoading = ProgressDialog(activity)
        otpLoading!!.setMessage(activity.resources.getString(R.string.please_wait))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()

        // prepare the Request
        apiGetPostMethodCalls.volleyApiRequestData(
            url,
            Request.Method.GET,
            null,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        val rootObj = response.getJSONObject("response")
                        receiptUrl = rootObj.getString("public_url")
                        chatListModel.receiptUrl = receiptUrl
                        val showReceipt = "Show Receipt"
                        val content = SpannableString(showReceipt)
                        content.setSpan(UnderlineSpan(), 0, showReceipt.length, 0)
                        itemViewHolder.paymentStatusCreateReceipt.text = content
                        otpLoading!!.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading!!.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(activity, err)
                }
            })
    }
}