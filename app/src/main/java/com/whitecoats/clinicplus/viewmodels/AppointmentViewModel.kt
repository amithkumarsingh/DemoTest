package com.whitecoats.clinicplus.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.repositories.AppointmentRepository
import com.whitecoats.clinicplus.repositories.AppointmentRepository.Companion.instance
import org.json.JSONObject

class AppointmentViewModel : ViewModel() {
    private var appointmentRepository: AppointmentRepository? = null
    fun init() {
        appointmentRepository = instance
    }

    fun storeMedication(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        return appointmentRepository!!.storeMedication(activity, params)
    }

    fun getRecordStructure(activity: Activity?, recordId: String?): MutableLiveData<String> {
        return appointmentRepository!!.getRecordStructure(activity, recordId!!)
    }

    fun addMedicine(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        return appointmentRepository!!.addMedicine(activity, params)
    }

    fun searchMedicines(activity: Activity?, searchParam: String): MutableLiveData<String> {
        val url = ApiUrls.searchMedication + "?search=" + searchParam
        return appointmentRepository!!.searchMedicines(activity, url)
    }

    fun getServiceDetails(activity: Activity?): MutableLiveData<String> {
        return appointmentRepository!!.getServiceDetails(activity)
    }

    fun cancelAppt(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        return appointmentRepository!!.cancelAppt(activity, params)
    }

    fun setUpVideoLink(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        return appointmentRepository!!.setUpVideoLink(activity, params)
    }

    fun delayAppointment(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        return appointmentRepository!!.updateDelayIntemation(activity, params)
    }

    fun cancelApptRefund(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        return appointmentRepository!!.cancelAppointmentRefund(activity, params)
    }

    fun sendVideoLink(activity: Activity?, appointmentId: Int): MutableLiveData<String> {
        return appointmentRepository!!.sendVideoLinkDetails(activity, appointmentId)
    }

    fun appointmentRefundAmount(
        activity: Activity?,
        params: JSONObject?,
        orderId: Int,
        cancelReason: Int
    ): MutableLiveData<String> {
        return appointmentRepository!!.getAppointmentRefundAmount(
            activity,
            params,
            orderId,
            cancelReason
        )
    }

    fun getAppointmentDetails(
        activity: Activity?,
        duration: String,
        mode: String,
        search: String,
        sort: String,
        sortBy: String,
        pageNum: String,
        perPage: String,
        type: String,
        apptType: String,
        checkInStatus: String,
        payment: String,
        productId: String,
        date: String
    ): MutableLiveData<String> {
        val url: String = if (duration.equals("All", ignoreCase = true)) {
            ApiUrls.appointmentDetails + "?duration=" + duration + "&mode=" + mode + "&search=" + search + "&sort=" + sort + "&sortBy=" + sortBy +
                    "&page=" + pageNum + "&per_page=" + perPage + "&type=" + type + "&appt_type=" + apptType + "&checkinStatus=" + checkInStatus + "&payment=" + payment + "&product_ids=" + productId
        } else {
            ApiUrls.appointmentDetails + "?duration=" + duration + "&mode=" + mode + "&search=" + search + "&sort=" + sort + "&sortBy=" + sortBy +
                    "&page=" + pageNum + "&per_page=" + perPage + "&type=" + type + "&appt_type=" + apptType + "&checkinStatus=" + checkInStatus + "&payment=" + payment + "&product_ids=" + productId + "&fromDate=" + date + "&toDate=" + date
        }
        Log.i("url", url)
        return appointmentRepository!!.getAppointmentDetails(activity, url)
    }

    fun apptStatus(activity: Activity?, apptID: Int) {
        appointmentRepository!!.getApptStatus(activity, apptID)
    }

    val apptStatus: LiveData<Int?>
        get() = appointmentRepository!!.apptStatus()

    fun clearStatus() {
        appointmentRepository!!.clearStatus()
    }
}