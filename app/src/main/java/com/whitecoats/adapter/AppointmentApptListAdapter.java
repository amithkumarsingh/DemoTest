package com.whitecoats.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import com.whitecoats.clinicplus.MyClinicGlobalClass;
import com.whitecoats.clinicplus.VolleyCallback;
import com.whitecoats.clinicplus.activities.VideoScreenActivity;
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel;
import com.whitecoats.clinicplus.activities.EMRActivity;
import com.whitecoats.fragments.AppointmentFragment;
import com.whitecoats.fragments.HomeFragment;
import com.whitecoats.model.AppointmentApptListModel;
import com.whitecoats.clinicplus.AppUtilities;
import com.whitecoats.clinicplus.AppointmentListClickListner;
import com.whitecoats.clinicplus.CategoryGridViewClickListener;
import com.whitecoats.clinicplus.ConfirmOrderActivity;
import com.whitecoats.clinicplus.PatientRecordActivity;
import com.whitecoats.clinicplus.R;

import com.zoho.salesiqembed.ZohoSalesIQ;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

import static android.content.Context.MODE_PRIVATE;

public class AppointmentApptListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppointmentApptListModel> appointmentApptListModelList;
    private Activity activity;
    private int groupNo = -1;
    private TextView summaryDuration;
    private PopupWindow popupWindow;
    private ArrayList<String> durationList;
    private String selectedApptType;
    private int apptId;
    private ProgressDialog otpLoading;
    private JSONObject jsonValue;
    private CategoryGridViewCustomAdapter mListAdapter;
    private List<AppointmentApptListModel> saveCategoryList;
    private AppointmentListClickListner appointmentListner;
    private int positionSecond = 0;
    private SharedPreferences appPreference;

    private List<AppointmentApptListModel> doctorSaveCategoryList;
    private List<AppointmentApptListModel> doctorCategoryList;
    private GridView gridview;
    private String GetItem;
    private AutoCompleteTextView actv;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    public static FooterViewHolder footerHolder;
    private Context context;
    private ArrayList<Integer> groupData;
    private AppUtilities appUtilities;
    public static int flagAppointmentListAdp = 0;
    public static int flagAppointmentListAdpShowReceipt = 0;
    private int orderUserIdData;
    private String receiptUrl;
    private int callCurrentState = 0;
    MyClinicGlobalClass globalClass;
    private String[] myImageNameList = new String[]{"First Visit", "Routine",
            "Follow-up", "Procedure/Vaccination"
            , "Dressing/Plaster", "Other"};
    private DashboardViewModel dashboardViewModel;
    private ApiGetPostMethodCalls globalApiCall;


    public AppointmentApptListAdapter(Context context, List<AppointmentApptListModel> appointmentApptListModelList,
                                      Activity activity, ArrayList<Integer> groupData, AppointmentListClickListner appointmentListner) {
        this.appointmentApptListModelList = appointmentApptListModelList;
        doctorSaveCategoryList = new ArrayList<AppointmentApptListModel>();
        doctorCategoryList = new ArrayList<AppointmentApptListModel>();
        this.activity = activity;
        this.context = context;
        this.appointmentListner = appointmentListner;
        this.groupData = groupData;
        this.appUtilities = new AppUtilities();
        flagAppointmentListAdp = 1;
        appPreference = activity.getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE);
        globalClass = (MyClinicGlobalClass) context.getApplicationContext();
        dashboardViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(DashboardViewModel.class);
        dashboardViewModel.init();
        globalApiCall = new ApiGetPostMethodCalls();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        if (i == TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_appointment_appt, viewGroup, false);
            context = viewGroup.getContext();
            return new MyViewHolder(view);

        } else if (i == TYPE_HEADER) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_appointment_appt, viewGroup, false);
            context = viewGroup.getContext();
            return new MyViewHolder(view);

        } else if (i == TYPE_FOOTER) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_path_orderview_footer,
                    viewGroup, false);
            return new FooterViewHolder(view);

        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder myViewHolder, final int i) {
        myViewHolder.itemView.setTag(appointmentApptListModelList.get(i));

        if (myViewHolder instanceof AppointmentApptListAdapter.MyViewHolder) {
            try {
                final MyViewHolder itemViewHolder = (MyViewHolder) myViewHolder;

                final AppointmentApptListModel appointmentApptListModel = appointmentApptListModelList.get(i);

                orderUserIdData = appointmentApptListModel.getOrderUserId();
                if (groupData.get(i) == 0) {
                    itemViewHolder.dateGroup.setVisibility(View.VISIBLE);
                    String date = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", appointmentApptListModel.getApptDate());
                    itemViewHolder.apptDate.setText(date);
                } else {
                    itemViewHolder.dateGroup.setVisibility(View.GONE);
                }

                itemViewHolder.patientName.setText(appointmentApptListModel.getPatientName());
                itemViewHolder.apptClinicName.setText(appointmentApptListModel.getClinicName());
                itemViewHolder.apptClinicAddress.setText(appointmentApptListModel.getClinicAddress());
                itemViewHolder.apptListCatAssignText.setText(appointmentApptListModel.getApptCatAssign());

                itemViewHolder.paymentText.setText("Received");
                itemViewHolder.paymentText.setTextColor(activity.getResources().getColor(R.color.colorGreen3));
                if (appointmentApptListModel.getPaymentStatus().equalsIgnoreCase("pending")) {
                    itemViewHolder.paymentText.setText("Pending");
                    itemViewHolder.paymentText.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                    itemViewHolder.apptListReceipt.setVisibility(View.GONE);
                    if (appointmentApptListModel.getApptMode() == 1) {
                        if (appointmentApptListModel.getSendPaymentNotification() == 1) {
                            if (appointmentApptListModel.getActivePast() == 1) {
                                itemViewHolder.apptListSendPaymentLink.setVisibility(View.VISIBLE);
                                itemViewHolder.paymentText.setEnabled(false);
                                String resendPaymentLink = "Resend payment link";
                                SpannableString resendPaymentLinkContent = new SpannableString(resendPaymentLink);
                                resendPaymentLinkContent.setSpan(new UnderlineSpan(), 0, resendPaymentLink.length(), 0);
                                itemViewHolder.apptListSendPaymentLink.setText(resendPaymentLinkContent);
                            } else {
                                itemViewHolder.apptListSendPaymentLink.setVisibility(View.VISIBLE);
                                itemViewHolder.paymentText.setEnabled(false);
                                itemViewHolder.apptListSendPaymentLink.setText("Payment link send");
                                itemViewHolder.apptListSendPaymentLink.setEnabled(false);

                            }
                        } else {
                            if (appointmentApptListModel.getActivePast() == 1) {

                                itemViewHolder.apptListSendPaymentLink.setVisibility(View.VISIBLE);
                                itemViewHolder.paymentText.setEnabled(true);
                                String sendPaymentLink = "Send payment link";
                                SpannableString sendPaymentLinkContent = new SpannableString(sendPaymentLink);
                                sendPaymentLinkContent.setSpan(new UnderlineSpan(), 0, sendPaymentLink.length(), 0);
                                itemViewHolder.apptListSendPaymentLink.setText(sendPaymentLinkContent);
                            } else {
                                itemViewHolder.apptListSendPaymentLink.setVisibility(View.GONE);
                                itemViewHolder.paymentText.setEnabled(true);

                            }
                        }

                    } else {
                        itemViewHolder.apptListSendPaymentLink.setVisibility(View.GONE);
                    }

                } else {
                    itemViewHolder.apptListReceipt.setVisibility(View.VISIBLE);
                    itemViewHolder.apptListSendPaymentLink.setVisibility(View.GONE);
                    if (appointmentApptListModel.getReceiptUrl().equalsIgnoreCase("")) {
                        String createReceipt = "Create Receipt";
                        SpannableString content = new SpannableString(createReceipt);
                        content.setSpan(new UnderlineSpan(), 0, createReceipt.length(), 0);
                        itemViewHolder.apptListReceipt.setText(content);
                        itemViewHolder.apptListReceipt.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                    } else {

                        String showReceipt = "Show Receipt";
                        SpannableString content = new SpannableString(showReceipt);
                        content.setSpan(new UnderlineSpan(), 0, showReceipt.length(), 0);
                        itemViewHolder.apptListReceipt.setText(content);
                        itemViewHolder.apptListReceipt.setTextColor(activity.getResources().getColor(R.color.colorPrimary));

                    }
                }

                //added on 15th auguest 2020


                itemViewHolder.apptAttendence.setText("");
//            Log.d("Attend Status", appointmentApptListModel.getApptAttendanceStatus() + "");
                switch (appointmentApptListModel.getApptAttendanceStatus()) {
                    case 0:
                        itemViewHolder.apptAttendence.setText("None");
                        break;

                    case 1:
                        itemViewHolder.apptAttendence.setText("Checked In");
                        break;

                    case 3:
                        itemViewHolder.apptAttendence.setText("In Consult");
                        break;

                    case 4:
                        itemViewHolder.apptAttendence.setText("Checkout");
                        break;
                }

                final Calendar c = Calendar.getInstance();
                String todaysDate = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
                todaysDate = appUtilities.changeDateFormat("yyyy-MM-dd", "yyyy-MM-dd", todaysDate);
//            Log.d("Todays Date", todaysDate);
                ((MyViewHolder) myViewHolder).iamLateIcon.setVisibility(View.INVISIBLE);
                if (appointmentApptListModel.getApptDate().equalsIgnoreCase(todaysDate)) {
                    ((MyViewHolder) myViewHolder).iamLateIcon.setVisibility(View.VISIBLE);
                }

                String date = appUtilities.changeDateFormat("hh:mm:ss", "h:mm aa", appointmentApptListModel.getApptTime());

                String currentString = date;
                String[] separated = currentString.split(":");
//            separated[0]; // this will contain "Fruit"
//            separated[1];

                if (Integer.parseInt(separated[0]) >= 12) {
                    // removeLastChar(date);
                    String dateOne = date.substring(0, date.length() - 2);

                    itemViewHolder.apptTime.setText(dateOne + "PM");

                } else {
                    itemViewHolder.apptTime.setText(date);

                }

                ((MyViewHolder) myViewHolder).apptAttendence.setClickable(true);
//            ((MyViewHolder) myViewHolder).apptListTypeAction.setClickable(true);
                ((MyViewHolder) myViewHolder).statusCard.setEnabled(true);
                ((MyViewHolder) myViewHolder).cancelAllAppt.setVisibility(View.VISIBLE);
                ((MyViewHolder) myViewHolder).apptListCompleteIcon.setVisibility(View.VISIBLE);
                //((MyViewHolder) myViewHolder).iamLateIcon.setVisibility(View.VISIBLE);
                ((MyViewHolder) myViewHolder).statusText.setText("Change/Update");
                ((MyViewHolder) myViewHolder).statusText.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                //for past appt
                if (appointmentApptListModel.getActivePast() == 2) {
                    ((MyViewHolder) myViewHolder).statusCard.setEnabled(false);
                    ((MyViewHolder) myViewHolder).cancelAllAppt.setVisibility(View.GONE);
                    ((MyViewHolder) myViewHolder).iamLateIcon.setVisibility(View.GONE);
                    ((MyViewHolder) myViewHolder).apptListCompleteIcon.setVisibility(View.GONE);

                    switch (appointmentApptListModel.getApptStatus()) {
                        case 3:
                            ((MyViewHolder) myViewHolder).statusText.setText("Consulted");
//                        ((MyViewHolder) myViewHolder).completeJoinIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_tick));
//                        ((MyViewHolder) myViewHolder).completeJoinIcon.setColorFilter(activity.getResources().getColor(R.color.colorSuccess), PorterDuff.Mode.SRC_IN);
                            ((MyViewHolder) myViewHolder).statusText.setTextColor(activity.getResources().getColor(R.color.colorGreen3));
                            break;

                        case 4:
                            ((MyViewHolder) myViewHolder).statusText.setText("Cancelled");
//                        ((MyViewHolder) myViewHolder).completeJoinIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_close));
//                        ((MyViewHolder) myViewHolder).completeJoinIcon.setColorFilter(activity.getResources().getColor(R.color.colorDanger), PorterDuff.Mode.SRC_IN);
                            ((MyViewHolder) myViewHolder).statusText.setTextColor(activity.getResources().getColor(R.color.colorDanger));
                            break;

                        case 5:
                            ((MyViewHolder) myViewHolder).statusText.setText("Cancelled by patient");
//                        ((MyViewHolder) myViewHolder).completeJoinIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_close));
//                        ((MyViewHolder) myViewHolder).completeJoinIcon.setColorFilter(activity.getResources().getColor(R.color.colorDanger), PorterDuff.Mode.SRC_IN);
                            ((MyViewHolder) myViewHolder).statusText.setTextColor(activity.getResources().getColor(R.color.colorDanger));
                            break;

                        case 6:
                            ((MyViewHolder) myViewHolder).statusText.setText("Patient no show");
//                        ((MyViewHolder) myViewHolder).completeJoinIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_close));
//                        ((MyViewHolder) myViewHolder).completeJoinIcon.setColorFilter(activity.getResources().getColor(R.color.colorDanger), PorterDuff.Mode.SRC_IN);
                            ((MyViewHolder) myViewHolder).statusText.setTextColor(activity.getResources().getColor(R.color.colorDanger));
                            break;

                        case 7:
                            ((MyViewHolder) myViewHolder).statusText.setText("Doctor no-show");
//                        ((MyViewHolder) myViewHolder).completeJoinIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_close));
//                        ((MyViewHolder) myViewHolder).completeJoinIcon.setColorFilter(activity.getResources().getColor(R.color.colorDanger), PorterDuff.Mode.SRC_IN);
                            ((MyViewHolder) myViewHolder).statusText.setTextColor(activity.getResources().getColor(R.color.colorDanger));
                            break;

                        case 8:
                            ((MyViewHolder) myViewHolder).statusText.setText("Cancelled by doctor");
                            ((MyViewHolder) myViewHolder).statusText.setTextColor(activity.getResources().getColor(R.color.colorDanger));
                            break;
                    }
                }

                ((MyViewHolder) myViewHolder).apptAttendence.setVisibility(View.VISIBLE);
                ((MyViewHolder) myViewHolder).apptAttendLabel.setVisibility(View.VISIBLE);
                ((MyViewHolder) myViewHolder).joinVideo.setVisibility(View.GONE);
                ((MyViewHolder) myViewHolder).addressLayout.setVisibility(View.VISIBLE);
                itemViewHolder.typeIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_hospital));
                if (appointmentApptListModel.getApptMode() == 1) {
                    //no attendance for video appt
                    ((MyViewHolder) myViewHolder).apptAttendence.setVisibility(View.GONE);
                    ((MyViewHolder) myViewHolder).apptAttendLabel.setVisibility(View.GONE);
                    ((MyViewHolder) myViewHolder).joinVideo.setVisibility(View.VISIBLE);
                    ((MyViewHolder) myViewHolder).addressLayout.setVisibility(View.GONE);
                    itemViewHolder.typeIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_video));

                    if (appointmentApptListModel.getActivePast() == 2) {
                        ((MyViewHolder) myViewHolder).joinVideo.setVisibility(View.GONE);
                    }
                }

                if (HomeFragment.caseChannelPresent) {
                    itemViewHolder.createDiscussion.setVisibility(View.VISIBLE);
                } else {
                    itemViewHolder.createDiscussion.setVisibility(View.GONE);
                }

                if (appointmentApptListModel.getPaymentType() == 1) {
//                itemViewHolder.apptListTypeAction.setText("Routine");
                    itemViewHolder.typeText.setText("Routine");
                } else if (appointmentApptListModel.getPaymentType() == 2) {
//                itemViewHolder.apptListTypeAction.setText("Follow-up");
                    itemViewHolder.typeText.setText("Follow-up");
                } else if (appointmentApptListModel.getPaymentType() == 3) {
//                itemViewHolder.apptListTypeAction.setText("Procedure/Vaccination");
                    itemViewHolder.typeText.setText("Procedure/Vaccination");
                } else if (appointmentApptListModel.getPaymentType() == 4) {
//                itemViewHolder.apptListTypeAction.setText("Dressing/Plaster");
                    itemViewHolder.typeText.setText("Dressing/Plaster");
                } else if (appointmentApptListModel.getPaymentType() == 5) {
//                itemViewHolder.apptListTypeAction.setText("Other");
                    itemViewHolder.typeText.setText("Other");
                } else if (appointmentApptListModel.getPaymentType() == 6) {
//                itemViewHolder.apptListTypeAction.setText("First Visit");
                    itemViewHolder.typeText.setText("First Visit");
                }
                itemViewHolder.paymentText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - Payment Card");

                        if (itemViewHolder.paymentText.getText().toString().equalsIgnoreCase("pending")) {

                            final Dialog dialog = new Dialog(activity);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.dailog_apointment_payment_status_new);
                            final Spinner spinner = (Spinner) dialog.findViewById(R.id.paymentModeSpinner);
                            final EditText userNameText = (EditText) dialog.findViewById(R.id.amountPaid);
                            final ImageView dailogArticleCancel = (ImageView) dialog.findViewById(R.id.dailogArticleCancel);
                            final TextView sendReminderText = (TextView) dialog.findViewById(R.id.sendReminderText);
                            final RelativeLayout sendSMSReminderButtonLayout = (RelativeLayout) dialog.findViewById(R.id.sendSMSReminderButtonLayout);
                            final RelativeLayout received = (RelativeLayout) dialog.findViewById(R.id.received);
                            final RelativeLayout receivedCreateReceipt = (RelativeLayout) dialog.findViewById(R.id.receivedCreateReceipt);
                            final RelativeLayout orSendReminderLayout = (RelativeLayout) dialog.findViewById(R.id.orSendReminderLayout);

                            if (appointmentApptListModel.getApptMode() == 3) {
                                orSendReminderLayout.setVisibility(View.GONE);
                            } else {
                                orSendReminderLayout.setVisibility(View.GONE);
                            }

                            if (appointmentApptListModel.getNetAmount() == 0) {
                                userNameText.setText("0");
                            } else {
                                int netAmount = appointmentApptListModel.getOrderAmount() - appointmentApptListModel.getDiscount();
                                userNameText.setText(String.valueOf(netAmount));

                            }

                            String str = "<a><font color='#000000'> Send an SMS reminder to </font> " + appointmentApptListModel.getPatientName() + " with payment link </a>";
                            sendReminderText.setText(Html.fromHtml(str));

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                                    android.R.layout.simple_spinner_item, activity.getResources()
                                    .getStringArray(R.array.paymentTypelistArray));
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                            dailogArticleCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    dialog.dismiss();
                                }
                            });

                            received.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (globalClass.isOnline()) {


                                        if (spinner.getSelectedItemPosition() == 0) {
                                            Toast.makeText(activity, "Please select payment mode", Toast.LENGTH_LONG).show();
                                        } else {
                                            dialog.dismiss();
                                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Payment Card - Only Pay Done");

                                            String paymentModespin = spinner.getSelectedItem().toString();
                                            String paymentMode = "";

                                            if (paymentModespin.equalsIgnoreCase("Cash")) {
                                                paymentMode = "Cash";
                                            } else if (paymentModespin.equalsIgnoreCase("Credit Card")) {
                                                paymentMode = "CC";
                                            }
                                            if (paymentModespin.equalsIgnoreCase("Debit Card")) {
                                                paymentMode = "DC";
                                            }
                                            if (paymentModespin.equalsIgnoreCase("Net Banking")) {
                                                paymentMode = "Net Banking";
                                            }

                                            String amountPaid = userNameText.getText().toString();
                                            updatePaymentStatus(amountPaid, appointmentApptListModel.getOrderId(), paymentMode, itemViewHolder, false, appointmentApptListModel);

                                        }
                                    } else {
                                        globalClass.noInternetConnection.showDialog(activity);
                                    }
                                }
                            });

                            receivedCreateReceipt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (globalClass.isOnline()) {

                                        if (spinner.getSelectedItemPosition() == 0) {
                                            Toast.makeText(activity, "Please select payment mode", Toast.LENGTH_LONG).show();
                                        } else {
                                            dialog.dismiss();
                                            String paymentModespin = spinner.getSelectedItem().toString();
                                            String paymentMode = "";
                                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Payment Card - Pay & Create Receipt");

                                            if (paymentModespin.equalsIgnoreCase("Cash")) {
                                                paymentMode = "Cash";
                                            } else if (paymentModespin.equalsIgnoreCase("Credit Card")) {
                                                paymentMode = "CC";
                                            }
                                            if (paymentModespin.equalsIgnoreCase("Debit Card")) {
                                                paymentMode = "DC";
                                            }
                                            if (paymentModespin.equalsIgnoreCase("Net Banking")) {
                                                paymentMode = "Net Banking";
                                            }

                                            String amountPaid = userNameText.getText().toString();
                                            updatePaymentStatus(amountPaid, appointmentApptListModel.getOrderId(), paymentMode, itemViewHolder, true, appointmentApptListModel);
                                        }

                                    } else {
                                        globalClass.noInternetConnection.showDialog(activity);
                                    }

                                }
                            });

                            sendSMSReminderButtonLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    dashboardViewModel.sendPaymentReminderDetails(activity, appointmentApptListModel.getAppointmentId()).observe((LifecycleOwner) activity, new Observer<String>() {
                                        @Override
                                        public void onChanged(String s) {
                                            try {

                                                JSONObject jsonObject = new JSONObject(s);
                                                if (jsonObject.getInt("status_code") == 200) {
                                                    JSONObject response = new JSONObject(s).getJSONObject("response");
                                                    int responseValue = response.getInt("response");
                                                    if (responseValue == 1) {
                                                        Toast.makeText(activity, "Payment reminder send successfully", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                    }
                                                } else {
                                                    ErrorHandlerClass.INSTANCE.errorHandler(activity, s);
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                            });
                            dialog.show();
                        }


                    }
                });

                itemViewHolder.apptListSendPaymentLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = "Your patient needs to complete the payment before consultation time";
                        showCustomDialog(text, appointmentApptListModel, itemViewHolder);
                    }
                });


                itemViewHolder.apptListReceipt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (itemViewHolder.apptListReceipt.getText().toString().equalsIgnoreCase("Show Receipt")) {
                            getReceiptUrl(appointmentApptListModel.getReceiptUrl());
                            ConfirmOrderActivity.confirmOrderFlag = 0;
                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Show Receipt");
                        } else {
                            ConfirmOrderActivity.confirmOrderFlag = 0;
                            createReceipt(appointmentApptListModel.getOrderId(), appointmentApptListModel, itemViewHolder);

                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Create Receipt");
                        }


                    }
                });

                if (AppointmentFragment.Companion.isStatusClicked()) {
                    if (itemViewHolder.details.getVisibility() == View.VISIBLE) {
                        AppointmentFragment.Companion.setStatusClicked(false);
                        itemViewHolder.details.setVisibility(View.GONE);
                        itemViewHolder.arrowIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_arrow_right));
