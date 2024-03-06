package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.adapter.CategoryGridViewCustomAdapter
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.EMRMoreInfoClickListener
import com.whitecoats.clinicplus.models.MoreInfoListModel
import com.whitecoats.clinicplus.trainingschedule.TrainingScheduleActivity
import org.json.JSONObject

class EMRMoreInfoListAdapter(
    private var context: Context,
    private val emrMoreInfoModelList: List<MoreInfoListModel>,
    private val activity: Activity,
    private val emrMoreInfoClickListener: EMRMoreInfoClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val groupNo = -1
    private val popupWindow: PopupWindow? = null
    private val otpLoading: ProgressDialog? = null
    private val jsonValue: JSONObject? = null
    private val mListAdapter: CategoryGridViewCustomAdapter? = null
    private val appPreference: SharedPreferences
    private var groupData: ArrayList<Int>? = null
    private val appUtilities: AppUtilities
    private val diagnosisData: String? = null
    private val selectedPosition = -1

    init {
        groupData = groupData
        appUtilities = AppUtilities()
        appPreference = context.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (i == TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_emr_moreinfo, viewGroup, false)
            context = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_HEADER) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_emr_moreinfo, viewGroup, false)
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


        // return new AppointmentApptListAdapter.MyViewHolder(itemView);
    }

    override fun onBindViewHolder(myViewHolder: RecyclerView.ViewHolder, i: Int) {
        myViewHolder.itemView.tag = emrMoreInfoModelList.get(i)
        if (myViewHolder is MyViewHolder) {
            try {
                val itemViewHolder = myViewHolder
                val moreInfoListModel = emrMoreInfoModelList[i]
                Log.d("FieldValue: ", "FieldValue: " + moreInfoListModel.fieldValue)
                itemViewHolder.fieldName.text = moreInfoListModel.fieldName
                if (moreInfoListModel.fieldValue.equals("View Attachment", ignoreCase = true)) {
                    itemViewHolder.attachmentImage.setImageResource(R.drawable.ic_attachment)
                    itemViewHolder.attachmentImage.visibility = View.VISIBLE
                    itemViewHolder.fieldValue.text = moreInfoListModel.fieldValue
                    itemViewHolder.fieldValue.setTextColor(activity.resources.getColor(R.color.colorAccent))
                    if (moreInfoListModel.fileUrl != null) {
                        val fileURL = moreInfoListModel.fileUrl.trim { it <= ' ' }
                        if (fileURL.equals("", ignoreCase = true) ||
                            fileURL.equals("null", ignoreCase = true)
                        ) {
                            itemViewHolder.attachmentImage.visibility = View.GONE
                            itemViewHolder.fieldValue.text = "No"
                            itemViewHolder.fieldValue.setTextColor(activity.resources.getColor(R.color.colorBlack))
                        }
                    } else {
                        itemViewHolder.attachmentImage.visibility = View.GONE
                        itemViewHolder.fieldValue.text = "No"
                        itemViewHolder.fieldValue.setTextColor(activity.resources.getColor(R.color.colorBlack))
                    }
                } else if (itemViewHolder.fieldName.text.toString().equals(
                        "Attachments",
                        ignoreCase = true
                    ) && moreInfoListModel.fieldValue.equals("Yes", ignoreCase = true)
                ) {
                    itemViewHolder.attachmentImage.setImageResource(R.drawable.ic_attachment)
                    itemViewHolder.attachmentImage.visibility = View.VISIBLE
                    itemViewHolder.fieldValue.text = "View Attachment"
                    itemViewHolder.fieldValue.setTextColor(activity.resources.getColor(R.color.colorAccent))
                } else {
                    if (moreInfoListModel.fieldValue != null) {
                        itemViewHolder.attachmentImage.visibility = View.GONE
                        itemViewHolder.fieldValue.text = moreInfoListModel.fieldValue
                        itemViewHolder.fieldValue.setTextColor(activity.resources.getColor(R.color.colorBlack))
                        if (moreInfoListModel.fieldName.equals("file", ignoreCase = true)) {
                            if (moreInfoListModel.fileUrl != null) {
                                itemViewHolder.fieldValue.text = "View Attachment"
                                itemViewHolder.fieldValue.setTextColor(activity.resources.getColor(R.color.colorAccent))
                            } else {
                                itemViewHolder.fieldValue.text = "-"
                                itemViewHolder.fieldValue.setTextColor(activity.resources.getColor(R.color.colorBlack))
                            }
                        }
                    } else {
                        itemViewHolder.attachmentImage.visibility = View.GONE
                        itemViewHolder.fieldValue.text = "-"
                        itemViewHolder.fieldValue.setTextColor(activity.resources.getColor(R.color.colorBlack))
                    }
                }
                itemViewHolder.fieldValue.setOnClickListener(View.OnClickListener { view ->
                    if (itemViewHolder.fieldValue.text.toString()
                            .equals("View Attachment", ignoreCase = true)
                    ) {
                        emrMoreInfoClickListener.onItemClick(
                            view,
                            i,
                            moreInfoListModel.fileUrl,
                            "FILE_URL"
                        )
                    }
                })
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
            footerHolder!!.footerText.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {

//                    emrSwitchCaseClickListener.onItemClick(view, "LOADMORE", i, null);
                    //   Toast.makeText(context, "You clicked at Load More", Toast.LENGTH_SHORT).show();
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return emrMoreInfoModelList.size
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
        return if (position >= (emrMoreInfoModelList.size - 1) && emrMoreInfoModelList.size >= 50) { // && data.size() >= 10 && appointmentsActivity.isMoreData
            true
        } else {
            false
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var caseName: TextView? = null
        var createdOnDate: TextView? = null
        var numberOfInteraction: TextView? = null
        var lastInteractionOn: TextView? = null
        var diagnosis: TextView? = null
        var status: TextView? = null
        var selectCaseRadioButton: RadioButton? = null
        var dateGroup: RelativeLayout? = null
        var details: LinearLayout? = null
        private val trainingTitle: TextView? = null
        private val slotText: TextView? = null
        private val trainingDescriptionInfoButton: ImageView? = null
        private val bookLayout: RelativeLayout? = null
        private val rescheduleText: TextView? = null
        private val cancelText: TextView? = null
        private val cancelledText: TextView? = null
        private val completedText: TextView? = null
        private val postpondText: TextView? = null
        private val trainingDateValue: TextView? = null
        var fieldName: TextView
        var fieldValue: TextView
        val attachmentImage: ImageView

        init {
            fieldName = itemView.findViewById(R.id.fieldName)
            fieldValue = itemView.findViewById(R.id.fieldValue)
            attachmentImage = itemView.findViewById(R.id.attachmentImage)

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // Toast.makeText(view.getContext(), cpu.getPersonName()+" is "+ cpu.getJobProfile(), Toast.LENGTH_SHORT).show();
//
//                }
//            });
        }
    }

    private fun onItemClickListener(
        myViewHolder: MyViewHolder, appointmentId: Int
    ): AdapterView.OnItemClickListener {
        return object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
            }
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
        private val TYPE_HEADER = 0
        private val TYPE_ITEM = 1
        private val TYPE_FOOTER = 2
        var footerHolder: FooterViewHolder? = null
    }
}