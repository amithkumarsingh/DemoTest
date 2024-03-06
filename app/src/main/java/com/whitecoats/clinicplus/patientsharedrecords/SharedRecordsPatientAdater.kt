package com.whitecoats.clinicplus.patientsharedrecords

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.PatientListClickListner
import com.whitecoats.clinicplus.R
import com.whitecoats.model.PatientPListModel

class SharedRecordsPatientAdater(
    private val patientPListModelList: List<PatientPListModel>,
    private val activity: Activity,
    groupData: ArrayList<Int>,
    patientListner: PatientListClickListner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val patientListner: PatientListClickListner
    private val groupData: ArrayList<Int>
    private val appUtilities: AppUtilities
    private lateinit var context: Context

    init {
        this.groupData = groupData
        this.patientListner = patientListner
        appUtilities = AppUtilities()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        when (i) {
            TYPE_ITEM -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.list_row_shared_patient_list, viewGroup, false)
                context = viewGroup.context
                return MyViewHolder(view)
            }
            TYPE_HEADER -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.list_row_shared_patient_list, viewGroup, false)
                context = viewGroup.context
                return MyViewHolder(view)
            }
            TYPE_FOOTER -> {
                val view = LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.activity_path_orderview_footer,
                    viewGroup, false
                )
                return FooterViewHolder(view)
            }
            else -> throw RuntimeException("there is no type that matches the type $i + make sure your using types correctly")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(myViewHolder: RecyclerView.ViewHolder, i: Int) {
        myViewHolder.itemView.tag = patientPListModelList[i]
        if (myViewHolder is MyViewHolder) {
            val patientPListModel = patientPListModelList[i]
            if (groupData[i] == 0) {
                myViewHolder.dateGroup.visibility = View.VISIBLE
                val date = appUtilities.changeDateFormat(
                    "yyyy-MM-dd",
                    "dd-MM-yyyy",
                    patientPListModel.apptDate
                )
                myViewHolder.apptDate.text = date
            } else {
                myViewHolder.dateGroup.visibility = View.GONE
            }
            myViewHolder.patientName.text = patientPListModel.getPatientName()
           // val N = 10 // total number of textviews to add
           // val myTextViews = arrayOfNulls<TextView>(N) // create an empty array;
            myViewHolder.categoryContainer.removeAllViews()
            for (j in patientPListModel.sharedCategoryData.indices) {
                // create a new textview
                val rowTextView = TextView(activity)
               // val marginView = View(activity)
                rowTextView.text = patientPListModel.sharedCategoryData[j].toString()
                rowTextView.background =
                    ContextCompat.getDrawable(activity,R.drawable.drawable_capsule_outline)
                rowTextView.setPadding(20, 15, 20, 15)
                rowTextView.setTextColor(ContextCompat.getColor(activity,R.color.colorPrimary))
                rowTextView.textSize = 12f
                rowTextView.setTypeface(null, Typeface.BOLD)
                val params = LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 10, 10, 10)
                rowTextView.layoutParams = params
                myViewHolder.categoryContainer.addView(rowTextView)
            }
            if (patientPListModel.roleId == 5) {
                myViewHolder.patientRole.text = "Internal"
            } else if (patientPListModel.roleId == 4 || patientPListModel.roleId == 6) {
                myViewHolder.patientRole.text = "Registered"
            }
            myViewHolder.header.setOnClickListener { v ->
                patientListner.onItemClick(
                    v,
                    "GET_PATIENT_RECORDS",
                    patientPListModel.apptDate,
                    0,
                    0,
                    0,
                    0,
                    patientPListModel.patientId,
                    patientPListModel.getPatientName(),
                    null,
                    null
                )
            }
        } else if (myViewHolder is FooterViewHolder) {
            footerHolder = myViewHolder
            if (PatientSharedRecordsActivity.isMoreData && patientPListModelList.size >= 10) {
                myViewHolder.footerText.visibility = View.VISIBLE
            } else {
                myViewHolder.footerText.visibility = View.GONE
            }
            footerHolder!!.footerText.setOnClickListener { view ->
                patientListner.onItemClick(
                    view,
                    "LOADMORE",
                    "",
                    0,
                    0,
                    0,
                    0,
                    0,
                    "",
                    null,
                    null
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return patientPListModelList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (isPositionHeader(position)) {
            return TYPE_HEADER
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER
        }
        return TYPE_ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    private fun isPositionFooter(position: Int): Boolean {
        return position >= patientPListModelList.size - 1 && patientPListModelList.size >= 10
    }

    @SuppressLint("CutPasteId")
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var patientName: TextView
        var apptDate: TextView
        var categoryContainer: LinearLayout
        var header: RelativeLayout
        var dateGroup: RelativeLayout
        var patientRole: TextView

        init {
            patientName = itemView.findViewById(R.id.apptListPatientName)
            dateGroup = itemView.findViewById(R.id.apptListDateGroupLayout)
            header = itemView.findViewById(R.id.apptListHeaderLayout)
            patientRole = itemView.findViewById(R.id.patientRole)
            categoryContainer = itemView.findViewById(R.id.categoryContainer)
            apptDate = itemView.findViewById(R.id.apptListApptDate)
        }
    }

    inner class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var footerText: TextView
        var bottomSpace: View

        init {
            footerText = view.findViewById(R.id.footer_text)
            bottomSpace = view.findViewById(R.id.bottomSpace)
        }
    }

    inner class HeaderViewHolder(var headerView: View) : RecyclerView.ViewHolder(
        headerView
    )

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_FOOTER = 2
        @SuppressLint("StaticFieldLeak")
        var footerHolder: FooterViewHolder? = null
    }
}