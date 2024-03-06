package com.whitecoats.clinicplus.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
//=======
//>>>>>>> 587e7bbcfa0129e1282fe7979760905b7543ce13
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.whitecoats.clinicplus.ActivityMoreClickListener;
import com.whitecoats.clinicplus.MyClinicGlobalClass;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.VolleyCallback;
import com.whitecoats.clinicplus.activities.AppointmentDetailsActivity;
import com.whitecoats.clinicplus.activities.EMRCreateRecordsFormActivity;
import com.whitecoats.clinicplus.activities.MedicationActivity;
import com.whitecoats.clinicplus.adapters.CaptureNoteAdapter;
import com.whitecoats.clinicplus.adapters.CaptureNoteMedicationAdapter;
import com.whitecoats.clinicplus.adapters.CustomGridViewAdapter;
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.CaptureNotesClickListner;
import com.whitecoats.clinicplus.interfaces.CaptureNotesMedicationListener;
import com.whitecoats.clinicplus.models.CaptureNotesMedicationModel;
import com.whitecoats.clinicplus.models.CaptureNotesModel;
import com.whitecoats.clinicplus.models.ItemPrescriptionView;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.clinicplus.viewmodels.AppointmentDetailsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CaptureNotesFragment extends Fragment {

    private RecyclerView captureNotesRecyclerView;
    private CaptureNoteAdapter captureNotesAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<CaptureNotesModel> captureNoteList = new ArrayList<>();
    public static ArrayList<CaptureNotesModel> handWrittenNoteFileArray = new ArrayList<>();
    EditText captureNoteText;
    String notesText = "";
    RelativeLayout notesCapturedEditTextLayoutButton, deletePointLayout;
    public static int encounterId, episodeId, patientId;
    public static String encounterCreatedOn, encounterName;
    private AppointmentDetailsViewModel appointmentDetailsViewModel;
    private TextView addMedicine;

    public static boolean isSavedNewAppointment;
    private int editCaptureNoteId;
    private RecyclerView captureNotesMedicationRecyclerView;
    private CaptureNoteMedicationAdapter captureNotesMedicationAdapter;
    ArrayList<CaptureNotesMedicationModel> captureNotesMedicationList = new ArrayList<>();
    private ProgressBar noteMainLayoutProgressBar;
    private RelativeLayout notesRelativeLayout;
    private LinearLayout imageTileOneLayout, imageTileTwoLayout, imageTileThreeLayout, imageTileFourLayout, addPrescriptionButton;
    private ImageView imageTileOne, imageTileTwo, imageTileThree, imageTileFour;
    private String imageTileOneUrl, imageTileTwoUrl, imageTileThreeUrl, imageTileFourUrl;
    private RelativeLayout noMedicationLayout, addMedicationTextLayout;
    private String dayDurationMorning, dayDurationAfternoon, dayDurationEvening, dayDurationNight;
    private TextView addMedicineNoRecord, prescriptionViewAll;
    private RelativeLayout addMedicineLayoutRight, addMedicineNoRecordClick;
    private WebView webViewLayout;
    private RelativeLayout allPrescriptionViewLayout, backButtonLayout, cancelLayout, DownloadLayout, nextArrowLayout;
    private int prescriptionViewPositionStatus;
    private AppointmentDetailsActivity appointmentDetailsActivity;
    private RelativeLayout noteLayoutHideAndShow, consultBottomLayout, noMedicationText;
    private ProgressBar notesProgress;
    private RelativeLayout vewFileLayout;
    private String viewPdfFileUrl;
    private RelativeLayout webViewRelativeLayout, pdfRelativeLayout;


    //prescriptionView Section
    private RecyclerView prescriptionViewRecyclerView;
    public static CustomGridViewAdapter prescriptionViewGridDataAdapter;
    public static List<ItemPrescriptionView> prescriptionViewItemList = null;
    private boolean isPDFFile = false;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private String[] permissionsRequired = new String[]{android.Manifest.permission.CAMERA};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private String uploadImageResponse;
    private File pdfFile;
    private Uri fileUri;
    private ImageView noteFileImage;
    private String imageFilePath;
    private String attachedFileName;
    private String type;
    private Boolean isInvestigationImageUpload = false;
    private Bitmap scaled;
    private boolean isAddMedication, isAddPrescription;
    private TextView cunsultationTimeText, cunsultationText;
    private RelativeLayout cunsultaionClickButton;
    private ApiGetPostMethodCalls apiGetPostMethodCalls;


    public CaptureNotesFragment() {
        // Required empty public constructor
    }

    public static CaptureNotesFragment newInstance(String param1, String param2) {
        CaptureNotesFragment fragment = new CaptureNotesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture_notes, container, false);
        apiGetPostMethodCalls = new ApiGetPostMethodCalls();
        appointmentDetailsActivity = (AppointmentDetailsActivity) getActivity();
        captureNotesRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        captureNotesAdapter = new CaptureNoteAdapter(getActivity(), captureNoteList, new CaptureNotesClickListner() {
            @Override
            public void OnCaptureNoteClick(int captureNotesId, String captureNotesText) {

                if (CaptureNoteAdapter.lastEditNoteList != null) {

                    captureNoteText.setText(captureNotesText);
                    for (int i = 0; i < captureNoteList.size(); i++) {
                        if (captureNotesId == (captureNoteList.get(i).getCaptureNoteId())) {
                            captureNoteList.remove(i);

                        }
                    }
                    captureNotesAdapter.notifyData(captureNoteList);

                } else {
                    captureNoteText.setText(captureNotesText);
                    for (int i = 0; i < captureNoteList.size(); i++) {
                        if (captureNotesId == (captureNoteList.get(i).getCaptureNoteId())) {
                            captureNoteList.remove(i);
                        }
                    }
                    captureNotesAdapter.notifyData(captureNoteList);
                }

            }
        });
        addMedicine = view.findViewById(R.id.addMedicine);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        captureNotesRecyclerView.setLayoutManager(layoutManager);
        captureNotesRecyclerView.setAdapter(captureNotesAdapter);
        notesCapturedEditTextLayoutButton = view.findViewById(R.id.notesCapturedEditTextLayoutButton);
        captureNoteText = (EditText) view.findViewById(R.id.notesCaptureEditText);
        deletePointLayout = view.findViewById(R.id.deletePointLayout);
        noteLayoutHideAndShow = view.findViewById(R.id.noteLayoutHideAndShow);
        consultBottomLayout = view.findViewById(R.id.consultBottomLayout);
        cunsultationText = view.findViewById(R.id.cunsultationText);
        cunsultationTimeText = view.findViewById(R.id.cunsultationTimeText);
        cunsultaionClickButton = view.findViewById(R.id.cunsultaionClickButton);
        noteMainLayoutProgressBar = view.findViewById(R.id.noteMainLayoutProgressBar);
        notesRelativeLayout = view.findViewById(R.id.notesRelativeLayout);
        imageTileOneLayout = view.findViewById(R.id.imageTileOneLayout);
        imageTileTwoLayout = view.findViewById(R.id.imageTileTwoLayout);
        imageTileThreeLayout = view.findViewById(R.id.imageTileThreeLayout);
        imageTileFourLayout = view.findViewById(R.id.imageTileFourLayout);
        imageTileOne = view.findViewById(R.id.imageTileOne);
        imageTileTwo = view.findViewById(R.id.imageTileTwo);
        imageTileThree = view.findViewById(R.id.imageTileThree);
        imageTileFour = view.findViewById(R.id.imageTileFour);
        addPrescriptionButton = view.findViewById(R.id.addPrescriptionButton);
        noMedicationLayout = view.findViewById(R.id.noMedicationLayout);
        noMedicationText = view.findViewById(R.id.noMedicationText);
        addMedicationTextLayout = view.findViewById(R.id.addMedicineLayoutRight);
//        addMedicineLayoutRight = view.findViewById(R.id.addMedicineLayoutRight);
        addMedicine = view.findViewById(R.id.addMedicine);
        addMedicineNoRecord = view.findViewById(R.id.addMedicineNoRecord);
        addMedicineNoRecordClick = view.findViewById(R.id.addMedicineNoRecordClick);
        prescriptionViewAll = view.findViewById(R.id.prescriptionViewAll);
        webViewLayout = view.findViewById(R.id.webViewLayout);
        allPrescriptionViewLayout = view.findViewById(R.id.allPrescriptionViewLayout);
        backButtonLayout = view.findViewById(R.id.backButtonLayout);
        cancelLayout = view.findViewById(R.id.cancelLayout);
        DownloadLayout = view.findViewById(R.id.DownloadLayout);
        nextArrowLayout = view.findViewById(R.id.nextArrowLayout);
        vewFileLayout = view.findViewById(R.id.vewFileLayout);
        webViewRelativeLayout = view.findViewById(R.id.webViewRelativeLayout);
        pdfRelativeLayout = view.findViewById(R.id.pdfRelativeLayout);
        notesProgress = view.findViewById(R.id.notes_progress);
        notesProgress.setVisibility(View.GONE);
        appointmentDetailsViewModel = new ViewModelProvider(this).get(AppointmentDetailsViewModel.class);
        appointmentDetailsViewModel.init();
        prescriptionViewItemList = new ArrayList<>();

        if (NewAppointmentFragment.Companion.getActivePastFilter().equalsIgnoreCase("past")) {
            deletePointLayout.setVisibility(View.GONE);
            addPrescriptionButton.setVisibility(View.GONE);
            noteLayoutHideAndShow.setVisibility(View.GONE);
            addMedicationTextLayout.setVisibility(View.GONE);
            noMedicationLayout.setVisibility(View.GONE);
            noMedicationText.setVisibility(View.GONE);
//            consultBottomLayout.setVisibility(View.GONE);
        } else {
            addPrescriptionButton.setVisibility(View.VISIBLE);
            deletePointLayout.setVisibility(View.VISIBLE);
            noteLayoutHideAndShow.setVisibility(View.VISIBLE);
            addMedicationTextLayout.setVisibility(View.VISIBLE);
            noMedicationText.setVisibility(View.VISIBLE);
            noMedicationLayout.setVisibility(View.VISIBLE);
//            consultBottomLayout.setVisibility(View.VISIBLE);
        }

//        if (appointmentDetailsActivity.appointmentMode == 1) {
//            consultBottomLayout.setVisibility(View.VISIBLE);
//            cunsultationText.setText("Join Video");
//            cunsultationTimeText.setText("");
//        } else {
//            consultBottomLayout.setVisibility(View.GONE);
//        }
        captureNoteText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationMedicineCompanyName
                ), null);
            }
        });
        cunsultaionClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        SpannableString addmedicine = new SpannableString("Add Medicine");
        addmedicine.setSpan(new UnderlineSpan(), 0, addmedicine.length(), 0);
        addMedicine.setText(addmedicine);

        SpannableString addmedicinenorecord = new SpannableString("Add Medicine");
        addmedicinenorecord.setSpan(new UnderlineSpan(), 0, addmedicinenorecord.length(), 0);
        addMedicineNoRecord.setText(addmedicinenorecord);

        SpannableString prescriptionviewall = new SpannableString("View all");
        prescriptionviewall.setSpan(new UnderlineSpan(), 0, prescriptionviewall.length(), 0);
        prescriptionViewAll.setText(prescriptionviewall);


        addMedicationTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getActivity().getString(R.string.CaptureNotesScreenAddMedicine
                ), null);
                isAddMedication = true;
                Intent intent = new Intent(getActivity(), MedicationActivity.class);
                intent.putExtra("patientId", patientId);
                intent.putExtra("episodeId", episodeId);
                intent.putExtra("encounterId", encounterId);
                startActivity(intent);

            }
        });
        addMedicineNoRecordClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAddMedication = true;
                Intent intent = new Intent(getActivity(), MedicationActivity.class);
                intent.putExtra("patientId", patientId);
                intent.putExtra("episodeId", episodeId);
                intent.putExtra("encounterId", encounterId);
                startActivity(intent);

            }
        });

        prescriptionViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getActivity().getString(R.string.CaptureNotesScreenViewAllPrescription
                ), null);
                prescriptionViewPositionStatus = 0;
                allPrescriptionViewLayout.setVisibility(View.VISIBLE);
                String fileUrl = handWrittenNoteFileArray.get(prescriptionViewPositionStatus).getFileUrl();
                getPresingedUrl(fileUrl, "view");
            }
        });


        addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.CaptureNotesScreenAddMedicine
                ), null);
                isAddMedication = true;
                Intent intent = new Intent(getActivity(), MedicationActivity.class);
                intent.putExtra("patientId", patientId);
                intent.putExtra("episodeId", episodeId);
                intent.putExtra("encounterId", encounterId);
                startActivity(intent);
            }
        });
        notesCapturedEditTextLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getActivity().getString(R.string.CaptureNotesScreenAddNotes
                ), null);
                notesText = captureNoteText.getText().toString();
                if (notesText.matches("")) {
                    Toast.makeText(v.getContext(), "You did not enter a notes", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (CaptureNoteAdapter.isEditClick) {
                    handWrittenNoteFileArray.clear();
                    captureNotesMedicationList.clear();
                    prescriptionViewItemList.clear();
                    if (CaptureNoteAdapter.lastEditNoteList.size() > 0) {
                        CaptureNoteAdapter.lastEditNoteList.clear();
                    }
                    updateAppointmentNotes(getActivity(), editCaptureNoteId, notesText);
                } else {
                    handWrittenNoteFileArray.clear();
                    captureNotesMedicationList.clear();
                    prescriptionViewItemList.clear();
                    if (CaptureNoteAdapter.lastEditNoteList.size() > 0) {
                        CaptureNoteAdapter.lastEditNoteList.clear();
                    }
                    CaptureNotesModel mLog = new CaptureNotesModel();
                    mLog.setNotesText(notesText);
                    captureNoteList.add(mLog);
                    captureNotesAdapter.notifyData(captureNoteList);
                    captureNoteText.setText("");
                    saveNewAppointmentNotes(getActivity(), encounterId, episodeId, patientId, notesText);


                }
            }
        });

        deletePointLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getActivity().getString(R.string.CaptureNotesScreenDeletePoint
                ), null);

                if (CaptureNoteAdapter.selectedNotedIdArray.length() > 0) {
                    deleteAppointmentNotes(getActivity(), CaptureNoteAdapter.selectedNotedIdArray);
                } else {
                    Toast.makeText(getActivity(), "Please select note", Toast.LENGTH_LONG).show();
                }
            }
        });


        captureNotesMedicationRecyclerView = (RecyclerView) view.findViewById(R.id.medicationRecyclerView);
        captureNotesMedicationAdapter = new CaptureNoteMedicationAdapter(captureNotesMedicationList, getContext(), new CaptureNotesMedicationListener() {
            @Override
            public void OnCaptureNoteMedicationClick(int capturedNotesId, CaptureNotesMedicationModel captureNotesMedicationModel) {
                JSONObject params = new JSONObject();
                try {
                    params.put("record_id", captureNotesMedicationModel.getRecordId());
                    params.put("user_id", patientId);
                    params.put("status", "Not Active");
                    notesProgress.setVisibility(View.VISIBLE);
                    discountinueMedicine(getActivity(), params, capturedNotesId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        final LinearLayoutManager layoutManagerMedication = new LinearLayoutManager(getActivity());
        layoutManagerMedication.setOrientation(LinearLayoutManager.VERTICAL);
        captureNotesMedicationRecyclerView.setLayoutManager(layoutManagerMedication);
        captureNotesMedicationRecyclerView.setAdapter(captureNotesMedicationAdapter);

        addPrescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                isAddPrescription = true;
                Intent myIntent = new Intent(getActivity(), EMRCreateRecordsFormActivity.class);
                myIntent.putExtra("patientId", patientId);
                myIntent.putExtra("episodeId", episodeId);
                myIntent.putExtra("encounterId", encounterId);
                myIntent.putExtra("categoryName", "");
                myIntent.putExtra("patientName", "Dileep");
                startActivity(myIntent);
            }
        });

        imageTileOneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getPdfImage(imageTileOneUrl);
                if (handWrittenNoteFileArray.size() > 0) {
                    allPrescriptionViewLayout.setVisibility(View.VISIBLE);
                    prescriptionViewPositionStatus = 0;
                    String fileUrl = handWrittenNoteFileArray.get(prescriptionViewPositionStatus).getFileUrl();
                    getPresingedUrl(fileUrl, "view");
                }
            }
        });

        imageTileTwoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getPdfImage(imageTileTwoUrl);
                if (handWrittenNoteFileArray.size() > 1) {
                    allPrescriptionViewLayout.setVisibility(View.VISIBLE);
                    prescriptionViewPositionStatus = 1;
                    String fileUrl = handWrittenNoteFileArray.get(prescriptionViewPositionStatus).getFileUrl();
                    getPresingedUrl(fileUrl, "view");
                }
            }
        });

        imageTileThreeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getPdfImage(imageTileThreeUrl);
                if (handWrittenNoteFileArray.size() > 2) {
                    allPrescriptionViewLayout.setVisibility(View.VISIBLE);
                    prescriptionViewPositionStatus = 2;
                    String fileUrl = handWrittenNoteFileArray.get(prescriptionViewPositionStatus).getFileUrl();
                    getPresingedUrl(fileUrl, "view");
                }
            }
        });

        imageTileFourLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getPdfImage(imageTileFourUrl);
                if (handWrittenNoteFileArray.size() > 3) {
                    allPrescriptionViewLayout.setVisibility(View.VISIBLE);
                    prescriptionViewPositionStatus = 3;
                    String fileUrl = handWrittenNoteFileArray.get(prescriptionViewPositionStatus).getFileUrl();
                    getPresingedUrl(fileUrl, "view");
                }
            }
        });


        backButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.PrescriptionScreenPrevious), null);
                if (prescriptionViewPositionStatus == 0) {

                } else {
                    prescriptionViewPositionStatus--;
                    String fileUrl = handWrittenNoteFileArray.get(prescriptionViewPositionStatus).getFileUrl();
                    getPresingedUrl(fileUrl, "view");
                }
            }
        });
        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.PrescriptionScreenClose), null);
                prescriptionViewPositionStatus = 0;
                allPrescriptionViewLayout.setVisibility(View.GONE);
            }
        });
        DownloadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.PrescriptionScreenDownload), null);
                String fileUrl = handWrittenNoteFileArray.get(prescriptionViewPositionStatus).getFileUrl();
                getPresingedUrl(fileUrl, "download");

            }
        });
        nextArrowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.PrescriptionScreenNext), null);
                if (handWrittenNoteFileArray.size() == prescriptionViewPositionStatus + 1) {

                } else {
                    prescriptionViewPositionStatus++;
                    String fileUrl = handWrittenNoteFileArray.get(prescriptionViewPositionStatus).getFileUrl();
                    getPresingedUrl(fileUrl, "view");
                }

            }
        });

        vewFileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(viewPdfFileUrl));
                startActivity(browserIntent);
            }
        });


        // Create the recyclerview for quick action.
        prescriptionViewRecyclerView = view.findViewById(R.id.prescription_view_recycler_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        prescriptionViewRecyclerView.setLayoutManager(gridLayoutManager);
        prescriptionViewGridDataAdapter = new CustomGridViewAdapter(prescriptionViewItemList, getActivity(), new ActivityMoreClickListener() {
            @Override
            public void onItemClick(View v, int position, int clickId, String urlFile) {
//                if (globalClass.isOnline()) {

                if (clickId == 0) {
//                    Toast.makeText(getContext(),"valled",Toast.LENGTH_LONG).show();

                    MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getActivity().getString(R.string.CaptureNotesScreenAddPrescription
                    ), null);
                    selectMethodDialog(container);
                } else {
                    if (handWrittenNoteFileArray.size() > 0) {
                        allPrescriptionViewLayout.setVisibility(View.VISIBLE);
                        prescriptionViewPositionStatus = position;
                        String fileUrl = urlFile;
                        getPresingedUrl(fileUrl, "view");
                    }
                }


//                } else {
//                    globalClass.noInternetConnection.showDialog(getActivity());
//                }

            }

        });
        // Set data adapter.
        prescriptionViewRecyclerView.setAdapter(prescriptionViewGridDataAdapter);

