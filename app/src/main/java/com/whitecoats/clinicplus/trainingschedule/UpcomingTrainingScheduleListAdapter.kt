package com.whitecoats.clinicplus.trainingschedule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.adapter.CategoryGridViewCustomAdapter
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R

class UpcomingTrainingScheduleListAdapter(
    private var context: Context,
    private val upcomingTrainingListModelList: List<upcomingTrainingScheduleListModel>,
    private val activity: Activity,
    private val groupData: ArrayList<Int>,
    private val upcomingTrainingClickListener: UpcomingTrainingClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mListAdapter: CategoryGridViewCustomAdapter? = null
    private val appUtilities: AppUtilities

    init {
        appUtilities = AppUtilities()
        flagAppointmentListAdp = 1
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (i == TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_upcoming_training, viewGroup, false)
            context = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_HEADER) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_upcoming_training, viewGroup, false)
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

    override fun onBindViewHolder(myViewHolder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") i: Int) {
        myViewHolder.itemView.tag = upcomingTrainingListModelList.get(i)
        if (myViewHolder is MyViewHolder) {
            try {
                val itemViewHolder = myViewHolder
                val appointmentApptListModel = upcomingTrainingListModelList[i]
                itemViewHolder.dateGroup.visibility = View.VISIBLE
                itemViewHolder.trainingTitle.text = appointmentApptListModel.trainingTitle

                val currentStringStart = appointmentApptListModel.startTrainingSlot
                val separatedStart =
                    currentStringStart.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val startSlotDate = separatedStart[1].substring(0, separatedStart[1].length - 3)
                val currentStringEnd = appointmentApptListModel.endTrainingSlot
                val separatedEnd =
                    currentStringEnd.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val endSlotDate = separatedEnd[1].substring(0, separatedEnd[1].length - 3)
                itemViewHolder.slotText.text = "$startSlotDate - $endSlotDate"
                val startTrainingDate = appUtilities.changeDateFormat(
                    "yyyy-MM-dd hh:mm:ss",
                    "dd MMM, yy",
                    appointmentApptListModel.startTrainingSlot
                )
                itemViewHolder.trainingDateValue.text = startTrainingDate
                itemViewHolder.trainingDescriptionInfoButton.setOnClickListener(View.OnClickListener { v ->
                    upcomingTrainingClickListener.onItemClick(
                        v,
                        "Description",
                        i,
                        appointmentApptListModel
                    )
                })
                itemViewHolder.bookTextView.setOnClickListener { v ->
                    upcomingTrainingClickListener.onItemClick(
                        v,
                        "TaringBook",
                        i,
                        appointmentApptListModel
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
            footerHolder!!.footerText.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    upcomingTrainingClickListener.onItemClick(view, "LOADMORE", i, null)
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return upcomingTrainingListModelList.size
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
        return position >= (upcomingTrainingListModelList.size - 1) && upcomingTrainingListModelList.size >= 50
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var patientName: TextView? = null
        var apptTime: TextView? = null
        var apptDate: TextView? = null
        var dateGroup: RelativeLayout
        var details: LinearLayout? = null
        val trainingTitle: TextView
        val slotText: TextView
        val trainingDescriptionInfoButton: ImageView
        private val bookLayout: RelativeLayout
        val bookTextView: TextView
        val trainingDateValue: TextView

        init {
            dateGroup = itemView.findViewById(R.id.apptListDateGroupLayout)
            //            apptDate = itemView.findViewById(R.id.apptListApptDate);
            trainingTitle = itemView.findViewById(R.id.trainingTitle)
            trainingDescriptionInfoButton =
                itemView.findViewById(R.id.trainingDescriptionInfoButton)
            slotText = itemView.findViewById(R.id.slotText)
            bookLayout = itemView.findViewById(R.id.bookLayout)
            bookTextView = itemView.findViewById(R.id.bookTextView)
            trainingDateValue = itemView.findViewById(R.id.trainingDateValue)
            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {

                }
            })
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
        private val TYPE_HEADER = 0
        private val TYPE_ITEM = 1
        private val TYPE_FOOTER = 2
        var footerHolder: FooterViewHolder? = null
        var flagAppointmentListAdp = 0
    }
}