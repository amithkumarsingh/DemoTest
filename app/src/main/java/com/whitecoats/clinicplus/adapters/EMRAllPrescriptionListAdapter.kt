package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.EMRMoreInfoClickListener
import com.whitecoats.clinicplus.interfaces.EMRPrescriptionClickListener
import com.whitecoats.clinicplus.models.EMRPrescriptionModel
import com.whitecoats.clinicplus.trainingschedule.TrainingScheduleActivity

class EMRAllPrescriptionListAdapter(
    private val emrAllPrescriptionListModel: List<EMRPrescriptionModel>,
    private val activity: Activity,
    private val emrPrescriptionClickListener: EMRPrescriptionClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val emrMoreInfoClickListener: EMRMoreInfoClickListener? = null
    private var context: Context? = null
    private val appUtilities = AppUtilities()
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (i == TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_emr_all_prescription, viewGroup, false)
            context = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_HEADER) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_emr_all_prescription, viewGroup, false)
            context = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_FOOTER) {
            val view = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.activity_path_orderview_footer,
                viewGroup, false
            )
            return FooterViewHolder(view)
        }
        throw RuntimeException("there is no type that matches the type $i + make sure your using types correctly")
    }

    override fun onBindViewHolder(myViewHolder: RecyclerView.ViewHolder, i: Int) {
        myViewHolder.itemView.tag = emrAllPrescriptionListModel[i]
        if (myViewHolder is MyViewHolder) {
            try {
                val itemViewHolder = myViewHolder
                val moreInfoListModel = emrAllPrescriptionListModel[i]
                itemViewHolder.prescriptionName.text = appUtilities.changeDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    "dd MMM, yyyy",
                    moreInfoListModel.createdAt
                )
                itemViewHolder.viewPrescription.setOnClickListener { view ->
                    emrPrescriptionClickListener.onItemClick(
                        view,
                        emrAllPrescriptionListModel[i]
                    )
                }
            } catch (e: Exception) {
                e.message
            }
        } else if (myViewHolder is FooterViewHolder) {
            footerHolder = myViewHolder
            if (TrainingScheduleActivity.isMoreData) {
                myViewHolder.footerText.visibility = View.VISIBLE
            } else {
                myViewHolder.footerText.visibility = View.GONE
            }
            footerHolder!!.footerText.setOnClickListener { }
        }
    }

    override fun getItemCount(): Int {
        return emrAllPrescriptionListModel.size
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
        return if (position >= emrAllPrescriptionListModel.size - 1 && emrAllPrescriptionListModel.size >= 50) { // && data.size() >= 10 && appointmentsActivity.isMoreData
            true
        } else {
            false
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var prescriptionName: TextView
        val viewPrescription: TextView

        init {
            prescriptionName = itemView.findViewById(R.id.prescriptionName)
            viewPrescription = itemView.findViewById(R.id.prescriptionViewText)
        }
    }

    private fun onItemClickListener(
        myViewHolder: MyViewHolder, appointmentId: Int
    ): AdapterView.OnItemClickListener {
        return AdapterView.OnItemClickListener { parent, view, position, id -> }
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
        var footerHolder: FooterViewHolder? = null
    }
}