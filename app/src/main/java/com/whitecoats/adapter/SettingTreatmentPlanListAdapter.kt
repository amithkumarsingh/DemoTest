package com.whitecoats.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.SettingObserItemClickListener
import com.whitecoats.model.SettingObservationModel

class SettingTreatmentPlanListAdapter(private val settingObserModelList: MutableList<SettingObservationModel>?, private val mContext: Context, private val obserListener: SettingObserItemClickListener) : RecyclerView.Adapter<SettingTreatmentPlanListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_setting_observation, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val settingObserModel = settingObserModelList?.get(i)
        myViewHolder.categoryName.text = settingObserModel!!.name
        if (settingObserModel.categoryStatus) {
            myViewHolder.enableDisable.text = "Enable"
            myViewHolder.enableDisable.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent))
        } else {
            myViewHolder.enableDisable.text = "Disable"
            myViewHolder.enableDisable.setTextColor(ContextCompat.getColor(mContext, R.color.colorDanger))
        }
        myViewHolder.enableDisable.setOnClickListener { v ->
            val obserStatus = settingObserModel.categoryStatus
            val categegoryId = settingObserModel.category
            obserListener.onItemClick(v, i, obserStatus, categegoryId, "treatmentplan")
        }
    }

    override fun getItemCount(): Int {
        return settingObserModelList!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryName: TextView
        var type: TextView? = null
        var enableDisable: TextView

        init {
            categoryName = itemView.findViewById(R.id.name)
            enableDisable = itemView.findViewById(R.id.enableDisable)
        }
    }
}