package com.whitecoats.clinicplus.casechannels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R

class PostFragmentAdapter(
    private val postDiscussionModelList: List<PostDiscussionModel>
) : RecyclerView.Adapter<PostFragmentAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_case_channel_post, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val postDiscussionModel = postDiscussionModelList[i]
        if (postDiscussionModel.msgType == 1) {
            myViewHolder.selfLayout.visibility = View.VISIBLE
            myViewHolder.oppoLayout.visibility = View.GONE
            myViewHolder.selfMsgTime.text = postDiscussionModel.msgTime
            myViewHolder.selfMsg.text = postDiscussionModel.msgText
            myViewHolder.selfDocName.text = postDiscussionModel.docName
        } else {
            myViewHolder.oppoLayout.visibility = View.VISIBLE
            myViewHolder.selfLayout.visibility = View.GONE
            myViewHolder.oppoMsgTime.text = postDiscussionModel.msgTime
            myViewHolder.oppoMsg.text = postDiscussionModel.msgText
            myViewHolder.oppoDocName.text = postDiscussionModel.docName
        }
    }

    override fun getItemCount(): Int {
        return postDiscussionModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val selfDocName: TextView
         val selfMsg: TextView
         val selfMsgTime: TextView
         val oppoDocName: TextView
         val oppoMsg: TextView
         val oppoMsgTime: TextView
         val selfLayout: RelativeLayout
         val oppoLayout: RelativeLayout

        init {
            selfDocName = itemView.findViewById(R.id.ccPostSelfDocName)
            selfMsg = itemView.findViewById(R.id.ccPostSelfMsgText)
            selfMsgTime = itemView.findViewById(R.id.ccPostSelfMsgTime)
            oppoDocName = itemView.findViewById(R.id.ccPostOppoDocName)
            oppoMsg = itemView.findViewById(R.id.ccPostOppoMsgText)
            oppoMsgTime = itemView.findViewById(R.id.ccPostOppoMsgTime)
            selfLayout = itemView.findViewById(R.id.ccPostSelfLayout)
            oppoLayout = itemView.findViewById(R.id.ccPostOppoLayout)
        }
    }
}