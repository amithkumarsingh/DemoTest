package com.whitecoats.clinicplus.casechannels

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
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.model.CaseChannelModel
class HomeCaseChannelsPostListAdapter(
    private val activity: Activity,
    caseChannelModelsList: List<CaseChannelModel>,
    caseChannelPostDiscussionInterface: HomeCaseChannelPostDiscussionInterface
) : RecyclerView.Adapter<HomeCaseChannelsPostListAdapter.ViewHolder>() {
    private val caseChannelModelsList: List<CaseChannelModel>
    private val appUtilities: AppUtilities = AppUtilities()
    private val caseChannelPostDiscussionInterface: HomeCaseChannelPostDiscussionInterface

    init {
        this.caseChannelModelsList = caseChannelModelsList
        this.caseChannelPostDiscussionInterface = caseChannelPostDiscussionInterface
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_home_case_channels_posts, viewGroup, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val caseModel = caseChannelModelsList[i]
        viewHolder.postMsg.text = caseModel.postMessage
        viewHolder.senderName.text = caseModel.postSenderName
        viewHolder.postDate.text =
            appUtilities.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yy", caseModel.postDate)
        if (caseModel.postSenderId == ApiUrls.doctorId) {
            viewHolder.senderName.text = "Me"
        }
        viewHolder.viewPost.setOnClickListener {
            val intent = Intent(activity, CaseChannelDashboardActivity::class.java)
            intent.putExtra("caseChannelId", caseModel.caseId)
            activity.startActivity(intent)
        }
        viewHolder.replyPost.setOnClickListener { view: View ->
            val discussionID = caseModel.caseId.toString() + ""
            caseChannelPostDiscussionInterface.onButtonClick(view, discussionID)
        }
    }

    override fun getItemCount(): Int {
        return caseChannelModelsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val senderName: TextView
         val postDate: TextView
         val postMsg: TextView
         val viewPost: LinearLayout
         val replyPost: LinearLayout

        init {
            senderName = itemView.findViewById(R.id.homeCasePostBy)
            postDate = itemView.findViewById(R.id.homeCasePostDate)
            postMsg = itemView.findViewById(R.id.homeCasePostMsg)
            viewPost = itemView.findViewById(R.id.homeCasePostView)
            replyPost = itemView.findViewById(R.id.homeCasePostReply)
        }
    }
}