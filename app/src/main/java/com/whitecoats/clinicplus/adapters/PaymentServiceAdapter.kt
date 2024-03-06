package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.app.Dialog
import android.text.InputFilter
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.AddProcedureClickListener
import com.whitecoats.clinicplus.models.AddProcedureListModel
import com.whitecoats.clinicplus.utils.InputFilterMinMax
import java.text.MessageFormat

class PaymentServiceAdapter(
    private val activity: Activity,
    private val myList: ArrayList<AddProcedureListModel>,
    private val mListener: AddProcedureClickListener
) : RecyclerView.Adapter<PaymentServiceAdapter.RecyclerItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_payments_procedure, parent, false)
        return RecyclerItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        Log.d("onBindViewHoler ", myList.size.toString() + "")
        holder.serviceName.text = String.format("%s", myList[position].procedureName)
        holder.orderDetails_amount.text =
            MessageFormat.format("{0}", myList[position].serviceAmount)
        holder.serviceTax_amount.text = String.format("%s", myList[position].serviceCharges)
        holder.gst_Text.text = String.format("Tax (%s)", myList[position].taxText)
        holder.taxAmt_text.text = String.format("%s", myList[position].serviceTax)
        holder.discount.text = String.format("%s", myList[position].serviceDiscount)
        holder.totalPay_amount.text = MessageFormat.format("{0}", myList[position].totalAmount)
        val removeDiscount: SpannableString = if (myList[position].isDiscountApplied) {
            SpannableString("(Remove)")
        } else {
            SpannableString("(Apply Coupon)")
        }
        removeDiscount.setSpan(UnderlineSpan(), 0, removeDiscount.length, 0)
        holder.discountRemoveText.text = removeDiscount
        holder.discountRemoveText.setOnClickListener {
            if (holder.discountRemoveText.text.toString()
                    .equals("(Apply Coupon)", ignoreCase = true)
            ) {
                val dialog = Dialog(activity)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.procedure_apply_coupon_popup)
                val applyCouponEditText = dialog.findViewById<EditText>(R.id.applyCouponEditText)
                val applyCouponCardView = dialog.findViewById<CardView>(R.id.applyCouponCardView)
                val dailogArticleCancel = dialog.findViewById<ImageView>(R.id.dailogArticleCancel)
                applyCouponEditText.filters = arrayOf<InputFilter>(InputFilterMinMax(1, 100))
                dailogArticleCancel.setOnClickListener { dialog.dismiss() }
                applyCouponCardView.setOnClickListener {
                    /*Removed the crash issue*/
                    val couponValue = applyCouponEditText.text.toString()
                    if (!couponValue.equals("", ignoreCase = true)) {
                        myList[position].isDiscountApplied = true
                        val removeDiscount1 = SpannableString("(Remove)")
                        removeDiscount1.setSpan(UnderlineSpan(), 0, removeDiscount1.length, 0)
                        holder.discountRemoveText.text = removeDiscount1
                        dialog.dismiss()
                        val applycouponValue = couponValue.toInt()
                        mListener.OnProcedureClick(
                            position,
                            "applyDiscount",
                            myList[position],
                            applycouponValue
                        )
                    }
                }
                dialog.show()
            } else if (holder.discountRemoveText.text.toString()
                    .equals("(Remove)", ignoreCase = true)
            ) {
                myList[position].isDiscountApplied = false
                val removeDiscount12 = SpannableString("(Apply Coupon)")
                removeDiscount12.setSpan(UnderlineSpan(), 0, removeDiscount12.length, 0)
                holder.discountRemoveText.text = removeDiscount12
                mListener.OnProcedureClick(position, "Remove", myList[position], 0)
            }
        }
        holder.orderDetails_expand.setOnClickListener {
            holder.orderDetails_collapse.visibility = View.VISIBLE
            holder.orderDetails_expand.visibility = View.GONE
            holder.icon_collapse.visibility = View.VISIBLE
            holder.icon_expand.visibility = View.GONE
            holder.costDetails_Layout.visibility = View.VISIBLE
        }
        holder.orderDetails_collapse.setOnClickListener {
            holder.orderDetails_collapse.visibility = View.GONE
            holder.orderDetails_expand.visibility = View.VISIBLE
            holder.icon_collapse.visibility = View.GONE
            holder.icon_expand.visibility = View.VISIBLE
            holder.costDetails_Layout.visibility = View.GONE
        }
        holder.icon_expand.setOnClickListener {
            holder.orderDetails_collapse.visibility = View.VISIBLE
            holder.orderDetails_expand.visibility = View.GONE
            holder.icon_collapse.visibility = View.VISIBLE
            holder.icon_expand.visibility = View.GONE
            holder.costDetails_Layout.visibility = View.VISIBLE
        }
        holder.icon_collapse.setOnClickListener {
            holder.orderDetails_collapse.visibility = View.GONE
            holder.orderDetails_expand.visibility = View.VISIBLE
            holder.icon_collapse.visibility = View.GONE
            holder.icon_expand.visibility = View.VISIBLE
            holder.costDetails_Layout.visibility = View.GONE
        }
        holder.payment_remove_procedure.setOnClickListener {
            mListener.OnProcedureClick(
                position,
                "RemoveProcedure",
                myList[position],
                0
            )
        }
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    class RecyclerItemViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var serviceName: TextView
        var orderDetails_amount: TextView
        var serviceTax_amount: TextView
        var gst_Text: TextView
        var taxAmt_text: TextView
        var discount: TextView
        var totalPay_amount: TextView
        var orderDetails_expand: TextView
        var orderDetails_collapse: TextView
        var costDetails_Layout: RelativeLayout
        var icon_expand: ImageButton
        var icon_collapse: ImageButton
        var discountRemoveText: TextView
        var payment_remove_procedure: TextView

        init {
            serviceName = parent.findViewById(R.id.serviceName)
            orderDetails_amount = parent.findViewById(R.id.orderDetails_amount)
            serviceTax_amount = parent.findViewById(R.id.serviceTax_amount)
            gst_Text = parent.findViewById(R.id.gst_Text)
            taxAmt_text = parent.findViewById(R.id.taxAmt_text)
            discount = parent.findViewById(R.id.discount)
            totalPay_amount = parent.findViewById(R.id.totalPay_amount)
            orderDetails_expand = parent.findViewById(R.id.orderDetails_expand)
            orderDetails_collapse = parent.findViewById(R.id.orderDetails_collapse)
            costDetails_Layout = parent.findViewById(R.id.costDetails_Layout)
            icon_expand = parent.findViewById(R.id.icon_expand)
            icon_collapse = parent.findViewById(R.id.icon_collapse)
            discountRemoveText = parent.findViewById(R.id.discountRemoveText)
            payment_remove_procedure = parent.findViewById(R.id.payment_remove_procedure)
        }
    }
}