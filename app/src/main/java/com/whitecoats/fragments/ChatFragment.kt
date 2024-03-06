package com.whitecoats.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.android.volley.Request
import com.whitecoats.adapter.ChatListAdapter
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.fragments.AppointmentFragment.Companion.appointmentTabFlag
import com.whitecoats.fragments.AppointmentFragment.Companion.lastHeaderDate
import com.whitecoats.fragments.PatientFragment.Companion.patientTabFlag
import com.whitecoats.model.ChatListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ChatFragment : Fragment() {
    private var chatActiveList: RecyclerView? = null
    private var chatPastList: RecyclerView? = null
    private var chatListModelActiveList: MutableList<ChatListModel>? = null
    private var chatListModelPastList: MutableList<ChatListModel>? = null
    private var chatListActiveAdapter: ChatListAdapter? = null
    private var chatListPastAdapter: ChatListAdapter? = null
    private var appointmentApiRequests: AppointmentApiRequests? = null
    private var activeProgressBar: RelativeLayout? = null
    private var pastProgressBar: RelativeLayout? = null
    var toolbar: Toolbar? = null
    private var totalActiveChat = 0
    private var totalPastChat = 0
    private var appUtilities: AppUtilities? = null
    private val swipeContainer: SwipeRefreshLayout? = null
    private var pastEmptyText: TextView? = null
    private var activeEmptyText: TextView? = null
    private var activeEmptyLayout: RelativeLayout? = null
    private var bookChat: Button? = null
    private var dateAndTimeFormat: String? = null
    private var pullToRefresh: SwipeRefreshLayout? = null
    var globalClass: MyClinicGlobalClass? = null
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val chatView = inflater.inflate(R.layout.fragment_chat, container, false)

        //getting the toolbar
        toolbar = chatView.findViewById(R.id.chatListToolbar)
        toolbar?.title = "Chats"
        chatTabFlag = 1
        appointmentTabFlag = 0
        patientTabFlag = 0
        lastHeaderDate = ""
        //        toolbar.setSubtitle("Total: 0");
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        appointmentApiRequests = AppointmentApiRequests()
        appUtilities = AppUtilities()
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        globalClass = requireActivity().applicationContext as MyClinicGlobalClass
        activeProgressBar = chatView.findViewById(R.id.activeProgressBar)
        pastProgressBar = chatView.findViewById(R.id.pastProgressBar)
        pastEmptyText = chatView.findViewById(R.id.chatListPastEmptyText)
        activeEmptyText = chatView.findViewById(R.id.chatListActiveEmptyText)
        activeEmptyLayout = chatView.findViewById(R.id.activeEmptyLayout)
        bookChat = chatView.findViewById(R.id.activeBookChat)
        pullToRefresh = chatView.findViewById(R.id.pullToRefresh)
        dateAndTimeFormat = if (appUtilities!!.timeFormatPreferences(requireContext()) == 12) {
            "MMMM dd, yyyy hh:mm aa"
        } else {
            "MMMM dd, yyyy HH:mm"
        }

        //bottom setting type
        ApiUrls.bottomNaviType = 3
        ZohoSalesIQ.Tracking.setPageTitle("On Chat List Page")
        chatActiveList = chatView.findViewById(R.id.chatListActiveRecycleView)
        chatPastList = chatView.findViewById(R.id.chatListPastRecycleView)
        chatListModelActiveList = ArrayList()
        chatListActiveAdapter = activity?.let { ChatListAdapter(chatListModelActiveList as ArrayList<ChatListModel>, it, 0) }
        chatListModelPastList = ArrayList()
        chatListPastAdapter = activity?.let { ChatListAdapter(chatListModelPastList as ArrayList<ChatListModel>, it, 1) }

        //for active list
        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(requireActivity().applicationContext)
        chatActiveList?.layoutManager = mLayoutManager
        chatActiveList?.itemAnimator = DefaultItemAnimator()
        chatActiveList?.adapter = chatListActiveAdapter

        //for past list
        val mLayoutManager2: RecyclerView.LayoutManager =
            LinearLayoutManager(requireActivity().applicationContext)
        chatPastList?.layoutManager = mLayoutManager2
        chatPastList?.itemAnimator = DefaultItemAnimator()
        chatPastList?.adapter = chatListPastAdapter
//        activeChatList
//        pastChatList
        bookChat?.setOnClickListener(View.OnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ChatList - Book New Chat")
            val intent = Intent(activity, BookAppointmentActivity::class.java)
            intent.putExtra("bookAppointment", 1)
            requireActivity().startActivity(intent)
        })
        pullToRefresh?.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                if (globalClass!!.isOnline) {
                    (chatListModelActiveList as ArrayList<ChatListModel>).clear()
                    (chatListModelPastList as ArrayList<ChatListModel>).clear()
                    if (chatListPastAdapter != null) {
                        chatListPastAdapter!!.notifyDataSetChanged()
                    }
                    if (chatListActiveAdapter != null) {
                        chatListActiveAdapter!!.notifyDataSetChanged()
                    }
                    activeChatList
                    pastChatList
                } else {
                    globalClass!!.noInternetConnection.showDialog(activity)
                }
                pullToRefresh!!.isRefreshing = false
            }
        })
        return chatView
    }// It's something else, like a string or number// It's an object

    // It's an array
    // model.setPhNo(patientObject.getString("phone"));
    private val activeChatList: Unit
        private get() {
            activeProgressBar!!.visibility = View.VISIBLE
            activeEmptyText!!.visibility = View.GONE
            bookChat!!.visibility = View.GONE
            activeEmptyLayout!!.visibility = View.VISIBLE
            val url =
                ApiUrls.getActiveAndPastChatList + "?search&sortby=start_time&sortorder=desc&unread=false&type=1"
            apiGetPostMethodCalls!!.volleyApiRequestData(
                url,
                Request.Method.GET,
                null,
                requireContext(),
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        try {
                            activeProgressBar!!.visibility = View.GONE
                            val resObj = JSONObject(result)
                            val resObject = resObj.getJSONObject("response")
                            val resArr = resObject.getJSONArray("participants")
                            totalActiveChat = resArr.length()
                            if (resArr.length() > 0) {
                                activeEmptyLayout!!.visibility = View.GONE
                                for (i in 0 until resArr.length()) {
                                    val temp = resArr.getJSONObject(i)
                                    val unreadCount = temp.getInt("unreadCnt")
                                    val sessionObject = temp.getJSONObject("chat_session")
                                    val chatUserObject = temp.getJSONObject("chat_user")
                                    val chatAppointmentObject =
                                        temp.getJSONObject("appointment_details")
                                    val chatAppointmentOrderObject =
                                        chatAppointmentObject.getJSONObject("order")
                                    val chatListModel = ChatListModel()
                                    chatListModel.readCount = unreadCount.toString()
                                    chatListModel.chatId = temp.getInt("chat_id")
                                    chatListModel.orderId = chatAppointmentObject.getInt("order_id")
                                    chatListModel.appointmentId = chatAppointmentObject.getInt("id")
                                    chatListModel.paymentStatus =
                                        chatAppointmentOrderObject.getString("payment_status")
                                    chatListModel.sendPaymentReminder =
                                        chatAppointmentOrderObject.getInt("send_payment_notification")
                                    val intervention = chatAppointmentOrderObject["receipt"]
                                    if (intervention is JSONArray) {
                                        // It's an array
                                        // model.setPhNo(patientObject.getString("phone"));
                                    } else if (intervention is JSONObject) {
                                        // It's an object
                                        val receiptObject =
                                            chatAppointmentOrderObject.getJSONObject("receipt")
                                        chatListModel.receiptUrl =
                                            receiptObject.getString("public_url")
                                    } else {
                                        chatListModel.receiptUrl = ""
                                        // It's something else, like a string or number
                                    }
                                    chatListModel.netAmount =
                                        chatAppointmentOrderObject.getInt("net_amount")
                                    chatListModel.orderAmount =
                                        chatAppointmentOrderObject.getInt("order_amt")
                                    chatListModel.discount =
                                        chatAppointmentOrderObject.getInt("discount")
                                    chatListModel.recipientId = temp.getInt("participant_id")
                                    chatListModel.patientName = chatUserObject.getString("fname")
                                    val date = appUtilities!!.changeDateFormat(
                                        "yyyy-MM-dd HH:mm:ss",
                                        dateAndTimeFormat,
                                        sessionObject.getString("expiry_time")
                                    )
                                    chatListModel.chatDateTime = date
                                    chatListModelActiveList!!.add(chatListModel)
                                    chatListActiveAdapter!!.notifyDataSetChanged()
                                }
                                toolbar!!.subtitle =
                                    "Total: " + (totalActiveChat + totalPastChat).toString()
                            } else {
                                activeEmptyLayout!!.visibility = View.VISIBLE
                                activeEmptyText!!.text = "You have no active chat"
                                activeEmptyText!!.visibility = View.VISIBLE
                                bookChat!!.visibility = View.VISIBLE
                            }
                        } catch (e: JSONException) {
                            e.message
                            activeProgressBar!!.visibility = View.GONE
                            activeEmptyText!!.text = "Error Occurred. Try again later"
                            activeEmptyText!!.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(err: String) {
                        err.lowercase(Locale.getDefault())
                        activeProgressBar!!.visibility = View.GONE
                        activeEmptyText!!.text = "Error Occurred. Try again later"
                        activeEmptyText!!.visibility = View.VISIBLE
                        errorHandler(requireContext(), err)
                    }
                })
        }// It's something else, like a string or number// It's an object

    // It's an array
    // model.setPhNo(patientObject.getString("phone"));
    private val pastChatList: Unit
        private get() {
            pastProgressBar!!.visibility = View.VISIBLE
            val url =
                ApiUrls.getActiveAndPastChatList + "?search&sortby=start_time&sortorder=desc&unread=false&type=2"
            apiGetPostMethodCalls!!.volleyApiRequestData(
                url,
                Request.Method.GET,
                null,
                requireContext(),
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        try {
                            pastProgressBar!!.visibility = View.GONE
                            val resObj = JSONObject(result)
                            val resObject = resObj.getJSONObject("response")
                            val resArr = resObject.getJSONArray("participants")
                            totalPastChat = resArr.length()
                            if (resArr.length() > 0) {
                                for (i in 0 until resArr.length()) {
                                    val temp = resArr.getJSONObject(i)
                                    val unreadCount = temp.getInt("unreadCnt")
                                    val sessionObject = temp.getJSONObject("chat_session")
                                    val chatUserObject = temp.getJSONObject("chat_user")
                                    val chatAppointmentObject =
                                        temp.getJSONObject("appointment_details")
                                    val chatAppointmentOrderObject =
                                        chatAppointmentObject.getJSONObject("order")
                                    val chatListModel = ChatListModel()
                                    chatListModel.readCount = unreadCount.toString()
                                    chatListModel.orderId = chatAppointmentObject.getInt("order_id")
                                    chatListModel.appointmentId = chatAppointmentObject.getInt("id")
                                    chatListModel.paymentStatus =
                                        chatAppointmentOrderObject.getString("payment_status")
                                    chatListModel.sendPaymentReminder =
                                        chatAppointmentOrderObject.getInt("send_payment_notification")
                                    val intervention = chatAppointmentOrderObject["receipt"]
                                    if (intervention is JSONArray) {
                                        // It's an array
                                        // model.setPhNo(patientObject.getString("phone"));
                                    } else if (intervention is JSONObject) {
                                        // It's an object
                                        val receiptObject =
                                            chatAppointmentOrderObject.getJSONObject("receipt")
                                        chatListModel.receiptUrl =
                                            receiptObject.getString("public_url")
                                    } else {
                                        chatListModel.receiptUrl = ""
                                        // It's something else, like a string or number
                                    }
                                    chatListModel.netAmount =
                                        chatAppointmentOrderObject.getInt("net_amount")
                                    chatListModel.orderAmount =
                                        chatAppointmentOrderObject.getInt("order_amt")
                                    chatListModel.discount =
                                        chatAppointmentOrderObject.getInt("discount")
                                    chatListModel.chatId = temp.getInt("chat_id")
                                    chatListModel.recipientId = temp.getInt("participant_id")
                                    chatListModel.patientName = chatUserObject.getString("fname")
                                    val date = appUtilities!!.changeDateFormat(
                                        "yyyy-MM-dd HH:mm:ss",
                                        dateAndTimeFormat,
                                        sessionObject.getString("expiry_time")
                                    )
                                    chatListModel.chatDateTime = date
                                    chatListModelPastList!!.add(chatListModel)
                                    chatListPastAdapter!!.notifyDataSetChanged()
                                }
                                toolbar!!.subtitle =
                                    "Total: " + (totalActiveChat + totalPastChat).toString()
                            } else {
                                pastEmptyText!!.visibility = View.VISIBLE
                                pastEmptyText!!.text = "you have no past chat"
                            }
                        } catch (e: JSONException) {
                            e.message
                            pastProgressBar!!.visibility = View.GONE
                            pastEmptyText!!.visibility = View.VISIBLE
                            pastEmptyText!!.text = "Error Occurred. Try again later."
                        }
                    }

                    override fun onError(err: String) {
                        err.lowercase(Locale.getDefault())
                        pastProgressBar!!.visibility = View.GONE
                        pastEmptyText!!.visibility = View.VISIBLE
                        pastEmptyText!!.text = "Error Occurred. Try again later."
                        errorHandler(requireContext(), err)
                    }
                })
        }

    companion object {
        @JvmField
        var chatTabFlag = 0
        @JvmStatic
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }

    override fun onResume() {
        super.onResume()

        dateAndTimeFormat = if (appUtilities!!.timeFormatPreferences(requireContext()) == 12) {
            "MMMM dd, yyyy hh:mm aa"
        } else {
            "MMMM dd, yyyy HH:mm"
        }
        if (globalClass!!.isOnline) {
            (chatListModelActiveList as ArrayList<ChatListModel>).clear()
            (chatListModelPastList as ArrayList<ChatListModel>).clear()
            if (chatListPastAdapter != null) {
                chatListPastAdapter!!.notifyDataSetChanged()
            }
            if (chatListActiveAdapter != null) {
                chatListActiveAdapter!!.notifyDataSetChanged()
            }
            activeChatList
            pastChatList
        } else {
            globalClass!!.noInternetConnection.showDialog(activity)
        }
    }
}