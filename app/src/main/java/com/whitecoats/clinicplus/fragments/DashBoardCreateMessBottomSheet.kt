package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.PorterDuff
import android.view.View
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.AddPatientViewModel
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import com.whitecoats.model.AddPatientModel
import org.json.JSONException
import org.json.JSONObject

class DashBoardCreateMessBottomSheet : BottomSheetDialogFragment() {
    private var commMessagActivity: DashboardFullMode? = null
    private lateinit var cancelIcon: ImageView
    private lateinit var bottomSheetMessageText: EditText
    private lateinit var bottomSheetToggleLayout: RelativeLayout
    private lateinit var switchButton: SwitchMaterial
    private lateinit var bottomSheetDone: RelativeLayout
    private lateinit var bottomSheetSendMessage: Button
    private lateinit var interFaceSpinner: Spinner
    private lateinit var selectInterface: RelativeLayout
    var addInterfaceModelList: MutableList<AddPatientModel>? = null
    private var interFaceId = 0
    private lateinit var bottomSheetMessageNote: RelativeLayout
    private var hasInterface = false
    private var addPatientViewModel: AddPatientViewModel? = null
    private var dashboardViewModel: DashboardViewModel? = null
    fun setupConfig(commMessagActivity: DashboardFullMode?) {
        this.commMessagActivity = commMessagActivity
        addInterfaceModelList = ArrayList()
    }

    //Bottom Sheet Callback
    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
//        super.setupDialog(dialog, style);
        //Get the content View
        val contentView =
            View.inflate(context, R.layout.fragment_bottom_sheet_comm_create_message, null)
        dialog.setContentView(contentView)
        //Set the coordinator layout behavior
        addPatientViewModel = ViewModelProvider(this@DashBoardCreateMessBottomSheet)[AddPatientViewModel::class.java]
        addPatientViewModel!!.init()
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        dashboardViewModel!!.init()
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon)
        bottomSheetMessageText = contentView.findViewById(R.id.bottomSheetMessageText)
        bottomSheetToggleLayout = contentView.findViewById(R.id.bottomSheetToggleIcon)
        interFaceSpinner = contentView.findViewById(R.id.interFaceSpinner)
        selectInterface = contentView.findViewById(R.id.selectInterface)
        bottomSheetSendMessage = contentView.findViewById(R.id.bottomSheetSendMessage)
        bottomSheetMessageNote = contentView.findViewById(R.id.bottomSheetMessageNote)
        switchButton = contentView.findViewById(R.id.switchButton)
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            hasInterface = if (isChecked) {
                // The toggle is enabled
                bottomSheetMessageNote.visibility = View.GONE
                selectInterface.visibility = View.VISIBLE
                true
            } else {
                // The toggle is disabled
                selectInterface.visibility = View.GONE
                bottomSheetMessageNote.visibility = View.VISIBLE
                false
            }
        }
        bottomSheetDone = contentView.findViewById(R.id.bottomSheetDone)
        bottomSheetSendMessage.setOnClickListener {
            if (bottomSheetMessageText.text.toString().equals("", ignoreCase = true)) {
                Toast.makeText(
                    commMessagActivity!!.activity,
                    "Please write message.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                sendMessage(dialog)
            }
        }
        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        cancelIcon.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        cancelIcon.setOnClickListener { dialog.dismiss() }
        addPatientViewModel!!.getInterfaceDetails(activity)
            .observe(this) { s ->
                try {
                    val response = JSONObject(s).getJSONObject("response")
                    val rootObj = response.getJSONArray("response")
                    for (j in 0 until rootObj.length()) {
                        val appointmentJsonObject = rootObj.getJSONObject(j)
                        if (!appointmentJsonObject.isNull("parentinterf")) {
                            val parentInterfaceObject =
                                appointmentJsonObject.getJSONObject("parentinterf")
                            val model = AddPatientModel()
                            model.interfaceId = parentInterfaceObject.getInt("id")
                            model.interfaceName =
                                parentInterfaceObject.getString("interface_name")
                            addInterfaceModelList!!.add(model)
                        }
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        addInterfaceModelList!!
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    interFaceSpinner.adapter = adapter

                    // Set the ClickListener for Spinner
                    interFaceSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            adapterView: AdapterView<*>?,
                            view: View, i: Int, l: Long
                        ) {
                            // TODO Auto-generated method stub
                            //                                    Toast.makeText(MainActivity.this,"You Selected : "
                            //                                            + difficultyLevelOptionsList.get(i)+" Level ",Toast.LENGTH_SHORT).show();
                            interFaceId = addInterfaceModelList!![i].interfaceId
                        }

                        // If no option selected
                        override fun onNothingSelected(arg0: AdapterView<*>?) {
                            // TODO Auto-generated method stub
                        }
                    }

                    //                    patientInterfaceAdapter.notifyDataSetChanged();
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    private fun sendMessage(dialog: Dialog) {
        dashboardViewModel!!.sendMessageDetails(
            activity,
            bottomSheetMessageText.text.toString(),
            interFaceId,
            hasInterface
        ).observe(
            requireActivity()
        ) { s ->
            try {
                val jsonObject = JSONObject(s)
                if (jsonObject.getInt("status_code") == 200) {
                    val response = JSONObject(s).getJSONObject("response")
                    val createMessageResponse = response.getBoolean("response")
                    if (createMessageResponse) {
                        Toast.makeText(
                            commMessagActivity!!.activity,
                            "Message sent successfully.",
                            Toast.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                    }
                } else {
                    errorHandler(requireContext(), s)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}