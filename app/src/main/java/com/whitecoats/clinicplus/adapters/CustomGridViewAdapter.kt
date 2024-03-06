package com.whitecoats.clinicplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.ActivityMoreClickListener
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.R.drawable
import com.whitecoats.clinicplus.models.ItemPrescriptionView
import com.whitecoats.clinicplus.models.PrescriptionViewItemHolder

class CustomGridViewAdapter(
    private val carItemList: List<ItemPrescriptionView>?,
    private val mContext: Context,
    private val activityMoreListner: ActivityMoreClickListener
) : RecyclerView.Adapter<PrescriptionViewItemHolder>() {
    private var prescriptionViewItem: ItemPrescriptionView? = null
    private var imageTileOneLayout: LinearLayout? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrescriptionViewItemHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val carItemView = layoutInflater.inflate(
            R.layout.list_row_appointment_prescription_view,
            parent,
            false
        )
        imageTileOneLayout =
            carItemView.findViewById<View>(R.id.imageTileOneLayout) as LinearLayout
        return PrescriptionViewItemHolder(carItemView)
    }

    override fun onBindViewHolder(holder: PrescriptionViewItemHolder, position: Int) {
        if (carItemList != null) {
            prescriptionViewItem = carItemList[position]
            if (prescriptionViewItem != null) {
                // holder.getCarTitleText().setText(carItem.getQuickActionName());
                val icon: Int
                icon = if (prescriptionViewItem!!.image != null) {
                    getId(prescriptionViewItem!!.image, drawable::class.java)
                } else {
                    getId("ic_pdf_file", drawable::class.java)
                }
                holder.prescriptionImageView.setImageResource(icon)
            }
            imageTileOneLayout!!.setOnClickListener { v ->
                if (carItemList[position].clickId == 0) {
//                        Snackbar snackbar = Snackbar.make(quickLinkLayout, "Service not available", Snackbar.LENGTH_LONG);
//                        snackbar.show();
                    activityMoreListner.onItemClick(
                        v,
                        position,
                        carItemList[position].clickId,
                        carItemList[position].fileUrl
                    )
                } else {
                    activityMoreListner.onItemClick(
                        v,
                        position,
                        carItemList[position].clickId,
                        carItemList[position].fileUrl
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