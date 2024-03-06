package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whitecoats.adapter.DashBoardMoreListAdapter
import com.whitecoats.clinicplus.CommunicationActivity
import com.whitecoats.clinicplus.SettingsActivity
import com.whitecoats.clinicplus.activities.FeedActivity
import com.whitecoats.clinicplus.activities.MyPaymentsFragment
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.DashboardFragment
import com.whitecoats.fragments.AppointmentFragment
import com.whitecoats.fragments.AppointmentFragment.Companion.appointmentTabFlag
import com.whitecoats.fragments.ChatFragment
import com.whitecoats.fragments.HomeFragment
import com.whitecoats.fragments.PatientFragment
import com.whitecoats.fragments.PatientFragment.Companion.patientTabFlag
import com.whitecoats.model.DashBoardMoreListModel
import org.json.JSONException

/**
 * Created by vaibhav on 07-02-2018.
 */
class DashBoardMoreBottomSheet : BottomSheetDialogFragment() {
    private var mainActivity: MainActivity? = null
    private val patientListRView: RecyclerView? = null
    private var dashBoardMoreModelList: MutableList<DashBoardMoreListModel>? = null
    private var dashBoardMoreListAdapter: DashBoardMoreListAdapter? = null
    private lateinit var cancelIcon: ImageView
    private var mContext: Context? = null
    private var appDatabaseManager: AppDatabaseManager? = null
    private var appPreference: SharedPreferences? = null
    fun setupConfig(mainActivity: MainActivity?) {
        this.mainActivity = mainActivity
        mContext = mainActivity
        dashBoardMoreModelList = ArrayList()
        dashBoardMoreData()
        appDatabaseManager = AppDatabaseManager(mContext)
        appPreference = mContext!!.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
    }

