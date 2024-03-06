package com.whitecoats.clinicplus.adapters

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.AddProcedureClickListener
import com.whitecoats.clinicplus.models.AddProcedureListModel

class AppointmentDetailsAddProcedureAdapter(
    private var myList: ArrayList<AddProcedureListModel>?,
    private var discountArrayList: ArrayList<Int>,
    private val mListner: AddProcedureClickListener
) : RecyclerView.Adapter<AppointmentDetailsAddProcedureAdapter.RecyclerItemViewHolder>() {
    private var mLastPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_add_procedure, parent, false)
        return RecyclerItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: RecyclerItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        Log.d("onBindViewHoler ", myList!!.size.toString() + "")
        val discountApplyValue: Int
        var discountAmountAfterCalculate = 0
        holder.procedureName.text = myList!![position].procedureName
        holder.chargesAmount.text = "Rs " + myList!![position].charges + ""
        holder.taxAmount.text = "Rs " + myList!![position].tax + ""
        if (discountArrayList[position] == 0) {
            holder.discountAmount.visibility = View.GONE
            holder.applyDiscountText.visibility = View.VISIBLE
            holder.discountRemove.visibility = View.GONE
        } else {
            discountApplyValue = discountArrayList[position]
            discountAmountAfterCalculate = myList!![position].charges / 100 * discountApplyValue
            holder.discountAmount.text = "Rs $discountAmountAfterCalculate"
            holder.discountAmount.visibility = View.VISIBLE
            holder.applyDiscountText.visibility = View.GONE
            holder.discountRemove.visibility = View.VISIBLE
        }
        val totalAmount = myList!![position].charges - discountAmountAfterCalculate
        holder.totalAmount.text = "Rs $totalAmount"
        val removeDiscount = SpannableString("(Remove)")
        removeDiscount.setSpan(UnderlineSpan(), 0, removeDiscount.length, 0)
        holder.discountRemove.text = removeDiscount
        val applyDiscountText = SpannableString("Apply Discount")
        applyDiscountText.setSpan(UnderlineSpan(), 0, applyDiscountText.length, 0)
        holder.applyDiscountText.text = applyDiscountText
        val removeProcedureText = SpannableString("Remove Service")
        removeProcedureText.setSpan(UnderlineSpan(), 0, removeProcedureText.length, 0)
        holder.removeProcedureText.text = removeProcedureText
        mLastPosition = position
        holder.discountRemove.setOnClickListener {
            mListner.OnProcedureClick(
                position,
                "DiscountRemove",
                myList!![position],
                0
            )
        }
        holder.applyDiscountText.setOnClickListener {
            mListner.OnProcedureClick(
                position,
                "applyDiscount",
                myList!![position],
                0
            )
        }
        holder.removeProcedureText.setOnClickListener {
            mListner.OnProcedureClick(
                position,
                "removeProcedure",
                myList!![position],
                0
            )
        }
    }

    override fun getItemCount(): Int {
        return if (null != myList) myList!!.size else 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyData(myList: ArrayList<AddProcedureListModel>, discountArrayList: ArrayList<Int>) {
        Log.d("notifyData ", myList.size.toString() + "")
        this.myList = myList
        this.discountArrayList = discountArrayList
        notifyDataSetChanged()
    }

    inner class RecyclerItemViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        val procedureName: TextView
        val chargesAmount: TextView
        val taxAmount: TextView
        val discountAmount: TextView
        val totalAmount: TextView
        val discountRemove: TextView
        val applyDiscountText: TextView
        val removeProcedureText: TextView

        init {
            procedureName = parent.findViewById<View>(R.id.medicationNameText) as TextView
            chargesAmount = parent.findViewById<View>(R.id.chargesAmount) as TextView
            taxAmount = parent.findViewById<View>(R.id.taxAmount) as TextView
            discountAmount = parent.findViewById<View>(R.id.discountAmount) as TextView
            totalAmount = parent.findViewById<View>(R.id.totalAmount) as TextView
            discountRemove = parent.findViewById<View>(R.id.discountRemove) as TextView
            applyDiscountText = parent.findViewById<View>(R.id.applyDiscountText) as TextView
            removeProcedureText = parent.findViewById<View>(R.id.removeProcedureText) as TextView
        }
    }
}