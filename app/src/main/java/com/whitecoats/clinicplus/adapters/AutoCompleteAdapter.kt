package com.whitecoats.clinicplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Filter
import android.widget.TextView
import com.whitecoats.clinicplus.ItemClick
import com.whitecoats.clinicplus.R
import com.whitecoats.model.MedicineNameResponse

class AutoCompleteAdapter(
    context: Context,
    private var itemClick: ItemClick
) :
    ArrayAdapter<MedicineNameResponse?>(
        context, R.layout.auto_complete_list_view
    ) {
    val mListItems: MutableList<MedicineNameResponse> = ArrayList()

    override fun getCount(): Int {
        return mListItems.size
    }

    fun setData(list: MutableList<MedicineNameResponse>) {
        mListItems.clear()
        mListItems.addAll(list)
    }

    override fun getItem(position: Int): MedicineNameResponse {
        return mListItems[position]
    }

    override fun getFilter(): Filter {
        return mListItemFilter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                R.layout.auto_complete_list_view, parent,
                false
            )
        }
        val tvMedName = convertView!!.findViewById<TextView>(R.id.tv_med_name)
        val view = convertView.findViewById<View>(R.id.view)
        val tvNoRecords = convertView.findViewById<TextView>(R.id.tv_no_records)
        val btnAddToDatabase = convertView.findViewById<Button>(R.id.btnAddToDatabase)
        val item = getItem(position)
        if (!item.name.equals("No Records", ignoreCase = true)) {
            tvMedName.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
            tvNoRecords.visibility = View.GONE
            btnAddToDatabase.visibility = View.GONE
            tvMedName.text = buildString {
                append(item.name)
                append(" (By-")
                append(item.company)
                append(")")
            }
        } else {
            tvMedName.visibility = View.GONE
            view.visibility = View.GONE
            tvNoRecords.visibility = View.VISIBLE
            btnAddToDatabase.visibility = View.VISIBLE
            btnAddToDatabase.setOnClickListener {
                itemClick.onItemClickListener(position, "showAddToDatabasePopup")
            }
        }
        return convertView
    }

    private var mListItemFilter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val filterResult = FilterResults()
            val autoSuggestionResponses: MutableList<MedicineNameResponse> = ArrayList()
            if (charSequence != null && charSequence.length > 2) {
                autoSuggestionResponses.addAll(mListItems)
            }
            filterResult.values = autoSuggestionResponses
            filterResult.count = autoSuggestionResponses.size
            return filterResult
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            if (filterResults != null && (filterResults.count > 0)) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }

        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as MedicineNameResponse).name + " (By-" + resultValue.company + ")"
        }

    }
}