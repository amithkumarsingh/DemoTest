package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.repositories.AppointmentDetailsRepository;
import com.whitecoats.clinicplus.repositories.EMRSharedRecordRepository;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Mohammad suhail ahmed on 24-07-2020.
 */
public class AppointmentDetailsViewModel extends ViewModel {
    private AppointmentDetailsRepository appointmentDetailsRepository;

    public void init(){
        appointmentDetailsRepository = AppointmentDetailsRepository.getInstance();
    }
    public MutableLiveData<String> getCheckInStatus(Activity activity,String startTime){
        return appointmentDetailsRepository.getCheckInStatus(activity,startTime);
    }
    public MutableLiveData<String> getProductTaxes(Activity activity,int product_id){
        return appointmentDetailsRepository.getProductTax(activity,product_id);
    }
    public MutableLiveData<String> saveWrittenNotes(Activity activity,int createdBy,int encounterId,int episodeId,String fileUrl,int patientId){
        return appointmentDetailsRepository.saveWrittenNotes(activity,createdBy,encounterId,episodeId,fileUrl,patientId);
    }
    public MutableLiveData<String> updateMedicines(Activity activity,JSONObject params){
        return appointmentDetailsRepository.updateMedicine(activity,params);
    }
    public MutableLiveData<String> updateProcedure(Activity activity, JSONObject params){
        return appointmentDetailsRepository.addProcedure(activity,params);
    }
    public MutableLiveData<String> creteInvoice(Activity activity, JSONObject params){
        return appointmentDetailsRepository.createInvoice(activity,params);
    }
    public MutableLiveData<String> sendFollowUp(Activity activity, JSONObject params){
        return appointmentDetailsRepository.sendFollowUp(activity,params);
    }
    public MutableLiveData<String> getProcedure(Activity activity,String search,String productId,String sortOrder,String sortBy,int perPage,int page){
        return appointmentDetailsRepository.getProcedures(activity,search,productId,sortOrder,sortBy,perPage,page);
    }
    public MutableLiveData<String> getCaptureNotesRecordsList(Activity activity, int appointmentId){
        return appointmentDetailsRepository.getCapturedNotesListRecords(activity,appointmentId);
    }

    public MutableLiveData<String> getServicesForAppointmentData(Activity activity, int appointmentID){
        return appointmentDetailsRepository.getAppointmentServices(activity,appointmentID);
    }
    public MutableLiveData<String> getOrderDetailsData(Activity activity, int orderID){
        return appointmentDetailsRepository.getOrderDetails(activity,orderID);
    }
    public MutableLiveData<String> getInvoiceData(Activity activity, int orderID,JSONArray changeArray){
        return appointmentDetailsRepository.getInvoiceDetails(activity,orderID,changeArray);
    }

    public MutableLiveData<String> getPaymentTimeline(Activity activity, int appointmentId){
        return appointmentDetailsRepository.getPaymentTimeline(activity,appointmentId);
    }


    public MutableLiveData<String> saveNewAppointmentNote(Activity activity, int encounterId,int episodeId,int patientId,String appointmentNotes){
       return appointmentDetailsRepository.saveNewCaptureAppointmentNote(activity,encounterId,episodeId,patientId,appointmentNotes);
    }

    public MutableLiveData<String> updateAppointmentNote(Activity activity, int appointmentNoteId,String appointmentNotes){
        return appointmentDetailsRepository.updateCaptureAppointmentNote(activity,appointmentNoteId,appointmentNotes);
    }

    public MutableLiveData<String> deleteAppointmentNote(Activity activity, JSONArray appointmentNoteArrayId){
        return appointmentDetailsRepository.deleteCaptureAppointmentNote(activity,appointmentNoteArrayId);
    }

    public MutableLiveData<String> saveCheckedInStatus(Activity activity, int appointmentId,int statusId){
        return appointmentDetailsRepository.saveCheckInStatus(activity,appointmentId,statusId);
    }
}
