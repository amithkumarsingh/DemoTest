package com.whitecoats.clinicplus.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.GBPListClickListener
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.models.GBPListModel
import com.whitecoats.fragments.PatientFragment.Companion.isMoreData

class GBPListAdapter(
    private val gbpModelList: List<GBPListModel>,var context: Context,
    gbpListClickListener: GBPListClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val gbpListClickListener: GBPListClickListener

    init {
        this.gbpListClickListener = gbpListClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (i == TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_gbp_list, viewGroup, false)
            context = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_HEADER) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_gbp_list, viewGroup, false)
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
        myViewHolder.itemView.tag = gbpModelList[i]
        if (myViewHolder is MyViewHolder) {
            val itemViewHolder = myViewHolder
            val gbpListModel = gbpModelList[i]
            if (gbpListModel.service_id == 2) {
                itemViewHolder.chatServiceLayout.visibility = View.VISIBLE
                itemViewHolder.gbpListFormLayout.visibility = View.GONE
            } else {
                itemViewHolder.chatServiceLayout.visibility = View.GONE
                itemViewHolder.gbpListFormLayout.visibility = View.VISIBLE
                itemViewHolder.gbpPageLinkEditText.setText(gbpListModel.gbp_page_link)
                itemViewHolder.reviewLinkEditText.setText(gbpListModel.gbp_review_link)
            }
            itemViewHolder.serviceName.text = gbpListModel.serviceName
            itemViewHolder.gbpSwitchButton.setOnCheckedChangeListener(null)
            itemViewHolder.gbpSwitchButton.isChecked = gbpListModel.isAuto_share_post_consultation
            if (gbpListModel.editModeStatus) {
                if (itemViewHolder.gbpListDetailLayout.visibility == View.VISIBLE) {
                    itemViewHolder.gbpLinkActionLayout.visibility = View.GONE
                    itemViewHolder.gbpLinkEditActionLayout.visibility = View.VISIBLE
                    itemViewHolder.gbpPageLinkEditText.isEnabled = true
                    itemViewHolder.reviewLinkEditText.isEnabled = true
                }
            } else {
                if (itemViewHolder.gbpListDetailLayout.visibility == View.VISIBLE) {
                    itemViewHolder.gbpLinkActionLayout.visibility = View.VISIBLE
                    itemViewHolder.gbpLinkEditActionLayout.visibility = View.GONE
                    itemViewHolder.gbpPageLinkEditText.isEnabled = false
                    itemViewHolder.reviewLinkEditText.isEnabled = false
                }
            }
            if (gbpListModel.isInExpandedMode) {
                itemViewHolder.gbpListDetailLayout.visibility = View.VISIBLE
                itemViewHolder.gbpListArrowIcon.setImageResource(R.drawable.ic_arrow_up)
            } else {
                itemViewHolder.gbpListDetailLayout.visibility = View.GONE
                itemViewHolder.gbpListArrowIcon.setImageResource(R.drawable.ic_arrow_down)
            }
            itemViewHolder.gbpSwitchButton.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
                // do something, the isChecked will be
                // true if the switch is in the On position
                gbpListClickListener.onListItemClick("SwitchClick", gbpListModel, isChecked, i)
            }
            itemViewHolder.gbpListLayout.setOnClickListener {
                if (itemViewHolder.gbpListDetailLayout.visibility == View.VISIBLE) {
                    itemViewHolder.gbpListDetailLayout.visibility = View.GONE
                    itemViewHolder.gbpListArrowIcon.setImageResource(R.drawable.ic_arrow_down)
                    gbpListModel.isInExpandedMode = false
                } else {
                    itemViewHolder.gbpListDetailLayout.visibility = View.VISIBLE
                    itemViewHolder.gbpListArrowIcon.setImageResource(R.drawable.ic_arrow_up)
                    gbpListModel.isInExpandedMode = true
                }
            }
            itemViewHolder.editLinkButtonLayout.setOnClickListener {
                gbpListModel.editModeStatus = true
                itemViewHolder.gbpLinkActionLayout.visibility = View.GONE
                itemViewHolder.gbpLinkEditActionLayout.visibility = View.VISIBLE
                itemViewHolder.gbpPageLinkEditText.isEnabled = true
                itemViewHolder.reviewLinkEditText.isEnabled = true
                itemViewHolder.gpbProfileLinkEditText.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.drawable_rectangle_border_blue,null)
                itemViewHolder.gpbProfileReviewEditText.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.drawable_rectangle_border_blue,null)
                if (itemViewHolder.applyLinkToAllCB.isChecked) {
                    CompoundButtonCompat.setButtonTintList(
                        itemViewHolder.applyLinkToAllCB, ColorStateList
                            .valueOf(ContextCompat.getColor(context, R.color.colorGreyText))
                    )
                    itemViewHolder.applyLinkToAllCB.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                    itemViewHolder.applyLinkToAllCB.isChecked = false
                }
            }
            itemViewHolder.CloseEditButtonLayout.setOnClickListener {
                gbpListModel.editModeStatus = false
                itemViewHolder.gbpPageLinkEditText.setText(gbpListModel.gbp_page_link)
                itemViewHolder.reviewLinkEditText.setText(gbpListModel.gbp_review_link)
                itemViewHolder.gbpLinkActionLayout.visibility = View.VISIBLE
                itemViewHolder.gbpLinkEditActionLayout.visibility = View.GONE
                itemViewHolder.gbpPageLinkEditText.isEnabled = false
                itemViewHolder.reviewLinkEditText.isEnabled = false
                itemViewHolder.applyLinkToAllCB.isChecked = false
                itemViewHolder.gpbProfileLinkEditText.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.drawable_rectangle_border_grey,null)
                itemViewHolder.gpbProfileReviewEditText.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.drawable_rectangle_border_grey,null)
            }
            itemViewHolder.deleteButtonLayout.setOnClickListener {
                gbpListClickListener.onListItemClick(
                    "GBP_RemoveLink",
                    gbpListModel,
                    false,
                    i
                )
            }
            itemViewHolder.applyLinkToAllButtonLayout.setOnClickListener {
                gbpListClickListener.onListItemClick(
                    "GBP_ApplyLinkToAll",
                    gbpListModel,
                    false,
                    i
                )
            }
            itemViewHolder.saveChangeButtonLayout.setOnClickListener {
                val gbpModel = GBPListModel()
                gbpModel.dr_service_id = gbpListModel.dr_service_id
                gbpModel.gbp_page_link = itemViewHolder.gbpPageLinkEditText.text.toString()
                gbpModel.gbp_review_link = itemViewHolder.reviewLinkEditText.text.toString()
                gbpModel.applyToAll = itemViewHolder.applyLinkToAllCB.isChecked
                itemViewHolder.gpbProfileLinkEditText.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.drawable_rectangle_border_grey,null)
                itemViewHolder.gpbProfileReviewEditText.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.drawable_rectangle_border_grey,null)
                gbpListClickListener.onListItemClick("saveChanges", gbpModel, false, i)
            }
            itemViewHolder.applyLinkToAllCB.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    CompoundButtonCompat.setButtonTintList(
                        itemViewHolder.applyLinkToAllCB, ColorStateList
                            .valueOf(ContextCompat.getColor(context, R.color.colorPrimary))
                    )
                    itemViewHolder.applyLinkToAllCB.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                } else {
                    CompoundButtonCompat.setButtonTintList(
                        itemViewHolder.applyLinkToAllCB, ColorStateList
                            .valueOf(ContextCompat.getColor(context, R.color.colorGreyText))
                    )
                    itemViewHolder.applyLinkToAllCB.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                }
            }
        } else if (myViewHolder is FooterViewHolder) {
            footerHolder = myViewHolder
            if (isMoreData && gbpModelList.size >= 50) {
                myViewHolder.footerText.visibility = View.VISIBLE
            } else {
                myViewHolder.footerText.visibility = View.GONE
            }
            footerHolder!!.footerText.setOnClickListener {
                gbpListClickListener.onListItemClick("LOADMORE", null, false, i)
            }
        }
    }

    override fun getItemCount(): Int {
        return gbpModelList.size
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
        return position >= gbpModelList.size - 1 && gbpModelList.size >= 50
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var serviceName: TextView
        var gbpListDetailLayout: LinearLayout
        var gbpListLayout: LinearLayout
        var gbpListArrowIcon: ImageView
        var gbpLinkActionLayout: RelativeLayout
        var gbpLinkEditActionLayout: RelativeLayout
        var CloseEditButtonLayout: RelativeLayout
        var editLinkButtonLayout: RelativeLayout
        var deleteButtonLayout: RelativeLayout
        var applyLinkToAllButtonLayout: RelativeLayout
        var gbpPageLinkEditText: EditText
        var reviewLinkEditText: EditText
        var gbpSwitchButton: Switch
        var applyLinkToAllCB: CheckBox
        var saveChangeButtonLayout: RelativeLayout
        var gbpListFormLayout: RelativeLayout
        var chatServiceLayout: RelativeLayout
        var gpbProfileLinkEditText: RelativeLayout
        var gpbProfileReviewEditText: RelativeLayout

        init {
            serviceName = itemView.findViewById(R.id.serviceName)
            gbpListDetailLayout = itemView.findViewById(R.id.gbpListDetailLayout)
            gbpListLayout = itemView.findViewById(R.id.gbpListLayout)
            gbpListArrowIcon = itemView.findViewById(R.id.gbpListArrowIcon)
            gbpLinkActionLayout = itemView.findViewById(R.id.gbpLinkActionLayout)
            gbpLinkEditActionLayout = itemView.findViewById(R.id.gbpLinkEditActionLayout)
            CloseEditButtonLayout = itemView.findViewById(R.id.CloseEditButtonLayout)
            editLinkButtonLayout = itemView.findViewById(R.id.editLinkButtonLayout)
            deleteButtonLayout = itemView.findViewById(R.id.deleteButtonLayout)
            applyLinkToAllButtonLayout = itemView.findViewById(R.id.applyLinkToAllButtonLayout)
            gbpPageLinkEditText = itemView.findViewById(R.id.gbpPageLinkEditText)
            reviewLinkEditText = itemView.findViewById(R.id.reviewLinkEditText)
            gbpSwitchButton = itemView.findViewById(R.id.gbpSwitchButton)
            applyLinkToAllCB = itemView.findViewById(R.id.applyLinkToAllCB)
            saveChangeButtonLayout = itemView.findViewById(R.id.saveChangeButtonLayout)
            gbpListFormLayout = itemView.findViewById(R.id.gbpListFormLayout)
            chatServiceLayout = itemView.findViewById(R.id.chatServiceLayout)
            gpbProfileLinkEditText = itemView.findViewById(R.id.gpbProfileLinkEditText)
            gpbProfileReviewEditText = itemView.findViewById(R.id.gpbProfileReviewEditText)
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
        var footerHolder: FooterViewHolder? = null
    }
}