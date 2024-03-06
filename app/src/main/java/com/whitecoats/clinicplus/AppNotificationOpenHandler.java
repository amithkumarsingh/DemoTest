//package com.whitecoats.clinicplus;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.telephony.PhoneStateListener;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.widget.Toast;
//
////import com.caregivers.in.patientsharedrecords.PatientSharedRecordsActivity;
//import androidx.activity.result.ActivityResultLauncher;
//
//import com.onesignal.OSNotificationAction;
//import com.onesignal.OSNotificationOpenResult;
//import com.onesignal.OneSignal;
//import com.whitecoats.clinicplus.constants.AppConstants;
//import com.whitecoats.clinicplus.patientsharedrecords.PatientSharedRecordsActivity;
//import com.whitecoats.clinicplus.utils.ShowAlertDialog;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//public class AppNotificationOpenHandler implements OneSignal.NotificationOpenedHandler {
//
//    private Context activity;
//    private int callCurrentState = 0;
//    public static boolean isNotificationOpen = false;
//    private SharedPreferences sharedpreferences;
//    private TelephonyManager telephonyManager;
//    private PhoneStateListener callStateListener;
//    private ActivityResultLauncher<String> requestPermissionLauncher;
//
//    public AppNotificationOpenHandler(Context activity) {
//        this.activity = activity;
//        sharedpreferences = this.activity.getSharedPreferences(AppConstants.appSharedPref, Context.MODE_PRIVATE);
//    }
//
//    @Override
//    public void notificationOpened(OSNotificationOpenResult result) {
//
//        OSNotificationAction.ActionType actionType = result.action.type;
//        JSONObject data = result.notification.payload.additionalData;
//        if (data != null) {
//            Log.d("Notification Data", data.toString());
//            if (JoinVideoActivity.isVideoStart) {
//                isNotificationOpen = true;
//            }
//            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
//                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
//
//                if (result.action.actionID.equalsIgnoreCase("chat_capture")) {
////                    Log.d("Opening Chat", "**********************");
//                    Intent intent = new Intent(activity, ChatRoomActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    intent.putExtra("chatId", Integer.parseInt(data.optString("chatId")));
//                    intent.putExtra("patientName", "");
//                    intent.putExtra("ChatType", "Active");
//                    intent.putExtra("recipientId", Integer.parseInt(data.optString("userId")));
//                    activity.startActivity(intent);
//                } else if (result.action.actionID.equalsIgnoreCase("view_capture")) {
//                    try {
//                        JSONArray catid = data.optJSONArray("catIds");
//                        Intent intent = new Intent(activity, PatientRecordViewActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("CategoryId", catid.getInt(0));
//                        intent.putExtra("CategoryName", "");
//                        intent.putExtra("PatientId", Integer.parseInt(data.optString("user_id")));
//                        activity.startActivity(intent);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else if (result.action.actionID.equalsIgnoreCase("join_capture")) {
//
//                    telephonyManager =
//                            (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
//                    callStateListener = new PhoneStateListener() {
//                        public void onCallStateChanged(int state, String incomingNumber) {
//                            if (state == TelephonyManager.CALL_STATE_RINGING) {
//                                callCurrentState = 1;
//                                Toast.makeText(activity.getApplicationContext(), "You can't place a video call if you're already on a phone call.",
//                                        Toast.LENGTH_LONG).show();
//                            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
//                                callCurrentState = 2;
//                                Toast.makeText(activity.getApplicationContext(), "You can't place a video call if you're already on a phone call.",
//                                        Toast.LENGTH_LONG).show();
//                            } else {
//                                if (callCurrentState == 1 || callCurrentState == 2) {
//                                    callCurrentState = 0;
//                                } else {
//                                    //checking if the calling activity is open
//                                    if (sharedpreferences.getBoolean("InVideoCalling", false)) {
//                                        Intent local = new Intent();
//                                        local.setAction("EndCallingVideo");
//                                        activity.sendBroadcast(local);
//                                    }
//
//                                    Intent intent = new Intent(activity, JoinVideoActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.putExtra("AppointmentId", Integer.parseInt(data.optString("apptId")));
//                                    activity.startActivity(intent);
//                                }
//                            }
//                        }
//                    };
//                    if (Build.VERSION.SDK_INT >= 31) {
//                        if (activity.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
//                                != PackageManager.PERMISSION_GRANTED) {
//                            new ShowAlertDialog().showPopupToMovePermissionPage(activity);
//                        } else {
//                            telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//                        }
//                    } else {
//                        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//                    }
//                }
//            } else {
//                if (data.optString("push_type").equalsIgnoreCase("user_chat")) {
//                    Intent intent = new Intent(activity, ChatRoomActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("chatId", Integer.parseInt(data.optString("chatId")));
//                    intent.putExtra("patientName", "");
//                    intent.putExtra("ChatType", "Active");
//                    intent.putExtra("recipientId", Integer.parseInt(data.optString("userId")));
//                    activity.startActivity(intent);
//                } else if (data.optString("push_type").equalsIgnoreCase("user_share")) {
//                    try {
//                        JSONArray catid = data.optJSONArray("catIds");
//                        Intent intent = new Intent(activity, PatientSharedRecordsActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
////                        intent.putExtra("CategoryId", catid.getInt(0));
////                        intent.putExtra("CategoryName", "");
////                        intent.putExtra("recordIdArray", data.optJSONArray("recIds").toString());
//                        intent.putExtra("RecordsDate", data.optString("date"));
//                        intent.putExtra("PatientName", data.optString("user_name"));
//                        intent.putExtra("PatientId", Integer.parseInt(data.optString("user_id")));
//                        intent.putExtra("FromNotification", true);
//                        activity.startActivity(intent);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else if (data.optString("push_type").equalsIgnoreCase("user_push")) {
//
//                    telephonyManager =
//                            (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
//                    callStateListener = new PhoneStateListener() {
//                        public void onCallStateChanged(int state, String incomingNumber) {
//                            if (state == TelephonyManager.CALL_STATE_RINGING) {
//                                callCurrentState = 1;
//                                Toast.makeText(activity.getApplicationContext(), "You can't place a video call if you're already on a phone call.",
//                                        Toast.LENGTH_LONG).show();
//                            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
//                                callCurrentState = 2;
//                                Toast.makeText(activity.getApplicationContext(), "You can't place a video call if you're already on a phone call.",
//                                        Toast.LENGTH_LONG).show();
//                            } else {
//                                if (callCurrentState == 1 || callCurrentState == 2) {
//                                    callCurrentState = 0;
//                                } else {
//                                    Intent intent = new Intent(activity, JoinVideoActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.putExtra("AppointmentId", Integer.parseInt(data.optString("apptId")));
//                                    activity.startActivity(intent);
//                                }
//                            }
//                        }
//                    };
//                    if (Build.VERSION.SDK_INT >= 31) {
//                        if (activity.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
//                                != PackageManager.PERMISSION_GRANTED) {
//                            new ShowAlertDialog().showPopupToMovePermissionPage(activity);
//                        } else {
//                            telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//                        }
//                    } else {
//                        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//                    }
//                }
//            }
//        }
//    }
//}
