package com.whitecoats.clinicplus.casechannels;

//import static org.otwebrtc.ContextUtils.getApplicationContext;

//import static com.vonage.webrtc.ContextUtils.getApplicationContext;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//<<<<<<< HEAD:app/src/main/java/com/whitecoats/clinicplus/casechannels/TreatmentPlanFragment.java
import com.android.volley.Request;
import com.whitecoats.clinicplus.PatientRecordsApi;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.VolleyCallback;
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.model.PatientRecordsModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import static org.webrtc.ContextUtils.getApplicationContext;

public class TreatmentPlanFragment extends Fragment {

    RecyclerView caseChannelRecordTreatmentPlaneRecyclerView;
    private CaseChannelEvaluationAdapter treatmentAdapter;
    private List<PatientRecordsModel> tratmentModel;
//    private List<PatientRecordsModel> obsCatModel;
//    private CaseChannelEvaluationAdapter observationAdapter;


    private int episodeID, patientID, doctorID;
    private String duration = "";
    private PatientRecordsApi apiCall;
    private int typeID;
    private JSONArray doctorArrayHW;
    private ApiGetPostMethodCalls globalApiCall;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_case_channel_record_treatmentplan, container, false);
        tratmentModel = new ArrayList<>();
//        obsCatModel = new ArrayList<>();
        globalApiCall = new ApiGetPostMethodCalls();


        episodeID = getArguments().getInt("caseChannelEpisodeId");
        patientID = getArguments().getInt("caseChannelPatientId");
        doctorID = getArguments().getInt("caseChannelDoctorId");
        duration = "All";
        doctorArrayHW = CaseChannelDashboardActivity.doctorArray.put(doctorID);


        caseChannelRecordTreatmentPlaneRecyclerView = view.findViewById(R.id.caseChannelRecordTreatmentPlaneRecyclerView);
        treatmentAdapter = new CaseChannelEvaluationAdapter(getActivity(), tratmentModel, new CaseDoctorOrganisationClickListener() {
            @Override
            public void onItemClick(View v, int position, String selectState, String sortByString) {

                if (sortByString != "") {
                    //getFileFromUrl(sortByString);
                }
            }

            @Override
            public void getFilters(View v, int position, String selectState, String sortByString, int statusPos, int sortPos) {

            }
        });

        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(requireContext());
        caseChannelRecordTreatmentPlaneRecyclerView.setLayoutManager(mLayoutManager3);
        LinearLayoutManager horizontalLayoutManager3
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        caseChannelRecordTreatmentPlaneRecyclerView.setLayoutManager(horizontalLayoutManager3);
        caseChannelRecordTreatmentPlaneRecyclerView.setItemAnimator(new DefaultItemAnimator());
        caseChannelRecordTreatmentPlaneRecyclerView.setAdapter(treatmentAdapter);
        getCaseDiscussionObservations();
        return view;
    }


    public void getCaseDiscussionObservations() {


        final String URL = ApiUrls.getCaseDiscussionTreatmentplan;
        // Post params to be sent to the server

        JSONObject jsonObject = new JSONObject();
        try {

//            JSONArray doctorArray =new JSONArray();
//            doctorArray.put(doctorID);
            jsonObject.put("patient_id", patientID);
            jsonObject.put("episode_id", episodeID);
            jsonObject.put("duration", duration);
            jsonObject.put("doctor_id", doctorArrayHW);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        globalApiCall.volleyApiRequestData(URL, Request.Method.POST, jsonObject, requireContext(), new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONObject jsonObject1 = null;

                        //jsonObject1 = response.getJSONObject("response");
                        JSONObject observationObj = response.getJSONObject("response");
                        JSONArray recordsArr = observationObj.getJSONArray("records");
                        if (recordsArr.length() > 0) {
                            for (int i = 0; i < recordsArr.length(); i++) {
                                JSONObject catObj = recordsArr.getJSONObject(i);
                                PatientRecordsModel model = new PatientRecordsModel();
                                model.setObsCatName(catObj.getJSONObject("category").getString("category"));
//                                    model.setObsCount(catObj.getJSONArray("records").length());
                                model.setRecordId(catObj.getJSONObject("category").getInt("id"));
                                model.setCreatedUser(catObj.getString("doctor"));
                                model.setCreated_At(catObj.getString("created_at"));
                                model.setType(5);

                                JSONObject fieldDic = observationObj.getJSONObject("field-dictionary");
                                if (fieldDic.get(catObj.getJSONObject("category").getInt("id") + "") instanceof JSONArray) {

                                    JSONArray fieldArr = fieldDic.getJSONArray(catObj.getJSONObject("category").getInt("id") + "");

                                    model.setPrimaryKey(fieldArr.getJSONObject(0).getString("name"));
                                    if (fieldArr.length() > 1) {
                                        model.setSecKey(fieldArr.getJSONObject(1).getString("name"));
                                        if (catObj.has(fieldArr.getJSONObject(1).getString("key"))) {
                                            model.setSecData(catObj.getString(fieldArr.getJSONObject(1).getString("key")));
                                        } else {
                                            model.setSecData("");
                                        }
                                    }

                                    if (catObj.getJSONObject("category").getInt("id") == 33) {

                                        model.setTernaryKey("Date");
                                        // model.setTernaryData(fileDate);

                                    } else if (fieldArr.length() > 2) {
                                        model.setTernaryKey(fieldArr.getJSONObject(2).getString("name"));

                                        if (catObj.has(fieldArr.getJSONObject(2).getString("key"))) {
                                            model.setTernaryData(catObj.getString(fieldArr.getJSONObject(2).getString("key")));
                                        } else {
                                            model.setTernaryData("");
                                        }

//                                    if (recordOBj.has(recordsArr.getJSONObject(2).getString("key"))) {
//                                        String ternaryData = recordOBj.getString(recordsArr.getJSONObject(2).getString("key"));
//                                        model.setTernaryData(ternaryData);
//                                    } else {
//                                        model.setTernaryData("");
//                                    }

                                    }

                                    if (catObj.has(fieldArr.getJSONObject(0).getString("key")))
                                        model.setPrimaryData(catObj.getString(fieldArr.getJSONObject(0).getString("key")));

                                    if (catObj.has("url")) {
                                        model.setFileUrl(catObj.getString("url"));
                                    }
                                }
                                model.setFieldDictionary(fieldDic.toString());
                                model.setCatRecordData(recordsArr.toString());
                                model.setCatId(catObj.getJSONObject("category").getInt("id") + "");
                                tratmentModel.add(model);
                            }
                            treatmentAdapter.notifyDataSetChanged();
                        }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), err);
            }
        });

    }

    public void getFileFromUrl(String filrUrl) {

        JSONObject url = new JSONObject();
        try {
            url.put("url", filrUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        apiCall.postRecords(ApiUrls.getFileFromUrl, url, getActivity(), new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject resObj = new JSONObject(result);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resObj.getString("response")));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(requireActivity(), err);
            }
        });
    }


}
