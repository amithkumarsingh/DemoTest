package com.whitecoats.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.casechannels.CaseChannelDashboardActivity
import com.whitecoats.model.CaseChannelModel

class HomeCaseChannelsListAdapter(
    private val activity: Activity,
    private val caseChannelModelsList: List<CaseChannelModel>?
) : RecyclerView.Adapter<HomeCaseChannelsListAdapter.ViewHolder>() {
    private val appUtilities: AppUtilities = AppUtilities()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_home_case_channels, viewGroup, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val caseModel = caseChannelModelsList!![i]
        viewHolder.homeCaseChannelsName.text = caseModel.caseChannelName
        viewHolder.homeCaseChannelsPatientName.text = caseModel.patientName
        viewHolder.homeCaseChannelsTaskCount.text = caseModel.taskCount.toString() + " Task(s)"
        viewHolder.homeCaseChannelsRecordsCount.text =
            caseModel.recordsCount.toString() + " Record(s)"
        viewHolder.homeCaseChannelsDiscussionCount.text =
            caseModel.messageCounts.toString() + " Post(s)"
        val channelDateString = caseModel.createdAt
        viewHolder.homeCaseChannelsCreateOn.text =
            appUtilities.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yy", channelDateString)
        viewHolder.homeCaseChannelsViewMore.setOnClickListener {
            val intent = Intent(activity, CaseChannelDashboardActivity::class.java)
            intent.putExtra("caseChannelId", caseModel.caseId)
            intent.putExtra("caseChannelPatientId", caseModel.patientId)
            intent.putExtra("caseChannelEpisodeId", caseModel.episodeId)
            intent.putExtra("caseChannelDoctorId", caseModel.ownerId)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return caseChannelModelsList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var homeCaseChannelsName: TextView
        var homeCaseChannelsPatientName: TextView
        var homeCaseChannelsCreateOn: TextView
        var homeCaseChannelsTaskCount: TextView
        var homeCaseChannelsRecordsCount: TextView
        var homeCaseChannelsDiscussionCount: TextView
        var homeCaseChannelsViewMore: LinearLayout

        init {
            homeCaseChannelsName = itemView.findViewById(R.id.homeCaseChannelsCaseName)
            homeCaseChannelsPatientName = itemView.findViewById(R.id.homeCaseChannelsPatientName)
            homeCaseChannelsCreateOn = itemView.findViewById(R.id.homeCaseChannelsCreateOn)
            homeCaseChannelsTaskCount = itemView.findViewById(R.id.homeCaseChannelsTaskCount)
            homeCaseChannelsRecordsCount = itemView.findViewById(R.id.homeCaseChannelsRecordsCount)
            homeCaseChannelsDiscussionCount =
                itemView.findViewById(R.id.homeCaseChannelsDiscussionCount)
            homeCaseChannelsViewMore = itemView.findViewById(R.id.homeCaseChannelsView)
        }
    }
}