package com.whitecoats.clinicplus.casechannels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.model.CaseChannelModel

class HomeCaseChannelsTaskListAdapter(
    private val activity: Activity,
    caseChannelModelsList: List<CaseChannelModel>,
    caseChannelPostDiscussionInterface: HomeCaseChannelPostDiscussionInterface
) : RecyclerView.Adapter<HomeCaseChannelsTaskListAdapter.ViewHolder>() {
    private val caseChannelModelsList: List<CaseChannelModel>
    private val appUtilities: AppUtilities = AppUtilities()
    private val caseChannelPostDiscussionInterface: HomeCaseChannelPostDiscussionInterface

    init {
        this.caseChannelModelsList = caseChannelModelsList
        this.caseChannelPostDiscussionInterface = caseChannelPostDiscussionInterface
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_home_case_channels_tasks, viewGroup, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val caseModel = caseChannelModelsList[i]
        viewHolder.taskName.text = caseModel.taskName
        viewHolder.taskAssignedTo.text = caseModel.taskAssignedTo
        viewHolder.taskDueOn.text =
            appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", caseModel.taskDueOn)
        viewHolder.statusText.paintFlags =
            viewHolder.statusText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        when (caseModel.taskStatus) {
            1 -> {
                viewHolder.statusText.text = "Open"
                viewHolder.statusText.setTextColor(ContextCompat.getColor(activity,R.color.colorAccent))
                viewHolder.statusIcon.setColorFilter(ContextCompat.getColor(activity,R.color.colorAccent))
            }
            2 -> {
                viewHolder.statusText.text = "On Hold"
                viewHolder.statusText.setTextColor(ContextCompat.getColor(activity,R.color.colorInfo))
                viewHolder.statusIcon.setColorFilter(ContextCompat.getColor(activity,R.color.colorInfo))
            }
            3 -> {
                viewHolder.statusText.text = "Cancelled"
                viewHolder.statusText.setTextColor(ContextCompat.getColor(activity,R.color.colorDanger))
                viewHolder.statusIcon.setColorFilter(ContextCompat.getColor(activity,R.color.colorDanger))
            }
            4 -> {
                viewHolder.statusText.text = "Completed"
                viewHolder.statusText.setTextColor(ContextCompat.getColor(activity,R.color.colorSuccess))
                viewHolder.statusIcon.setColorFilter(ContextCompat.getColor(activity,R.color.colorSuccess))
            }
        }
        viewHolder.viewTask.setOnClickListener {
            val intent = Intent(activity, CaseChannelDashboardActivity::class.java)
            intent.putExtra("caseChannelId", caseModel.caseId)
            activity.startActivity(intent)
        }
        viewHolder.changeStatus.setOnClickListener { view: View ->
            caseChannelPostDiscussionInterface.onButtonClick(
                view,
                caseModel.taskId.toString() + "," + caseModel.caseId
            )
        }
    }

    override fun getItemCount(): Int {
        return caseChannelModelsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView
        val taskAssignedTo: TextView
        val taskDueOn: TextView
        val statusText: TextView
        val viewTask: LinearLayout
        val changeStatus: LinearLayout
        var statusIcon: ImageView

        init {
            taskName = itemView.findViewById(R.id.homeCaseTaskName)
            taskAssignedTo = itemView.findViewById(R.id.homeCaseTaskAssignedTo)
            taskDueOn = itemView.findViewById(R.id.homeCaseTaskDueOn)
            viewTask = itemView.findViewById(R.id.homeCaseTaskView)
            changeStatus = itemView.findViewById(R.id.homeCaseTaskStatus)
            statusText = itemView.findViewById(R.id.homeCaseTaskStatusText)
            statusIcon = itemView.findViewById(R.id.homeCaseTaskStatusIcon)
        }
    }
}