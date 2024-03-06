package com.whitecoats.clinicplus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.whitecoats.adapter.CommunicationLanguageListAdapter;
import com.whitecoats.clinicplus.activities.VoiceEMRActivity;
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.model.AddPatientModel;
import com.whitecoats.model.CommunicationMessageListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhav on 07-02-2018.
 */

public class VoiceEmrHelpBottomSheet extends BottomSheetDialogFragment {
    private VoiceEMRActivity voiceEMRActivity;
    private RecyclerView patientListRView;
    private List<CommunicationMessageListModel> commLanguageModelList;
    private CommunicationLanguageListAdapter commLanguageListAdapter;
    private RelativeLayout cancel, done, searchLayout, addPatientLayout;
    private ImageView cancelIcon;
    private Context mContext;
    private FloatingActionButton fab;
    private FloatingActionButton addIcon;
    private EditText bottomSheetMessageText;
    private RelativeLayout bottomSheetToggleLayout;
    private Switch switchButton;
    private RelativeLayout bottomSheetDone;
    private Button bottomSheetSendMessage;
    private int intValue;
    private JSONObject jsonValue;
    private ProgressDialog otpLoading;
    private Spinner interFaceSpinner;
    private RelativeLayout selectInterface;
    public List<AddPatientModel> addInterfaceModelList;
    private int interFaceId = 0;
    private RelativeLayout bottomSheetMessageNote;
    private boolean hasInterface;
    Button micTestingButton;
    ProgressBar volumeProgressBar, volumeProgressBarWhite;
    private boolean stopTestingClick = false;
    private ApiGetPostMethodCalls globalApiCall;

    public VoiceEmrHelpBottomSheet() {
    }

    public void setupConfig(VoiceEMRActivity voiceEMRActivity) {
        this.voiceEMRActivity = voiceEMRActivity;
        addInterfaceModelList = new ArrayList<>();
        globalApiCall = new ApiGetPostMethodCalls();
    }

