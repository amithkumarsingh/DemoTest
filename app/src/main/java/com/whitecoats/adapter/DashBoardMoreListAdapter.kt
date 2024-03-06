package com.whitecoats.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request.Method
import com.brandkinesis.BKUserInfo
import com.brandkinesis.BrandKinesis
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.R.drawable
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.fragments.DashboardFullMode
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.fragments.AppointmentFragment.Companion.appointmentTabFlag
import com.whitecoats.fragments.AppointmentFragment.Companion.lastHeaderDate
import com.whitecoats.fragments.ChatFragment
import com.whitecoats.fragments.PatientFragment.Companion.patientTabFlag
import com.whitecoats.model.DashBoardMoreListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONObject

class DashBoardMoreListAdapter(
    private val dashBoardMoreModelList: List<DashBoardMoreListModel>,
    private val mContext: Context,
    activityMoreListener: ActivityMoreClickListener
) : RecyclerView.Adapter<DashBoardMoreListAdapter.MyViewHolder>() {
    private val activityMoreListener: ActivityMoreClickListener
    private val appDatabaseManager: AppDatabaseManager
    private val appPreference: SharedPreferences
    var jsonValue: JSONObject? = null
    private val apiGetPostMethodCalls: ApiGetPostMethodCalls
    private lateinit var dialog: Dialog
    private var commonViewModel: CommonViewModel

    init {
        this.activityMoreListener = activityMoreListener
        appDatabaseManager = AppDatabaseManager(mContext)
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        appPreference = mContext.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
        commonViewModel =
            ViewModelProvider((mContext as ViewModelStoreOwner))[CommonViewModel::class.java]
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_dashboard_more, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        myViewHolder: MyViewHolder,
        @SuppressLint("RecyclerView") i: Int
    ) {
        val dashBoardMoreDetailsListModel = dashBoardMoreModelList[i]
        myViewHolder.menuName.text = dashBoardMoreDetailsListModel.menuName
        /*Added the null, empty check condition to avoid the crash replace condition*/
        val icon: Int = if (dashBoardMoreDetailsListModel.pageName != null &&
            dashBoardMoreDetailsListModel.pageName.equals("Feeds", ignoreCase = true)
        ) {
            getId("ic_feeds", drawable::class.java)
        } else if (dashBoardMoreDetailsListModel.iconName != null &&
            !dashBoardMoreDetailsListModel.iconName.equals("", ignoreCase = true)
        ) {
            getId(dashBoardMoreDetailsListModel.iconName, drawable::class.java)
        } else {
            getId("ic_home", drawable::class.java)
        }
        myViewHolder.dashBoardMenuImage.setImageResource(icon)
        if (dashBoardMoreDetailsListModel.hiddenForDoctor == 1 && ApiUrls.isDoctorOnly == 1) {
            myViewHolder.dashBoardMenuImage.setColorFilter(
                ContextCompat.getColor(mContext, R.color.colorGrey1),
                PorterDuff.Mode.SRC_IN
            )
            myViewHolder.menuName.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey1))
        } else {
            myViewHolder.dashBoardMenuImage.setColorFilter(
                ContextCompat.getColor(mContext, R.color.colorBlack),
                PorterDuff.Mode.SRC_IN
            )
            myViewHolder.menuName.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack))
        }
        myViewHolder.dashBoardMoreLayout.setOnClickListener { view ->
            if (dashBoardMoreDetailsListModel.menuId == -1) {
                logoutApp()
                activityMoreListener.onItemClick(view, i, dashBoardMoreDetailsListModel.menuId, "")
            } else {
                activityMoreListener.onItemClick(view, i, dashBoardMoreDetailsListModel.menuId, "")
            }
        }
    }

    override fun getItemCount(): Int {
        return dashBoardMoreModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuName: TextView
        val dashBoardMenuImage: ImageView
        val dashBoardMoreLayout: RelativeLayout

        init {
            menuName = itemView.findViewById(R.id.dashboardMenuName)
            dashBoardMenuImage = itemView.findViewById(R.id.dashBoardMenuImage)
            dashBoardMoreLayout = itemView.findViewById(R.id.dashBoardMoreLayout)
        }
    }

    private fun logout() {
        val appUserManagers = appDatabaseManager.userData
        var userid = 0
        if (appUserManagers.size > 0) {
            userid = appUserManagers[0].userId
        }
        val playerId = appPreference.getString(ApiUrls.playerId, "")
        val url =
            ApiUrls.logout + "?user_id=" + userid + "&type=doctor&action=logout&platform=android&player_id=" + playerId
        Log.d("Url Url", url)
        Log.d("User Data", userid.toString() + "")
        Log.d("User Data", playerId + "")

        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            (mContext as LifecycleOwner)
        ) { result ->
            Log.d("Patient Logout Response", result)
            dialog.dismiss()
            try {
                val res = JSONObject(result)
                if (res.getInt("status_code") == 200) {
                    val response = res.optJSONObject("response")
                    if (response!!.getInt("response") == 1 || response.getInt("response") == 0) {
                        ApiUrls.loginToken = ""
                        ZohoSalesIQ.unregisterVisitor(mContext)
                        deleteResponse = appDatabaseManager.deletePatient(
                            appUserManagers[0]
                        )
                        Log.d("Delete response", deleteResponse.toString() + "")
                        if (deleteResponse == 1) {
                            ApiUrls.loginToken = ""
                            ApiUrls.doctorId = 0
                            patientTabFlag = 0
                            appointmentTabFlag = 0
                            ChatFragment.chatTabFlag = 0
                            ConfirmOrderActivity.confirmOrderFlagChat = 0
                            ConfirmOrderActivity.confirmOrderFlag = 0
                            ChatRoomActivity.chatClickFlag = 0
                            ApiUrls.bottomNaviType = 0
                            AppConstants.durationSelectedValue = 0
                            AppConstants.selectedClinicClickOnDashBoard = 0
                            AppConstants.selectedClinicIdOnDashBoard = 0
                            AppConstants.clinicCount = 0
                            DashboardFullMode.switchClickSelectedPosition = 0
                            DashboardFullMode.switchClinicSelectedString = ""
                            lastHeaderDate = ""
                            ApiUrls.activePastFilterFlag = ""
                            Toast.makeText(
                                mContext,
                                "You Have Been Successfully Logged Out",
                                Toast.LENGTH_SHORT
                            ).show()
                            val i = Intent(mContext, LoginActivity::class.java)
                            mContext.startActivity(i)
                            (mContext as Activity).finish()
                        } else {
                            Toast.makeText(
                                mContext,
                                "Error While Logging Out",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    dialog.dismiss()
                    errorHandler(mContext, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }

    private fun logoutApp() {
        val appUserManagers = appDatabaseManager.userData
        var userid = 0
        if (appUserManagers.size > 0) {
            userid = appUserManagers[0].userId
        }
        showCustomProgressAlertDialog(
            mContext.resources.getString(R.string.logging_out),
            mContext.resources.getString(R.string.please_wait_loading_message)
        )
        val playerId = appPreference.getString(ApiUrls.playerId, "")
        val url =
            ApiUrls.logoutApp + "?type=doctor&action=logout&platform=android&player_id=" + playerId
        Log.d("Url Url", url)
        Log.d("User Data", userid.toString() + "")
        Log.d("User Data", playerId + "")

        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            (mContext as LifecycleOwner)
        ) { result ->
            Log.d("Doctor Logout Response", result)
            try {
                val res = JSONObject(result)
                if (res.getInt("status_code") == 200) {
                    val response = res.optJSONObject("response")
                    if (response!!.getBoolean("response")) {
                        val userInfo = Bundle()
                        userInfo.putString(BKUserInfo.BKExternalIds.APPUID, "")
                        val bkInstance = BrandKinesis.getBKInstance()
                        bkInstance?.setUserInfoBundle(userInfo) { }
                        doctorSessionLoggedOut()
                    }
                } else {
                    dialog.dismiss()
                    errorHandler(mContext, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }

    private fun doctorSessionLoggedOut() {
        val url = ApiUrls.doctorSessionLogout
        val sharedPreferences =
            mContext.getSharedPreferences(AppConstants.appSharedPref, Context.MODE_PRIVATE)
        val sessionId = sharedPreferences.getString("DOCTOR_SESSION_ID", "")
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("platform", "android")
            jsonValue!!.put("session_id", sessionId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        commonViewModel.commonViewModelCall(url, jsonValue!!, Method.POST).observe(
            (mContext as LifecycleOwner)
        ) { result ->
            try {
                Log.d("session_logout", result)
                val res = JSONObject(result)
                if (res.getInt("status_code") == 200) {
                    val sharedPreference = mContext.getSharedPreferences(
                        AppConstants.appSharedPref,
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreference.edit()
                    editor.remove("DOCTOR_SESSION_ID")
                    editor.apply()
                    logout()
                } else {
                    errorHandler(mContext, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var deleteResponse = 0
        fun getId(resourceName: String?, c: Class<*>): Int {
            return try {
                val idField = c.getDeclaredField(resourceName!!)
                idField.getInt(idField)
            } catch (e: Exception) {
                0
            }
        }
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(mContext)
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}