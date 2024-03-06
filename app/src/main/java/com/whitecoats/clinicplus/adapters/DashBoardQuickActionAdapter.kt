package com.whitecoats.clinicplus.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.whitecoats.clinicplus.ActivityMoreClickListener
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.R.drawable
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.models.DashBoardQuickActionViewItem
import com.zoho.salesiqembed.ZohoSalesIQ

class DashBoardQuickActionAdapter(
    private val carItemList: List<DashBoardQuickActionViewItem>?,
    private val mContext: Context,
    private val activityMoreListner: ActivityMoreClickListener
) : RecyclerView.Adapter<QuickActionItemHolder>() {
    private var carItem: DashBoardQuickActionViewItem? = null
    private var quickLinkLayout: RelativeLayout? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuickActionItemHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val carItemView =
            layoutInflater.inflate(R.layout.activity_quickaction_view_item, parent, false)
        val carTitleView =
            carItemView.findViewById<View>(R.id.card_view_image_title) as TextView
        quickLinkLayout =
            carItemView.findViewById<View>(R.id.quickLinkLayout) as RelativeLayout
        return QuickActionItemHolder(carItemView)
    }

    override fun onBindViewHolder(holder: QuickActionItemHolder, position: Int) {
        if (carItemList != null) {
            carItem = carItemList[position]
            if (carItem != null) {
                holder.carTitleText.text = carItem!!.quickActionName
                val icon: Int
                icon = if (carItem!!.quickActionIcon != null) {
                    getId(carItem!!.quickActionIcon, drawable::class.java)
                } else {
                    getId("ic_home", drawable::class.java)
                }
                holder.carImageView.setImageResource(icon)
                if (carItem!!.quickActionHiddenForDoctorOnly == 1 && ApiUrls.isDoctorOnly == 1) {
                    holder.carImageView.setColorFilter(
                      ContextCompat.getColor(mContext,R.color.colorGrey1),
                        PorterDuff.Mode.SRC_IN
                    )
                    holder.carTitleText.setTextColor(ContextCompat.getColor(mContext,R.color.colorGrey1))
                }
            }
            quickLinkLayout!!.setOnClickListener { v ->
                ZohoSalesIQ.Tracking.setCustomAction("HomeTab - Quick Action - " + carItemList[position].quickActionName)
                if (carItemList[position].quickActionHiddenForDoctorOnly == 1 && ApiUrls.isDoctorOnly == 1) {
                    val snackbar = Snackbar.make(
                        quickLinkLayout!!,
                        "Service not available",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                } else {
                    activityMoreListner.onItemClick(
                        v,
                        position,
                        carItemList[position].quickActionId,
                        ""
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        var ret = 0
        if (carItemList != null) {
            ret = carItemList.size
        }
        return ret
    }

    companion object {
        fun getId(resourceName: String, c: Class<*>): Int {
            return try {
                val idField = c.getDeclaredField(resourceName)
                idField.getInt(idField)
            } catch (e: Exception) {
                throw RuntimeException(
                    "No resource ID found for: "
                            + resourceName + " / " + c, e
                )
            }
        }
    }
}