//        dummyData();

        //get capture notes list data
        noteMainLayoutProgressBar.setVisibility(View.VISIBLE);
        notesRelativeLayout.setVisibility(View.GONE);
        captureNoteList.clear();
        handWrittenNoteFileArray.clear();
        captureNotesMedicationList.clear();
        prescriptionViewItemList.clear();
        getCaptureNoteListData(getActivity(), appointmentDetailsActivity.appointmentID);


        return view;
    }

//    public void dummyData() {
//        String iconNamePdf = "";
//        String iconNameImage = "";
//        String iconNameAdd = "";
//        iconNamePdf = "ic_pdf_file2";
//        iconNameImage = "ic_insert_photo";
//        iconNameAdd = "ic_add3";
//        for (int i = 0; i < 10; i++) {
//            ItemPrescriptionView temp = new ItemPrescriptionView(iconNamePdf,1);
//            prescriptionViewItemList.add(temp);
//        }
//        ItemPrescriptionView temp = new ItemPrescriptionView(iconNameAdd,0);
//        prescriptionViewItemList.add(temp);
//
//
//
//
//        // Set data adapter.
//        prescriptionViewRecyclerView.setAdapter(prescriptionViewGridDataAdapter);
//    }

    @Override
    public void onResume() {
        super.onResume();

        if (isAddMedication || isAddPrescription) {
            isAddMedication = false;
            isAddPrescription = false;
            captureNoteList.clear();
            handWrittenNoteFileArray.clear();
            captureNotesMedicationList.clear();
            prescriptionViewItemList.clear();
            getCaptureNoteListData(getActivity(), appointmentDetailsActivity.appointmentID);
        }

//        //get capture notes list data
//        noteMainLayoutProgressBar.setVisibility(View.VISIBLE);
//        notesRelativeLayout.setVisibility(View.GONE);
//        captureNoteList.clear();
//        handWrittenNoteFileArray.clear();
//        captureNotesMedicationList.clear();
//        prescriptionViewItemList.clear();
//        getCaptureNoteListData(getActivity(), appointmentDetailsActivity.appointmentID);
    }

    public void getCaptureNoteListData(Activity activity, int appointmentID) {
        appointmentDetailsViewModel.getCaptureNotesRecordsList(activity, appointmentID).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.i("capture notes res", s);
                if (noteMainLayoutProgressBar.getVisibility() == View.VISIBLE) {
                    noteMainLayoutProgressBar.setVisibility(View.GONE);
                }
                if (notesRelativeLayout.getVisibility() == View.GONE) {
                    notesRelativeLayout.setVisibility(View.VISIBLE);
                }
                try {
                    JSONObject response = new JSONObject(s);
                    if (response.getInt("status_code") == 200) {
                        JSONObject resObject = response.getJSONObject("response").getJSONObject("response");
                        JSONObject encounterDetails = resObject.getJSONObject("encounter_details");
                        encounterId = encounterDetails.getInt("id");
                        appointmentDetailsActivity.encounterId = encounterId;
                        episodeId = encounterDetails.getInt("episode_id");
                        appointmentDetailsActivity.episodeId = episodeId;
                        patientId = encounterDetails.getInt("patient_id");
                        encounterCreatedOn = encounterDetails.getString("encounter_date_time");
                        encounterName = encounterDetails.getString("encounter_mode");
                        JSONArray appointmentNoteArray = resObject.getJSONArray("appointment_notes");
                        //Get Appointment Notes Data
                        if (appointmentNoteArray.length() > 0) {
                            deletePointLayout.setVisibility(View.VISIBLE);
                            for (int i = 0; i < appointmentNoteArray.length(); i++) {
                                JSONObject appointmentNoteObject = appointmentNoteArray.getJSONObject(i);
                                CaptureNotesModel mLog = new CaptureNotesModel();
                                mLog.setNotesText(appointmentNoteObject.getString("appointment_notes"));
                                mLog.setCaptureNoteId(appointmentNoteObject.getInt("id"));
                                mLog.setSelected(false);
                                captureNoteList.add(mLog);
                                captureNotesAdapter = new CaptureNoteAdapter(getActivity(), captureNoteList, new CaptureNotesClickListner() {
                                    @Override
                                    public void OnCaptureNoteClick(int captureNotePosition, String captureNotesText) {

                                        if (CaptureNoteAdapter.lastEditNoteList.size() > 0) {

                                            captureNoteText.setText(captureNotesText);
                                            editCaptureNoteId = captureNotePosition;
                                            for (int i = 0; i < captureNoteList.size(); i++) {
                                                if (captureNotePosition == (captureNoteList.get(i).getCaptureNoteId())) {
                                                    captureNoteList.remove(i);

                                                }
                                            }
                                            CaptureNotesModel cnm = new CaptureNotesModel();
                                            cnm.setCaptureNoteId(CaptureNoteAdapter.lastEditNoteList.get(0).getCaptureNoteId());
                                            cnm.setNotesText(CaptureNoteAdapter.lastEditNoteList.get(0).getNotesText());
                                            captureNoteList.add(cnm);
                                            captureNotesAdapter.notifyData(captureNoteList);

                                            CaptureNoteAdapter.lastEditNoteList = new ArrayList<>();
                                            CaptureNotesModel cnmLast = new CaptureNotesModel();
                                            cnmLast.setCaptureNoteId(captureNotePosition);
                                            cnmLast.setNotesText(captureNotesText);
                                            CaptureNoteAdapter.lastEditNoteList.add(cnmLast);

                                        } else {
                                            captureNoteText.setText(captureNotesText);
                                            editCaptureNoteId = captureNotePosition;
                                            for (int i = 0; i < captureNoteList.size(); i++) {
                                                if (captureNotePosition == (captureNoteList.get(i).getCaptureNoteId())) {
                                                    captureNoteList.remove(i);
                                                }
                                            }
                                            captureNotesAdapter.notifyData(captureNoteList);


                                            CaptureNoteAdapter.lastEditNoteList = new ArrayList<>();
                                            CaptureNotesModel cnm = new CaptureNotesModel();
                                            cnm.setCaptureNoteId(captureNotePosition);
                                            cnm.setNotesText(captureNotesText);
                                            CaptureNoteAdapter.lastEditNoteList.add(cnm);
                                        }


                                    }
                                });
                                captureNotesRecyclerView.setAdapter(captureNotesAdapter);
                            }
                        } else {

                            deletePointLayout.setVisibility(View.GONE);

                        }

                        //Get Appointment Prescription Data
                        JSONArray getHandWrittenNoteArray = resObject.getJSONArray("handwritten_notes");
                        if (getHandWrittenNoteArray.length() > 0) {
                            prescriptionViewAll.setVisibility(View.GONE);
//                            prescriptionViewAll.setVisibility(View.VISIBLE);
                            String iconNamePdf = "";
                            String iconNameImage = "";
                            String iconNameAdd = "";
                            iconNamePdf = "ic_pdf_file2";
                            iconNameImage = "ic_insert_photo";
                            iconNameAdd = "ic_add3";
                            for (int i = 0; i < getHandWrittenNoteArray.length(); i++) {
                                JSONObject handWrittenObject = getHandWrittenNoteArray.getJSONObject(i);
                                CaptureNotesModel cnm1 = new CaptureNotesModel();
                                cnm1.setFileUrl(handWrittenObject.getString("file_url"));
                                handWrittenNoteFileArray.add(cnm1);
                                if (handWrittenObject.getString("file_url").contains(".pdf")) {
                                    ItemPrescriptionView cnm = new ItemPrescriptionView(iconNamePdf, 1, handWrittenObject.getString("file_url"));
                                    prescriptionViewItemList.add(cnm);
                                } else {
                                    ItemPrescriptionView cnm = new ItemPrescriptionView(iconNameImage, 1, handWrittenObject.getString("file_url"));
                                    prescriptionViewItemList.add(cnm);
                                }
                            }
                            if (NewAppointmentFragment.Companion.getActivePastFilter().equalsIgnoreCase("Active")) {
                                ItemPrescriptionView cnm = new ItemPrescriptionView(iconNameAdd, 0, "");
                                prescriptionViewItemList.add(cnm);
                            }

                            prescriptionViewGridDataAdapter.notifyDataSetChanged();
                        } else {
                            if (NewAppointmentFragment.Companion.getActivePastFilter().equalsIgnoreCase("Active")) {
                                ItemPrescriptionView cnm = new ItemPrescriptionView("ic_add3", 0, "");
                                prescriptionViewItemList.add(cnm);
                            }
                            prescriptionViewGridDataAdapter.notifyDataSetChanged();
                            prescriptionViewAll.setVisibility(View.GONE);
                        }

                        //Get Medications Data
                        if (resObject.isNull("medicine_records") == false) {
                            JSONArray getMedicationsArray = resObject.getJSONArray("medicine_records");
                            noMedicationLayout.setVisibility(View.GONE);
                            if (NewAppointmentFragment.Companion.getActivePastFilter().equalsIgnoreCase("past")) {
                                addMedicationTextLayout.setVisibility(View.GONE);
                                addMedicineNoRecord.setVisibility(View.GONE);
                            } else {
                                addMedicationTextLayout.setVisibility(View.VISIBLE);
                                addMedicineNoRecord.setVisibility(View.VISIBLE);
                            }
                            for (int i = 0; i < getMedicationsArray.length(); i++) {
                                JSONObject getMedicationObject = getMedicationsArray.getJSONObject(i);
                                CaptureNotesMedicationModel mLogMedication = new CaptureNotesMedicationModel();
//                                if (getMedicationObject.getString("232").equalsIgnoreCase("Active")) {
                                mLogMedication.setMedicationName(getMedicationObject.getString("222"));
                                mLogMedication.setMedicationDays(getMedicationObject.getInt("233"));
                                mLogMedication.setRecordId(getMedicationObject.getInt("record_id"));
                                mLogMedication.setStatus(getMedicationObject.getString("232"));
                                if (getMedicationObject.has("234")) {
                                    dayDurationMorning = "1";
                                } else {
                                    dayDurationMorning = "0";
                                }
                                if (getMedicationObject.has("235")) {
                                    dayDurationAfternoon = "1";
                                } else {
                                    dayDurationAfternoon = "0";
                                }
                                if (getMedicationObject.has("236")) {
                                    dayDurationEvening = "1";
                                } else {
                                    dayDurationEvening = "0";
                                }
                                if (getMedicationObject.has("237")) {
                                    dayDurationNight = "1";
                                } else {
                                    dayDurationNight = "0";
                                }
                                String dayDuration = dayDurationMorning + "-" + dayDurationAfternoon + "-" + dayDurationEvening + "-" + dayDurationNight;
                                mLogMedication.setMedicationDuration(dayDuration);
                                captureNotesMedicationList.add(mLogMedication);
//                                }
                            }
                            if (captureNotesMedicationList.size() <= 0) {
                                noMedicationLayout.setVisibility(View.VISIBLE);
                            } else {
                                noMedicationLayout.setVisibility(View.GONE);
                            }
                            captureNotesMedicationAdapter.notifyData(captureNotesMedicationList);
                        } else {
                            if (NewAppointmentFragment.Companion.getActivePastFilter().equalsIgnoreCase("past")) {
                                addMedicineNoRecordClick.setVisibility(View.GONE);
                                noMedicationLayout.setVisibility(View.VISIBLE);
                                noMedicationText.setVisibility(View.VISIBLE);
                            } else {
                                noMedicationLayout.setVisibility(View.VISIBLE);
                                addMedicationTextLayout.setVisibility(View.GONE);

                                addMedicineNoRecordClick.setVisibility(View.VISIBLE);
                                noMedicationText.setVisibility(View.GONE);
                            }

//                            noMedicationLayout.setVisibility(View.VISIBLE);
//                            addMedicationTextLayout.setVisibility(View.GONE);


                        }


                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), s);
                    }
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                    Log.d("parserError:", "parserError:" + e);
                }

            }
        });
    }

    private void selectMethodDialog(ViewGroup viewGroup) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
