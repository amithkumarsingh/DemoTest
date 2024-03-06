package com.whitecoats.clinicplus.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.activities.EMRAddNotesActivity
import com.whitecoats.clinicplus.activities.EMRCaseHistoryMoreInfoActivity
import com.whitecoats.clinicplus.models.EMRConsultCaseHistoryModel

/**
 * Created by Mohammad suhail ahmed on 28-08-2020.
 */
class EMRAddingNotesAdapter(
    private val context: Context,
    private val caseDetailsList: List<EMRConsultCaseHistoryModel>,
    canDeleteEditRecords: Boolean,
    canDeleteEditWrittenNotes: Boolean,
    patientName: String,
    dynamicEncounterIds: String
) : RecyclerView.Adapter<EMRAddingNotesAdapter.EMRAddingNotesViewHolder>() {
    private val appUtils: AppUtilities = AppUtilities()
    private val emrAddNotesActivity: EMRAddNotesActivity = context as EMRAddNotesActivity
    private val canDeleteEditWrittenNotes: Boolean
    private val canDeleteEditRecords: Boolean
    private val patientName: String
    private val dynamicEncounterIds: String

    init {
        this.canDeleteEditRecords = canDeleteEditRecords
        this.canDeleteEditWrittenNotes = canDeleteEditWrittenNotes
        this.patientName = patientName
        this.dynamicEncounterIds = dynamicEncounterIds
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EMRAddingNotesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.case_details_item, parent, false)
        return EMRAddingNotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: EMRAddingNotesViewHolder, position: Int) {
        val emrConsultCaseHistoryModel = caseDetailsList[position]
        holder.caseTitle.text = emrConsultCaseHistoryModel.categoryName
        if (emrConsultCaseHistoryModel.createdAt != null) {
            val interactionCreatedOn = emrConsultCaseHistoryModel.createdAt
            val separatedInteractionDate =
                interactionCreatedOn.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val createInteractionDate = separatedInteractionDate[0]
            val interactionDate =
                AppUtilities().changeDateFormat("yyyy-MM-dd", "dd MMM, yy", createInteractionDate)
            val createdInteractionTime =
                separatedInteractionDate[1].substring(0, separatedInteractionDate[1].length - 3)
            holder.caseDate.text = "$interactionDate " + appUtils.formatTimeBasedOnPreference(
                context, "HH:mm", createdInteractionTime
            )
        }
        holder.caseItemLayout.setOnClickListener(View.OnClickListener {
            val intent = Intent(emrAddNotesActivity, EMRCaseHistoryMoreInfoActivity::class.java)
            intent.putExtra("CatId", emrConsultCaseHistoryModel.categoryId.toString())
            intent.putExtra("RecordId", emrConsultCaseHistoryModel.recordId.toString() + "")
            intent.putExtra("RecordDetail", emrConsultCaseHistoryModel.categoryRecordData)
            intent.putExtra("RecordField", emrConsultCaseHistoryModel.fieldDictionary)
            intent.putExtra("CatName", emrConsultCaseHistoryModel.categoryName)
            intent.putExtra("mode", emrConsultCaseHistoryModel.encounterName)
            intent.putExtra("interactionDate", emrConsultCaseHistoryModel.encounterDateTime)
            intent.putExtra("addedOnDate", emrConsultCaseHistoryModel.createdAt)
            intent.putExtra("multiRecordPosition", emrConsultCaseHistoryModel.multiRecordPosition)
            intent.putExtra("canDeleteEditRecords", canDeleteEditRecords)
            intent.putExtra("canDeleteEditWrittenNotes", canDeleteEditWrittenNotes)
            intent.putExtra("dynamicEncounterDataIds", dynamicEncounterIds)
            intent.putExtra("PatientName", patientName)
            intent.putExtra("categoryType", emrConsultCaseHistoryModel.categoryType)
            emrAddNotesActivity.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return caseDetailsList.size
    }

    inner class EMRAddingNotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val caseDate: TextView
        val caseTitle: TextView
        val caseItemLayout: LinearLayout

        init {
            caseDate = itemView.findViewById(R.id.case_item_date)
            caseTitle = itemView.findViewById(R.id.case_record_title)
            caseItemLayout = itemView.findViewById(R.id.case_record_item)
        }
    }
}