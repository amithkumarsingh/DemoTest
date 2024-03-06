package com.whitecoats.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.model.AppointmentSlotListModel

class ApptTimeSlotGridViewCustomAdapter(
    activity: Activity,
    var data: List<AppointmentSlotListModel>,
    slotEndArr: ArrayList<AppointmentSlotListModel>
) : BaseAdapter() {
    private val appUtilities: AppUtilities
    var context: Context
    private val slotEndArr: List<AppointmentSlotListModel>

    @JvmField
    var selectedSlot = ""

    @JvmField
    var selectedStartTime: String? = null

    @JvmField
    var selectedEndTime: String? = null

    init {
        this.slotEndArr = slotEndArr
        context = activity
        appUtilities = AppUtilities()
    }

    override fun getCount(): Int {
        return try {
            data.size
        } catch (e: Exception) {
            data.size
        }
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val slotTime: TextView
        val slotEndTime: TextView
        val vi: View =LayoutInflater.from(context).inflate(R.layout.list_row_slot_item, null)
        try {
            slotTime = vi.findViewById(R.id.bookSlotTimeTv)
            slotEndTime = vi.findViewById(R.id.bookSlotEndTimeTv)
            slotTime.text =
                appUtilities.formatTimeBasedOnPreference(context, "HH:mm", data[position].slotTime)
            slotEndTime.text = slotEndArr[position].endSlotTime
            val slotTimeLayout = vi.findViewById<LinearLayout>(R.id.slotTimeLayout)
            if (selectedSlot.equals(data[position].slotTime, ignoreCase = true)) {
                slotTimeLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
                slotTime.setTextColor(Color.WHITE)
            } else {
                slotTimeLayout.setBackgroundColor(Color.WHITE)
                slotTime.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            slotTimeLayout.setOnClickListener {
                slotTimeLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
                slotTime.setTextColor(Color.WHITE)
                selectedSlot = data[position].slotTime
                selectedStartTime = data[position].slotTime
                selectedEndTime = slotEndArr[position].endSlotTime
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return vi
    }
}