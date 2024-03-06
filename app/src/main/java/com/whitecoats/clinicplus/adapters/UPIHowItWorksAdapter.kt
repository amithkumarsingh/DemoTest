package com.whitecoats.clinicplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R

class UPIHowItWorksAdapter(private var upiHowItWorksList:MutableList<String>) : RecyclerView.Adapter<UPIHowItWorksAdapter.MyViewModel>() {

    class MyViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imgCircle: ImageView? = null
        var tvNote: TextView? = null

        init {
            imgCircle = itemView.findViewById(R.id.img_circle)
            tvNote = itemView.findViewById(R.id.tv_note)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewModel {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.upi_how_it_works_items, parent, false)
        return MyViewModel(view)
    }

    override fun getItemCount(): Int {
        return upiHowItWorksList.size
    }

    override fun onBindViewHolder(holder: MyViewModel, position: Int) {
       if(!upiHowItWorksList[position].equals("",ignoreCase = true)){
           holder.tvNote!!.text= upiHowItWorksList[position]
       }
    }
}