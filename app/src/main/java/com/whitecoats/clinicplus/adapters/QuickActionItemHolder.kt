package com.whitecoats.clinicplus.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R

class QuickActionItemHolder(itemView: View?) : RecyclerView.ViewHolder(
    itemView!!
) {
    lateinit var carTitleText: TextView
    lateinit var carImageView: ImageView

    init {
        if (itemView != null) {
            carTitleText = itemView.findViewById<View>(R.id.card_view_image_title) as TextView
            carImageView = itemView.findViewById<View>(R.id.card_view_image) as ImageView
        }
    }
}