package com.whitecoats.clinicplus.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.RescheduleTimeSlotOnClick
import com.whitecoats.model.AppointmentSlotListModel

class AppointmentRescheduleSlotAdapter(
    private val context: Context,
    private val data: ArrayList<AppointmentSlotListModel>,
    private val slotEndArr: ArrayList<AppointmentSlotListModel>,
    private val rescheduleTimeSlotOnClick: RescheduleTimeSlotOnClick
) : RecyclerView.Adapter<AppointmentRescheduleSlotAdapter.MyViewHolder>() {
    private val appUtilities: AppUtilities = AppUtilities()

    @JvmField
    var selectedSlot = ""
    private var selectedEndSlot: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reschedule_slot_layout, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.slotTime.text =
            appUtilities.formatTimeBasedOnPreference(context, "HH:mm", data[position].slotTime)
        holder.slotEndTime.text = slotEndArr[position].endSlotTime
        if (selectedSlot.equals(holder.slotTime.text.toString(), ignoreCase = true)) {
            holder.slotTimeLayout.background = ContextCompat.getDrawable(
                context.applicationContext, R.drawable.appt_reschedule_timeslot_active_bg
            )
            holder.slotTime.setTextColor(Color.WHITE)
        } else {
            holder.slotTimeLayout.background = ContextCompat.getDrawable(
                context.applicationContext, R.drawable.appt_reschedule_timeslot_bg
            )
            holder.slotTime.setTextColor(ContextCompat.getColor(context,R.color.colorBlack))
        }
        holder.slotTimeLayout.setOnClickListener {
            selectedSlot = holder.slotTime.text.toString()
            selectedEndSlot = holder.slotEndTime.text.toString()
            notifyDataSetChanged()
            rescheduleTimeSlotOnClick.onTimeSlotClick(
                data[position].slotTime,
                slotEndArr[position].endSlotTime
            )
        }
    }

    override fun getItemCount(): Int {
        return try {
            data.size
        } catch (e: Exception) {
            data.size
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val slotTime: TextView
         val slotEndTime: TextView
         val slotTimeLayout: LinearLayout

        init {
            slotTime = itemView.findViewById(R.id.bookSlotTimeTv)
            slotEndTime = itemView.findViewById(R.id.bookSlotEndTimeTv)
            slotTimeLayout = itemView.findViewById(R.id.slotTimeLayout)
        }
    }
}