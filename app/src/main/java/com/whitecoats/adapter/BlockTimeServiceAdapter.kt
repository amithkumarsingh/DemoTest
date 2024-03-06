package com.whitecoats.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.BlockTimeActivity
import com.whitecoats.clinicplus.R
import com.whitecoats.model.BlockTimeListModel

class BlockTimeServiceAdapter(
    private val activity: Activity,
    private val blockTimeListModels: MutableList<BlockTimeListModel>?
) : RecyclerView.Adapter<BlockTimeServiceAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_blocktime_services, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        val blockTimeListModel = blockTimeListModels?.get(position)
        myViewHolder.serviceNameTv.text = blockTimeListModel!!.serviceNameString
        myViewHolder.productId = blockTimeListModel.productId
        myViewHolder.serviceId = blockTimeListModel.serviceId
        val blockTimeActivity = activity as BlockTimeActivity
        myViewHolder.serviceCard.setBackgroundColor(ContextCompat.getColor(activity,R.color.colorWhite))
        myViewHolder.serviceNameTv.setTextColor(ContextCompat.getColor(activity,R.color.colorBlack))
        if (myViewHolder.serviceId == blockTimeActivity.selectedServiceID) {
            myViewHolder.serviceCard.setBackgroundColor(ContextCompat.getColor(activity,R.color.colorAccent))
            myViewHolder.serviceNameTv.setTextColor(ContextCompat.getColor(activity,R.color.colorWhite))
        }
        myViewHolder.serviceCard.setOnClickListener {
            blockTimeActivity.selectedServiceID = blockTimeListModel.serviceId
            blockTimeActivity.selectedProductID = blockTimeListModel.productId
            blockTimeActivity.selectedService = blockTimeListModel.serviceType
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return blockTimeListModels!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val serviceCard: CardView
         val serviceNameTv: TextView
         var serviceId = 0
         var productId = 0
         private val isSelected: Boolean

        init {
            serviceCard = itemView.findViewById(R.id.blockTimeServiceCard)
            serviceNameTv = itemView.findViewById(R.id.blockTimeServiceName)
            isSelected = false
        }
    }
}