    //Bottom Sheet Callback
    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
                mainActivity!!.onResume()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
//        super.setupDialog(dialog, style);
        //Get the content View
        val contentView =
            View.inflate(context, R.layout.fragment_bottom_sheet_dashboard_more_list, null)
        dialog.setContentView(contentView)
        //Set the coordinator layout behavior
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon)
        val llm = LinearLayoutManager(mContext)
        val recList = contentView.findViewById<RecyclerView>(R.id.bottomSheetCardList)

        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        recList.setHasFixedSize(true)
        llm.orientation = LinearLayoutManager.VERTICAL
        recList.layoutManager = llm
        recList.adapter = dashBoardMoreListAdapter
        cancelIcon.setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        cancelIcon.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            mainActivity!!.onResume()
        })
    }

    private fun dashBoardMoreData() {
        try {
            val rootObj = MainActivity.getMenuJsonObject!!.optJSONObject("response")
            val menuArr = rootObj.getJSONArray("menus")
            val menuArrLenth = menuArr.length()
            if (menuArrLenth >= 4) {
                for (i in 3 until menuArrLenth) {
                    val tempobj = menuArr.getJSONObject(i)
                    //                    Log.d("DashBoardMenuObjectResp", tempobj.toString());
                    if (tempobj.getInt("id") != 49) {
                        val temp = DashBoardMoreListModel()
                        temp.menuName = tempobj.getString("page_name")
                        temp.iconName = tempobj.getString("android_icon")
                        temp.pageName = tempobj.getString("page_name")
                        temp.menuId = tempobj.getInt("id")
                        temp.hiddenForDoctor = tempobj.getInt("is_hidden_for_doctor_only")
                        dashBoardMoreModelList!!.add(temp)
                    }
                }
                val temp = DashBoardMoreListModel()
                temp.menuName = "Logout"
                temp.iconName = "ic_logout"
                temp.menuId = -1
                temp.hiddenForDoctor = -1
                dashBoardMoreModelList!!.add(temp)
                dashBoardMoreListAdapter = DashBoardMoreListAdapter(
                    dashBoardMoreModelList!!,
                    mContext!!,
                    object : ActivityMoreClickListener {
                        override fun onItemClick(
                            v: View,
                            position: Int,
                            menuId: Int,
                            loadMore: String
                        ) {
                            var selectedFragment: Fragment? = null
                            if (menuId == -1) {
                                HomeFragment.caseChannelPresent = false
                            }
                            if (menuId == 0) {
                                selectedFragment = HomeFragment.newInstance()
                            }
                            if (menuId == 1) {
                                if (dashBoardMoreModelList!![position].menuId == 1 && dashBoardMoreModelList!![position].hiddenForDoctor == 1 && ApiUrls.isDoctorOnly == 1) {
                                    Toast.makeText(
                                        mContext,
                                        "Service not available",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    ApiUrls.activePastFilterFlag = ""
                                    selectedFragment = AppointmentFragment.newInstance()
                                }
                            }
                            if (menuId == 18) {
                                if (dashBoardMoreModelList!![position].menuId == 18 && dashBoardMoreModelList!![position].hiddenForDoctor == 1 && ApiUrls.isDoctorOnly == 1) {
                                    Toast.makeText(
                                        mContext,
                                        "Service not available",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    ApiUrls.activePastFilterFlag = ""
                                    selectedFragment = PatientFragment.newInstance()
                                }
                            }
                            if (menuId == 6) {
                                if (dashBoardMoreModelList!![position].menuId == 6 && dashBoardMoreModelList!![position].hiddenForDoctor == 1 && ApiUrls.isDoctorOnly == 1) {
                                    Toast.makeText(
                                        mContext,
                                        "Service not available",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    ApiUrls.activePastFilterFlag = ""
                                    selectedFragment = ChatFragment.newInstance()
                                }
                            }
                            if (menuId == 7) { //communication
                                //selectedFragment = ChatFragment.newInstance();
                                if (dashBoardMoreModelList!![position].menuId == 7 && dashBoardMoreModelList!![position].hiddenForDoctor == 1 && ApiUrls.isDoctorOnly == 1) {
                                    Toast.makeText(
                                        mContext,
                                        "Service not available",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    ApiUrls.activePastFilterFlag = ""
                                    val intent = Intent(mContext, CommunicationActivity::class.java)
                                    startActivity(intent)
                                }

//                            Intent intent = new Intent(mContext, CommunicationActivity.class);
//                            startActivity(intent);
                            }
                            if (menuId == 10) { //feed
                                if (dashBoardMoreModelList!![position].menuId == 10 && dashBoardMoreModelList!![position].hiddenForDoctor == 1 && ApiUrls.isDoctorOnly == 1) {
                                    Toast.makeText(
                                        mContext,
                                        "Service not available",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
//                                ApiUrls.activePastFilterFlag = "";
////                                selectedFragment = FeedsFragment.newInstance("", "");
//                                MainActivity.viewPager.setCurrentItem(4);
                                    ApiUrls.activePastFilterFlag = ""
                                    val intent = Intent(mContext, FeedActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            if (menuId == 19) { //Payments 52 dev
                                //selectedFragment = ChatFragment.newInstance();
                                if (dashBoardMoreModelList!![position].menuId == 19 && dashBoardMoreModelList!![position].hiddenForDoctor == 1 && ApiUrls.isDoctorOnly == 1) {
                                    Toast.makeText(
                                        mContext,
                                        "Service not available",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    ApiUrls.activePastFilterFlag = ""
                                    val intent = Intent(mContext, MyPaymentsFragment::class.java)
                                    startActivity(intent)
                                }
                            }
                            //                        if (menuId == 52) {//Payments 52 dev
//                            //selectedFragment = ChatFragment.newInstance();
//                            if (dashBoardMoreModelList.get(position).getMenuId() == 52 && dashBoardMoreModelList.get(position).getHiddenForDoctor() == 1 && ApiUrls.isDoctorOnly == 1) {
//                                Toast.makeText(mContext, "Service not available", Toast.LENGTH_LONG).show();
//                            } else {
//                                ApiUrls.activePastFilterFlag = "";
//                                Intent intent = new Intent(mContext, MyPaymentsFragment.class);
//                                startActivity(intent);
//                            }
//
//                        }
                            if (menuId == 22) { //setting
                                //selectedFragment = ChatFragment.newInstance();
                                if (dashBoardMoreModelList!![position].menuId == 22 && dashBoardMoreModelList!![position].hiddenForDoctor == 1 && ApiUrls.isDoctorOnly == 1) {
                                    Toast.makeText(
                                        mContext,
                                        "Service not available",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    ApiUrls.activePastFilterFlag = ""
                                    val intent = Intent(mContext, SettingsActivity::class.java)
                                    startActivity(intent)
                                }

//                            Intent intent = new Intent(mContext, SettingsActivity.class);
//                            startActivity(intent);
                            }
                            if (menuId == 11) { //get help
                                // selectedFragment = GetHelpTabFragment;
                                //GetHelpTabFragment tab3 = new GetHelpTabFragment();
                                //selectedFragment = HomeFragment.newInstance();
                                ApiUrls.activePastFilterFlag = ""
                                if (patientTabFlag == 1 || appointmentTabFlag == 1 || ChatFragment.chatTabFlag == 1) {
                                    val intent = Intent(mContext, HelpActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    if (DashboardFragment.caseChannelPresent) {
                                        DashboardFragment.viewPager!!.currentItem = 3
                                    } else {
                                        DashboardFragment.viewPager!!.currentItem = 2
                                    }
                                }
                            }
                            if (menuId == 20) { //privacy policy
                                //selectedFragment = ChatFragment.newInstance();
//                            Intent intent = new Intent(mContext, CommunicationActivity.class);
//                            startActivity(intent);
                                if (dashBoardMoreModelList!![position].menuId == 20 && dashBoardMoreModelList!![position].hiddenForDoctor == 1 && ApiUrls.isDoctorOnly == 1) {
                                    Toast.makeText(
                                        mContext,
                                        "Service not available",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    //getPrivacyPolicy();
                                    ApiUrls.activePastFilterFlag = ""
                                    privacyPolicyHardCoded
                                }

//                            getPrivacyPolicy();
                            }
                            if (selectedFragment != null) {
                                val transaction =
                                    activity!!.supportFragmentManager.beginTransaction()
                                transaction.replace(R.id.fragmentContainer, selectedFragment)
                                transaction.commit()
                            }
                            dismiss()
                        }
                    })
            }
        } catch (e: JSONException) {
        }
    }

    private val privacyPolicyHardCoded: Unit
        private get() {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://www.whitecoats.com/practiceplus-privacypolicyandterms")
            mContext!!.startActivity(i)
        }

    companion object {
        var deleteResponse = 0
    }
}