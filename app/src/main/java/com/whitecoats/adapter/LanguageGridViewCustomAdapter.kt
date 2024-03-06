package com.whitecoats.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.whitecoats.clinicplus.PatientRecordListener
import com.whitecoats.clinicplus.R
import com.whitecoats.model.SettingProfesionalModel

class LanguageGridViewCustomAdapter(
    activity: Activity,
    private val result: List<SettingProfesionalModel>,
    languageClickListner: PatientRecordListener
) : BaseAdapter() {
    var context: Context
    private val languageClickListner: PatientRecordListener

    init {
        // TODO Auto-generated constructor stub
        context = activity
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.languageClickListner = languageClickListner
    }

    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return result.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return position
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    inner class Holder {
        var tv: TextView? = null
        var img: ImageView? = null
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // TODO Auto-generated method stub
        val holder = Holder()
        val rowView: View? = inflater!!.inflate(R.layout.list_row_category_item, null)
        holder.tv = rowView?.findViewById<View>(R.id.categogryName) as TextView
        holder.img = rowView.findViewById<View>(R.id.categoryCancel) as ImageView
        holder.tv!!.text = result[position].languageName

        holder.img!!.setOnClickListener { view ->
            languageClickListner.onItemClick(view, position.toString(), "LANG_REMOVE", "")
        }
        return rowView
    }

    companion object {
        private var inflater: LayoutInflater? = null
    }
}