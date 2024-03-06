package com.whitecoats.clinicplus.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.AppointmentListClickListner
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.activities.EMRCaseHistoryMoreInfoActivity
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.EMRAddRecordsClickListener
import com.whitecoats.clinicplus.models.EMRConsultCaseHistoryModel
import com.whitecoats.clinicplus.models.ShowEditAndDeleteButtons
import com.whitecoats.fragments.AppointmentFragment.Companion.isMoreData
import org.json.JSONObject

class EMRConsultCaseHistoryListAdapter(
    private var context: Context,
    private val EMRConsultCaseHistoryModelList: List<EMRConsultCaseHistoryModel>,
    private val activity: Activity,
    groupData: ArrayList<Int>,
    editAndDeleteButtons: List<ShowEditAndDeleteButtons>,
    patientName: String,
    emtAddRecordsClickListener: EMRAddRecordsClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val appUtils: AppUtilities
    private val groupNo = -1
    private val summaryDuration: TextView? = null
    private val popupWindow: PopupWindow? = null
    private val durationList: ArrayList<String>? = null
    private val selectedApptType: String? = null
    private val apptId = 0
    private val otpLoading: ProgressDialog? = null
    private val jsonValue: JSONObject? = null
    private var appointmentListner: AppointmentListClickListner ?=null
    private val emtAddRecordsClickListener: EMRAddRecordsClickListener
    private val appPreference: SharedPreferences
    private val isGroupDataDisplay = 0
    private val groupData: ArrayList<Int>
    private val appUtilities: AppUtilities
    private val orderUserIdData = 0
    private val receiptUrl: String? = null
    private val patientName: String
    private val callCurrentState = 0
    var globalClass: MyClinicGlobalClass
    private val editAndDeleteButtons: List<ShowEditAndDeleteButtons>

    init {
        appointmentListner = appointmentListner
        this.emtAddRecordsClickListener = emtAddRecordsClickListener
        this.groupData = groupData
        appUtilities = AppUtilities()
        appPreference = activity.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
        globalClass = context.applicationContext as MyClinicGlobalClass
        appUtils = AppUtilities()
        this.patientName = patientName
        this.editAndDeleteButtons = editAndDeleteButtons
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
//        View itemView = LayoutInflater.from(viewGroup.getContext())
//                .inflate(R.layout.list_row_appointment_appt, viewGroup, false);
        if (i == TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_emr_consult_case_history, viewGroup, false)
            context = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_HEADER) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_emr_consult_case_history, viewGroup, false)
            context = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_FOOTER) {
            val view = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.activity_path_orderview_footer,
                viewGroup, false
            )
            return FooterViewHolder(view)
        }
        throw RuntimeException("there is no type that matches the type $i + make sure your using types correctly")


        // return new AppointmentApptListAdapter.MyViewHolder(itemView);
    }

    override fun onBindViewHolder(myViewHolder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") i: Int) {
        myViewHolder.itemView.tag = EMRConsultCaseHistoryModelList.get(i)
        if (myViewHolder is MyViewHolder) {
            try {
                val itemViewHolder = myViewHolder
                val emrConsultCaseHistoryModel = EMRConsultCaseHistoryModelList[i]
                if (groupData[i] == 0) {
                    itemViewHolder.dateGroup.visibility = View.VISIBLE
                    if (emrConsultCaseHistoryModel.enableSeparatorLine == 1) {
                        itemViewHolder.sepratorLayout.visibility = View.VISIBLE
                    } else {
                        itemViewHolder.sepratorLayout.visibility = View.GONE
                    }
                    if (emrConsultCaseHistoryModel.isRecordData == 1) {
                        itemViewHolder.isNoRecordData.visibility = View.GONE
                        itemViewHolder.isRecordData.visibility = View.VISIBLE
                        itemViewHolder.dateCreatedAtLayout.visibility = View.VISIBLE
                    } else {
                        itemViewHolder.isNoRecordData.visibility = View.VISIBLE
                        itemViewHolder.isRecordData.visibility = View.GONE
                        itemViewHolder.dateCreatedAtLayout.visibility = View.GONE
                    }
                } else {
                    itemViewHolder.dateGroup.visibility = View.GONE
                    if (emrConsultCaseHistoryModel.enableSeparatorLine == 1) {
                        itemViewHolder.sepratorLayout.visibility = View.VISIBLE
                    } else {
                        itemViewHolder.sepratorLayout.visibility = View.GONE
                    }
                    if (emrConsultCaseHistoryModel.isRecordData == 1) {
                        itemViewHolder.isNoRecordData.visibility = View.GONE
                        itemViewHolder.isRecordData.visibility = View.VISIBLE
                        itemViewHolder.dateCreatedAtLayout.visibility = View.VISIBLE
                    } else {
                        itemViewHolder.isNoRecordData.visibility = View.VISIBLE
                        itemViewHolder.isRecordData.visibility = View.GONE
                        itemViewHolder.dateCreatedAtLayout.visibility = View.GONE
                    }
                }
                itemViewHolder.categoryName.text = emrConsultCaseHistoryModel.categoryName
                if (emrConsultCaseHistoryModel.createdAt != null) {
                    val currentStringStart = emrConsultCaseHistoryModel.createdAt
                    val separatedDate =
                        currentStringStart.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val createDate = separatedDate[0]
                    val date = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", createDate)
                    val createTime = separatedDate[1].substring(0, separatedDate[1].length - 3)
                    itemViewHolder.categoryCreatedAt.text =
                        "$date " + appUtils.formatTimeBasedOnPreference(
                            context, "HH:mm", createTime
                        )
                }
                if (emrConsultCaseHistoryModel.encounterDateTime != null) {
                    val interactionCreatedOn = emrConsultCaseHistoryModel.encounterDateTime
                    val separatedInteractionDate =
                        interactionCreatedOn.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val createInteractionDate = separatedInteractionDate[0]
                    val interactionDate = appUtilities.changeDateFormat(
                        "yyyy-MM-dd",
                        "dd MMM, yy",
                        createInteractionDate
                    )
                    val createdInteractionTime = separatedInteractionDate[1].substring(
                        0,
                        separatedInteractionDate[1].length - 3
                    )
                    val formattedTime = appUtils.formatTimeBasedOnPreference(
                        context, "HH:mm", createdInteractionTime
                    )
                    itemViewHolder.interactionName.text =
                        emrConsultCaseHistoryModel.encounterName + " on " + interactionDate + " @ " + formattedTime
                }
                itemViewHolder.isRecordData.setOnClickListener(View.OnClickListener {
                    try {
                        for (k in editAndDeleteButtons.indices) {
                            if (editAndDeleteButtons[k].id == emrConsultCaseHistoryModel.encounterID) {
                                val intent =
                                    Intent(activity, EMRCaseHistoryMoreInfoActivity::class.java)
                                intent.putExtra(
                                    "CatId",
                                    emrConsultCaseHistoryModel.categoryId.toString()
                                )
                                intent.putExtra(
                                    "RecordId",
                                    emrConsultCaseHistoryModel.recordId.toString() + ""
                                )
                                intent.putExtra(
                                    "RecordDetail",
                                    emrConsultCaseHistoryModel.categoryRecordData
                                )
                                intent.putExtra(
                                    "RecordField",
                                    emrConsultCaseHistoryModel.fieldDictionary
                                )
                                intent.putExtra("CatName", emrConsultCaseHistoryModel.categoryName)
                                intent.putExtra("mode", emrConsultCaseHistoryModel.encounterName)
                                intent.putExtra(
                                    "interactionDate",
                                    emrConsultCaseHistoryModel.encounterDateTime
                                )
                                intent.putExtra("addedOnDate", emrConsultCaseHistoryModel.createdAt)
                                intent.putExtra(
                                    "multiRecordPosition",
                                    emrConsultCaseHistoryModel.multiRecordPosition
                                )
                                intent.putExtra(
                                    "canDeleteEditRecords",
                                    editAndDeleteButtons[k].isCanDeleteEditRecords
                                )
                                intent.putExtra(
                                    "canDeleteEditWrittenNotes",
                                    editAndDeleteButtons[k].isCanDeleteEditWrittenNotes
                                )
                                val dynamicEncounterIds = JSONObject()
                                dynamicEncounterIds.put(
                                    "encounter_id",
                                    editAndDeleteButtons[k].encounter_Id
                                )
                                dynamicEncounterIds.put(
                                    "episode_id",
                                    editAndDeleteButtons[k].episode_id
                                )
                                dynamicEncounterIds.put(
                                    "patient_id",
                                    editAndDeleteButtons[k].patient_id
                                )
                                dynamicEncounterIds.put(
                                    "doctor_id",
                                    editAndDeleteButtons[k].doctor_id
                                )
                                intent.putExtra(
                                    "dynamicEncounterDataIds",
                                    dynamicEncounterIds.toString()
                                )
                                intent.putExtra("PatientName", patientName)
                                intent.putExtra(
                                    "categoryType",
                                    emrConsultCaseHistoryModel.categoryType
                                )
                                activity.startActivity(intent)
                                break
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
                itemViewHolder.addingRecords.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        emtAddRecordsClickListener.onItemClick(
                            view,
                            i,
                            emrConsultCaseHistoryModel.encounterID,
                            "ADD_RECORDS",
                            emrConsultCaseHistoryModel.encounterName,
                            emrConsultCaseHistoryModel.encounterDateTime
                        )
                    }
                })
                itemViewHolder.isNoRecordData.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        emtAddRecordsClickListener.onItemClick(
                            view,
                            i,
                            emrConsultCaseHistoryModel.encounterID,
                            "ADD_RECORDS",
                            emrConsultCaseHistoryModel.encounterName,
                            emrConsultCaseHistoryModel.encounterDateTime
                        )
                    }
                })
            } catch (e: Exception) {
                e.message
            }
        } else if (myViewHolder is FooterViewHolder) {
            footerHolder = myViewHolder
            if (isMoreData) {
                myViewHolder.footerText.visibility = View.VISIBLE
            } else {
                myViewHolder.footerText.visibility = View.GONE
            }
            footerHolder!!.footerText.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    appointmentListner?.onItemClick(view, "LOADMORE", null)
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return EMRConsultCaseHistoryModelList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (isPositionHeader(position)) {
            return TYPE_HEADER
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER
        }
        return TYPE_ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    private fun isPositionFooter(position: Int): Boolean {
        return if (position >= (EMRConsultCaseHistoryModelList.size - 1) && EMRConsultCaseHistoryModelList.size >= 50) { // && data.size() >= 10 && appointmentsActivity.isMoreData
            true
        } else {
            false
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryName: TextView
        var categoryCreatedAt: TextView
        var apptDate: TextView? = null
        var interactionName: TextView
        var apptAttendLabel //completeJoinText
                : TextView? = null
        var header: RelativeLayout
        var dateGroup: RelativeLayout
        var sepratorLayout: RelativeLayout
        var dateCreatedAtLayout: RelativeLayout
        var isNoRecordData: RelativeLayout
        var addingRecords: RelativeLayout
        var isRecordData: CardView
        var details: LinearLayout? = null
        var arrowIcon: ImageView? = null
        var typeIcon: ImageView? = null

        //        private CardView detailsCard;
        var apptClinicName: TextView? = null
        var apptClinicAddress: TextView? = null
        var apptListCatAssignText //apptListCatAssign, apptListTypeAction, apptListPaymentAction
                : TextView? = null

        init {
            categoryName = itemView.findViewById(R.id.categoryName)
            categoryCreatedAt = itemView.findViewById(R.id.createdAtDate)
            header = itemView.findViewById(R.id.apptListHeaderLayout)
            //            details = itemView.findViewById(R.id.apptListDetailLayout);
//            arrowIcon = itemView.findViewById(R.id.apptListArrowIcon);
            dateGroup = itemView.findViewById(R.id.apptListDateGroupLayout)
            sepratorLayout = itemView.findViewById(R.id.sepratorLayout)
            isNoRecordData = itemView.findViewById(R.id.isNoRecordData)
            isRecordData = itemView.findViewById(R.id.isRecordData)
            dateCreatedAtLayout = itemView.findViewById(R.id.dateCreatedAtLayout)
            interactionName = itemView.findViewById(R.id.interactionName)
            addingRecords = itemView.findViewById(R.id.addingRecords)
            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    val cpu = view.tag as EMRConsultCaseHistoryModel
                    // Toast.makeText(view.getContext(), cpu.getPersonName()+" is "+ cpu.getJobProfile(), Toast.LENGTH_SHORT).show();
                }
            })
        }
    }

    inner class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var footerText: TextView
        var bottomSpace: View

        init {
            footerText = view.findViewById(R.id.footer_text)
            bottomSpace = view.findViewById(R.id.bottomSpace)
        }
    }

    inner class HeaderViewHolder(var headerView: View) : RecyclerView.ViewHolder(
        headerView
    )

    companion object {
        private val TYPE_HEADER = 0
        private val TYPE_ITEM = 1
        private val TYPE_FOOTER = 2
        var footerHolder: FooterViewHolder? = null
        var flagAppointmentListAdp = 0
        var flagAppointmentListAdpShowReceipt = 0
    }
}