//        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_uploadimg_method, viewGroup, false);
        LinearLayout camera = dialogView.findViewById(R.id.uploadMethodCamera);
        LinearLayout attach = dialogView.findViewById(R.id.uploadMethodAttachment);

        ImageButton cameraButton = dialogView.findViewById(R.id.cameraButton);
        ImageButton attachImageButton = dialogView.findViewById(R.id.attachImageButton);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                if (ActivityCompat.checkSelfPermission(getContext(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    ((AppointmentDetailsActivity) getActivity()).openCamera();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                }
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                ((AppointmentDetailsActivity) getActivity()).openFileDialog();
            }
        });


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                if (ActivityCompat.checkSelfPermission(getContext(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    ((AppointmentDetailsActivity) getActivity()).openCamera();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                }
            }
        });

        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                ((AppointmentDetailsActivity) getActivity()).openFileDialog();
            }
        });


    }

//    public void openCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File photo = new File(Environment.getExternalStorageDirectory(), "Pic1.png");
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            fileUri = Uri.fromFile(photo);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//        } else {
//            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                //Create a file to store the image
//                File photoFile = null;
//                try {
//                    photoFile = createImageFile();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//                if (photoFile != null) {
//                    fileUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", photoFile);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                            fileUri);
////                    startActivityForResult(pictureIntent,
////                            100);
//                }
//            }
//        }
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//            startActivityForResult(intent, 1);
//        }
//    }
//
//    private void openFileDialog() {
//
//        Intent intent = new Intent();
//// Show only images, no videos or anything else
//        intent.setType("*/*");
//        String[] mimeTypes = {"image/*","application/pdf"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//// Always show the chooser (if there are multiple options available)
//        startActivityForResult(Intent.createChooser(intent, "Select File"), 2);
//    }
//
//    private File createImageFile() throws IOException {
//        String timeStamp =
//                new SimpleDateFormat("yyyyMMdd_HHmmss",
//                        Locale.getDefault()).format(new Date());
//        String imageFileName = "IMG_" + timeStamp + "_";
//        File storageDir =
//                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".png",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        imageFilePath = image.getAbsolutePath();
//        return image;
//    }
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 1:
//                if (resultCode == Activity.RESULT_OK) {
//
//                    //new
//                    try {
//                        ContentResolver contentResolver = getActivity().getContentResolver();
//
//                        // Use the content resolver to open camera taken image input stream through image uri.
//                        attachedFileName = getFileName(fileUri);
//                        InputStream inputStream = contentResolver.openInputStream(fileUri);
//
//                        // Decode the image input stream to a bitmap use BitmapFactory.
//                        Bitmap pictureBitmap = BitmapFactory.decodeStream(inputStream);
//                        int nh = (int) (pictureBitmap.getHeight() * (720.0 / pictureBitmap.getWidth()));
//                        scaled = Bitmap.createScaledBitmap(pictureBitmap, 720, nh, true);
//                        type = "image";
//                        uploadImage("image");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            case 2:
//                if (resultCode == Activity.RESULT_OK) {
//                    Uri uri = data.getData();
//                    String fileName = getFileName(uri);
//                    attachedFileName = fileName;
//                    if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")|| fileName.endsWith(".pdf")){
//                        try {
//                            if (fileName.contains("pdf")) {
//                                isPDFFile = true;
//                                uploadPDFFile(fileName,uri);
//                            }
//                            else {
//                                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                                int nh = (int) (bitmapImage.getHeight() * (720.0 / bitmapImage.getWidth()));
//                                scaled = Bitmap.createScaledBitmap(bitmapImage, 720, nh, true);
//                                type = "image";
//                                uploadImage("image");
//                            }
//                        }
//                        catch (IOException e){
//                            e.printStackTrace();
//                        }
//                    }
//                    else{
//                        Toast.makeText(getContext(),"Please Upload .jpg or .png files only",Toast.LENGTH_LONG).show();
//                    }
//                }
//                break;
//            case REQUEST_PERMISSION_SETTING:
//                if (ActivityCompat.checkSelfPermission(getContext(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
//                    //Got Permission
////                    Toast.makeText(RecordCreateActivity.this, "You Have All The Permission", Toast.LENGTH_SHORT).show();
//                }
//        }
//    }
//
//    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream);
//        return byteArrayOutputStream.toByteArray();
//    }
//
//    public String getFileName(Uri uri) {
//        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
//        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
//        if (cursor != null) {
//            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
//            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
//            int column_index = cursor
//                    .getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } else return null;
//    }
//
//    private void uploadPDFFile(String selectedFilePath,Uri uri) {
//        ContentResolver resolver = getContext()
//                .getContentResolver();
//        try {
//            ParcelFileDescriptor pfd =
//                    resolver.openFileDescriptor(uri, "r");
//            InputStream stream = new FileInputStream(pfd.getFileDescriptor());
//            File localfile = new File(getActivity().getCacheDir(),selectedFilePath);
//            OutputStream localStream = new FileOutputStream(localfile);
//            IOUtils.copyStream(stream,localStream);
//            pdfFile = localfile;
//            type = "pdf";
//            uploadImage("pdf");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void uploadImage(String type) {
//        String url = appConfigClass.uploadRecordImage;
//
//        final ProgressDialog loadingDialog = new ProgressDialog(getContext());
//        if(type.equalsIgnoreCase("image")){
//            loadingDialog.setMessage(getResources().getString(R.string.uploading_image));
//        }
//        else {
//            loadingDialog.setMessage(getResources().getString(R.string.uploading_pdf));
//        }
//
//        loadingDialog.setCancelable(false);
//        loadingDialog.setInverseBackgroundForced(false);
//        loadingDialog.show();
//
//        //our custom volley request
//        AppImageUploader volleyMultipartRequest = new AppImageUploader(Request.Method.POST, url,
//                new Response.Listener<NetworkResponse>() {
//                    @Override
//                    public void onResponse(NetworkResponse response) {
//                        try {
//
//                            JSONObject rootObj = new JSONObject(new String(response.data));
////                            Log.d("Image Upload", rootObj.toString());
//                            rootObj = rootObj.getJSONObject("response");
//                            String url = rootObj.getString("url");
//                            if (url != null && !url.equals("")) {
//                                uploadImageResponse = url;
//                                //Log.i("upload response",uploadImageResponse);
//                                if(type.equalsIgnoreCase("image"))
//                                {
//                                    Toast.makeText(getContext(), getResources().getString(R.string.upload_img_success), Toast.LENGTH_SHORT).show();
//                                }
//                                else if(type.equalsIgnoreCase("pdf")){
//                                    Toast.makeText(getContext(), getResources().getString(R.string.upload_pdf_success), Toast.LENGTH_SHORT).show();
//
//                                }
//                            }
//                            isPDFFile = false;
//                            loadingDialog.dismiss();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Toast.makeText(getContext(),
//                                    getResources().getString(R.string.slow_internet_connection),
//                                    Toast.LENGTH_LONG).show();
//                        } else if (error instanceof AuthFailureError) {
//                            Toast.makeText(getContext(),
//                                    getResources().getString(R.string.slow_internet_connection),
//                                    Toast.LENGTH_LONG).show();
//
//
//                        } else if (error instanceof ServerError) {
//                            Toast.makeText(getContext(),
//                                    getResources().getString(R.string.slow_internet_connection),
//                                    Toast.LENGTH_LONG).show();
//
//                            //TODO
//                        } else if (error instanceof NetworkError) {
//                            Toast.makeText(getContext(),
//                                    getResources().getString(R.string.slow_internet_connection),
//                                    Toast.LENGTH_LONG).show();
//
//                            //TODO
//                        } else if (error instanceof ParseError) {
//                            Toast.makeText(getContext(),
//                                    getResources().getString(R.string.slow_internet_connection),
//                                    Toast.LENGTH_LONG).show();
//
//                            //TODO
//                        } else {
//                            Toast.makeText(getContext(), getResources().getString(R.string.upload_img_error), Toast.LENGTH_SHORT).show();
//
//                        }
//
//
//                        loadingDialog.dismiss();
//                    }
//                }) {
//
//            /*
//             * If you want to add more parameters with the image
//             * you can do it here
//             * here we have only one parameter with the image
//             * which is tags
//             * */
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
////                params.put("path", "records/" + episodeId + "/");//old
//                if (isInvestigationImageUpload) {
//                    params.put("path", "records_v2/investigation_files/" + appConfigClass.doctorId + "/" + "android" + "/");//new
//                } else {
//                    params.put("path", "records_v2/images/" + appConfigClass.doctorId + "/" + "android" + "/");//new
//                }
//                return params;
//            }
//
//            /*
//             * Here we are passing image by renaming it with a unique name
//             * */
//
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
////                long imagename = System.currentTimeMillis();
//                if (isPDFFile) {
//                    try {
//                        byte[] bytesArray = new byte[(int) pdfFile.length()];
//                        FileInputStream fis = new FileInputStream(pdfFile);
//                        int a = fis.read(bytesArray); //read file into bytes[]
//                        fis.close();
//                        params.put("file", new DataPart("Record" + ".pdf", bytesArray));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Bitmap bitmap = scaled;
//                    params.put("file", new DataPart("Record" + ".png", getFileDataFromDrawable(bitmap)));
//                }
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
////                headers.put("Content-Type", "application/json");
//                headers.put("App-Origin", appConfigClass.appOrigin);
//                headers.put("Authorization", "Bearer " + AppConfigClass.loginToken);//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vNGRkYjMxNjkubmdyb2suaW8vYXBpL3YxL2F1dGgvdG9rZW4iLCJpYXQiOjE1MTgwODcyMzAsImV4cCI6MTUxOTI5NjgzMCwibmJmIjoxNTE4MDg3MjMwLCJqdGkiOiJ2UEJnY09hVGdGOU9YaFh4Iiwic3ViIjozMzk1LCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.Y3wU_NBXXhw--b75x4iwo6fmHQQzMLkla2gHrheWqeU");
//                return headers;
//            }
//        };
//
//        //adding the request to volley
//        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                120000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
//    }


    public void saveNewAppointmentNotes(Activity activity, int encounterId, int episodeId,
                                        int patientId, String appointmentNotes) {
        appointmentDetailsViewModel.saveNewAppointmentNote(getActivity(), encounterId, episodeId, patientId, appointmentNotes).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
//                loadingDialog.dismiss();
                try {
                    JSONObject response = new JSONObject(s);
                    if (response.getInt("status_code") == 200) {
//                        JSONObject responseObject = response.getJSONObject("response");
                        captureNoteList.clear();
                        if (CaptureNoteAdapter.lastEditNoteList != null) {
                            CaptureNoteAdapter.lastEditNoteList.clear();
                            isSavedNewAppointment = true;
                        }
                        getCaptureNoteListData(getActivity(), appointmentDetailsActivity.appointmentID);
                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateAppointmentNotes(Activity activity, int appointmentNoteId, String
            appointmentNotes) {
        appointmentDetailsViewModel.updateAppointmentNote(getActivity(), appointmentNoteId, appointmentNotes).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
//                loadingDialog.dismiss();
                try {
                    JSONObject response = new JSONObject(s);
                    if (response.getInt("status_code") == 200) {
                        CaptureNoteAdapter.isEditClick = false;
                        captureNoteList.clear();
                        captureNoteText.setText("");
                        getCaptureNoteListData(getActivity(), appointmentDetailsActivity.appointmentID);
//                        JSONObject responseObject = response.getJSONObject("response");
                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void deleteAppointmentNotes(Activity activity, JSONArray appointmentNoteArrayId) {
        appointmentDetailsViewModel.deleteAppointmentNote(getActivity(), appointmentNoteArrayId).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
//                loadingDialog.dismiss();
                try {
                    JSONObject response = new JSONObject(s);
                    if (response.getInt("status_code") == 200) {
//                        JSONObject responseObject = response.getJSONObject("response");
                        captureNoteList.clear();
                        CaptureNoteAdapter.selectedNotedIdArray = new JSONArray();
                        handWrittenNoteFileArray.clear();
                        captureNotesMedicationList.clear();
                        prescriptionViewItemList.clear();
                        getCaptureNoteListData(getActivity(), appointmentDetailsActivity.appointmentID);
                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        webViewLayout.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
//                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });

        // Javascript inabled on webview
        webViewLayout.getSettings().setJavaScriptEnabled(true);

        // Other webview options
        webViewLayout.getSettings().setLoadWithOverviewMode(true);
        webViewLayout.getSettings().setUseWideViewPort(true);
        webViewLayout.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewLayout.setScrollbarFadingEnabled(false);
        webViewLayout.getSettings().setBuiltInZoomControls(true);

        /*
         String summary = "<html><body>You scored <b>192</b> points.</body></html>";
         webview.loadData(summary, "text/html", null);
         */

        //Load url in webview
        webViewLayout.loadUrl(url);


    }

//    // Open previous opened link from history on webview when back button pressed
//
//    @Override
//    // Detect when the back button is pressed
//    public void onBackPressed() {
//        if(webView.canGoBack()) {
//            webView.goBack();
//        } else {
//            // Let the system handle the back button
//            super.onBackPressed();
//        }
//    }

    private void getPresingedUrl(final String path, String type) {
        String url = ApiUrls.getArticleImage;
        JSONObject map = new JSONObject();
        try {
            map.put("url", path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // prepare the Request
        apiGetPostMethodCalls.volleyApiRequestData(url, Request.Method.POST, map, requireContext(),
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            if (type.equalsIgnoreCase("download")) {
                                Uri uri = Uri.parse(response.getString("response"));
                                String urlarr[] = response.getString("response").split("/");
                                String filename = urlarr[urlarr.length - 1].split("\\?")[0];
                                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                        DownloadManager.Request.NETWORK_MOBILE);

                                // set title and description
                                request.setTitle("File Download");
                                request.setDescription("File Download for Clinic Plus");

                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                //set the local destination for download file to a path within the application's external files directory
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                                request.setMimeType("*/*");
                                downloadManager.enqueue(request);
                            } else {
                                if (response.getString("response").contains(".png") || response.getString("response").contains(".jpeg")) {
                                    webViewRelativeLayout.setVisibility(View.VISIBLE);
                                    pdfRelativeLayout.setVisibility(View.GONE);
                                    startWebView(response.getString("response"));
                                } else {
                                    viewPdfFileUrl = response.getString("response");
                                    webViewRelativeLayout.setVisibility(View.GONE);
                                    pdfRelativeLayout.setVisibility(View.VISIBLE);

                                }
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

    public void discountinueMedicine(Activity activity, JSONObject params, int id) {
        appointmentDetailsViewModel.updateMedicines(activity, params).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    JSONObject reponse = new JSONObject(s);
                    if (reponse.getInt("status_code") == 200) {
                        String status = reponse.getJSONObject("response").getString("response");
                        if (status.equalsIgnoreCase("success")) {
                            Toast.makeText(getActivity(), "Medicine successfully discontinued", Toast.LENGTH_LONG).show();
                            captureNotesMedicationList.remove(id);
                            captureNotesMedicationAdapter.notifyData(captureNotesMedicationList);
                            if (captureNotesMedicationList.size() <= 0) {
                                noMedicationLayout.setVisibility(View.VISIBLE);
                            } else {
                                noMedicationLayout.setVisibility(View.GONE);
                            }

//                            if (isAddMedication || isAddPrescription) {
                            isAddMedication = false;
                            isAddPrescription = false;
                            captureNoteList.clear();
                            handWrittenNoteFileArray.clear();
                            captureNotesMedicationList.clear();
                            prescriptionViewItemList.clear();
                            getCaptureNoteListData(getActivity(), appointmentDetailsActivity.appointmentID);
//                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed to update the medicine", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                notesProgress.setVisibility(View.GONE);

            }
        });
    }


}