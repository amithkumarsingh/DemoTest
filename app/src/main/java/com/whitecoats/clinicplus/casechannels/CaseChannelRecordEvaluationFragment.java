package com.whitecoats.clinicplus.casechannels;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.whitecoats.clinicplus.PatientRecordsApi;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.VolleyCallback;
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CaseChannelRecordEvaluationFragment extends Fragment {
    //Symptoms
    RelativeLayout caseChannelRecordEvalSymptomLayout;
    TextView caseChannelRecordEvalSymptomCount;

    //Observation
    RelativeLayout caseChannelRecordEvalObsLayout;
    TextView caseChannelRecordEvalObsCount;

    //Investigation
    RelativeLayout caseChannelRecordEvalInvestLayout;
    TextView caseChannelRecordEvalInvestCount;

    //Diagnosis
    RelativeLayout caseChannelRecordEvalDiagnosLayout;
    TextView caseChannelRecordEvalDiagnosCount;

    private int episodeID, patientID, doctorID;
    private String duration = "";
    private PatientRecordsApi apiCall;


    private JSONArray doctorArrayHW;
    private ApiGetPostMethodCalls globalApiCall;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_case_channel_record_evaluation, container, false);

        apiCall = new PatientRecordsApi();
        globalApiCall = new ApiGetPostMethodCalls();
//        episodeID = 378;
//        patientID = 32386;
//        doctorID = 2529;
//        duration = "All";


        episodeID = getArguments().getInt("caseChannelEpisodeId");
        patientID = getArguments().getInt("caseChannelPatientId");
        doctorID = getArguments().getInt("caseChannelDoctorId");
        duration = "All";
        doctorArrayHW = CaseChannelDashboardActivity.doctorArray.put(doctorID);


        //Symptoms
        caseChannelRecordEvalSymptomLayout = view.findViewById(R.id.caseChannelRecordEvalSymptomLayout);
        caseChannelRecordEvalSymptomCount = view.findViewById(R.id.caseChannelRecordEvalSymptomCount);

        //Observation
        caseChannelRecordEvalObsLayout = view.findViewById(R.id.caseChannelRecordEvalObsLayout);
        caseChannelRecordEvalObsCount = view.findViewById(R.id.caseChannelRecordEvalObsCount);

        //Investigation
        caseChannelRecordEvalInvestLayout = view.findViewById(R.id.caseChannelRecordEvalInvestLayout);
        caseChannelRecordEvalInvestCount = view.findViewById(R.id.caseChannelRecordEvalInvestCount);

        //Diagnosis
        caseChannelRecordEvalDiagnosLayout = view.findViewById(R.id.caseChannelRecordEvalDiagnosLayout);
        caseChannelRecordEvalDiagnosCount = view.findViewById(R.id.caseChannelRecordEvalDiagnosCount);

        //Symptoms
        caseChannelRecordEvalSymptomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!caseChannelRecordEvalSymptomCount.getText().toString().equalsIgnoreCase("0")) {

                    Intent symptomsIntent = new Intent(getActivity(), CaseChannelRecordEvaluationDetailsActivity.class);
                    symptomsIntent.putExtra("type", 1);
                    symptomsIntent.putExtra("caseChannelPatientId", patientID);
                    symptomsIntent.putExtra("caseChannelEpisodeId", episodeID);
                    symptomsIntent.putExtra("caseChannelDoctorId", doctorID);
                    startActivity(symptomsIntent);

                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Observation
        caseChannelRecordEvalObsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!caseChannelRecordEvalObsCount.getText().toString().equalsIgnoreCase("0")) {

                    Intent symptomsIntent = new Intent(getActivity(), CaseChannelRecordEvaluationDetailsActivity.class);
                    symptomsIntent.putExtra("type", 2);
                    symptomsIntent.putExtra("caseChannelPatientId", patientID);
                    symptomsIntent.putExtra("caseChannelEpisodeId", episodeID);
                    symptomsIntent.putExtra("caseChannelDoctorId", doctorID);
                    startActivity(symptomsIntent);

                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Investigation
        caseChannelRecordEvalInvestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!caseChannelRecordEvalInvestCount.getText().toString().equalsIgnoreCase("0")) {

                    Intent symptomsIntent = new Intent(getActivity(), CaseChannelRecordEvaluationDetailsActivity.class);
                    symptomsIntent.putExtra("type", 3);
                    symptomsIntent.putExtra("caseChannelPatientId", patientID);
                    symptomsIntent.putExtra("caseChannelEpisodeId", episodeID);
                    symptomsIntent.putExtra("caseChannelDoctorId", doctorID);
                    startActivity(symptomsIntent);

                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Diagnosis
        caseChannelRecordEvalDiagnosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!caseChannelRecordEvalDiagnosCount.getText().toString().equalsIgnoreCase("0")) {
                    Intent symptomsIntent = new Intent(getActivity(), CaseChannelRecordEvaluationDetailsActivity.class);
                    symptomsIntent.putExtra("type", 4);
                    symptomsIntent.putExtra("caseChannelPatientId", patientID);
                    symptomsIntent.putExtra("caseChannelEpisodeId", episodeID);
                    symptomsIntent.putExtra("caseChannelDoctorId", doctorID);
                    startActivity(symptomsIntent);

                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        getCaseDiscussionEvaluation();

        return view;
    }

    public void getCaseDiscussionEvaluation() {


//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        final String URL = ApiUrls.getCaseDiscussionEvaluation;
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

        globalApiCall.volleyApiRequestData(URL, Request.Method.POST, jsonObject, requireActivity(), new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    //getting symptoms data
                    try {

                        JSONObject symptomObj = response.getJSONObject("response").getJSONObject("symptoms");
                        caseChannelRecordEvalSymptomCount.setText(symptomObj.getString("symptoms_count"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //getting investigation data
                    try {
                        //JSONObject resObj = new JSONObject(response);
                        JSONObject symptomObj = response.getJSONObject("response").getJSONObject("investigation_results");
                        caseChannelRecordEvalInvestCount.setText(symptomObj.getString("investigation_results_count"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //getting eval diagnosis
                    try {

                        JSONObject symptomObj = response.getJSONObject("response").getJSONObject("diagnosis");
                        caseChannelRecordEvalDiagnosCount.setText(symptomObj.getString("diagnosis_count"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //getting observation
                    try {
                        // JSONObject resObj = new JSONObject(result);
                        JSONObject symptomObj = response.getJSONObject("response").getJSONObject("observations");
                        caseChannelRecordEvalObsCount.setText(symptomObj.getString("observations_count"));
                    } catch (Exception e) {
                        e.printStackTrace();
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
}
