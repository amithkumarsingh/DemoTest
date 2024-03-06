package com.whitecoats.clinicplus.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.whitecoats.clinicplus.R
import com.whitecoats.model.PatientPListModel
import java.util.*

/**
 * Created by Mohammad suhail ahmed on 09-07-2020.
 */
class PatientSearchAdapter(
    context: Context,
    private val resourceId: Int,
    private val patientPListModelList: List<PatientPListModel>
) : ArrayAdapter<PatientPListModel?>(context, resourceId, patientPListModelList) {
    private var suggestions: MutableList<PatientPListModel>?=null
    private lateinit var tempPatients: List<PatientPListModel>
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        try {
            if (convertView == null) {
                val inflater = (context as Activity).layoutInflater
                view = inflater.inflate(resourceId, parent, false)
            }
            val patientPListModel = getItem(position)
            val name = view!!.findViewById<View>(R.id.patient_suggestion_name) as TextView


            val tvPatientGeneralId = view.findViewById<View>(R.id.tv_patient_general_id) as TextView
            // if (sharedPref.getPref("is_show_general_id", "").equalsIgnoreCase("1")) {
            if (patientPListModel != null) {
                if (patientPListModel.getGeneralID() != null && !patientPListModel.getGeneralID()
                        .equals("", ignoreCase = true)
                ) {
                    tvPatientGeneralId.visibility = View.VISIBLE
                    tvPatientGeneralId.text = patientPListModel.getGeneralID()
                } else {
                    tvPatientGeneralId.visibility = View.GONE
                }
            } else {
                tvPatientGeneralId.visibility = View.GONE
            }
            /* } else {
                tvPatientGeneralId.setVisibility(View.GONE);
            }*/

            name.text =
                patientPListModel.getPatientName() + "(" + patientPListModel.getPhNo() + ")"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view!!
    }

    override fun getItem(position: Int): PatientPListModel {
        return patientPListModelList[position]
    }

    override fun getCount(): Int {
        return patientPListModelList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return patientFilter
    }

    private val patientFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val patientPListModel = resultValue as PatientPListModel
            return patientPListModel.getPatientName()
        }

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            return if (charSequence != null) {
                suggestions!!.clear()
                for (patientPListModel in tempPatients) {
                    if (patientPListModel.getPatientName().lowercase(Locale.getDefault())
                            .startsWith(
                                charSequence.toString().lowercase(
                                    Locale.getDefault()
                                )
                            ) || patientPListModel.getPhNo().startsWith(charSequence.toString())
                    ) {
                        suggestions!!.add(patientPListModel)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions!!.size
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            if (filterResults.count > 0) {
                val tempValues = filterResults.values as ArrayList<*>
                clear()
                for (patientPListModel in tempValues) {
                    add(patientPListModel as PatientPListModel?)
                }
                notifyDataSetChanged()
            } else {
                clear()
                notifyDataSetChanged()
            }
        }
    }

    init {
        suggestions = ArrayList()
        tempPatients = ArrayList(patientPListModelList)
    }
}