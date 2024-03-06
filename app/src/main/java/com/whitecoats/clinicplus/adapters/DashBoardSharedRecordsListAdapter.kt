package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.PatientRecordListener
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.activities.EMRSharedRecordDetails
import com.whitecoats.clinicplus.models.DashBoardPatientRecordsModel

class DashBoardSharedRecordsListAdapter(
    private val activity: Activity,
    private val dashBoardPatientRecordsModels: List<DashBoardPatientRecordsModel>,
    private val patientRecordListener: PatientRecordListener
) : RecyclerView.Adapter<DashBoardSharedRecordsListAdapter.MyViewHolder>() {
    private val appUtilities: AppUtilities = AppUtilities()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_home_shared_records_new, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val recordsModel = dashBoardPatientRecordsModels[i]

        //getting patient name from getRecordsName Method
        val timeFormat: String = if (appUtilities.timeFormatPreferences(activity) == 12) {
            "dd MMM, yy hh:mm aa"
        } else {
            "dd MMM, yy HH:mm"
        }
        val date = appUtilities.changeDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            timeFormat,
            recordsModel.created_At
        )
        myViewHolder.createdDateText.text = date
        myViewHolder.patientName.text = recordsModel.recordName
        myViewHolder.recordSharedCategoryName.text = recordsModel.catName
        myViewHolder.priValue.text = recordsModel.primaryData
        myViewHolder.priLabel.text = recordsModel.primaryKey
        myViewHolder.secValue.text = recordsModel.secData
        myViewHolder.secLabel.text = recordsModel.secKey
        myViewHolder.terValue.text = recordsModel.ternaryData
        myViewHolder.terLabel.text = recordsModel.ternaryKey
        myViewHolder.priValue.setTextColor(ContextCompat.getColor(activity, R.color.colorBlack))
        if (recordsModel.primaryKey.contains("Attachment")) {
            myViewHolder.priValue.text = activity.resources.getString(R.string.view)
            myViewHolder.priValue.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            if (recordsModel.fileUrl.equals("", ignoreCase = true)) {
                myViewHolder.priValue.text = activity.resources.getString(R.string.na)
                myViewHolder.priValue.setTextColor(ContextCompat.getColor(activity, R.color.colorBlack))
            }
        }
        myViewHolder.secValue.setTextColor(ContextCompat.getColor(activity, R.color.colorBlack))
        if (recordsModel.secKey != null && recordsModel.secKey.contains("Attachment")) {
            myViewHolder.secValue.text = activity.resources.getString(R.string.view)
            myViewHolder.secValue.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            if (recordsModel.fileUrl.equals("", ignoreCase = true)) {
                myViewHolder.secValue.text = activity.resources.getString(R.string.na)
                myViewHolder.secValue.setTextColor(ContextCompat.getColor(activity, R.color.colorBlack))
            }
        }
        myViewHolder.terValue.setTextColor(ContextCompat.getColor(activity, R.color.colorBlack))
        if (recordsModel.ternaryKey != null && recordsModel.ternaryKey.contains("Attachment")) {
            myViewHolder.terValue.text = activity.resources.getString(R.string.view)
            myViewHolder.terValue.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            if (recordsModel.fileUrl.equals("", ignoreCase = true)) {
                myViewHolder.terValue.text = activity.resources.getString(R.string.na)
                myViewHolder.terValue.setTextColor(ContextCompat.getColor(activity, R.color.colorBlack))
            }
        }
        myViewHolder.priValue.setOnClickListener { view ->
            if (myViewHolder.priValue.text.toString()
                    .equals(activity.resources.getString(R.string.view), ignoreCase = true)
            ) {
                patientRecordListener.onItemClick(view, recordsModel.fileUrl, "FILE_URL", "")
            }
        }
        myViewHolder.secValue.setOnClickListener { view ->
            if (myViewHolder.secValue.text.toString()
                    .equals(activity.resources.getString(R.string.view), ignoreCase = true)
            ) {
                patientRecordListener.onItemClick(view, recordsModel.fileUrl, "FILE_URL", "")
            }
        }
        myViewHolder.terValue.setOnClickListener { view ->
            if (myViewHolder.terValue.text.toString()
                    .equals(activity.resources.getString(R.string.view), ignoreCase = true)
            ) {
                patientRecordListener.onItemClick(view, recordsModel.fileUrl, "FILE_URL", "")
            }
        }
        myViewHolder.viewRecord.setOnClickListener {
            shareRecordClickFlag = 1

            val intent = Intent(activity, EMRSharedRecordDetails::class.java)
            intent.putExtra("categoryID", recordsModel.categoryId)
            intent.putExtra("RecordId", recordsModel.recordId)
            intent.putExtra("RecordDetail", recordsModel.recordDetailsObject.toString())
            intent.putExtra("fieldDictionary", recordsModel.fieldDictionaryObject.toString())
            intent.putExtra("sharedOn", recordsModel.created_At)
            intent.putExtra("CatName", recordsModel.catName)
            activity.startActivity(intent)

        }
        myViewHolder.sharedRecordCardView.setOnClickListener {
            shareRecordClickFlag = 1
            val intent = Intent(activity, EMRSharedRecordDetails::class.java)
            intent.putExtra("categoryID", recordsModel.categoryId)
            intent.putExtra("RecordId", recordsModel.recordId)
            intent.putExtra("RecordDetail", recordsModel.recordDetailsObject.toString())
            intent.putExtra("fieldDictionary", recordsModel.fieldDictionaryObject.toString())
            intent.putExtra("sharedOn", recordsModel.created_At)
            intent.putExtra("CatName", recordsModel.catName)
            activity.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return dashBoardPatientRecordsModels.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientName: TextView
        val recordSharedCategoryName: TextView
        val viewRecord: TextView
        val priLabel: TextView
        val secLabel: TextView
        val terLabel: TextView
        val priValue: TextView
        val secValue: TextView
        val terValue: TextView
        val createdDateText: TextView
        val sharedRecordCardView: CardView

        init {
            patientName = itemView.findViewById(R.id.homeSharedRecordsPatientName)
            recordSharedCategoryName = itemView.findViewById(R.id.recordSharedCategoryName)
            viewRecord = itemView.findViewById(R.id.homeSharedRecordsView)
            priLabel = itemView.findViewById(R.id.homeSharedRecordsPriLabel)
            secLabel = itemView.findViewById(R.id.homeSharedRecordsSecLabel)
            terLabel = itemView.findViewById(R.id.homeSharedRecordsTerLabel)
            priValue = itemView.findViewById(R.id.homeSharedRecordsPriValue)
            secValue = itemView.findViewById(R.id.homeSharedRecordsSecValue)
            terValue = itemView.findViewById(R.id.homeSharedRecordsTerValue)
            sharedRecordCardView = itemView.findViewById(R.id.sharedRecordCardView)
            createdDateText = itemView.findViewById(R.id.createdDateText)
        }
    }

    companion object {
        @JvmField
        var shareRecordClickFlag = 0
    }
}