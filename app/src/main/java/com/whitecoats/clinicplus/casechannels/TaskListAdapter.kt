package com.whitecoats.clinicplus.casechannels

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.PatientRecordsApi
import com.whitecoats.clinicplus.R

class TaskListAdapter(
    private val activity: Activity,
    private val taskListModelList: List<TaskListModel>?,
    taskFragment: CaseChannelTaskFragment,
    caseDoctorOrganisationClickListener: CaseDoctorOrganisationClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val apiCalls: PatientRecordsApi
    private val caseDoctorOrganisationClickListener: CaseDoctorOrganisationClickListener
    private val total = 0
    private val taskFragment: CaseChannelTaskFragment

    init {
        apiCalls = PatientRecordsApi()
        this.caseDoctorOrganisationClickListener = caseDoctorOrganisationClickListener
        this.taskFragment = taskFragment
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.list_row_task_list,
                viewGroup, false
            )
            return ViewHolder(view)
        }
        return if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_case_channel_task_footer, viewGroup, false)
            FooterViewHolder(itemView)
        } else null!!
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") i: Int
    ) {
        if (viewHolder is ViewHolder) {
            val itemViewHolder = viewHolder
            val taskListModel = taskListModelList!![i]
            itemViewHolder.taskListName.text = taskListModel.name
            itemViewHolder.assigned_Name.text = taskListModel.fname
            itemViewHolder.created_Name.text = taskListModel.created_by_name
            itemViewHolder.statusText.paintFlags =
                itemViewHolder.statusText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            itemViewHolder.taskListDate.text = taskListModel.due_date
            val status = taskListModel.status
            when (status) {
                1 -> {
                    itemViewHolder.statusText.text = "Open"
                    itemViewHolder.statusText.setTextColor(ContextCompat.getColor(activity,R.color.colorAccent))
                }
                2 -> {
                    itemViewHolder.statusText.text = "On Hold"
                    itemViewHolder.statusText.setTextColor(ContextCompat.getColor(activity,R.color.colorInfo))
                }
                3 -> {
                    itemViewHolder.statusText.text = "Cancelled"
                    itemViewHolder.statusText.setTextColor(ContextCompat.getColor(activity,R.color.colorDanger))
                }
                4 -> {
                    itemViewHolder.statusText.text = "Completed"
                    itemViewHolder.statusText.setTextColor(ContextCompat.getColor(activity,R.color.colorSuccess))
                }
                else -> {}
            }
            itemViewHolder.statusText.setOnClickListener { v ->
                caseDoctorOrganisationClickListener.onItemClick(
                    v,
                    i,
                    "",
                    ""
                )
            }
        } else if (viewHolder is FooterViewHolder) {
            viewHolder.loadMore.visibility = View.VISIBLE
            viewHolder.loader.visibility = View.GONE
            if (taskListModelList!!.size - 1 == taskFragment.total || total == 0) {
                viewHolder.footerLayout.visibility = View.GONE
            }
            viewHolder.loadMore.setOnClickListener { view ->
                viewHolder.loadMore.visibility = View.GONE
                viewHolder.loader.visibility = View.VISIBLE
                caseDoctorOrganisationClickListener.onItemClick(view, i, "LOAD_MORE", "")
            }
        }
    }

    override fun getItemCount(): Int {
        return if (taskListModelList == null) 0 else taskListModelList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == taskListModelList!!.size) {
            TYPE_FOOTER
        } else TYPE_ITEM
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusText: TextView
        val taskListDate: TextView
        val taskListName: TextView
        val assigned_Name: TextView
        val created_Name: TextView

        init {
            statusText = itemView.findViewById(R.id.statusText)
            taskListDate = itemView.findViewById(R.id.taskListDate)
            taskListName = itemView.findViewById(R.id.taskListName)
            assigned_Name = itemView.findViewById(R.id.assigned_Name)
            created_Name = itemView.findViewById(R.id.created_Name)
        }
    }

    private inner class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val loader: ProgressBar
        val loadMore: Button
        val footerLayout: RelativeLayout

        init {
            loader = view.findViewById(R.id.caseChannelTaskFooterLoader)
            loadMore = view.findViewById(R.id.caseChannelTaskFooterLoadMore)
            footerLayout = view.findViewById(R.id.caseChannelTaskFooterLayout)
        }
    }

    companion object {
        private const val TYPE_FOOTER = 1
        private const val TYPE_ITEM = 2
    }
}