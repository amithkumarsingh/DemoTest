package com.whitecoats.clinicplus.casechannels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.model.CaseChannelModel

class CaseChannelListAdapter(
    private val activity: Activity,
    private val caseChannelModelList: List<CaseChannelModel>,
    private val caseDoctorOrganisationClickListener: CaseDoctorOrganisationClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_case_channel_list, viewGroup, false)
            return MyViewHolder(itemView)
        }
        if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_case_channel_task_footer, viewGroup, false)
            return FooterViewHolder(itemView)
        } else return null!!
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(myViewHolder: RecyclerView.ViewHolder, i: Int) {
        if (myViewHolder is MyViewHolder) {
            val itemViewHolder = myViewHolder
            val caseChannelModel = caseChannelModelList[i]
            itemViewHolder.channelName.text = caseChannelModel.caseChannelName
            itemViewHolder.createdOn.text = caseChannelModel.createdAt
            itemViewHolder.groupOwner.text = caseChannelModel.ownerName
            itemViewHolder.patientName.text = caseChannelModel.patientName
            if (caseChannelModel.ownerId == ApiUrls.doctorId) {
                itemViewHolder.groupOwner.text = "Me"
            }
            itemViewHolder.channelCard.setOnClickListener {
                val intent = Intent(activity, CaseChannelDashboardActivity::class.java)
                intent.putExtra("caseChannelId", caseChannelModel.caseId)
                activity.startActivity(intent)
            }
        } else if (myViewHolder is FooterViewHolder) {
            val caseChannelListActivity = activity as CaseChannelListActivity
            myViewHolder.loadMore.visibility = View.VISIBLE
            myViewHolder.loader.visibility = View.GONE
            if (caseChannelModelList.size == caseChannelListActivity.total) {
                myViewHolder.footerLayout.visibility = View.GONE
            } else {
                myViewHolder.footerLayout.visibility = View.VISIBLE
            }
            myViewHolder.loadMore.setOnClickListener { view ->
                myViewHolder.loadMore.visibility = View.GONE
                myViewHolder.loader.visibility = View.VISIBLE
                caseDoctorOrganisationClickListener.onItemClick(view, i, "LOAD_MORE", "")
            }
        }
    }

    override fun getItemCount(): Int {
        return caseChannelModelList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == caseChannelModelList.size) {
            TYPE_FOOTER
        } else TYPE_ITEM
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var channelName: TextView
        var groupOwner: TextView
        var patientName: TextView
        var createdOn: TextView
        var channelCard: CardView

        init {
            channelName = itemView.findViewById(R.id.caseChannelListName)
            groupOwner = itemView.findViewById(R.id.caseChannelListGroupOwner)
            patientName = itemView.findViewById(R.id.caseChannelListForPatient)
            createdOn = itemView.findViewById(R.id.caseChannelListDate)
            channelCard = itemView.findViewById(R.id.caseChannelListCard)
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
        private val TYPE_FOOTER = 1
        private val TYPE_ITEM = 2
    }
}