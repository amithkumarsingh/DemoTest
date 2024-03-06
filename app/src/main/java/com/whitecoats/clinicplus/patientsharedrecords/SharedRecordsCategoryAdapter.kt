package com.whitecoats.clinicplus.patientsharedrecords

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.PatientRecordListener
import com.whitecoats.clinicplus.R
import com.whitecoats.model.PatientRecordsModel

class SharedRecordsCategoryAdapter(
    private val patientRecordsModels: List<PatientRecordsModel>,
    private val activity: Activity,
    private val recordListener: PatientRecordListener
) : RecyclerView.Adapter<SharedRecordsCategoryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_records_category, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val recordsModel = patientRecordsModels[i]

        //show data of shared records
        if (recordsModel.type == 2) { //2 is for shared record data
            myViewHolder.recordLayout.visibility = View.VISIBLE
            myViewHolder.recordEpisLayout.visibility = View.GONE
            myViewHolder.recordCategoryName.text = recordsModel.recordName
            myViewHolder.recordCount.text = recordsModel.recordCount.toString() + ""
            myViewHolder.recordLayout.setOnClickListener { view ->
                recordListener.onItemClick(
                    view,
                    recordsModel.recordId.toString() + "_" + recordsModel.recordName,
                    "TO_DETAILS",
                    recordsModel.recordIdArray.toString()
                )
            }
        } else {
            myViewHolder.recordLayout.visibility = View.GONE
            myViewHolder.recordEpisLayout.visibility = View.VISIBLE
            myViewHolder.recordEpisName.text = recordsModel.episodeName
            myViewHolder.recordEpisLayout.setOnClickListener { view ->
                recordListener.onItemClick(
                    view,
                    recordsModel.episodeId.toString() + "",
                    "TO_EPISODE_DETAILS",
                    ""
                )
            }
            myViewHolder.recordEpisShare.setOnClickListener { view ->
                recordListener.onItemClick(
                    view,
                    recordsModel.episodeId.toString() + "",
                    "TO_SHARE_DATA",
                    ""
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return patientRecordsModels.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //shared record
        val recordCategoryName: TextView
        val recordCount: TextView
        val recordLayout: RelativeLayout

        //record episodes
        val recordEpisLayout: RelativeLayout
        val recordEpisName: TextView
        val recordEpisShare: ImageButton

        init {

            //shared data
            recordCategoryName = itemView.findViewById(R.id.recordCategoryNameTv)
            recordCount = itemView.findViewById(R.id.recordCategoryCountv)
            recordLayout = itemView.findViewById(R.id.recordCategoryLayout)

            //record Episode
            recordEpisLayout = itemView.findViewById(R.id.recordEpisodesLayout)
            recordEpisName = itemView.findViewById(R.id.recordEpisodesNameTv)
            recordEpisShare = itemView.findViewById(R.id.recordEpisodesShareBtn)
        }
    }
}