package com.whitecoats.clinicplus.repositories;

import android.app.Activity;
import android.app.ProgressDialog;

import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;
import com.whitecoats.clinicplus.models.DashboardMenuModel;
import com.whitecoats.clinicplus.utils.AppUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;

public class MainActivityRepository {

    private static MainActivityRepository instance;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    MutableLiveData<ArrayList<DashboardMenuModel>> dashboardMenu = new MutableLiveData<>();
    private ProgressDialog loadingDialog;


    public static MainActivityRepository getInstance() {
        if (instance == null) {
            instance = new MainActivityRepository();
        }

        return instance;
    }

    public MutableLiveData<ArrayList<DashboardMenuModel>> getDashboardMenu(Activity activity) {

        try {
            apiMethodCalls.getApiData(ApiUrls.getDashBoardDetails + "?mode=" + 0, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    JSONArray responseArr = new JSONArray();
                    ArrayList<DashboardMenuModel> tempArr = new ArrayList<>();
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        responseArr = responseObj.getJSONObject("response").getJSONObject("response").getJSONArray("menu");

                        if(responseArr != null) {

                            //adding the home by default
                            DashboardMenuModel menuModel = new DashboardMenuModel();
                            menuModel.setIsHidden(0);
                            menuModel.setPageName("Home");
                            menuModel.setPageIcon(AppUtilities.getId("ic_home", R.drawable.class));
                            menuModel.setPageId(0);
                            menuModel.setUrl("home");
                            tempArr.add(menuModel);

                            for(int i=0; i<responseArr.length(); i++) {
                                menuModel = new DashboardMenuModel();
                                menuModel.setIsHidden(responseArr.getJSONObject(i).getInt("is_hidden_for_doctor_only"));
                                menuModel.setPageName(responseArr.getJSONObject(i).getString("page_name"));
                                menuModel.setPageIcon(AppUtilities.getId(responseArr.getJSONObject(i).getString("icon_native"), R.drawable.class));
                                menuModel.setPageId(responseArr.getJSONObject(i).getInt("id"));
                                menuModel.setUrl(responseArr.getJSONObject(i).getString("url"));
                                tempArr.add(menuModel);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dashboardMenu.postValue(tempArr);
                }

                @Override
                public void onErrorResponse(String error) {
//                    MutableLiveData<ArrayList<DashboardMenuModel>> errorArr = new MutableLiveData<>();
//                    try {
//                        errorArr = new JSONArray(error);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    dashboardMenu.postValue(errorArr);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dashboardMenu;
    }

    public MutableLiveData<String> getTimePreferenceData(Activity activity){
        MutableLiveData<String> timeFormatPrefResponse = new MutableLiveData<>();
        try
        {
            apiMethodCalls.getApiData(ApiUrls.getTimePreferencesData, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    timeFormatPrefResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    timeFormatPrefResponse.postValue(error);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return timeFormatPrefResponse;
    }

}
