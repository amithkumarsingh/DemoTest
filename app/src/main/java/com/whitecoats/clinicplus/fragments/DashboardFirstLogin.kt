package com.whitecoats.clinicplus.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.SettingsActivity
import com.whitecoats.clinicplus.SettingsFormActivity
import com.whitecoats.clinicplus.activities.AddPatientActivity
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import org.json.JSONObject

class DashboardFirstLogin : Fragment() {
    private var addPatient: Button? = null
    private var setupPractice: Button? = null
    private var setupProfile: Button? = null
    private var setupPreferences: Button? = null
    private var first_patient_card: CardView? = null
    private var practice_card: CardView? = null
    private var search_layout: RelativeLayout? = null
    private var dashboardViewModel: DashboardViewModel? = null
    private var loginDocId = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Defines the xml file for the fragment
        val view = inflater.inflate(R.layout.fragment_dashboard_first_login, parent, false)
        addPatient = view.findViewById(R.id.add_first_patient_button)
        setupPractice = view.findViewById(R.id.setup_practice_button)
        first_patient_card = view.findViewById(R.id.first_patient_cardView)
        practice_card = view.findViewById(R.id.setup_practice_cardView)
        search_layout = view.findViewById(R.id.search_layout)
        setupProfile = view.findViewById(R.id.setup_profile_button)
        setupPreferences = view.findViewById(R.id.setup_preferences_button)
        loginDocId = ApiUrls.doctorId
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        dashboardViewModel!!.init()
        if (arguments != null) {
            val statusReponse = requireArguments().getString("response_string")
            processStatusResponse(statusReponse)
        } else {
            dashboardViewModel!!.getStatusString(activity)
                .observe(viewLifecycleOwner
                ) { s -> processStatusResponse(s) }
        }
        addPatient!!.setOnClickListener {
            val addPatientIntent = Intent(context, AddPatientActivity::class.java)
            startActivity(addPatientIntent)
        }
        setupPractice!!.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                loginDocId,
                getString(R.string.DashboardSetupPractices),
                null
            )
            val intent = Intent(context, SettingsFormActivity::class.java)
            intent.putExtra("FormType", 7)
            intent.putExtra("Title", resources.getString(R.string.service_setup))
            startActivity(intent)
        }
        setupProfile!!.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                loginDocId,
                getString(R.string.DashboardProfile),
                null
            )
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
        setupPreferences!!.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                loginDocId,
                getString(R.string.DashboardPreference),
                null
            )
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    private fun processStatusResponse(s: String?) {
        Log.i("first login res", s!!)
        try {
            val response = JSONObject(s)
            val statusObj = response.getJSONObject("response").getJSONObject("response")
                .getJSONObject("general_info")
            if (statusObj.getInt("is_practice_added") == 1) {
                practice_card!!.visibility = View.GONE
            } else {
                practice_card!!.visibility = View.VISIBLE
            }
            if (statusObj.getInt("is_patient_added") == 1) {
                first_patient_card!!.visibility = View.GONE
                search_layout!!.visibility = View.VISIBLE
            } else {
                first_patient_card!!.visibility = View.VISIBLE
                search_layout!!.visibility = View.GONE
            }
            //general upshots events
            if (statusObj.getInt("is_patient_added") == 1) {
                /*HashMap<String, Object> data = new HashMap<>();
                        data.put("VideoSetUpComplete", "VideoSetUpComplete");*/
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.PracticeSetUpComplete),
                    null
                )
            } else if (statusObj.getInt("is_practice_added") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.NoPracticeSetUp),
                    null
                )
            }
            if (statusObj.getInt("is_clinic_practice_added") == 1) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.ClinicSetUpComplete),
                    null
                )
            } else if (statusObj.getInt("is_clinic_practice_added") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.NoClinicSetUp),
                    null
                )
            }
            if (statusObj.getInt("is_video_practice_added") == 1) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.VideoSetUpComplete),
                    null
                )
            } else if (statusObj.getInt("is_video_practice_added") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.NoVideoSetUp),
                    null
                )
            }
            if (statusObj.getInt("is_chat_practice_added") == 1) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.ChatSetUpComplete),
                    null
                )
            } else if (statusObj.getInt("is_chat_practice_added") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.NoChatSetUp),
                    null
                )
            }
            if (statusObj.getInt("is_instant_video_practice_added") == 1) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.InstantVideoSetUpComplete),
                    null
                )
            } else if (statusObj.getInt("is_instant_video_practice_added") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.NoInstantVideoSetUp),
                    null
                )
            }
            if (statusObj.getInt("has_merchant_account") == 1) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.MerchantAccountExists),
                    null
                )
            } else if (statusObj.getInt("has_merchant_account") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.NoMerchantAccount),
                    null
                )
            }
            if (statusObj.getInt("is_bank_account_verified") == 1) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.BankAccountVerified),
                    null
                )
            } else if (statusObj.getInt("is_bank_account_verified") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.BankAccountNotVerified),
                    null
                )
            }
            if (statusObj.getInt("is_patient_added") == 1) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.AddPatientComplete),
                    null
                )
            } else if (statusObj.getInt("is_patient_added") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.NoPatientAdded),
                    null
                )
            }
            if (statusObj.getInt("is_there_appt") == 1) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.BookAppointmentComplete),
                    null
                )
            } else if (statusObj.getInt("is_there_appt") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.NoAppointmentBooked),
                    null
                )
            }
            if (statusObj.getInt("is_record_added") == 1) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.CreateRecordsComplete),
                    null
                )
            } else if (statusObj.getInt("is_record_added") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.NoRecordsCreated),
                    null
                )
            }
            if (statusObj.getInt("has_shared_record") == 1) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.ShareRecordsComplete),
                    null
                )
            } else if (statusObj.getInt("has_shared_record") == 0) {
                MyClinicGlobalClass.logUserActionEvent(
                    loginDocId, getString(R.string.NoRecordsShared),
                    null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}