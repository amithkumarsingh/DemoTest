package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.EMRMoreInfoClickListener
import com.whitecoats.clinicplus.models.EMRAddRecordCategoryModel
import com.whitecoats.clinicplus.trainingschedule.TrainingScheduleActivity

class EMRTreatmentPlanCategoryListAdapter(
    private val emrAddCategoryRecordsModel: List<EMRAddRecordCategoryModel>,
    private val activity: Activity,
    private val emrMoreInfoClickListener: EMRMoreInfoClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (i == TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_evaluation_category, viewGroup, false)
            context = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_HEADER) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_evaluation_category, viewGroup, false)
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
        myViewHolder.itemView.tag = emrAddCategoryRecordsModel[i]
        if (myViewHolder is MyViewHolder) {
            try {
                val itemViewHolder = myViewHolder
                val moreInfoListModel = emrAddCategoryRecordsModel[i]
                itemViewHolder.categoryName.text = moreInfoListModel.categoryName
                itemViewHolder.categoryNameLayout.setOnClickListener { view ->
                    emrMoreInfoClickListener.onItemClick(
                        view,
                        i,
                        moreInfoListModel.categoryId,
                        moreInfoListModel.categoryName
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
        return emrAddCategoryRecordsModel.size
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
        return if (position >= emrAddCategoryRecordsModel.size - 1 && emrAddCategoryRecordsModel.size >= 50) { // && data.size() >= 10 && appointmentsActivity.isMoreData
            true
        } else {
            false
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryName: TextView
        var categoryNameLayout: RelativeLayout

        init {
            categoryName = itemView.findViewById(R.id.categoryName)
            categoryNameLayout = itemView.findViewById(R.id.categoryNameLayout)
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