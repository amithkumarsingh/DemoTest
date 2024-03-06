package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import com.whitecoats.clinicplus.repositories.DashboardRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;

public class DashboardViewModel extends ViewModel {
    private LiveData<String> statusString = new MutableLiveData<>();
    private DashboardRepository dashboardRepository;

    public void init() {
        dashboardRepository = DashboardRepository.getInstance();
        //statusString = dashboardRepository.getStatus();
    }

    /*public void getDashboardStatus(Activity activity) {
        dashboardRepository.getDashboardStatus(activity);
    }*/

    public LiveData<String> getStatusString(Activity activity) {
        return dashboardRepository.getDashboardStatus(activity);
    }
    public LiveData<String> getQuickActionDetails(Activity activity){
        return dashboardRepository.getQuickAction(activity);
    }
    public LiveData<String> getSharedAndFollowUpDetails(Activity activity){
        return dashboardRepository.getSharedAndFollowUp(activity);
    }
    public LiveData<String> getDoctorDetails(Activity activity,int doctorId){
        return dashboardRepository.getDoctorDetail(activity,doctorId);
    }
    public LiveData<String> getDashboardQuickLink(Activity activity){
        return dashboardRepository.getDashboardQuickLink(activity);
    }
    public LiveData<String> getDashboardDoctorDetails(Activity activity){
        return dashboardRepository.getDashboardDoctorDetails(activity);
    }
    public LiveData<String> getImageData(Activity activity,String url){
        return dashboardRepository.getImageData(activity,url);
    }
    public LiveData<String> getFileFromUrl(Activity activity,String fileUrl){
        return dashboardRepository.getFileUrl(activity,fileUrl);
    }
    public LiveData<String> getDashBoardAppointmentDetails(Activity activity,String duration){
        return dashboardRepository.getDashBoardAppointment(activity,duration);
    }
    public LiveData<String> sendMessageDetails(Activity activity,String message,int interfaceId,boolean hasInterFace){
        return dashboardRepository.sendMessage(activity,message,interfaceId,hasInterFace);
    }
    public LiveData<String> sendDelayDetails(Activity activity, JSONArray pendingIdArray, String delayIn, String delayTime){
        return dashboardRepository.sendDelay(activity,pendingIdArray,delayIn,delayTime);
    }
    public LiveData<String> bulkCancelDetails(Activity activity,String duration, String toDate, String fromDate, JSONArray pendingIdArray, int reason,int product_id,String clinicName){
        return dashboardRepository.bulkCancel(activity,duration, toDate, fromDate, pendingIdArray, reason,product_id,clinicName);
    }
    public LiveData<String> bulkCompleteDetails(Activity activity,String duration, String toDate, String fromDate, JSONArray pendingIdArray,boolean isFollowUp,boolean isInVoice,int product_id){
        return dashboardRepository.bulkComplete(activity,duration, toDate, fromDate, pendingIdArray,isFollowUp,isInVoice,product_id);
    }
    public LiveData<String> getDefaultFollowUpSettingDetails(Activity activity){
        return dashboardRepository.getDefaultFollowUpSetting(activity);
    }

    public LiveData<String> getBookedTrainingDetails(Activity activity,final String perPage, String sortBy, String search, final String pageNum,
                                                     final String status, String shortOrder){
        return dashboardRepository.getBookedTraining(activity,"50", "", "", "1", "", "");
    }

    public LiveData<String> getUpComingTrainingDetails(Activity activity,final String perPage, String sortBy, String search, final String pageNum,
                                                     final String status, String shortOrder){
        return dashboardRepository.geUpcomingTraining(activity,"50", "", "", "1", "", "");
    }

    public LiveData<String> cancelBookedTrainingDetails(Activity activity,int id, int trainingScheduleId){
        return dashboardRepository.cancelBookedTraining(activity,id, trainingScheduleId);
    }

    public LiveData<String> sendPaymentReminderDetails(Activity activity,int appointment_id){
        return dashboardRepository.sendPaymentReminder(activity,appointment_id);
    }

    public LiveData<String> getDashboardRibbonData(Activity activity){
        return dashboardRepository.getDashboardData(activity);
    }


}
