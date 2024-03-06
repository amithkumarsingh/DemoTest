package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.adapter.CategoryGridViewCustomAdapter
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.EMRConsultationNotesFragment
import com.whitecoats.clinicplus.interfaces.EMRSwitchCaseClickListener
import com.whitecoats.clinicplus.models.EMRAllEpisodeModel
import com.whitecoats.clinicplus.trainingschedule.TrainingScheduleActivity
import org.json.JSONObject

class EMRAllCasesListAdapter(
    private var context: Context,
    private val emrAllCasesModelList: List<EMRAllEpisodeModel>,
    private val activity: Activity,
    private val groupData: ArrayList<Int>,
    private val emrSwitchCaseClickListener: EMRSwitchCaseClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val groupNo = -1
    private val popupWindow: PopupWindow? = null
    private val otpLoading: ProgressDialog? = null
    private val jsonValue: JSONObject? = null
    private val mListAdapter: CategoryGridViewCustomAdapter? = null
    private val appPreference: SharedPreferences
    private val appUtilities: AppUtilities
    private var diagnosisData: String? = null
    private var selectedPosition = -1

    init {
        appUtilities = AppUtilities()
        appPreference = context.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (i == TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_emr_all_cases, viewGroup, false)
            context = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_HEADER) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_emr_all_cases, viewGroup, false)
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

    override fun onBindViewHolder(myViewHolder: RecyclerView.ViewHolder, i: Int) {
        myViewHolder.itemView.tag = emrAllCasesModelList[i]
        if (myViewHolder is MyViewHolder) {
            try {
                val itemViewHolder = myViewHolder
                val appointmentApptListModel = emrAllCasesModelList[i]
                itemViewHolder.selectCaseRadioButton.isChecked =
                    i == EMRConsultationNotesFragment.caseSelectedPosition
                itemViewHolder.selectCaseRadioButton.tag = i
                if (i == EMRConsultationNotesFragment.caseSelectedPosition) {
                    itemViewHolder.selectCaseRadioButton.text = " Selected Case"
                    itemViewHolder.selectCaseRadioButton.setTextColor(activity.resources.getColor(R.color.colorAccent))
                } else {
                    itemViewHolder.selectCaseRadioButton.text = " View Case"
                    itemViewHolder.selectCaseRadioButton.setTextColor(activity.resources.getColor(R.color.colorBlack))
                }
                itemViewHolder.caseName.text = appointmentApptListModel.episodeName
                val createdOnDate = appUtilities.changeDateFormat(
                    "yyyy-MM-dd hh:mm:ss",
                    "dd MMM, yy",
                    appointmentApptListModel.createdOn
                )
                itemViewHolder.createdOnDate.text = createdOnDate
                itemViewHolder.numberOfInteraction.text =
                    appointmentApptListModel.numberOfInteraction.toString() + ""
                val lastInteractionOnDate = appUtilities.changeDateFormat(
                    "yyyy-MM-dd hh:mm:ss",
                    "dd MMM, yy",
                    appointmentApptListModel.lastInteractionOn
                )
                itemViewHolder.lastInteractionOn.text = lastInteractionOnDate
                val sb = StringBuilder()
                diagnosisData = if (appointmentApptListModel.diagnosis.length() > 0) {
                    for (k in 0 until appointmentApptListModel.diagnosis.length()) {
                        val categoryObject = appointmentApptListModel.diagnosis.getJSONObject(k)
                        val str = categoryObject.getString("diagnosis")
                        sb.append(str)
                        sb.append(",")
                    }
                    sb.deleteCharAt(sb.length - 1)
                    sb.toString()
                } else {
                    "NA"
                }
                itemViewHolder.diagnosis.text = diagnosisData
                if (appointmentApptListModel.status == 1) {
                    itemViewHolder.status.text = "Open"
                    itemViewHolder.status.setTextColor(activity.resources.getColor(R.color.colorGreen3))
                } else {
                    itemViewHolder.status.text = "Close"
                    itemViewHolder.status.setTextColor(activity.resources.getColor(R.color.colorDanger))
                }
                itemViewHolder.selectCaseRadioButton.setOnClickListener { view ->
                    itemCheckChanged(
                        view
                    )
                }
            } catch (e: Exception) {
                e.message
            }
        } else if (myViewHolder is FooterViewHolder) {
            footerHolder = myViewHolder
            if (TrainingScheduleActivity.isMoreData) {
                myViewHolder.footerText.visibility = View.VISIBLE
            } else {
                myViewHolder.footerText.visibility = View.GONE
            }
            footerHolder!!.footerText.setOnClickListener {
                //                    emrSwitchCaseClickListener.onItemClick(view, "LOADMORE", i, null);
                //   Toast.makeText(context, "You clicked at Load More", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun getItemCount(): Int {
        return emrAllCasesModelList.size
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
        return if (position >= emrAllCasesModelList.size - 1 && emrAllCasesModelList.size >= 50) { // && data.size() >= 10 && appointmentsActivity.isMoreData
            true
        } else {
            false
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var caseName: TextView
        var createdOnDate: TextView
        var numberOfInteraction: TextView
        var lastInteractionOn: TextView
        var diagnosis: TextView
        var status: TextView
        var selectCaseRadioButton: RadioButton
        var dateGroup: RelativeLayout? = null
        var details: LinearLayout? = null
        private val trainingTitle: TextView? = null
        private val slotText: TextView? = null
        private val trainingDescriptionInfoButton: ImageView? = null
        private val bookLayout: RelativeLayout? = null
        private val rescheduleText: TextView? = null
        private val cancelText: TextView? = null
        private val cancelledText: TextView? = null
        private val completedText: TextView? = null
        private val postpondText: TextView? = null
        private val trainingDateValue: TextView? = null

        init {
            caseName = itemView.findViewById(R.id.caseName)
            createdOnDate = itemView.findViewById(R.id.createdOnDate)
            numberOfInteraction = itemView.findViewById(R.id.numberOfInteraction)
            lastInteractionOn = itemView.findViewById(R.id.lastInteractionOn)
            diagnosis = itemView.findViewById(R.id.diagnosis)
            status = itemView.findViewById(R.id.status)
            selectCaseRadioButton =
                itemView.findViewById<View>(R.id.selectCaseRadioButton) as RadioButton

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // Toast.makeText(view.getContext(), cpu.getPersonName()+" is "+ cpu.getJobProfile(), Toast.LENGTH_SHORT).show();
//
//                }
//            });
        }
    }

    private fun onItemClickListener(
        myViewHolder: MyViewHolder, appointmentId: Int
    ): AdapterView.OnItemClickListener {
        return AdapterView.OnItemClickListener { parent, view, position, id -> }
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

    //On selecting any view set the current position to selectedPositon and notify adapter
    private fun itemCheckChanged(v: View) {
        selectedPosition = v.tag as Int
        EMRConsultationNotesFragment.caseSelectedPosition = (v.tag as Int)
        //        DashboardFullMode.switchClinicSelectedString = arrayList.get(selectedPosition).clinicName;
        emrSwitchCaseClickListener.onItemClick(v, selectedPosition)
        notifyDataSetChanged()
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_FOOTER = 2
        var footerHolder: FooterViewHolder? = null
    }
}