//                        itemViewHolder.details.setVisibility(View.VISIBLE);
//                        itemViewHolder.arrowIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_arrow_down));
                    } else {
                        itemViewHolder.details.setVisibility(View.GONE);
                        itemViewHolder.arrowIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_arrow_right));
                    }
                } else {
                    if (itemViewHolder.details.getVisibility() == View.VISIBLE) {
                        itemViewHolder.details.setVisibility(View.VISIBLE);
                        itemViewHolder.arrowIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_arrow_down));
                    } else {
                        itemViewHolder.details.setVisibility(View.GONE);
                        itemViewHolder.arrowIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_arrow_right));
                    }
                }


                itemViewHolder.header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemViewHolder.details.getVisibility() == View.VISIBLE) {
                            itemViewHolder.details.setVisibility(View.GONE);
                            itemViewHolder.arrowIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_arrow_right));
                        } else {
                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Details");
                            itemViewHolder.details.setVisibility(View.VISIBLE);
                            itemViewHolder.arrowIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_arrow_down));
                            showGuide(5, i, itemViewHolder);
                        }
                    }
                });

                itemViewHolder.typeText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Type Card");
                        showDialog(activity, itemViewHolder, appointmentApptListModel.getAppointmentId());


                    }
                });

                itemViewHolder.apptListCatAssign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<AppointmentApptListModel> saveCategoryList;
                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - Patient Category Card");

                        saveCategoryList = new ArrayList<>();
                        getSavedCategory(appointmentApptListModel.getOrderUserId());
                        getDoctorCategory();
                        final ArrayAdapter<String> arrayadapter;
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        final LayoutInflater inflater = activity.getLayoutInflater();
                        final View inflator = inflater.inflate(R.layout.dailog_patien_category, null);
                        final Spinner spinner = (Spinner) inflator.findViewById(R.id.paymentModeSpinner);
                        final EditText userNameText = (EditText) inflator.findViewById(R.id.amountPaid);
                        final ImageView dailogArticleCancel = (ImageView) inflator.findViewById(R.id.dailogArticleCancel);
                        actv = (AutoCompleteTextView) inflator.findViewById(R.id.autoCompleteTextView);
                        final Button categorySave = (Button) inflator.findViewById(R.id.categorySaveButton);

                        gridview = (GridView) inflator.findViewById(R.id.gridView1);
                        builder.setView(inflator)
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        //otpLoading.dismiss();
                                    }
                                })
                        ;
                        final AlertDialog alertDialog = builder.create();
                        dailogArticleCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                alertDialog.dismiss();
                                //otpLoading.dismiss();
                            }
                        });

                        categorySave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (actv.getText().toString().length() == 0) {
                                    Toast.makeText(activity, "Please enter category", Toast.LENGTH_LONG).show();
                                } else {
                                    ZohoSalesIQ.Tracking.setCustomAction("ApptList - Saving Patient Category");
                                    GetItem = actv.getText().toString();
                                    int selectedCategoryId = 0;
                                    for (int i = 0; i < doctorCategoryList.size(); i++) {
                                        if (GetItem.equalsIgnoreCase(doctorCategoryList.get(i).getDoctorCategoryName())) {
                                            selectedCategoryId = doctorCategoryList.get(i).getDoctorCategoryId();
                                        } else {

                                        }
                                    }
                                    if (GetItem.isEmpty()) {
                                        //Toast.makeText(activity, "Item Added SuccessFully", Toast.LENGTH_LONG).show();
                                    } else {
                                        AppointmentApptListModel selected = new AppointmentApptListModel();
                                        selected.setCategoryName(GetItem);
                                        selected.setCategoryId(selectedCategoryId);
                                        doctorSaveCategoryList.add(doctorSaveCategoryList.size(), selected);
                                        mListAdapter.notifyDataSetChanged();
                                        //Toast.makeText(activity, "Item Added SuccessFully", Toast.LENGTH_LONG).show();
                                    }
                                    savePatientCategory(appointmentApptListModel.getOrderUserId());
                                    actv.setText("");

                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < CategoryGridViewCustomAdapter.result.size(); i++) {
                                        // JSONObject categoryObject = appointmentAssignCategory.getJSONObject(k);
                                        String str = CategoryGridViewCustomAdapter.result.get(i).getCategoryName();
                                        sb.append(str);
                                        sb.append(",");

                                    }
                                    sb.deleteCharAt(sb.length() - 1);
                                    itemViewHolder.apptListCatAssignText.setText(sb.toString());

                                }

                            }
                        });

                        //Creating the instance of ArrayAdapter containing list of fruit names
                        ArrayAdapter<AppointmentApptListModel> adapter = new ArrayAdapter<AppointmentApptListModel>
                                (activity, android.R.layout.select_dialog_item, doctorCategoryList);
                        //Getting the instance of AutoCompleteTextView
                        actv.setThreshold(1);//will start working from first character
                        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                        actv.setTextColor(Color.RED);
                        alertDialog.show();
                    }
                });

                ((MyViewHolder) myViewHolder).takeNotes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Take Note Card");
                        Intent intent = new Intent(activity, EMRActivity.class);
                        intent.putExtra("ApptId", appointmentApptListModel.getAppointmentId());
                        intent.putExtra("PatientId", appointmentApptListModel.getPatientId());
                        intent.putExtra("ApptMode", appointmentApptListModel.getApptMode());
                        intent.putExtra("ApptDate", appointmentApptListModel.getApptDate());
                        intent.putExtra("ApptTime", appointmentApptListModel.getApptTime());
                        intent.putExtra("PatientName", appointmentApptListModel.getPatientName());
                        activity.startActivity(intent);
                    }
                });

                ((MyViewHolder) myViewHolder).apptAttendence.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Attendance Card");
                        appointmentListner.onItemClick(view, "ShowAttendDialog", appointmentApptListModel);
                    }
                });

                //new ui logic
                ((MyViewHolder) myViewHolder).statusCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Status Card");
                        appointmentListner.onItemClick(view, "CancelAppt", appointmentApptListModel);
                    }
                });

                ((MyViewHolder) myViewHolder).setTypeAttend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                ((MyViewHolder) myViewHolder).cancelAllAppt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - All Appt Cancel Shortcut");
                        appointmentListner.onItemClick(view, "CancelAll", appointmentApptListModel);
                    }
                });

                ((MyViewHolder) myViewHolder).apptListCompleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - All Appt Complete Shortcut");
                        appointmentListner.onItemClick(view, "Complete", appointmentApptListModel);
                    }
                });


                ((MyViewHolder) myViewHolder).iamLateIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - All Appt Delay Intimation Shortcut");
                        appointmentListner.onItemClick(view, "IamLate", appointmentApptListModel);
                    }
                });


                ((MyViewHolder) myViewHolder).joinVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        appointmentListner.onItemClick(view, "JoinVideoCall", appointmentApptListModel);
                    }
                });

                ((MyViewHolder) myViewHolder).recordsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, PatientRecordActivity.class);
                        intent.putExtra("PatientId", appointmentApptListModel.getPatientId());
                        intent.putExtra("PatientName", appointmentApptListModel.getPatientName());
                        activity.startActivity(intent);
                    }
                });

                ((MyViewHolder) myViewHolder).header.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        showGuide(1, i, (MyViewHolder) myViewHolder);
                        ((MyViewHolder) myViewHolder).header.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

                ((MyViewHolder) myViewHolder).createDiscussion.setOnClickListener((View view) -> {
                    appointmentListner.onItemClick(view, "CreateDiscussion", appointmentApptListModel);
                });


            } catch (Exception e) {
                e.getMessage();
            }


        } else if (myViewHolder instanceof FooterViewHolder) {
            footerHolder = (FooterViewHolder) myViewHolder;
            if (AppointmentFragment.Companion.isMoreData()) {
                ((FooterViewHolder) myViewHolder).footerText.setVisibility(View.VISIBLE);
            } else {
                ((FooterViewHolder) myViewHolder).footerText.setVisibility(View.GONE);
            }
            footerHolder.footerText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    appointmentListner.onItemClick(view, "LOADMORE", null);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return appointmentApptListModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;

        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {

        if (position >= (appointmentApptListModelList.size() - 1) && appointmentApptListModelList.size() >= 50) { // && data.size() >= 10 && appointmentsActivity.isMoreData
            return true;
        } else {
            return false;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView patientName, apptTime, apptDate, apptAttendence, apptAttendLabel;
        public RelativeLayout header, dateGroup;
        public LinearLayout details, addressLayout;
        public ImageView arrowIcon, typeIcon;
        public TextView apptClinicName, apptClinicAddress;
        public TextView apptListCatAssignText;

        //new ui
        private RelativeLayout apptListCatAssign;
        private CardView setTypeAttend, payment, takeNotes, statusCard, joinVideo, recordsView, createDiscussion;
        private TextView typeText, paymentText, attendText, statusText;
        private ImageView iamLateIcon, cancelAllAppt, apptListCompleteIcon;
        private TextView apptListReceipt, apptListSendPaymentLink;
        private LinearLayout apptStatusLayoutGuidePt;

        public MyViewHolder(View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.apptListPatientName);
            apptTime = itemView.findViewById(R.id.apptListTime);
            header = itemView.findViewById(R.id.apptListHeaderLayout);
            details = itemView.findViewById(R.id.apptListDetailLayout);
            arrowIcon = itemView.findViewById(R.id.apptListArrowIcon);
            dateGroup = itemView.findViewById(R.id.apptListDateGroupLayout);
            apptDate = itemView.findViewById(R.id.apptListApptDate);
            apptClinicName = itemView.findViewById(R.id.apptClinicName);
            apptClinicAddress = itemView.findViewById(R.id.apptClinicAddress);
            typeIcon = itemView.findViewById(R.id.apptListTypeIcon);
            iamLateIcon = itemView.findViewById(R.id.apptListImLateIcon);
            cancelAllAppt = itemView.findViewById(R.id.apptListCancelIcon);
            apptListCompleteIcon = itemView.findViewById(R.id.apptListCompleteIcon);

            //new ui
            apptListCatAssign = itemView.findViewById(R.id.apptListCatAssign);
            setTypeAttend = itemView.findViewById(R.id.apptListSetTypeAttend);
            payment = itemView.findViewById(R.id.apptListPaymentCard);
            apptListCatAssignText = itemView.findViewById(R.id.apptListCatAssignText);
            takeNotes = itemView.findViewById(R.id.apptListAddNotes);
            apptAttendence = itemView.findViewById(R.id.apptListAttendAction);
            apptAttendLabel = itemView.findViewById(R.id.apptListAttendText);
            typeText = itemView.findViewById(R.id.apptListType);
            paymentText = itemView.findViewById(R.id.apptListPayment);
            apptListReceipt = itemView.findViewById(R.id.apptListReceipt);
            apptListSendPaymentLink = itemView.findViewById(R.id.apptListSendPaymentLink);
            statusCard = itemView.findViewById(R.id.apptListStatusCard);
            statusText = itemView.findViewById(R.id.apptListStatus);
            joinVideo = itemView.findViewById(R.id.apptListJoinVideo);
            recordsView = itemView.findViewById(R.id.apptListRecordViewCard);
            addressLayout = itemView.findViewById(R.id.apptAddressLayout);
            apptStatusLayoutGuidePt = itemView.findViewById(R.id.apptListStatusGuidePt);
            createDiscussion = itemView.findViewById(R.id.apptListDiscussionCard);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppointmentApptListModel cpu = (AppointmentApptListModel) view.getTag();
                }
            });
        }
    }


    private AdapterView.OnItemClickListener onItemClickListener(
            final MyViewHolder myViewHolder, final int appointmentId) {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (position == 0) {
                    selectedApptType = "First Visit";
                    apptId = 6;
                    myViewHolder.typeText.setText(Html.fromHtml("First Visit"));

                } else if (position == 1) {
                    selectedApptType = "Routine";
                    apptId = 1;
                    myViewHolder.typeText.setText(Html.fromHtml("Routine"));

                } else if (position == 2) {
                    selectedApptType = "Follow-up";
                    apptId = 2;
                    myViewHolder.typeText.setText(Html.fromHtml("Follow-up"));

                }
                if (position == 3) {
                    selectedApptType = "Procedure/Vaccination";
                    apptId = 3;
                    myViewHolder.typeText.setText(Html.fromHtml("Procedure/Vaccination"));

                }
                if (position == 4) {
                    selectedApptType = "Dressing/Plaster";
                    apptId = 4;
                    myViewHolder.typeText.setText(Html.fromHtml("Dressing/Plaster"));

                }
                if (position == 5) {
                    selectedApptType = "Other";
                    apptId = 5;
                    myViewHolder.typeText.setText(Html.fromHtml("Other"));

                }
                updateAppointmentType(selectedApptType, apptId, appointmentId);
                dismissPopup();
            }

        };
    }

    private void dismissPopup() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    /**
     * show popup window method reuturn PopupWindow
     */
    private PopupWindow popupWindowsort(MyViewHolder myViewHolder, int appointmentId) {

        // initialize a pop up window type
        popupWindow = new PopupWindow(activity);

        durationList = new ArrayList<String>();
//        durationList.add("All");
        durationList.add("First Visit");
        durationList.add("Routine");
        durationList.add("Follow-up");
        durationList.add("Procedure/Vaccination");
        durationList.add("Dressing/Plaster");
        durationList.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1,
                durationList);
        // the drop down list is a list view
        ListView listViewSort = new ListView(activity);

        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(adapter);

        // set on item selected
        listViewSort.setOnItemClickListener(onItemClickListener(myViewHolder, appointmentId));

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(350);
        popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.color.colorGrey1));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the list view as pop up window content
        popupWindow.setContentView(listViewSort);

        return popupWindow;
    }


    public void updateAppointmentType(String selectedApptType, int apptTypeId,
                                      int apppointmentId) {
        otpLoading = new ProgressDialog(activity);
        otpLoading.setMessage(activity.getResources().getString(R.string.wait_while_we_fetching));
        otpLoading.setTitle(activity.getResources().getString(R.string.fetching));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();
        final String URL = ApiUrls.updateAppointmentType;

        try {
            jsonValue = new JSONObject();
            jsonValue.put("id", apppointmentId);
            jsonValue.put("appt_type", apptTypeId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        globalApiCall.volleyApiRequestData(URL, Request.Method.POST, jsonValue, context, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    //Process os success response
                    otpLoading.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    otpLoading.dismiss();
                }
            }

            @Override
            public void onError(String err) {
                otpLoading.dismiss();
            }
        });
    }


    public void updatePaymentStatus(String paidAmount, int orderId, String paymentMode, final MyViewHolder itemViewHolder, final boolean isGeneratedReceipt, final AppointmentApptListModel appointmentApptListModel) {
        otpLoading = new ProgressDialog(activity);
        otpLoading.setMessage(activity.getResources().getString(R.string.wait_while_we_updating));
        otpLoading.setTitle(activity.getResources().getString(R.string.updating));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();
        final String URL = ApiUrls.updatePaymentStatus;

        try {
            jsonValue = new JSONObject();
            jsonValue.put("order_net_amount", Integer.parseInt(paidAmount));
            jsonValue.put("order_id", orderId);
            jsonValue.put("order_payment_mode", paymentMode);
            jsonValue.put("isGenerateReport", isGeneratedReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        globalApiCall.volleyApiRequestData(URL, Request.Method.POST, jsonValue, context, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    //Process os success response
                    otpLoading.dismiss();
                    JSONObject response = new JSONObject(result);
                    JSONObject rootObj = response.getJSONObject("response");
                    appointmentApptListModel.setPaymentStatus(rootObj.getString("payment_status"));

                    itemViewHolder.paymentText.setText("Received");
                    itemViewHolder.paymentText.setTextColor(activity.getResources().getColor(R.color.colorGreen3));

                    itemViewHolder.apptListReceipt.setVisibility(View.VISIBLE);
                    if (isGeneratedReceipt) {
                        Object intervention = rootObj.get("receipt");
                        if (intervention instanceof JSONArray) {

                        } else if (intervention instanceof JSONObject) {
                            // It's an object
                            JSONObject receiptObject = rootObj.getJSONObject("receipt");
                            appointmentApptListModel.setReceiptUrl(receiptObject.getString("public_url"));
                        } else {
                            appointmentApptListModel.setReceiptUrl("");
                        }

                        String showReceipt = "Show Receipt";
                        SpannableString content = new SpannableString(showReceipt);
                        content.setSpan(new UnderlineSpan(), 0, showReceipt.length(), 0);
                        itemViewHolder.apptListReceipt.setText(content);
                        itemViewHolder.apptListReceipt.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                    } else {

                        String createReceipt = "Create Receipt";
                        SpannableString content = new SpannableString(createReceipt);
                        content.setSpan(new UnderlineSpan(), 0, createReceipt.length(), 0);
                        itemViewHolder.apptListReceipt.setText(content);
                        itemViewHolder.apptListReceipt.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    otpLoading.dismiss();
                }
            }

            @Override
            public void onError(String err) {
                otpLoading.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(context, err);
            }
        });
    }


    private List<String> getData() {
        List<String> dataList = new ArrayList<String>();
        dataList.add("Fashion Men");
        dataList.add("Fashion Women");
        dataList.add("Baby");
        dataList.add("Kids");
        dataList.add("Electronics");
        dataList.add("Appliance");
        dataList.add("Travel");
        dataList.add("Bags");
        dataList.add("FootWear");
        dataList.add("Jewellery");
        dataList.add("Sports");
        dataList.add("Electrical");
        dataList.add("Sports Kids");
        return dataList;
    }

    String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear", "aa", "ab", "ac"};


    String[] item = new String[]{
            "ONE",
            "TWO",
            "THREE",
            "FOUR",
            "FIVE",
            "SIX"
    };

    public static String[] prgmNameList = new String[]{"Let Us C", "c++", "JAVA", "Jsp", "Microsoft .Net", "Android", "PHP", "Jquery", "JavaScript"};

    public void getSavedCategory(int orderUserId) {
        final String URL = ApiUrls.getSavedCategory + "?p_id=" + orderUserId;

        globalApiCall.volleyApiRequestData(URL, Request.Method.GET, null, context, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                doctorSaveCategoryList.clear();
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray categoryArray = response.getJSONArray("response");
                    for (int j = 0; j < categoryArray.length(); j++) {
                        JSONObject categoryObject = categoryArray.getJSONObject(j);
                        AppointmentApptListModel model = new AppointmentApptListModel();
                        model.setCategoryId(categoryObject.getInt("id"));
                        model.setCategoryName(categoryObject.getString("category_name"));
                        doctorSaveCategoryList.add(model);

                    }
                    mListAdapter = new CategoryGridViewCustomAdapter(activity, doctorSaveCategoryList, new CategoryGridViewClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            savePatientCategory(orderUserIdData);

                        }
                    });
                    gridview.setAdapter(mListAdapter);
                    mListAdapter.notifyDataSetChanged();


                } catch (Exception e) {
                    e.printStackTrace();
                    otpLoading.dismiss();
                }
            }

            @Override
            public void onError(String err) {
                otpLoading.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(context, err);
            }
        });
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView footerText;
        public View bottomSpace;

        public FooterViewHolder(View view) {
            super(view);
            footerText = view.findViewById(R.id.footer_text);
            bottomSpace = view.findViewById(R.id.bottomSpace);

        }
    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public View headerView;

        public HeaderViewHolder(View v) {
            super(v);
            headerView = v;

        }
    }


    public void getDoctorCategory() {
        final String URL = ApiUrls.getDoctorCategory;
        globalApiCall.volleyApiRequestData(URL, Request.Method.GET, null, context, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                doctorCategoryList.clear();
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray categoryArray = response.getJSONArray("response");
                    for (int j = 0; j < categoryArray.length(); j++) {
                        JSONObject categoryObject = categoryArray.getJSONObject(j);
                        AppointmentApptListModel model = new AppointmentApptListModel();
                        model.setDoctorCategoryId(categoryObject.getInt("id"));
                        model.setDoctorCategoryName(categoryObject.getString("category_name"));
                        doctorCategoryList.add(model);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    otpLoading.dismiss();
                }
            }

            @Override
            public void onError(String err) {
                otpLoading.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(context, err);
            }
        });
    }


    public void savePatientCategory(int patientId) {
        final String URL = ApiUrls.savePatientCategory;
        try {
            JSONArray categoryExistArray = new JSONArray();
            JSONArray categoryInsertArray = new JSONArray();
            for (int i = 0; i < doctorCategoryList.size(); i++) {
                if (GetItem != null) {
                    if (GetItem.equalsIgnoreCase(doctorCategoryList.get(i).getDoctorCategoryName())) {
                        categoryInsertArray = new JSONArray();
                        break;
                    }
                } else {
                    if (GetItem != null) {
                        if (GetItem.isEmpty()) {
                            categoryInsertArray = new JSONArray();

                        } else {
                            categoryInsertArray = new JSONArray();
                            categoryInsertArray.put(GetItem.toString());

                        }
                    } else {
                        categoryInsertArray = new JSONArray();

                    }

                }
            }

            for (int j = 0; j < doctorSaveCategoryList.size(); j++) {

                if (doctorSaveCategoryList.get(j).getCategoryId() == 0) {
                    categoryInsertArray = new JSONArray();
                    categoryInsertArray.put(GetItem.toString());
                } else {
                    JSONObject saveCategoryObject = new JSONObject();
                    saveCategoryObject.put("id", doctorSaveCategoryList.get(j).getCategoryId());
                    saveCategoryObject.put("doctor_id", ApiUrls.doctorId);
                    saveCategoryObject.put("category_name", doctorSaveCategoryList.get(j).getCategoryName());
                    categoryExistArray.put(saveCategoryObject);
                }
            }
            jsonValue = new JSONObject();
            jsonValue.put("doctor_id", ApiUrls.doctorId);
            jsonValue.put("exists", categoryExistArray);
            jsonValue.put("insert", categoryInsertArray);
            jsonValue.put("p_id", patientId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        globalApiCall.volleyApiRequestData(URL, Request.Method.POST, jsonValue, context, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    Toast.makeText(activity, "Saved successfully", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    otpLoading.dismiss();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(context, err);
            }
        });
    }

    public void showDialog(Activity activity, final MyViewHolder myViewHolder, final int appointmentId) {

        final Dialog dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_summary_listview);

        ImageView cancelButtonDilog = (ImageView) dialog.findViewById(R.id.dailogArticleCancel);
        cancelButtonDilog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        ListView listView = (ListView) dialog.findViewById(R.id.listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, R.layout.list_item_dashboard_summary, R.id.tv, myImageNameList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    selectedApptType = "First Visit";
                    apptId = 6;
                    myViewHolder.typeText.setText(Html.fromHtml("First Visit"));

                } else if (position == 1) {
                    selectedApptType = "Routine";
                    apptId = 1;
                    myViewHolder.typeText.setText(Html.fromHtml("Routine"));

                } else if (position == 2) {
                    selectedApptType = "Follow-up";
                    apptId = 2;
                    myViewHolder.typeText.setText(Html.fromHtml("Follow-up"));

                }
                if (position == 3) {
                    selectedApptType = "Procedure/Vaccination";
                    apptId = 3;
                    myViewHolder.typeText.setText(Html.fromHtml("Procedure/Vaccination"));

                }
                if (position == 4) {
                    selectedApptType = "Dressing/Plaster";
                    apptId = 4;
                    myViewHolder.typeText.setText(Html.fromHtml("Dressing/Plaster"));

                }
                if (position == 5) {
                    selectedApptType = "Other";
                    apptId = 5;
                    myViewHolder.typeText.setText(Html.fromHtml("Other"));

                }
                ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Type Update");
                updateAppointmentType(selectedApptType, apptId, appointmentId);


                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private void getReceiptUrl(String url) {


        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }


    private void createReceipt(int orderId, final AppointmentApptListModel appointmentApptListModel, final MyViewHolder itemViewHolder) {
        String url = ApiUrls.createReceipt + "?order_id=" + orderId;

        otpLoading = new ProgressDialog(activity);
        otpLoading.setMessage(activity.getResources().getString(R.string.please_wait));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();

        globalApiCall.volleyApiRequestData(url, Request.Method.GET, null, context, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONObject rootObj = response.getJSONObject("response");
                    receiptUrl = rootObj.getString("public_url");

                    appointmentApptListModel.setReceiptUrl(receiptUrl);

                    String showReceipt = "Show Receipt";
                    SpannableString content = new SpannableString(showReceipt);
                    content.setSpan(new UnderlineSpan(), 0, showReceipt.length(), 0);
                    itemViewHolder.apptListReceipt.setText(content);
                    itemViewHolder.apptListReceipt.setTextColor(activity.getResources().getColor(R.color.colorPrimary));

                    otpLoading.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    otpLoading.dismiss();
                }
            }

            @Override
            public void onError(String err) {
                otpLoading.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(context, err);
            }
        });
    }


    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 2);
    }

    private void showGuide(int section, final int position, final AppointmentApptListAdapter.MyViewHolder myViewHolder) {
        if (position == 0) {
            switch (section) {
                case 1:

                    new MaterialIntroView.Builder((Activity) context)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("Your appointment list shows up here.\n\nClick on any appointment for more details and actions")
                            //.setInfoText("Get access to all your day to day appointments for every patients")
                            .setShape(ShapeType.RECTANGLE)
                            .setTarget(myViewHolder.header)
                            .setUsageId("intro_appointment_details") //THIS SHOULD BE UNIQUE ID
                            .setListener(new MaterialIntroListener() {
                                @Override
                                public void onUserClicked(String materialIntroViewId) {
                                    showGuide(2, position, myViewHolder);
                                    SharedPreferences.Editor editor = appPreference.edit();
                                    editor.putBoolean("ApptDetails", true);
                                    editor.commit();

                                }
                            })
                            .show();


//                    }
                    break;

                case 2:
                    if (myViewHolder.iamLateIcon.getVisibility() == View.VISIBLE) {

                        new MaterialIntroView.Builder((Activity) context)
                                .enableDotAnimation(true)
                                .enableIcon(false)
                                .dismissOnTouch(true)
                                .setFocusGravity(FocusGravity.CENTER)
                                .setFocusType(Focus.NORMAL)
                                .setDelayMillis(50)
                                .enableFadeAnimation(true)
                                .setInfoText("Let your patients know that you are late for your appointments")
                                .setShape(ShapeType.CIRCLE)
                                .setTarget(myViewHolder.iamLateIcon)
                                .setUsageId("intro_appointment_lateIcon") //THIS SHOULD BE UNIQUE ID
                                .setListener(new MaterialIntroListener() {
                                    @Override
                                    public void onUserClicked(String materialIntroViewId) {
                                        showGuide(3, position, myViewHolder);
                                    }
                                })
                                .show();


                    } else {
                        showGuide(3, position, myViewHolder);
                    }
                    break;

                case 3:

                    new MaterialIntroView.Builder((Activity) context)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("Click here to mark all appointments for the day as Complete")
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(myViewHolder.apptListCompleteIcon)
                            .setUsageId("intro_appointment_bulkComplete") //THIS SHOULD BE UNIQUE ID
                            .setListener(new MaterialIntroListener() {
                                @Override
                                public void onUserClicked(String materialIntroViewId) {
                                    showGuide(4, position, myViewHolder);
                                }
                            })
                            .show();


                    break;

                case 4:

                    new MaterialIntroView.Builder((Activity) context)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("Click here to cancel all appointments for the day")
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(myViewHolder.cancelAllAppt)
                            .setUsageId("intro_appointment_bulkCancel") //THIS SHOULD BE UNIQUE ID
                            .setListener(new MaterialIntroListener() {
                                @Override
                                public void onUserClicked(String materialIntroViewId) {
//                                    showGuide(2, position, myViewHolder);
//                                    appointmentListner.onItemClick(view, "ShowGuide", null);
                                    //sending broadcast
                                    Intent intent = new Intent(AppointmentFragment.CUSTOM_BROADCAST_ACTION);
                                    intent.putExtra("Activity", "AppointmentList");
                                    /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/
                                    if (activity != null) {
                                        intent.setPackage(activity.getPackageName());
                                        activity.sendBroadcast(intent);
                                    }
                                }
                            })
                            .show();

                    break;

                case 5:
                    if (!appPreference.getBoolean("ApptUpdate", false)) {
                        new MaterialIntroView.Builder((Activity) context)
                                .enableDotAnimation(true)
                                .enableIcon(false)
                                .dismissOnTouch(true)
                                .setFocusGravity(FocusGravity.CENTER)
                                .setFocusType(Focus.NORMAL)
                                .setDelayMillis(50)
                                .enableFadeAnimation(true)
                                .setInfoText("View & update payment status, check-clinicplus status, appointment category, patient records and appointment status")
                                .setShape(ShapeType.RECTANGLE)
                                .setTarget(myViewHolder.apptStatusLayoutGuidePt)
                                .setUsageId("intro_appointment_status") //THIS SHOULD BE UNIQUE ID
                                .setListener(new MaterialIntroListener() {
                                    @Override
                                    public void onUserClicked(String materialIntroViewId) {
//                                    showGuide(2, position, myViewHolder);
                                        SharedPreferences.Editor editor = appPreference.edit();
                                        editor.putBoolean("ApptUpdate", true);
                                        editor.commit();
                                    }
                                })
                                .show();


                    }
                    break;
            }
        }
    }

    private void showCustomDialog(String dialogTxt, AppointmentApptListModel appointmentApptListModel, MyViewHolder itemViewHolder) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_send_payment_reminder, viewGroup, false);
        TextView yes = dialogView.findViewById(R.id.yes);
        TextView no = dialogView.findViewById(R.id.no);
        TextView dialogText = dialogView.findViewById(R.id.dialogText);
        dialogText.setText(dialogTxt);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                dashboardViewModel.sendPaymentReminderDetails(activity, appointmentApptListModel.getAppointmentId()).observe((LifecycleOwner) activity, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        try {

                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.getInt("status_code") == 200) {
                                JSONObject response = new JSONObject(s).getJSONObject("response");
                                int responseValue = response.getInt("response");
                                if (responseValue == 1) {
                                    Toast.makeText(activity, "Patient has been notified successfully.", Toast.LENGTH_SHORT).show();
                                    if (itemViewHolder.apptListSendPaymentLink.getText().toString().equalsIgnoreCase("Send payment link")) {
                                        itemViewHolder.apptListSendPaymentLink.setVisibility(View.VISIBLE);
                                        itemViewHolder.paymentText.setEnabled(false);
                                        String resendPaymentLink = "Resend payment link";
                                        SpannableString resendPaymentLinkContent = new SpannableString(resendPaymentLink);
                                        resendPaymentLinkContent.setSpan(new UnderlineSpan(), 0, resendPaymentLink.length(), 0);
                                        itemViewHolder.apptListSendPaymentLink.setText(resendPaymentLinkContent);
                                    }
                                } else {
                                }
                            } else {
                                ErrorHandlerClass.INSTANCE.errorHandler(activity, s);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }

}
