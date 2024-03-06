package com.whitecoats.clinicplus.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.DashBoardSwitchClinicListAdapter.RecyclerViewHolder
import com.whitecoats.clinicplus.fragments.DashboardFullMode
import com.whitecoats.clinicplus.interfaces.SwitchClinicListClickListener
import com.whitecoats.clinicplus.models.SwitchClinicModel

/**
 * Created by dileep on 7/10/2020.
 */
class DashBoardSwitchClinicListAdapter(
    private val context: Context,
    private val arrayList: List<SwitchClinicModel>?,
    private val switchClinicListClickListener: SwitchClinicListClickListener
) : RecyclerView.Adapter<RecyclerViewHolder>() {
    class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clinicName: TextView
        val upcomingApptValue: TextView
        val radioButton: RadioButton
        val switchClinicListLayout: RelativeLayout

        init {
            radioButton = view.findViewById<View>(R.id.radio_button) as RadioButton
            switchClinicListLayout =
                view.findViewById<View>(R.id.switchClinicListLayout) as RelativeLayout
            clinicName = view.findViewById<View>(R.id.clinicName) as TextView
            upcomingApptValue = view.findViewById<View>(R.id.upcomingApptValue) as TextView
        }
    }

    private var selectedPosition = -1
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_custom_switch_clinic_row_layout, viewGroup, false)
        return RecyclerViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerViewHolder, i: Int) {
        val switchClinicModel = arrayList!![i]
        holder.clinicName.text = switchClinicModel.getClinicName()
        holder.upcomingApptValue.text = switchClinicModel.getApptCount().toString() + ""
        holder.radioButton.isChecked = i == DashboardFullMode.switchClickSelectedPosition
        holder.radioButton.tag = i
        holder.switchClinicListLayout.tag = i
        holder.radioButton.setOnClickListener { v -> itemCheckChanged(v) }
        holder.switchClinicListLayout.setOnClickListener { v -> itemCheckChanged(v) }
    }

    //On selecting any view set the current position to selectedPositon and notify adapter
    @SuppressLint("NotifyDataSetChanged")
    private fun itemCheckChanged(v: View) {
        selectedPosition = v.tag as Int
        DashboardFullMode.switchClickSelectedPosition = (v.tag as Int)
        DashboardFullMode.switchClinicSelectedString = arrayList!![selectedPosition].clinicName
        switchClinicListClickListener.onItemClick(v, selectedPosition)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }

    //Return the selectedPosition item
    val selectedItem: String
        get() {
            if (selectedPosition != -1) {
                Toast.makeText(
                    context,
                    "Selected Item : " + arrayList!![selectedPosition],
                    Toast.LENGTH_SHORT
                ).show()
            }
            return ""
        }
}