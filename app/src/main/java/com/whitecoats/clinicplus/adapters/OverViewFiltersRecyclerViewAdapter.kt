package com.whitecoats.clinicplus.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.OverViewFilterOnClick

class OverViewFiltersRecyclerViewAdapter(
    context: Context,
    filtersStrings: ArrayList<String>,
    overViewFilterOnClick: OverViewFilterOnClick
) : RecyclerView.Adapter<OverViewFiltersRecyclerViewAdapter.MyViewHolder>() {
    private val context: Context
    private val filterOnClick: OverViewFilterOnClick
    private var filtersList = ArrayList<String>()
    private var viewHolder: MyViewHolder? = null
    private var selectedPosition = -1

    init {
        filtersList = filtersStrings
        filterOnClick = overViewFilterOnClick
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.over_view_filters_layout, parent, false)
        viewHolder = MyViewHolder(view)
        return viewHolder!!
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.filterName.text = filtersList[position]
        if (filtersList[position].equals("Custom Date", ignoreCase = true)) {
            holder.spinnerIG.visibility = View.VISIBLE
        } else {
            holder.spinnerIG.visibility = View.GONE
        }
        if (selectedPosition == position) {
            holder.filterLay.background =
               ContextCompat.getDrawable(context,R.drawable.filter_selected_outline)
            holder.filterName.setTextColor(ContextCompat.getColor(context,R.color.white))
        } else {
            holder.filterLay.background =
                ContextCompat.getDrawable(context,R.drawable.filter_outline)
            holder.filterName.setTextColor(ContextCompat.getColor(context,R.color.colorBlack))
        }
        holder.filterLay.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            filterOnClick.onItemClick(filtersList[position])
        }
    }

    override fun getItemCount(): Int {
        return filtersList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filterName: TextView
         val spinnerIG: ImageView
         val filterLay: LinearLayout

        init {
            filterName = itemView.findViewById(R.id.filterName)
            spinnerIG = itemView.findViewById(R.id.spinner_ig)
            filterLay = itemView.findViewById(R.id.filter_lay)
        }
    }
}