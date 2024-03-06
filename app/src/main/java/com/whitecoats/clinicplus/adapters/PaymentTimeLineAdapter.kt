package com.whitecoats.clinicplus.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.PaymentTimelineClickListner
import com.whitecoats.clinicplus.models.PaymentTimeLineModel

class PaymentTimeLineAdapter(
    private val mCtx: Context,
    private var myList: ArrayList<PaymentTimeLineModel>?,
    private val mListener: PaymentTimelineClickListner
) : RecyclerView.Adapter<PaymentTimeLineAdapter.RecyclerItemViewHolder>() {

    init {
        paymentTimeLineList = ArrayList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_payment_timeline, parent, false)
        return RecyclerItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        Log.d("onBindViewHolder ", myList!!.size.toString() + "")
        if (position == myList!!.size - 1) {
            holder.leftCircleImageSecond.setImageDrawable(ContextCompat.getDrawable(mCtx,R.drawable.ic_circle))
            if (myList!![position].color1.equals("green", ignoreCase = true)) {
                holder.leftCircleImageSecond.setColorFilter(
                    ContextCompat.getColor(mCtx,R.color.colorGreen3),
                    PorterDuff.Mode.SRC_IN
                )
            } else if (myList!![position].color1.equals("blue", ignoreCase = true)) {
                holder.leftCircleImageSecond.setColorFilter(
                    ContextCompat.getColor(mCtx,R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            } else if (myList!![position].color1.equals("red", ignoreCase = true)) {
                holder.leftCircleImageSecond.setColorFilter(
                    ContextCompat.getColor(mCtx,R.color.colorDanger),
                    PorterDuff.Mode.SRC_IN
                )
            } else if (myList!![position].color1.equals("black", ignoreCase = true)) {
                holder.leftCircleImageSecond.setColorFilter(
                    ContextCompat.getColor(mCtx,R.color.colorBlack),
                    PorterDuff.Mode.SRC_IN
                )
            }
        } else {
            holder.leftCircleImageSecond.setImageDrawable(ContextCompat.getDrawable(mCtx,R.drawable.ic_circle_two))
            if (myList!![position].color1.equals("green", ignoreCase = true)) {
                holder.leftCircleImageSecond.setColorFilter(
                    ContextCompat.getColor(mCtx,R.color.colorGreen3),
                    PorterDuff.Mode.SRC_IN
                )
            } else if (myList!![position].color1.equals("blue", ignoreCase = true)) {
                holder.leftCircleImageSecond.setColorFilter(
                    ContextCompat.getColor(mCtx,R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            } else if (myList!![position].color1.equals("red", ignoreCase = true)) {
                holder.leftCircleImageSecond.setColorFilter(
                    ContextCompat.getColor(mCtx,R.color.colorDanger),
                    PorterDuff.Mode.SRC_IN
                )
            } else if (myList!![position].color1.equals("black", ignoreCase = true)) {
                holder.leftCircleImageSecond.setColorFilter(
                    ContextCompat.getColor(mCtx,R.color.colorBlack),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
        if (!myList!![position].text1.toString().equals("", ignoreCase = true)) {
            holder.paymentStatusTextSecond.text = myList!![position].text1
            holder.paymentStatusTextSecond.visibility = View.VISIBLE
            if (myList!![position].color1.equals("green", ignoreCase = true)) {
                holder.paymentStatusTextSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorGreen3))
            } else if (myList!![position].color1.equals("blue", ignoreCase = true)) {
                holder.paymentStatusTextSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorAccent))
            } else if (myList!![position].color1.equals("red", ignoreCase = true)) {
                holder.paymentStatusTextSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorDanger))
            } else if (myList!![position].color1.equals("black", ignoreCase = true)) {
                holder.paymentStatusTextSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorBlack))
            }
        } else {
            holder.paymentStatusTextSecond.visibility = View.GONE
        }
        if (!myList!![position].text2.toString().equals("", ignoreCase = true)) {
            holder.totalBalanceSecond.text = myList!![position].text2
            holder.totalBalanceSecond.visibility = View.VISIBLE
            if (myList!![position].color2.equals("green", ignoreCase = true)) {
                holder.totalBalanceSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorGreen3))
            } else if (myList!![position].color2.equals("blue", ignoreCase = true)) {
                holder.totalBalanceSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorAccent))
            } else if (myList!![position].color2.equals("red", ignoreCase = true)) {
                holder.totalBalanceSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorDanger))
            } else if (myList!![position].color2.equals("black", ignoreCase = true)) {
                holder.totalBalanceSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorBlack))
            }
        } else {
            holder.totalBalanceSecond.visibility = View.GONE
        }
        if (!myList!![position].text3.toString().equals("", ignoreCase = true)) {
            holder.totalBalanceNotesSecond.visibility = View.VISIBLE
            holder.totalBalanceNotesSecond.text = myList!![position].text3
            if (myList!![position].color3.equals("green", ignoreCase = true)) {
                holder.totalBalanceNotesSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorGreen3))
            } else if (myList!![position].color3.equals("blue", ignoreCase = true)) {
                holder.totalBalanceNotesSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorAccent))
            } else if (myList!![position].color3.equals("red", ignoreCase = true)) {
                holder.totalBalanceNotesSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorDanger))
            } else if (myList!![position].color3.equals("black", ignoreCase = true)) {
                holder.totalBalanceNotesSecond.setTextColor(ContextCompat.getColor(mCtx,R.color.colorBlack))
            }
        } else {
            holder.totalBalanceNotesSecond.visibility = View.GONE
        }
        if (myList!![position].isShowCancelRefund == 1) {
            holder.cancelRefundLayout.visibility = View.VISIBLE
        } else {
            holder.cancelRefundLayout.visibility = View.GONE
        }
        if (myList!![position].isShowPayULink == 1) {
            holder.toPayUPortalLayout.visibility = View.VISIBLE
        } else {
            holder.toPayUPortalLayout.visibility = View.GONE
        }
        holder.cancelRefundLayout.setOnClickListener {
            mListener.OnPaymentTimelineClick(
                position,
                "",
                "cancelRefund"
            )
        }
        holder.toPayUPortalLayout.setOnClickListener {
            mListener.OnPaymentTimelineClick(
                position,
                myList!![position].payULink,
                "toPayUPortal"
            )
        }
    }

    override fun getItemCount(): Int {
        return if (null != myList) myList!!.size else 0
    }


    inner class RecyclerItemViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        val paymentStatusTextSecond: TextView
        val totalBalanceSecond: TextView
        val totalBalanceNotesSecond: TextView
        val leftCircleImageSecond: ImageView
        val cancelRefundLayout: RelativeLayout
        val toPayUPortalLayout: RelativeLayout

        init {
            paymentStatusTextSecond =
                parent.findViewById<View>(R.id.paymentStatusTextSecond) as TextView
            totalBalanceSecond = parent.findViewById<View>(R.id.totalBalanceSecond) as TextView
            totalBalanceNotesSecond =
                parent.findViewById<View>(R.id.totalBalanceNotesSecond) as TextView
            leftCircleImageSecond =
                parent.findViewById<View>(R.id.leftCircleImageSecond) as ImageView
            cancelRefundLayout =
                parent.findViewById<View>(R.id.cancelRefundLayout) as RelativeLayout
            toPayUPortalLayout =
                parent.findViewById<View>(R.id.toPayUPortalLayout) as RelativeLayout
        }
    }

    companion object {
        lateinit var paymentTimeLineList: ArrayList<PaymentTimeLineModel>
    }
}