    //Bottom Sheet Callback
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(final Dialog dialog, int style) {
//        super.setupDialog(dialog, style);
        //Get the content View
        final View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_voice_emr_help, null);
        dialog.setContentView(contentView);
        //Set the coordinator layout behavior
//        getInterfaceName();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon);
        micTestingButton = contentView.findViewById(R.id.micTestingButton);
        volumeProgressBar = contentView.findViewById(R.id.volumeProgressBar);
        volumeProgressBarWhite = contentView.findViewById(R.id.volumeProgressBarWhite);
        //Set callback
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        cancelIcon.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        cancelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        micTestingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (VoiceEMRActivity.pauseAndResumeStatus.equalsIgnoreCase("resume")) {
                    if (micTestingButton.getText().toString().equalsIgnoreCase("START TESTING MIC")) {

                        checkValueLevel();

//                    micTestingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDanger)));
//                    micTestingButton.setText("STOP TESTING");
//
//                    AudioManager audioManager = (AudioManager) voiceEMRActivity.getSystemService(Context.AUDIO_SERVICE);
//                    int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                    int maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//                    int volumePercent = (int) (((float) volumeLevel / maxVolumeLevel) * 100);
//                    volumeProgressBar.setMax(maxVolumeLevel);
//                    volumeProgressBar.setProgress(volumeLevel);
                    } else {
                        micTestingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        micTestingButton.setText("START TESTING MIC");

                        stopTestingClick = true;

                        volumeProgressBar.setVisibility(View.GONE);
                        volumeProgressBarWhite.setVisibility(View.VISIBLE);

//                    AudioManager audioManager = (AudioManager) voiceEMRActivity.getSystemService(Context.AUDIO_SERVICE);
//                    int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                    int maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//                    int volumePercent = (int) (((float) volumeLevel / maxVolumeLevel) * 100);
//                    volumeProgressBar.setMax(maxVolumeLevel);
//                    volumeProgressBar.setProgress(0);
                    }
                } else {
                    Toast.makeText(getActivity(), "Please start dictation", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void sendMessage(Dialog dialog) {

        otpLoading = new ProgressDialog(getActivity());
        otpLoading.setMessage(getResources().getString(R.string.sending_message));
        otpLoading.setTitle(getResources().getString(R.string.sending_message));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();

        final String URL = ApiUrls.createMessage;

        try {

            jsonValue = new JSONObject();
            jsonValue.put("message", bottomSheetMessageText.getText().toString());
            if (interFaceId == 0) {
                jsonValue.put("interfaceId", "");

            } else {
                jsonValue.put("interfaceId", interFaceId);
            }

            jsonValue.put("hasInterface", hasInterface);


        } catch (Exception e) {
            e.printStackTrace();
        }

        globalApiCall.volleyApiRequestData(URL, Request.Method.POST, jsonValue, requireContext(), new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
//                            Log.d("sendMessageResponse", response.toString());
                    otpLoading.dismiss();
                    JSONObject rootObj = response.optJSONObject("response");

                    boolean createMessageResponse = response.getBoolean("response");

                    if (createMessageResponse) {
                        int messageCount = Integer.parseInt(CommunicationActivity.messageCount.getText().toString());
                        CommunicationActivity.messageCount.setText(String.valueOf(messageCount + 1));
                        dialog.dismiss();
                        Toast.makeText(voiceEMRActivity, "Message sent successfully.", Toast.LENGTH_LONG).show();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    otpLoading.dismiss();
                }
            }

            @Override
            public void onError(String err) {
                otpLoading.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), err);
            }
        });


    }


    public void getInterfaceName() {
        final String url = ApiUrls.getDoctorInterFace;
        // prepare the Request
        globalApiCall.volleyApiRequestData(url, Request.Method.GET, null, requireContext(),
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            JSONArray rootObj = response.getJSONArray("response");
                            for (int j = 0; j < rootObj.length(); j++) {
                                JSONObject appointmentJsonObject = rootObj.getJSONObject(j);
                                if (!appointmentJsonObject.isNull("parentinterf")) {
                                    JSONObject parentInterfaceObject = appointmentJsonObject.getJSONObject("parentinterf");
                                    AddPatientModel model = new AddPatientModel();
                                    model.setInterfaceId(parentInterfaceObject.getInt("id"));
                                    model.setInterfaceName(parentInterfaceObject.getString("interface_name"));
                                    addInterfaceModelList.add(model);
                                }

                            }

                            ArrayAdapter<AddPatientModel> adapter =
                                    new ArrayAdapter<AddPatientModel>(getContext(), android.R.layout.simple_spinner_dropdown_item, addInterfaceModelList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            interFaceSpinner.setAdapter(adapter);

                            // Set the ClickListener for Spinner
                            interFaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                public void onItemSelected(AdapterView<?> adapterView,
                                                           View view, int i, long l) {
                                    // TODO Auto-generated method stub
                                    interFaceId = addInterfaceModelList.get(i).getInterfaceId();

                                }

                                // If no option selected
                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub

                                }

                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String err) {
                        ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), err);
                    }
                });
    }

    // check valuem level
    public void checkValueLevel() {
        VoiceEMRActivity.viewModel.getAudioLevel().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer s) {
                Log.d("current valume level", String.valueOf(s));
                try {

                    if (stopTestingClick) {
                    } else {
                        micTestingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDanger)));
                        micTestingButton.setText("STOP TESTING");
                        volumeProgressBar.setVisibility(View.VISIBLE);
                        volumeProgressBarWhite.setVisibility(View.GONE);

//                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                    int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                    int maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        int volumeLevel = s;
                        int maxVolumeLevel = 50;
                        int volumePercent = (int) (((float) volumeLevel / maxVolumeLevel) * 100);
                        volumeProgressBar.setMax(maxVolumeLevel);
                        volumeProgressBar.setProgress(volumeLevel);
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}