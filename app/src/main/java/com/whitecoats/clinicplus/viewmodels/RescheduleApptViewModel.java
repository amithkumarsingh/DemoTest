package com.whitecoats.clinicplus.viewmodels;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//import com.whitecoats.clinicplus.repositories.RescheduleAppointmentRespository;
//import com.whitecoats.clinicplus.utils.RescheduleAppointmentApiResponse;

import java.util.ArrayList;
import java.util.Map;

public class RescheduleApptViewModel extends ViewModel {

    public ObservableField<Boolean> isListVisible=new ObservableField<>();
    public ObservableField<Boolean> isEmptyMsgVisible=new ObservableField<>();
    public ObservableField<Boolean> isLoaderVisible=new ObservableField<>();

    public void setListVisibility(boolean visibility){
        isListVisible.set(visibility);
    }

    public void displayUIBasedOnCount(int count){
        if(count>0) {
            setListVisibility(true);
            setIsEmptyMsgVisibility(false);
        }else{
            setListVisibility(false);
            setIsEmptyMsgVisibility(true);
        }
        setIsLoaderVisible(false);
    }

    private void setIsLoaderVisible(boolean loaderVisible) {
        isLoaderVisible.set(loaderVisible);
    }

    private void setIsEmptyMsgVisibility(boolean visibility) {
        isEmptyMsgVisible.set(visibility);
    }

}
