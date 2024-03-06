package com.whitecoats.clinicplus.repositories;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;

/**
 * Created by Mohammad suhail ahmed on 09-07-2020.
 */
public class SearchPatientRepository {
    private static SearchPatientRepository searchPatientRepository;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();

    public static SearchPatientRepository getInstance() {
        if(searchPatientRepository == null) {
            searchPatientRepository = new SearchPatientRepository();
        }
        return searchPatientRepository;
    }
    public MutableLiveData<String> searchPatient(Activity activity,String searchText){
        MutableLiveData<String> searchResponse = new MutableLiveData<>();
        String url = ApiUrls.searchPatient+"?mode=list&search="+searchText;
        apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
            @Override
            public void onSuccessResponse(String response) {
                searchResponse.postValue(response);
            }

            @Override
            public void onErrorResponse(String error) {
                searchResponse.postValue(error);
            }
        });
        return  searchResponse;
    }
}
