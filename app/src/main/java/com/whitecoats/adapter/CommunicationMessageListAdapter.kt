package com.whitecoats.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.CommunicationMessageListActivity
import com.whitecoats.clinicplus.PatientListClickListner
import com.whitecoats.clinicplus.R
import com.whitecoats.model.CommunicationMessageListModel

class CommunicationMessageListAdapter(
    private val communicationMessageModelList: List<CommunicationMessageListModel>,
    private var mContext: Context,
    patientListner: PatientListClickListner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val patientListner: PatientListClickListner

    init {
        this.patientListner = patientListner
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        when (i) {
            TYPE_ITEM -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.list_message_row_communication, viewGroup, false)
                mContext = viewGroup.context
                return MyViewHolder(view)
            }
            TYPE_HEADER -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.list_message_row_communication, viewGroup, false)
                mContext = viewGroup.context
                return MyViewHolder(view)
            }
            TYPE_FOOTER -> {
                val view = LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.activity_path_orderview_footer,
                    viewGroup, false
                )
                return FooterViewHolder(view)
            }
            else -> throw RuntimeException("there is no type that matches the type $i + make sure your using types correctly")
        }
    }

    override fun onBindViewHolder(myViewHolder: RecyclerView.ViewHolder, i: Int) {
        myViewHolder.itemView.tag = communicationMessageModelList[i]
        if (myViewHolder is MyViewHolder) {
            val communicationMessageListModel = communicationMessageModelList[i]
            myViewHolder.title.text = communicationMessageListModel.title
            myViewHolder.date.text = communicationMessageListModel.date
            myViewHolder.commMessageListAttemptedCount.text =
                communicationMessageListModel.attempted.toString()
            myViewHolder.commMessageListFailedCount.text =
                communicationMessageListModel.failed.toString()
            myViewHolder.commMessageListSentCount.text =
                communicationMessageListModel.send.toString()
        } else if (myViewHolder is FooterViewHolder) {
            footerHolder = myViewHolder
            if (CommunicationMessageListActivity.isMoreData && communicationMessageModelList.size >= 10) {
                myViewHolder.footerText.visibility = View.VISIBLE
            } else {
                myViewHolder.footerText.visibility = View.GONE
            }
            footerHolder!!.footerText.setOnClickListener { view: View? ->
                patientListner.onItemClick(
                    view,
                    "LOADMORE",
                    "",
                    0,
                    0,
                    0,
                    0,
                    0,
                    "",
                    null,
                    null
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return communicationMessageModelList.size
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
        return position >= communicationMessageModelList.size - 1 && communicationMessageModelList.size >= 10
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var date: TextView
        var category: TextView? = null
        var commMessageListAttemptedCount: TextView
        var commMessageListFailedCount: TextView
        var commMessageListSentCount: TextView

        init {
            title = itemView.findViewById(R.id.commMessageListText)
            date = itemView.findViewById(R.id.commMessageListDate)
            commMessageListAttemptedCount =
                itemView.findViewById(R.id.commMessageListAttemptedCount)
            commMessageListFailedCount = itemView.findViewById(R.id.commMessageListFailedCount)
            commMessageListSentCount = itemView.findViewById(R.id.commMessageListSentCount)
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
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_FOOTER = 2
        @SuppressLint("StaticFieldLeak")
        var footerHolder: FooterViewHolder? = null
    }
}