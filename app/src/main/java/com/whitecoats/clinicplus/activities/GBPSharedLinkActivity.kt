package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.SettingsFormActivity
import com.whitecoats.clinicplus.adapters.GBPListAdapter
import com.whitecoats.clinicplus.adapters.StaffPermissionListAdapter
import com.whitecoats.clinicplus.models.ActionPermissionsModel
import com.whitecoats.clinicplus.models.GBPListModel
import com.whitecoats.clinicplus.models.StaffPermissionDetailsModel
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.UserSocialProfileViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class GBPSharedLinkActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var descriptionTextView: TextView? = null

    //GBP List
    private var gbpListAdapter: GBPListAdapter? = null
    private var gbpRecycleView: RecyclerView? = null
    private var gbpModelList: MutableList<GBPListModel>? = null

    //Staff permission list
    private var staffPermissionListAdapter: StaffPermissionListAdapter? = null
    private var gbpStaffRecycleView: RecyclerView? = null

    //private List<StaffPermissionListModel> staffPermissionModelList;
    private var socialProfileViewModel: UserSocialProfileViewModel? = null
    private var setupPracticeButton: RelativeLayout? = null
    private var noServiceSetupLayout: RelativeLayout? = null
    private var staffPermissionDetailsList: ArrayList<StaffPermissionDetailsModel>? = null
    private var noStaffMemberAddedLayout: RelativeLayout? = null
    private var globalClass: MyClinicGlobalClass? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gbp_shared_link_activity)
        socialProfileViewModel = ViewModelProvider(this).get(
            UserSocialProfileViewModel::class.java
        )
        socialProfileViewModel!!.init()
        globalClass = applicationContext as MyClinicGlobalClass
        initView()
    }

    private fun openGBPPopupRemove_Apply(
        showPopupFrom: String,
        dr_service_id: Int,
        gbpListModel: GBPListModel
    ) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_popup_gbp)
        dialog.setCancelable(false)
        val rl_parent = dialog.findViewById<RelativeLayout>(R.id.rl_parent)
        val tv_title = dialog.findViewById<TextView>(R.id.tv_title)
        val tv_body = dialog.findViewById<TextView>(R.id.tv_body)
        val btn_close = dialog.findViewById<Button>(R.id.btn_close)
        val btn_remove = dialog.findViewById<Button>(R.id.btn_remove)
        val btn_apply_to_all = dialog.findViewById<Button>(R.id.btn_apply_to_all)
        val applyAllProgressBar = dialog.findViewById<RelativeLayout>(R.id.applyAllProgressBar)
        val removeLinkProgressBar = dialog.findViewById<RelativeLayout>(R.id.removeLinkProgressBar)
        val ll_buttons_parent = dialog.findViewById<LinearLayout>(R.id.ll_buttons_parent)
        if (showPopupFrom.equals("GBP_RemoveLink", ignoreCase = true)) {
            btn_remove.visibility = View.VISIBLE
            btn_apply_to_all.visibility = View.GONE
            tv_title.text = getString(R.string.remove_header_GBP)
            tv_body.setText(R.string.remove_title_GBP)
        } else {
            btn_remove.visibility = View.GONE
            btn_apply_to_all.visibility = View.VISIBLE
            tv_title.text = getString(R.string.apply_all_header_GBP)
            tv_body.setText(R.string.apply_all_title_GBP)
        }
        btn_close.setOnClickListener { dialog.dismiss() }
        btn_remove.setOnClickListener {
            var resetData: JSONObject? = null
            try {
                resetData = JSONObject()
                val linksArray = JSONArray()
                val service_id_obj = JSONObject()
                service_id_obj.put("dr_service_id", dr_service_id)
                linksArray.put(service_id_obj)
                resetData.put("links", linksArray)
            } catch (e: JSONException) {
            }
            if (globalClass!!.isOnline) {
                resetGBPLink(
                    resetData,
                    gbpListModel,
                    removeLinkProgressBar,
                    dialog,
                    ll_buttons_parent
                )
            } else {
                showAlert("No Internet", "Please check your internet connection", 1)
            }
        }
        btn_apply_to_all.setOnClickListener {
            var applyToAllData: JSONObject? = null
            try {
                applyToAllData = JSONObject()
                applyToAllData.put("dr_service_id", dr_service_id)
            } catch (e: JSONException) {
            }
            if (globalClass!!.isOnline) {
                applyLinkToAll(
                    applyToAllData,
                    gbpListModel.gbp_page_link,
                    gbpListModel.gbp_review_link,
                    applyAllProgressBar,
                    ll_buttons_parent,
                    dialog
                )
            } else {
                showAlert("No Internet", "Please check your internet connection", 1)
            }
        }
        dialog.show()
    }

    private fun openStaffPermissionSheet(staffPermissionDetailsModel: StaffPermissionDetailsModel) {
        val sheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        sheetDialog.setContentView(R.layout.bottom_sheet_gbp_staff_permi)
        sheetDialog.setCancelable(false)
        sheetDialog.findViewById<TextView>(R.id.tv_title)
        val tv_subTitle = sheetDialog.findViewById<TextView>(R.id.tv_subTitle)
        sheetDialog.findViewById<LinearLayout>(R.id.ll_auto_share_GBP)
        sheetDialog.findViewById<LinearLayout>(R.id.ll_auto_share_GBP_Content)
        sheetDialog.findViewById<TextView>(R.id.tv_auto_share_GBP_title)
        sheetDialog.findViewById<TextView>(R.id.tv_auto_share_GBP_content)
        val sc_auto_share_GBP = sheetDialog.findViewById<SwitchCompat>(R.id.sc_auto_share_GBP)
        sheetDialog.findViewById<LinearLayout>(R.id.ll_update_GBP)
        val tv_Update_GBP = sheetDialog.findViewById<TextView>(R.id.tv_Update_GBP)
        val sc_Update_GBP = sheetDialog.findViewById<SwitchCompat>(R.id.sc_Update_GBP)
        sheetDialog.findViewById<LinearLayout>(R.id.ll_buttons_parent)
        val btn_close = sheetDialog.findViewById<Button>(R.id.btn_close)
        val btn_update = sheetDialog.findViewById<Button>(R.id.btn_update)
        tv_subTitle!!.text = "Set Permission for " + staffPermissionDetailsModel.staffName
        var drawable = ContextCompat.getDrawable(this, R.drawable.custom_enter_right_icon)!!
        drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(drawable.mutate(), resources.getColor(R.color.colorGrey1))
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        assert(tv_Update_GBP != null)
        tv_Update_GBP!!.setCompoundDrawables(drawable, null, null, null)
        for (i in staffPermissionDetailsModel.permissionsModelArrayList.indices) {
            val permissionActionModel = staffPermissionDetailsModel.permissionsModelArrayList[i]
            if (permissionActionModel.permissionName.equals(
                    "view_enable_auto_share_gbp_links",
                    ignoreCase = true
                )
            ) {
                var drawable1 = ContextCompat.getDrawable(
                    this@GBPSharedLinkActivity,
                    R.drawable.custom_enter_right_icon
                )
                if (permissionActionModel.isEnabled) {
                    assert(drawable1 != null)
                    drawable1 = DrawableCompat.wrap(drawable1!!)
                    DrawableCompat.setTint(
                        drawable1.mutate(),
                        ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                    )
                    drawable1.setBounds(0, 0, drawable1.intrinsicWidth, drawable1.intrinsicHeight)
                    assert(tv_Update_GBP != null)
                    tv_Update_GBP.setCompoundDrawables(drawable1, null, null, null)
                    tv_Update_GBP.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
                } else {
                    sc_Update_GBP!!.isChecked = false
                    assert(drawable1 != null)
                    drawable1 = DrawableCompat.wrap(drawable1!!)
                    DrawableCompat.setTint(
                        drawable1.mutate(),
                        resources.getColor(R.color.colorGrey1)
                    )
                    drawable1.setBounds(0, 0, drawable1.intrinsicWidth, drawable1.intrinsicHeight)
                    assert(tv_Update_GBP != null)
                    tv_Update_GBP.setCompoundDrawables(drawable1, null, null, null)
                    tv_Update_GBP.setTextColor(resources.getColor(R.color.colorGrey1))
                }
                sc_auto_share_GBP!!.isChecked = permissionActionModel.isEnabled
                sc_Update_GBP!!.isEnabled = permissionActionModel.isEnabled
            } else if (permissionActionModel.permissionName.equals(
                    "configure_update_gbp_links",
                    ignoreCase = true
                )
            ) {
                sc_Update_GBP!!.isChecked = permissionActionModel.isEnabled
            }
        }
        sc_auto_share_GBP!!.setOnCheckedChangeListener { compoundButton: CompoundButton?, isChecked: Boolean ->
            var drawable1 = ContextCompat.getDrawable(
                this@GBPSharedLinkActivity,
                R.drawable.custom_enter_right_icon
            )
            if (isChecked) {
                sc_Update_GBP!!.isEnabled = true
                sc_Update_GBP.isClickable = true
                assert(drawable1 != null)
                drawable1 = DrawableCompat.wrap(drawable1!!)
                DrawableCompat.setTint(
                    drawable1!!.mutate(),
                    resources.getColor(R.color.colorPrimary)
                )
                drawable1!!.setBounds(0, 0, drawable1!!.intrinsicWidth, drawable1!!.intrinsicHeight)
                assert(tv_Update_GBP != null)
                tv_Update_GBP.setCompoundDrawables(drawable1, null, null, null)
                tv_Update_GBP.setTextColor(resources.getColor(R.color.black))
            } else {
                sc_Update_GBP!!.isChecked = false
                sc_Update_GBP.isEnabled = false
                sc_Update_GBP.isClickable = false
                assert(drawable1 != null)
                drawable1 = DrawableCompat.wrap(drawable1!!)
                DrawableCompat.setTint(drawable1!!.mutate(), resources.getColor(R.color.colorGrey1))
                drawable1!!.setBounds(0, 0, drawable1!!.intrinsicWidth, drawable1!!.intrinsicHeight)
                assert(tv_Update_GBP != null)
                tv_Update_GBP.setCompoundDrawables(drawable1, null, null, null)
                tv_Update_GBP.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGrey1))
            }
        }
        btn_close!!.setOnClickListener { sheetDialog.dismiss() }
        btn_update!!.setOnClickListener {
            val requestObj = JSONObject()
            try {
                requestObj.put("client_user_id", staffPermissionDetailsModel.staffId)
                val actionArray = JSONArray()
                val actionObj1 = JSONObject()
                val actionObj2 = JSONObject()
                actionObj1.put("action", "view_enable_auto_share_gbp_links")
                actionObj1.put("enabled", sc_auto_share_GBP.isChecked)
                actionArray.put(actionObj1)
                actionObj2.put("action", "configure_update_gbp_links")
                actionObj2.put("enabled", sc_Update_GBP!!.isChecked)
                actionArray.put(actionObj2)
                requestObj.put("action_permissions", actionArray)
                sheetDialog.dismiss()
                if (globalClass!!.isOnline) {
                    socialProfileViewModel!!.setStaffPermission(
                        this@GBPSharedLinkActivity,
                        requestObj
                    ).observe(this@GBPSharedLinkActivity) { s: String? ->
                        try {
                            val jsonObject = JSONObject(s)
                            if (jsonObject.getInt("status_code") == 200) {
                                Toast.makeText(
                                    this,
                                    "Staff Permission Updated Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                for (k in staffPermissionDetailsModel.permissionsModelArrayList.indices) {
                                    val permissionActionModel =
                                        staffPermissionDetailsModel.permissionsModelArrayList[k]
                                    if (permissionActionModel.permissionName.equals(
                                            "view_enable_auto_share_gbp_links",
                                            ignoreCase = true
                                        )
                                    ) {
                                        permissionActionModel.isEnabled =
                                            sc_auto_share_GBP.isChecked
                                    } else if (permissionActionModel.permissionName.equals(
                                            "configure_update_gbp_links",
                                            ignoreCase = true
                                        )
                                    ) {
                                        permissionActionModel.isEnabled = sc_Update_GBP.isChecked
                                    }
                                }
                                staffPermissionListAdapter!!.notifyDataSetChanged()
                            } else {
                                errorHandler(this@GBPSharedLinkActivity, s!!)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    showAlert("No Internet", "Please check your internet connection", 1)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        sheetDialog.show()
    }

    private fun initView() {
        toolbar = findViewById(R.id.gbpSharedLinkToolbar)

        //setting up back button on toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = AppUtilities.changeIconColor(
            resources.getDrawable(R.drawable.ic_arrow_back, theme), this, R.color.colorWhite
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        gbpRecycleView = findViewById(R.id.gbpRecycleView)
        setupPracticeButton = findViewById(R.id.setupPracticeButton)
        noServiceSetupLayout = findViewById(R.id.noServiceSetupLayout)
        noStaffMemberAddedLayout = findViewById(R.id.noStaffMemberAddedLayout)
        gbpModelList = ArrayList()
        gbpStaffRecycleView = findViewById(R.id.gbpStaffRecycleView)
        staffPermissionDetailsList = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            descriptionTextView!!.text = Html.fromHtml(
                "<p>Increase your business engagement by sending your Google Business Profile link to your Patient. Patient can check out  <font color ='#00A65A'><b>Your Articles, Add Review, Add Comments</b></font> to help you increase your presence on Google Search.</p>",
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            descriptionTextView!!.text = Html.fromHtml("<p>Increase your business engagement by sending your Google Business Profile link to your Patient. Patient can check out  <font color ='#00A65A'><b>Your Articles, Add Review, Add Comments</b></font> to help you increase your presence on Google Search.</p>")
        }
        gbpListAdapter = GBPListAdapter(
            gbpModelList as ArrayList<GBPListModel>,this@GBPSharedLinkActivity
        ) { callFrom: String, gbpListModel: GBPListModel, isSwitchClick: Boolean, position: Int ->
            if (callFrom.equals("GBP_RemoveLink", ignoreCase = true)) {
                if (gbpListModel.gbp_page_link.isNotEmpty() || !gbpListModel.gbp_review_link.isEmpty()) {
                    openGBPPopupRemove_Apply(callFrom, gbpListModel.dr_service_id, gbpListModel)
                } else {
                    Toast.makeText(this@GBPSharedLinkActivity, "No links found", Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (callFrom.equals("SwitchClick", ignoreCase = true)) {
                if (isSwitchClick && (gbpListModel.gbp_page_link.isEmpty() || gbpListModel.gbp_review_link.isEmpty())) {
                    Toast.makeText(
                        this@GBPSharedLinkActivity,
                        "To enable auto share you first need to add GBP links",
                        Toast.LENGTH_SHORT
                    ).show()
                    gbpListModel.isAuto_share_post_consultation = false
                    gbpListAdapter!!.notifyDataSetChanged()
                } else {
                    var gbpLinkShareDataObject: JSONObject? = null
                    var gbpLinkShareArray: JSONArray? = null
                    var gbpLinkShareData: JSONObject? = null
                    try {
                        gbpLinkShareDataObject = JSONObject()
                        gbpLinkShareArray = JSONArray()
                        gbpLinkShareData = JSONObject()
                        gbpLinkShareData.put("dr_service_id", gbpListModel.dr_service_id)
                        gbpLinkShareData.put("auto_share_post_consultation", isSwitchClick)
                        gbpLinkShareArray.put(gbpLinkShareData)
                        gbpLinkShareDataObject.put("links", gbpLinkShareArray)
                    } catch (e: JSONException) {
                    }
                    if (globalClass!!.isOnline) {
                        updateGBPSharePreferences(
                            gbpLinkShareDataObject,
                            gbpListModel,
                            isSwitchClick
                        )
                    } else {
                        showAlert("No Internet", "Please check your internet connection", 1)
                    }
                }
            } else {
                if (validateGBP_Profile(gbpListModel)) {
                    if (callFrom.equals("saveChanges", ignoreCase = true)) {
                        var gbpLinkData: JSONObject? = null
                        try {
                            gbpLinkData = JSONObject()
                            gbpLinkData.put("dr_service_id", gbpListModel.dr_service_id)
                            gbpLinkData.put("gbp_page_link", gbpListModel.gbp_page_link)
                            gbpLinkData.put("gbp_review_link", gbpListModel.gbp_review_link)
                            gbpLinkData.put("apply_link_to_all", gbpListModel.applyToAll)
                        } catch (e: JSONException) {
                        }
                        if (globalClass!!.isOnline) {
                            updateGBPLink(gbpLinkData, gbpListModel, (gbpModelList as ArrayList<GBPListModel>).get(position))
                        } else {
                            showAlert("No Internet", "Please check your internet connection", 1)
                        }
                    } else {
                        openGBPPopupRemove_Apply(callFrom, gbpListModel.dr_service_id, gbpListModel)
                    }
                } else {
                    Toast.makeText(
                        this@GBPSharedLinkActivity,
                        "Please enter valid links",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        staffPermissionListAdapter = StaffPermissionListAdapter(
            staffPermissionDetailsList!!, this
        ) { callFrom: String, gbpListModel: GBPListModel?, isSwitchClick: Boolean?, position: Int ->
            if (callFrom.equals("staffPermissions", ignoreCase = true)) {
                openStaffPermissionSheet(staffPermissionDetailsList!![position])
            }
        }
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        gbpRecycleView!!.layoutManager = mLayoutManager
        gbpRecycleView!!.itemAnimator = DefaultItemAnimator()
        gbpRecycleView!!.adapter = gbpListAdapter
        val mLayoutManagerStaff: RecyclerView.LayoutManager = LinearLayoutManager(
            applicationContext
        )
        gbpStaffRecycleView!!.layoutManager = mLayoutManagerStaff
        gbpStaffRecycleView!!.itemAnimator = DefaultItemAnimator()
        gbpStaffRecycleView!!.adapter = staffPermissionListAdapter

        if (globalClass!!.isOnline) {
            socialProfileViewModel!!.getStaffPermissionsData(this@GBPSharedLinkActivity, null)
                .observe(this@GBPSharedLinkActivity) { s: String? ->
                    try {
                        val jsonObject = JSONObject(s)
                        if (jsonObject.getInt("status_code") == 200) {
                            val completeResponseObj = jsonObject.getJSONObject("response")
                            val getStaffPermissionsResponse =
                                completeResponseObj.getJSONArray("response")
                            if (getStaffPermissionsResponse.length() > 0) {
                                for (i in 0 until getStaffPermissionsResponse.length()) {
                                    val actionPermissionsModelArrayList =
                                        ArrayList<ActionPermissionsModel>()
                                    val permissionDetailsModel = StaffPermissionDetailsModel()
                                    permissionDetailsModel.staffId =
                                        getStaffPermissionsResponse.optJSONObject(i)
                                            .optInt("client_user_id")
                                    permissionDetailsModel.staffName =
                                        getStaffPermissionsResponse.optJSONObject(i)
                                            .optString("name")
                                    permissionDetailsModel.staffPhNumber =
                                        getStaffPermissionsResponse.optJSONObject(i)
                                            .optString("phone")
                                    val actionPermissionsArray =
                                        getStaffPermissionsResponse.optJSONObject(i)
                                            .optJSONArray("action_permissions")
                                    for (j in 0 until actionPermissionsArray.length()) {
                                        val actionPermissionsModel = ActionPermissionsModel()
                                        actionPermissionsModel.permissionName =
                                            actionPermissionsArray.optJSONObject(j)
                                                .optString("action")
                                        actionPermissionsModel.isEnabled =
                                            actionPermissionsArray.optJSONObject(j)
                                                .optBoolean("enabled")
                                        actionPermissionsModelArrayList.add(actionPermissionsModel)
                                    }
                                    permissionDetailsModel.permissionsModelArrayList =
                                        actionPermissionsModelArrayList
                                    staffPermissionDetailsList!!.add(permissionDetailsModel)
                                }
                                staffPermissionListAdapter!!.notifyDataSetChanged()
                            } else {
                                gbpStaffRecycleView!!.visibility = View.GONE
                                noStaffMemberAddedLayout!!.visibility = View.VISIBLE
                            }
                        } else {
                            errorHandler(this@GBPSharedLinkActivity, s!!)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        } else {
            showAlert("No Internet", "Please check your internet connection", 2)
        }
        setupPracticeButton!!.setOnClickListener(View.OnClickListener { view: View? ->
            val intent = Intent(this@GBPSharedLinkActivity, SettingsFormActivity::class.java)
            intent.putExtra("FormType", 7)
            intent.putExtra("Title", resources.getString(R.string.service_setup))
            startActivity(intent)
        })
        if (globalClass!!.isOnline) {
            socialProfileViewModel!!.getGBPLinkData(this@GBPSharedLinkActivity)
                .observe(this@GBPSharedLinkActivity) { s: String ->
                    try {
                        Log.d("gbpLinkResp", "gbpLinkResp$s")
                        val jsonObject = JSONObject(s)
                        if (jsonObject.getInt("status_code") == 200) {
                            val gbpLinkObject = jsonObject.getJSONObject("response")
                            val gbpLinkArray = gbpLinkObject.getJSONArray("response")
                            if (gbpLinkArray.length() > 0) {
                                for (i in 0 until gbpLinkArray.length()) {
                                    val arrayObject = gbpLinkArray.getJSONObject(i)
                                    val gbpModel = GBPListModel()
                                    gbpModel.dr_service_id = arrayObject.getInt("dr_service_id")
                                    gbpModel.service_id = arrayObject.getInt("service_id")
                                    if (!arrayObject.isNull("gbp_page_link")) {
                                        gbpModel.gbp_page_link =
                                            arrayObject.getString("gbp_page_link")
                                    } else {
                                        gbpModel.gbp_page_link = ""
                                    }
                                    if (!arrayObject.isNull("gbp_review_link")) {
                                        gbpModel.gbp_review_link =
                                            arrayObject.getString("gbp_review_link")
                                    } else {
                                        gbpModel.gbp_review_link = ""
                                    }
                                    gbpModel.isAuto_share_post_consultation =
                                        arrayObject.getBoolean("auto_share_post_consultation")
                                    gbpModel.serviceName = arrayObject.getString("alias")
                                    gbpModel.editModeStatus = false
                                    gbpModel.isInExpandedMode = false
                                    (gbpModelList as ArrayList<GBPListModel>).add(gbpModel)
                                }
                                gbpListAdapter!!.notifyDataSetChanged()
                            } else {
                                noServiceSetupLayout!!.visibility = View.VISIBLE
                                gbpRecycleView!!.visibility = View.GONE
                            }
                        } else {
                            errorHandler(this@GBPSharedLinkActivity, s)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        } else {
            showAlert("No Internet", "Please check your internet connection", 2)
        }
    }

    private fun validateGBP_Profile(gbpListModel: GBPListModel): Boolean {
        return if (gbpListModel.gbp_page_link.isNotEmpty() && !gbpListModel.gbp_review_link.isEmpty()) {
            isURL(gbpListModel.gbp_page_link) && isURL(gbpListModel.gbp_review_link)
        } else {
            false
        }
    }

    fun isURL(url: String?): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    fun updateGBPLink(
        gbpLinkData: JSONObject?,
        gbpListModel: GBPListModel,
        unmodifiedGBPModel: GBPListModel
    ) {
        socialProfileViewModel!!.updateGBPLink(this@GBPSharedLinkActivity, gbpLinkData)
            .observe(this@GBPSharedLinkActivity) { value ->
                try {
                    Log.d("gbpLinkResp", "gbpLinkResp$value")
                    val jsonObject = JSONObject(value)
                    if (jsonObject.getInt("status_code") == 200) {
                        //                        showToast("Google Business Profile Link Updated Successfully");
                        Toast.makeText(
                            this@GBPSharedLinkActivity,
                            "Google Business Profile Link Updated Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        unmodifiedGBPModel.editModeStatus = false
                        if (gbpListModel.applyToAll) {
                            for (i in gbpModelList!!.indices) {
                                gbpModelList!![i].gbp_page_link = gbpListModel.gbp_page_link
                                gbpModelList!![i].gbp_review_link = gbpListModel.gbp_review_link
                                if (gbpModelList!![i].service_id != 2) {
                                    gbpModelList!![i].isAuto_share_post_consultation = true
                                }
                            }
                        } else {
                            unmodifiedGBPModel.gbp_page_link = gbpListModel.gbp_page_link
                            unmodifiedGBPModel.gbp_review_link = gbpListModel.gbp_review_link
                            unmodifiedGBPModel.isAuto_share_post_consultation = true
                        }
                        gbpListAdapter!!.notifyDataSetChanged()
                    } else {
                        errorHandler(this@GBPSharedLinkActivity, value)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    fun updateGBPSharePreferences(
        gbpLinkData: JSONObject?,
        gbpListModel: GBPListModel,
        isSwitchClick: Boolean
    ) {
        socialProfileViewModel!!.updateGBPSharePreference(this@GBPSharedLinkActivity, gbpLinkData)
            .observe(this@GBPSharedLinkActivity) { s: String ->
                try {
                    Log.d("gbpLinkPResp", "gbpLinkPResp$s")
                    val jsonObject = JSONObject(s)
                    if (jsonObject.getInt("status_code") == 200) {
                        if (isSwitchClick) {
                            Toast.makeText(
                                this@GBPSharedLinkActivity,
                                "Auto Share Enabled",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@GBPSharedLinkActivity,
                                "Auto Share Disabled",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        gbpListModel.isAuto_share_post_consultation = isSwitchClick
                        gbpListAdapter!!.notifyDataSetChanged()
                    } else {
                        errorHandler(this@GBPSharedLinkActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    private fun applyLinkToAll(
        applyToAllData: JSONObject?,
        pageLink: String,
        reviewLink: String,
        applyAllProgressBar: RelativeLayout,
        ll_buttons_parent: LinearLayout,
        dialog: Dialog
    ) {
        ll_buttons_parent.visibility = View.GONE
        applyAllProgressBar.visibility = View.VISIBLE
        socialProfileViewModel!!.applyToAllGbpLink(this@GBPSharedLinkActivity, applyToAllData)
            .observe(this@GBPSharedLinkActivity) { s: String ->
                try {
                    Log.d("applyToAllResp", "applyToAllResp$s")
                    val jsonObject = JSONObject(s)
                    applyAllProgressBar.visibility = View.GONE
                    ll_buttons_parent.visibility = View.VISIBLE
                    dialog.dismiss()
                    if (jsonObject.getInt("status_code") == 200) {
                        for (i in gbpModelList!!.indices) {
                            gbpModelList!![i].gbp_page_link = pageLink
                            gbpModelList!![i].gbp_review_link = reviewLink
                            if (gbpModelList!![i].service_id != 2) {
                                gbpModelList!![i].isAuto_share_post_consultation = true
                            }
                        }
                        gbpListAdapter!!.notifyDataSetChanged()
                        Toast.makeText(
                            applicationContext,
                            "Google Business Profile Link Updated Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        errorHandler(this@GBPSharedLinkActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetGBPLink(
        gbpResetData: JSONObject?,
        gbpListModel: GBPListModel,
        removeLinkProgressBar: RelativeLayout,
        dialog: Dialog,
        ll_buttons_parent: LinearLayout
    ) {
        removeLinkProgressBar.visibility = View.VISIBLE
        ll_buttons_parent.visibility = View.GONE
        socialProfileViewModel!!.restGBPLinkData(this@GBPSharedLinkActivity, gbpResetData)
            .observe(this@GBPSharedLinkActivity) { value ->
                try {
                    Log.d("applyToAllResp", "applyToAllResp$value")
                    val jsonObject = JSONObject(value)
                    removeLinkProgressBar.visibility = View.GONE
                    ll_buttons_parent.visibility = View.VISIBLE
                    dialog.dismiss()
                    if (jsonObject.getInt("status_code") == 200) {
                        Toast.makeText(
                            applicationContext,
                            "GBP Link has been reset",
                            Toast.LENGTH_LONG
                        ).show()
                        gbpListModel.gbp_page_link = ""
                        gbpListModel.gbp_review_link = ""
                        gbpListModel.isAuto_share_post_consultation = false
                        gbpListAdapter!!.notifyDataSetChanged()
                    } else {
                        errorHandler(this@GBPSharedLinkActivity, value)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun dummyData() {
        var gbpModel = GBPListModel()
        gbpModel.serviceName = "Clinic Name 1"
        gbpModelList!!.add(gbpModel)
        gbpModel = GBPListModel()
        gbpModel.serviceName = "Clinic Name 2"
        gbpModelList!!.add(gbpModel)
        gbpModel = GBPListModel()
        gbpModel.serviceName = "Video"
        gbpModelList!!.add(gbpModel)
        gbpModel = GBPListModel()
        gbpModel.serviceName = "Chat"
        gbpModelList!!.add(gbpModel)
        gbpListAdapter!!.notifyDataSetChanged()
    }

    /*public void dummyDataStaff() {
        StaffPermissionListModel staffModel = new StaffPermissionListModel();
        staffModel.setServiceName("Dileep Singh");
        staffPermissionModelList.add(staffModel);

        staffModel = new StaffPermissionListModel();
        staffModel.setServiceName("Amit Singh");
        staffPermissionModelList.add(staffModel);


        staffPermissionListAdapter.notifyDataSetChanged();


    }*/
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAlert(title: String, msg: String, type: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton("Ok") { dialog: DialogInterface, which: Int ->
            if (type == 2) {
                dialog.dismiss()
                finish()
            } else {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    fun showToast(message: String?) {
        val toast = Toast(this@GBPSharedLinkActivity)
        val view = LayoutInflater.from(this@GBPSharedLinkActivity)
            .inflate(R.layout.toast_layout, null)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        tvMessage.text = message
        toast.view = view
        toast.show()
    }
}