package com.whitecoats.clinicplus.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.activities.EMRSharedRecordDetails
import com.whitecoats.clinicplus.adapters.EMRSharedRecordsAdapter.EMRSharedRecordsViewHolder
import com.whitecoats.clinicplus.models.EMRSharedRecordModel
import org.json.JSONObject

/**
 * Created by Mohammad suhail ahmed on 28-07-2020.
 */
class EMRSharedRecordsAdapter(
    private val context: Context,
    private var emrSharedRecordModelList: List<EMRSharedRecordModel>,
    private var recordsGroup: List<Int>,
    private var noOfRecords: Map<String, Int>,
    private var fieldDictionary: JSONObject
) : RecyclerView.Adapter<EMRSharedRecordsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EMRSharedRecordsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.shared_record_item, parent, false)
        return EMRSharedRecordsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EMRSharedRecordsViewHolder, position: Int) {
        val emrSharedRecordModel = emrSharedRecordModelList[position]
        if (recordsGroup[position] == 0) {
            holder.sharedRecordHeader.visibility = View.VISIBLE
            holder.sharedRecordDate.text = emrSharedRecordModel.headerDate
            holder.sharedRecordCount.text =
                "( " + noOfRecords[emrSharedRecordModel.headerDate] + " Records) "
        } else {
            holder.sharedRecordHeader.visibility = View.GONE
        }
        holder.sharedRecordItemDate.text = emrSharedRecordModel.dateTime
        holder.sharedRecordTitle.text = emrSharedRecordModel.category
        if (noOfRecords[emrSharedRecordModel.headerDate]!! - 1 == recordsGroup[position]) {
            holder.sharedRecordFooter.visibility = View.VISIBLE
        } else {
            holder.sharedRecordFooter.visibility = View.GONE
        }
        holder.sharedRecordItem.setOnClickListener {
            val intent = Intent(context, EMRSharedRecordDetails::class.java)
            intent.putExtra("categoryID", emrSharedRecordModel.categoryId)
            intent.putExtra("RecordId", emrSharedRecordModel.recordId.toString() + "")
            intent.putExtra("RecordDetail", emrSharedRecordModel.record.toString())
            intent.putExtra("fieldDictionary", fieldDictionary.toString())
            //                intent.putExtra("sharedOn",emrSharedRecordModel.getDateTime());
            intent.putExtra("sharedOn", emrSharedRecordModel.createdAt)
            intent.putExtra("CatName", emrSharedRecordModel.category)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return emrSharedRecordModelList.size
    }

    fun updateAdapter(
        emrSharedRecordModels: List<EMRSharedRecordModel>,
        recordsGroup: List<Int>,
        noOfRecords: Map<String, Int>,
        fieldDictionary: JSONObject
    ) {
        emrSharedRecordModelList = emrSharedRecordModels
        this.recordsGroup = recordsGroup
        this.noOfRecords = noOfRecords
        this.fieldDictionary = fieldDictionary
        notifyDataSetChanged()
    }

    inner class EMRSharedRecordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sharedRecordHeader: LinearLayout
        private val sharedRecordData: LinearLayout
        val sharedRecordItem: LinearLayout
        val sharedRecordFooter: ImageView
        val sharedRecordDate: TextView
        val sharedRecordCount: TextView
        val sharedRecordItemDate: TextView
        val sharedRecordTitle: TextView

        init {
            sharedRecordHeader = itemView.findViewById(R.id.shared_item_header)
            sharedRecordData = itemView.findViewById(R.id.shared_record_data)
            sharedRecordFooter = itemView.findViewById(R.id.shared_record_footer)
            sharedRecordItemDate = itemView.findViewById(R.id.shared_record_item_date)
            sharedRecordDate = itemView.findViewById(R.id.shared_record_date)
            sharedRecordCount = itemView.findViewById(R.id.shared_record_count)
            sharedRecordTitle = itemView.findViewById(R.id.shared_record_title)
            sharedRecordItem = itemView.findViewById(R.id.shared_record_item)
        }
    }
}