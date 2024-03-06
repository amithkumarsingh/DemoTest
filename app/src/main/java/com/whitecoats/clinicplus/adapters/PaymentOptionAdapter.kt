package com.whitecoats.clinicplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.activities.PaymentSetupScreen
import com.whitecoats.clinicplus.interfaces.PaymentTypeClick

class PaymentOptionAdapter(
    private var paymentModeList: MutableList<HashMap<String, String>>, private var paymentItemClick: PaymentTypeClick
) : RecyclerView.Adapter<PaymentOptionAdapter.MyViewHolder>() {
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.paymentoption_list_item, parent, false)
        context = parent.context
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return paymentModeList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.tag = paymentModeList[position]
        if (paymentModeList[position]["payment_mode"] == PaymentSetupScreen.defaultPaymentMode) {
            holder.tvPaymentEnableDefault!!.visibility = View.VISIBLE
        } else {
            holder.tvPaymentEnableDefault!!.visibility = View.GONE
        }
        holder.tvPaymentType!!.text = paymentModeList[position]["title"]
        holder.constParent!!.setOnClickListener {
            paymentItemClick.onItemClick(position, paymentModeList[position]["title"]!!,null,null)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var constParent: ConstraintLayout? = null
        var tvPaymentType: TextView? = null
        private var imgPaymentType: ImageView? = null
        var tvPaymentEnableDefault: TextView? = null

        init {
            constParent = itemView.findViewById(R.id.const_parent)
            tvPaymentType = itemView.findViewById(R.id.tv_payment_type)
            imgPaymentType = itemView.findViewById(R.id.img_payment_type)
            tvPaymentEnableDefault = itemView.findViewById(R.id.tv_payment_enable)
        }

    }
}