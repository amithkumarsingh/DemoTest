package com.whitecoats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.model.ChatRoomModel

class ChatRoomAdapter(private val chatRoomModelList: List<ChatRoomModel>) :
    RecyclerView.Adapter<ChatRoomAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_chat_room, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHoler: MyViewHolder, i: Int) {
        val chatRoomModel = chatRoomModelList[i]
        if (chatRoomModel.type == 0) {
            myViewHoler.selfLayout.visibility = View.VISIBLE
            myViewHoler.oppoLayout.visibility = View.GONE
            myViewHoler.chatRoomMsgText.text = chatRoomModel.chatMsg
            myViewHoler.chatTime.text = chatRoomModel.chatTime
            //  notifyItemInserted(i);
        } else {
            myViewHoler.selfLayout.visibility = View.GONE
            myViewHoler.oppoLayout.visibility = View.VISIBLE
            myViewHoler.chatRoomOppoMsgText.text = chatRoomModel.chatMsg
            myViewHoler.optChatTime.text = chatRoomModel.chatTime
            // notifyItemInserted(i);
        }
    }

    override fun getItemCount(): Int {
        return chatRoomModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selfLayout: RelativeLayout
        var oppoLayout: RelativeLayout
        var chatRoomMsgText: TextView
        var chatTime: TextView
        var chatRoomOppoMsgText: TextView
        var optChatTime: TextView

        init {
            selfLayout = itemView.findViewById(R.id.chatRoomSelfLayout)
            oppoLayout = itemView.findViewById(R.id.chatRoomOppoLayout)
            chatRoomMsgText = itemView.findViewById(R.id.chatRoomMsgText)
            chatTime = itemView.findViewById(R.id.chatTime)
            chatRoomOppoMsgText = itemView.findViewById(R.id.chatRoomOppoMsgText)
            optChatTime = itemView.findViewById(R.id.optChatTime)
        }
    }
}