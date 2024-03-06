package com.whitecoats.clinicplus.trainingschedule

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.adapter.CategoryGridViewCustomAdapter
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import org.json.JSONObject
class BookedTrainingScheduleListAdapter(
    private var context: Context,
    private val bookedTrainingListModelList: List<upcomingTrainingScheduleListModel>,
    private val activity: Activity,
    private val groupData: ArrayList<Int>,
    private val bookedTrainingClickListener: UpcomingTrainingClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val groupNo = -1
    private val popupWindow: PopupWindow? = null
    private val otpLoading: ProgressDialog? = null
    private val jsonValue: JSONObject? = null
    private val mListAdapter: CategoryGridViewCustomAdapter? = null
    private val appPreference: SharedPreferences
    private val appUtilities: AppUtilities

    init {
        appUtilities = AppUtilities()
        flagAppointmentListAdp = 1
        appPreference = context.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (i == TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_booked_training, viewGroup, false)
            context = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_HEADER) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_booked_training, viewGroup, false)
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
        myViewHolder.itemView.tag = bookedTrainingListModelList[i]
        if (myViewHolder is MyViewHolder) {
            try {
                val itemViewHolder = myViewHolder
                val appointmentApptListModel = bookedTrainingListModelList[i]
                //            Log.d("Group No", groupData.get(i) + " " + i);
                if (groupData[i] == 0) {
                    itemViewHolder.dateGroup.visibility = View.VISIBLE
                    val date = appUtilities.changeDateFormat(
                        "yyyy-MM-dd hh:mm:ss",
                        "dd MMM, yy",
                        appointmentApptListModel.trainingDate
                    )
                    itemViewHolder.apptDate.text = date
                } else {
                    itemViewHolder.dateGroup.visibility = View.GONE
                }
                itemViewHolder.trainingTitle.text = appointmentApptListModel.trainingTitle
                //                String startSlotDate = appUtilities.changeDateFormat("yyyy-MM-dd hh:mm:ss", "HH:mm", appointmentApptListModel.getStartTrainingSlot());
//                String endSlotDate = appUtilities.changeDateFormat("yyyy-MM-dd hh:mm:ss", "HH:mm", appointmentApptListModel.getEndTrainingSlot());
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
                if (appointmentApptListModel.status == 1) {
                    itemViewHolder.rescheduleText.visibility = View.VISIBLE
                    itemViewHolder.cancelText.visibility = View.VISIBLE
                    itemViewHolder.cancelledText.visibility = View.GONE
                    itemViewHolder.completedText.visibility = View.GONE
                    itemViewHolder.postpondText.visibility = View.GONE
                } else if (appointmentApptListModel.status == 2) {
                    itemViewHolder.rescheduleText.visibility = View.VISIBLE
                    itemViewHolder.cancelText.visibility = View.VISIBLE
                    itemViewHolder.cancelledText.visibility = View.GONE
                    itemViewHolder.completedText.visibility = View.GONE
                    itemViewHolder.postpondText.visibility = View.GONE
                } else if (appointmentApptListModel.status == 3) {
                    itemViewHolder.rescheduleText.visibility = View.GONE
                    itemViewHolder.cancelText.visibility = View.GONE
                    itemViewHolder.completedText.visibility = View.GONE
                    itemViewHolder.cancelledText.visibility = View.VISIBLE
                    itemViewHolder.postpondText.visibility = View.GONE
                } else if (appointmentApptListModel.status == 4) {
                    itemViewHolder.rescheduleText.visibility = View.GONE
                    itemViewHolder.cancelText.visibility = View.GONE
                    itemViewHolder.cancelledText.visibility = View.GONE
                    itemViewHolder.completedText.visibility = View.VISIBLE
                    itemViewHolder.postpondText.visibility = View.GONE
                } else if (appointmentApptListModel.status == 5) {
                    itemViewHolder.rescheduleText.visibility = View.GONE
                    itemViewHolder.cancelText.visibility = View.GONE
                    itemViewHolder.cancelledText.visibility = View.GONE
                    itemViewHolder.completedText.visibility = View.GONE
                    itemViewHolder.postpondText.visibility = View.VISIBLE
                }
                itemViewHolder.trainingDescriptionInfoButton.setOnClickListener { v ->
                    bookedTrainingClickListener.onItemClick(
                        v,
                        "Description",
                        i,
                        appointmentApptListModel
                    )
                }

                itemViewHolder.rescheduleText.setOnClickListener { v ->
                    bookedTrainingClickListener.onItemClick(
                        v,
                        "RESCHEDULE",
                        i,
                        appointmentApptListModel
                    )
                }
                itemViewHolder.cancelText.setOnClickListener { v ->
                    bookedTrainingClickListener.onItemClick(
                        v,
                        "CANCEL",
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
            footerHolder!!.footerText.setOnClickListener { view ->
                bookedTrainingClickListener.onItemClick(view, "LOADMORE", i, null)
                //   Toast.makeText(context, "You clicked at Load More", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun getItemCount(): Int {
        return bookedTrainingListModelList.size
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
        return position >= bookedTrainingListModelList.size - 1 && bookedTrainingListModelList.size >= 50
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var patientName: TextView? = null
        var apptTime: TextView? = null
        var apptDate: TextView
        var dateGroup: RelativeLayout
        var details: LinearLayout? = null
        val trainingTitle: TextView
        val slotText: TextView
        val trainingDescriptionInfoButton: ImageView
        private val bookLayout: RelativeLayout
        val rescheduleText: TextView
        val cancelText: TextView
        val cancelledText: TextView
        val completedText: TextView
        val postpondText: TextView
        val trainingDateValue: TextView

        init {
            dateGroup = itemView.findViewById(R.id.apptListDateGroupLayout)
            apptDate = itemView.findViewById(R.id.trainingDate)
            trainingTitle = itemView.findViewById(R.id.trainingTitle)
            trainingDescriptionInfoButton =
                itemView.findViewById(R.id.trainingDescriptionInfoButton)
            slotText = itemView.findViewById(R.id.slotText)
            bookLayout = itemView.findViewById(R.id.bookLayout)
            rescheduleText = itemView.findViewById(R.id.rescheduleText)
            cancelText = itemView.findViewById(R.id.cancelText)
            trainingDateValue = itemView.findViewById(R.id.trainingDateValue)
            cancelledText = itemView.findViewById(R.id.cancelledText)
            postpondText = itemView.findViewById(R.id.postpondText)
            completedText = itemView.findViewById(R.id.completedText)
            itemView.setOnClickListener {

            }
        }
    }

    private fun onItemClickListener(
        myViewHolder: MyViewHolder, appointmentId: Int
    ): AdapterView.OnItemClickListener {
        return AdapterView.OnItemClickListener { parent, view, position, id -> }
    }

    private fun setDynamicHeight(gridView: GridView) {
        var addSomeExtraHeight = 0
        val gridViewAdapter = gridView.adapter
            ?: // pre-condition
            return
        var totalHeight = 0
        val items = gridViewAdapter.count
        var rows = 0

        //added by dileep
        if (items > 3 && items <= 5) {
            addSomeExtraHeight = 150
        } else if (items > 5 && items <= 10) {
            addSomeExtraHeight = 200
        } else if (items > 10 && items <= 15) {
            addSomeExtraHeight = 250
        } else if (items > 15 && items <= 20) {
            addSomeExtraHeight = 300
        } else if (items > 20 && items <= 25) {
            addSomeExtraHeight = 350
        } else if (items > 25 && items <= 30) {
            addSomeExtraHeight = 400
        } else if (items > 30 && items <= 35) {
            addSomeExtraHeight = 450
        } else if (items > 35 && items <= 40) {
            addSomeExtraHeight = 500
        } else if (items > 40 && items <= 45) {
            addSomeExtraHeight = 550
        } else if (items > 45 && items <= 50) {
            addSomeExtraHeight = 600
        } else if (items > 50 && items <= 55) {
            addSomeExtraHeight = 650
        } else if (items > 55 && items <= 60) {
            addSomeExtraHeight = 700
        }
        val listItem = gridViewAdapter.getView(0, null, gridView)
        listItem.measure(0, 0)
        totalHeight = listItem.measuredHeight
        var x = 1f
        if (items > 5) {
            x = (items / 5).toFloat()
            rows = (x + 1).toInt()
            totalHeight *= rows
        }
        val params = gridView.layoutParams
        params.height = totalHeight + addSomeExtraHeight
        gridView.layoutParams = params
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
        var flagAppointmentListAdp = 0
    }
}