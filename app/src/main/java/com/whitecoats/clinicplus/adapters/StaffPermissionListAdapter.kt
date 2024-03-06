package com.whitecoats.clinicplus.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.GBPListClickListener
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.models.StaffPermissionDetailsModel

class StaffPermissionListAdapter(
    private val staffPermissionModelList: List<StaffPermissionDetailsModel>,
    private var context: Context,
    private val gbpListClickListener: GBPListClickListener
) : RecyclerView.Adapter<StaffPermissionListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_staff_permission_list, viewGroup, false)
        context = viewGroup.context
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        myViewHolder.itemView.tag = staffPermissionModelList[i]
        val permissionDetailsModel = staffPermissionModelList[i]
        myViewHolder.serviceName.text = permissionDetailsModel.staffName
        myViewHolder.staffSettingLayout.setOnClickListener {
            gbpListClickListener.onListItemClick(
                "staffPermissions",
                null,
                false,
                i
            )
        }
        myViewHolder.staffCallLayout.setOnClickListener { view: View? ->
            if (permissionDetailsModel.staffPhNumber != null && !permissionDetailsModel.staffPhNumber.isEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + permissionDetailsModel.staffPhNumber)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return staffPermissionModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var serviceName: TextView
        var staffSettingLayout: RelativeLayout
        var staffCallLayout: RelativeLayout

        init {
            serviceName = itemView.findViewById(R.id.serviceName)
            staffSettingLayout = itemView.findViewById(R.id.staffSettingLayout)
            staffCallLayout = itemView.findViewById(R.id.staffCallLayout)
        }
    }
}