package com.whitecoats.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.SettingServiceViewItemClickListener
import com.whitecoats.model.SettingServiceModel
import com.zoho.salesiqembed.ZohoSalesIQ

class SettingServiceListAdapter(
    private val mContext: Context,
    private val settingServiceModelList: List<SettingServiceModel>,
    private val serviceListener: SettingServiceViewItemClickListener
) : RecyclerView.Adapter<SettingServiceListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_setting_service, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        myViewHolder: MyViewHolder,
        @SuppressLint("RecyclerView") i: Int
    ) {
        val settingServiceModel = settingServiceModelList[i]
        myViewHolder.serviceNameText.text = settingServiceModel.serviceName
        if (settingServiceModel.serviceName.equals("Video", ignoreCase = true)) {
            val mDrawable = ContextCompat.getDrawable(mContext,R.drawable.ic_video)
            mDrawable!!.setColorFilter(
               ContextCompat.getColor(mContext,R.color.colorAccent),
                PorterDuff.Mode.SRC_IN
            )
            myViewHolder.serviceIcon.setImageDrawable(mDrawable)
        } else if (settingServiceModel.serviceName.equals("Instant Video", ignoreCase = true)) {
            val mDrawable = ContextCompat.getDrawable(mContext,R.drawable.ic_video)
            mDrawable!!.setColorFilter(
                ContextCompat.getColor(mContext,R.color.colorAccent),
                PorterDuff.Mode.SRC_IN
            )
            myViewHolder.serviceIcon.setImageDrawable(mDrawable)
        } else if (settingServiceModel.serviceName.equals("Chat", ignoreCase = true)) {
            val mDrawable = ContextCompat.getDrawable(mContext,R.drawable.ic_chat)
            mDrawable!!.setColorFilter(
                ContextCompat.getColor(mContext,R.color.colorAccent),
                PorterDuff.Mode.SRC_IN
            )
            myViewHolder.serviceIcon.setImageDrawable(mDrawable)
        } else if (settingServiceModel.serviceName.contains("Clinic")) {
            val mDrawable = ContextCompat.getDrawable(mContext,R.drawable.ic_hospital)
            mDrawable!!.setColorFilter(
                ContextCompat.getColor(mContext,R.color.colorAccent),
                PorterDuff.Mode.SRC_IN
            )
            myViewHolder.serviceIcon.setImageDrawable(mDrawable)
        }
        myViewHolder.serviceCardViewLayout.setOnClickListener { view ->
            ZohoSalesIQ.Tracking.setCustomAction("Settings - Service Setup - " + settingServiceModelList[i].serviceName)
            serviceListener.onItemClick(view, i)

            //Toast.makeText(mContext,"clicked on card:"+i,Toast.LENGTH_LONG).show();
        }
    }

    override fun getItemCount(): Int {
        return settingServiceModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceCardViewLayout: CardView
        val serviceNameText: TextView
        val serviceIcon: ImageView

        init {
            serviceNameText = itemView.findViewById(R.id.serviceNameText)
            serviceCardViewLayout = itemView.findViewById(R.id.serviceCardViewLayout)
            serviceIcon = itemView.findViewById(R.id.serviceIcon)
        }
    }
}