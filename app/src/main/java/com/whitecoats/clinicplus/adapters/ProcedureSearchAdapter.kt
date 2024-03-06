package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.models.AddProcedureListModel
import java.util.*

class ProcedureSearchAdapter(
    contextObj: Context,
    private val resourceid: Int,
    private val addProcedureListModels: List<AddProcedureListModel>
) : ArrayAdapter<AddProcedureListModel?>(
    contextObj, resourceid, addProcedureListModels
) {
    private var suggestions: MutableList<AddProcedureListModel> = ArrayList()
    private var tempPatients: List<AddProcedureListModel> = ArrayList(addProcedureListModels)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        try {
            if (convertView == null) {
                val inflater = (context as Activity).layoutInflater
                view = inflater.inflate(resourceid, parent, false)
            }
            val addProcedureListModel = getItem(position)
            val procedureName = view!!.findViewById<View>(R.id.procedure_name) as TextView
            val procedurePrice = view.findViewById<TextView>(R.id.procedure_price)
            procedureName.text = addProcedureListModel.procedureName
            procedurePrice.setText(addProcedureListModel.charges)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view!!
    }

    override fun getItem(position: Int): AddProcedureListModel {
        return addProcedureListModels[position]
    }

    override fun getCount(): Int {
        return addProcedureListModels.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return patientFilter
    }

    private val patientFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val addProcedureListModel = resultValue as AddProcedureListModel
            return addProcedureListModel.procedureName
        }

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            return run {
                suggestions.clear()
                for (addProcedureListModel in tempPatients) {
                    if (addProcedureListModel.procedureName.lowercase(Locale.getDefault())
                            .startsWith(
                                charSequence.toString().lowercase(
                                    Locale.getDefault()
                                )
                            )
                    ) {
                        suggestions.add(addProcedureListModel)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            }
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            val tempValues = filterResults.values as ArrayList<AddProcedureListModel>
            if (filterResults.count > 0) {
                clear()
                for (addProcedureListModel in tempValues) {
                    add(addProcedureListModel)
                }
                notifyDataSetChanged()
            } else {
                clear()
                notifyDataSetChanged()
            }
        }
    }

}