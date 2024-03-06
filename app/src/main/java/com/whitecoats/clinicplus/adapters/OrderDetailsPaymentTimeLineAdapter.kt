package com.whitecoats.clinicplus.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.models.OrderDetailsPaymentTimeLineModel

class OrderDetailsPaymentTimeLineAdapter(
    private var myList: ArrayList<OrderDetailsPaymentTimeLineModel>?
) : RecyclerView.Adapter<OrderDetailsPaymentTimeLineAdapter.RecyclerItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_order_details_payment_timeline, parent, false)
        return RecyclerItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        Log.d("onBindViewHoler ", myList!!.size.toString() + "")
        holder.headingText.text = myList!![position].paymentStatusHeadingData
        holder.headingData.text = myList!![position].paymentStatusData
    }

    override fun getItemCount(): Int {
        return if (null != myList) myList!!.size else 0
    }

    inner class RecyclerItemViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        val headingText: TextView
        val headingData: TextView

        init {
            headingText = parent.findViewById<View>(R.id.headingText) as TextView
            headingData = parent.findViewById<View>(R.id.headingData) as TextView
        }
    }
}