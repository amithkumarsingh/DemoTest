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
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.whitecoats.clinicplus.PatientRecordsApi;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.VolleyCallback;
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.model.PatientRecordsModel;
//=======
//import com.caregivers.in.AppConfigClass;
//import com.caregivers.in.JSonArrayParser;
//import com.caregivers.in.PatientRecordsApi;
//import com.caregivers.in.R;
//import com.caregivers.in.VolleyCallback;
//import com.caregivers.model.PatientRecordsModel;
//>>>>>>> 9cd2f719774959ef939e2ca0ebf3be149f2ac7b1:app/src/main/java/com/caregivers/in/casechannels/CaseChannelRecordHandWrittenNotesFragment.java

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import static org.webrtc.ContextUtils.getApplicationContext;

public class CaseChannelRecordHandWrittenNotesFragment extends Fragment {

    //written notes
    private List<PatientRecordsModel> notesModel;
    private CaseChannelRecordHandWrittenNotesAdapter caseChannelRecordHandWrittenNotesAdapter;

    private int episodeID, patientID, doctorID;
    private String duration = "";
    private PatientRecordsApi apiCall;
    private JSONArray doctorArrayHW;

    RecyclerView caseChannelRecordHandWrittenRecyclerView;
    private RelativeLayout caseChannelRecordHandNotesProgressBar;
    private ApiGetPostMethodCalls globalApiCall;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_case_channel_record_hand_written_notes, container, false);

        apiCall = new PatientRecordsApi();
        notesModel = new ArrayList<>();
        globalApiCall = new ApiGetPostMethodCalls();
//        episodeID = 378;
//        patientID = 32386;
//        doctorID = 2529;
//        duration = "All";

        // Intent intent = getIntent();
        //  String jsonArray = getArguments().getString("caseChannelDoctorIdParticipantArray");
//        try {
//            doctorArray = ((JSonArrayParser) ((Bundle) getArguments()).getSerializable("events")).getObj();
//            Log.d("object","object"+doctorArray);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        episodeID = getArguments().getInt("caseChannelEpisodeId");
        patientID = getArguments().getInt("caseChannelPatientId");
        doctorID = getArguments().getInt("caseChannelDoctorId");
        duration = "All";
        doctorArrayHW = CaseChannelDashboardActivity.doctorArray.put(doctorID);


        caseChannelRecordHandNotesProgressBar = view.findViewById(R.id.caseChannelRecordHandNotesProgressBar);

        caseChannelRecordHandWrittenRecyclerView = view.findViewById(R.id.caseChannelRecordHandWrittenRecyclerView);

        caseChannelRecordHandWrittenRecyclerView = view.findViewById(R.id.caseChannelRecordHandWrittenRecyclerView);
        caseChannelRecordHandWrittenNotesAdapter = new CaseChannelRecordHandWrittenNotesAdapter(getActivity(), notesModel, new CaseDoctorOrganisationClickListener() {
            @Override
            public void onItemClick(View v, int position, String selectState, String sortByString) {

                if (sortByString != "") {
                    getFileFromUrl(sortByString);
                }
            }

            @Override
            public void getFilters(View v, int position, String selectState, String sortByString, int statusPos, int sortPos) {

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireContext());
        caseChannelRecordHandWrittenRecyclerView.setLayoutManager(mLayoutManager);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        caseChannelRecordHandWrittenRecyclerView.setLayoutManager(horizontalLayoutManager);
        caseChannelRecordHandWrittenRecyclerView.setItemAnimator(new DefaultItemAnimator());
        caseChannelRecordHandWrittenRecyclerView.setAdapter(caseChannelRecordHandWrittenNotesAdapter);

        getEpisWrittenNote();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
//        getActivity().setTitle("Menu 1");
    }

    //getCaseDiscussionEvaluation
    public void getEpisWrittenNote() {

        caseChannelRecordHandNotesProgressBar.setVisibility(View.VISIBLE);
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        final String URL = ApiUrls.getCaseDiscussionWritten_Notes;
        // Post params to be sent to the server

        JSONObject jsonObject = new JSONObject();
        try {

            //JSONArray doctorArray =new JSONArray();
            // doctorArray.put(doctorID);
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
                    caseChannelRecordHandNotesProgressBar.setVisibility(View.GONE);
                    JSONObject jsonObject1 = null;
                    jsonObject1 = response.getJSONObject("response");
                    JSONArray notesArr = response.getJSONArray("response");
                    if (notesArr.length() > 0) {
                        for (int i = 0; i < notesArr.length(); i++) {
                            JSONObject notesObj = notesArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setHnAttachURL(notesObj.getString("file_url"));
                            model.setHnDesp(notesObj.getString("description"));
                            model.setHnValidTill(notesObj.getString("med_prescription_valid_till"));
                            model.setHnMedPres("" + notesObj.getInt("has_med_prescription"));
                            model.setHnTestPres("" + notesObj.getInt("has_test_prescription"));
                            model.setCreatedUser(notesObj.getJSONObject("created_by_user").getString("fname"));
                            model.setCreated_At(notesObj.getString("created_at"));
                            notesModel.add(model);
                        }
                        //caseRecordEpisNoteCount.setText(notesArr.length() + "");
                        caseChannelRecordHandWrittenNotesAdapter.notifyDataSetChanged();
                    } else {
                        // caseRecordWriteNotesListLayout.setVisibility(View.GONE);
                        //caseRecordEpisNoteCount.setText(notesArr.length() + "");
//                        Toast.makeText(PatientEpisodeActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
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

        caseChannelRecordHandNotesProgressBar.setVisibility(View.VISIBLE);

        JSONObject url = new JSONObject();
        try {
            url.put("url", filrUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        apiCall.postRecords(ApiUrls.getFileFromUrl, url, getActivity(), new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                caseChannelRecordHandNotesProgressBar.setVisibility(View.GONE);
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

                caseChannelRecordHandNotesProgressBar.setVisibility(View.GONE);
                ErrorHandlerClass.INSTANCE.errorHandler(requireActivity(), err);

            }
        });
    }

}
