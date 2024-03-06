package com.whitecoats.clinicplus.casechannels

import android.annotation.SuppressLint
import android.app.Activity
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

class DoctorOrganisationListAdapter(
    private val activity: Activity,
    private val caseChannelDoctorModelList: List<CaseChannelDoctorModel>,
    private val caseDoctorOrganisationClickListener: CaseDoctorOrganisationClickListener
) : RecyclerView.Adapter<DoctorOrganisationListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.list_row_case_doctor_organisation,
            viewGroup, false
        )
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val docModel = caseChannelDoctorModelList[i]
        viewHolder.doctorOrgNameText.text = docModel.docName
        if (docModel.isSelected) {
            viewHolder.addText.text = "Selected"
            viewHolder.addText.setTextColor(ContextCompat.getColor(activity,R.color.colorPrimaryDark))
            viewHolder.icon.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_tick))
            viewHolder.icon.setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimaryDark))
        } else {
            viewHolder.addText.text = "ADD"
            viewHolder.addText.setTextColor(ContextCompat.getColor(activity,R.color.colorAccent))
            viewHolder.icon.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_add_person))
            viewHolder.icon.setColorFilter(ContextCompat.getColor(activity,R.color.colorAccent))
        }
        viewHolder.doctorOrgLayout.setOnClickListener { view: View->
            Log.d("Selecting doc", "***********")
            caseDoctorOrganisationClickListener.onItemClick(
                view,
                i,
                caseChannelDoctorModelList[i].isSelected.toString() + "",
                ""
            )
        }
    }

    override fun getItemCount(): Int {
        return caseChannelDoctorModelList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val doctorOrgNameText: TextView
         val addText: TextView
         val doctorOrgLayout: RelativeLayout
         val icon: ImageView

        init {
            doctorOrgNameText = itemView.findViewById(R.id.doctorOrgNameText)
            doctorOrgLayout = itemView.findViewById(R.id.doctorOrgLayout)
            icon = itemView.findViewById(R.id.addUserIcon)
            addText = itemView.findViewById(R.id.addUserText)
        }
    }
}