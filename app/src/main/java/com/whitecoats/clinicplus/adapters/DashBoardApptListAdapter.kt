package com.whitecoats.clinicplus.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.fragments.DashboardFullMode
import com.whitecoats.clinicplus.interfaces.DashBoardAppointmentClickListener
import com.whitecoats.clinicplus.models.DashBoardApptListModel
import com.whitecoats.clinicplus.utils.SharedPref
import org.json.JSONException

class DashBoardApptListAdapter(
    private val mContext: Context,
    private val dashBoardApptListModelList: List<DashBoardApptListModel>,
    private var dashBoardApptClickLitner: DashBoardAppointmentClickListener
) : RecyclerView.Adapter<DashBoardApptListAdapter.MyViewHolder>() {
    private val sharedPref: SharedPref = SharedPref(mContext)
    private val appUtilities: AppUtilities = AppUtilities()

    init {
        isAppointmentNextBackClinicClick = 0
        isAppointmentNextBackVideoClick = 0
        isAppointmentNextBackChatClick = 0
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_dashboard_appt, viewGroup, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        myViewHolder: MyViewHolder,
        @SuppressLint("RecyclerView") i: Int
    ) {

        //getting current position value
        try {
            val homeApptListModel = dashBoardApptListModelList[i]
            if (homeApptListModel.serviceId == 1) {
                if (sharedPref.isPrefExists("Video")) {
                    if (homeApptListModel.apptCount > 0) {
                        myViewHolder.appointmentAndChatBookedLayout.visibility = View.VISIBLE
                        myViewHolder.noAppointmentAndChatBookedLayout.visibility = View.GONE
                        myViewHolder.homeApptListClinicNameBooked.text =
                            homeApptListModel.clinicName
                        DashboardFullMode.switchClinicSelectedString = homeApptListModel.clinicName
                        myViewHolder.switchClinicTextBooked.visibility = View.GONE
                        if (DashboardFullMode.selectedDuration.equals("Today", ignoreCase = true)) {
                            myViewHolder.todaysCountBooked.text = buildString {
                                append("Today's count")
                            }
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "All",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCountBooked.text = buildString {
                                append("All Count")
                            }
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "This Week",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCountBooked.text = buildString {
                                append("This Week Count")
                            }
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "This Month",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCountBooked.text = buildString {
                                append("This Month Count")
                            }
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "This Year",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCountBooked.text =
                                buildString { append("This Year Count") }
                        }
                        myViewHolder.todaysCountValueBooked.text =
                            buildString {
                                append(homeApptListModel.apptCount.toString())
                                append("")
                            }
                        myViewHolder.bulkCancel.visibility = View.GONE // change
                        myViewHolder.delayIntimation.visibility = View.GONE
                        myViewHolder.bulkComplete.visibility = View.GONE
                        myViewHolder.bulkCompleteVideo.visibility =
                            View.GONE //change visible to gone  on 22nd aug.
                        myViewHolder.dashBoardBookAppointment.visibility = View.VISIBLE
                        myViewHolder.viewAllChat.visibility = View.GONE
                        if (homeApptListModel.patientApptArray.length() > 0) {
                            if (homeApptListModel.patientApptArray.length() == 1) {
                                myViewHolder.leftArrowButtonLayout.isClickable = false
                                myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorGrey1),
                                    PorterDuff.Mode.SRC_IN
                                )
                                myViewHolder.rightArrowButtonLayout.isClickable = false
                                myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorGrey1),
                                    PorterDuff.Mode.SRC_IN
                                )
                            } else {
                                if (isAppointmentNextBackVideoClick == 0) {
                                    myViewHolder.leftArrowButtonLayout.isClickable = false
                                    myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorGrey1),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                    myViewHolder.rightArrowButtonLayout.isClickable = true
                                    myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                }
                            }
                            val appointmentDateString =
                                homeApptListModel.patientApptArray.getJSONObject(0)
                                    .getString("appt_time")
                            val seprateAppointmentString =
                                appointmentDateString.split(" ".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            val appointmentDate = seprateAppointmentString[0]
                            val appointmentDateFormat = appUtilities.changeDateFormat(
                                "yyyy-MM-dd",
                                "dd MMM, yy",
                                appointmentDate
                            )
                            val appointmetTime = appUtilities.formatTimeBasedOnPreference(
                                mContext, "HH:mm:ss", seprateAppointmentString[1]
                            )
                            if (DashboardFullMode.selectedDuration.equals(
                                    "Today",
                                    ignoreCase = true
                                )
                            ) {
                                myViewHolder.patientName.text =
                                    homeApptListModel.patientApptArray.getJSONObject(0)
                                        .getString("patient_name")
                                myViewHolder.appointmentDate.text = buildString {
                                    append("@ ")
                                    append(appointmetTime)
                                }
                            } else {
                                myViewHolder.patientName.text =
                                    homeApptListModel.patientApptArray.getJSONObject(0)
                                        .getString("patient_name")
                                myViewHolder.appointmentDate.text =
                                    buildString {
                                        append("On ")
                                        append(appointmentDateFormat)
                                        append(" @ ")
                                        append(appointmetTime)
                                    }
                            }
                        }
                    } else {
                        myViewHolder.appointmentAndChatBookedLayout.visibility = View.GONE
                        myViewHolder.noAppointmentAndChatBookedLayout.visibility = View.VISIBLE
                        myViewHolder.homeApptListClinicName.text = homeApptListModel.clinicName
                        myViewHolder.switchClinicText.visibility = View.GONE
                        myViewHolder.noAppointmentText.text = "You have no appointment for now"
                        myViewHolder.bookNewAppointmetText.text = "Book new Appt."
                        if (DashboardFullMode.selectedDuration.equals("Today", ignoreCase = true)) {
                            myViewHolder.todaysCount.text = "Today's Count"
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "All",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCount.text = "All Count"
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "This Week",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCount.text = "This Week Count"
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "This Month",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCount.text = "This Month Count"
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "This Year",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCount.text = "This Year Count"
                        }
                        myViewHolder.todaysCountValue.text =
                            homeApptListModel.apptCount.toString() + ""
                    }
                } else {
                    myViewHolder.appointmentAndChatBookedLayout.visibility = View.GONE
                    myViewHolder.noAppointmentAndChatBookedLayout.visibility = View.GONE
                }
            } else if (homeApptListModel.serviceId == 2) {
                if (sharedPref.isPrefExists("Chat")) {
                    if (homeApptListModel.apptCount > 0) {
                        myViewHolder.appointmentAndChatBookedLayout.visibility = View.VISIBLE
                        myViewHolder.noAppointmentAndChatBookedLayout.visibility = View.GONE
                        myViewHolder.homeApptListClinicNameBooked.text =
                            homeApptListModel.clinicName
                        myViewHolder.switchClinicTextBooked.visibility = View.GONE
                        if (DashboardFullMode.selectedDuration.equals("Today", ignoreCase = true)) {
                            myViewHolder.todaysCountBooked.text = "Today's Count"
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "All",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCountBooked.text = "All Count"
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "This Week",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCountBooked.text = "This Week Count"
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "This Month",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCountBooked.text = "This Month Count"
                        } else if (DashboardFullMode.selectedDuration.equals(
                                "This Year",
                                ignoreCase = true
                            )
                        ) {
                            myViewHolder.todaysCountBooked.text = "This Year Count"
                        }
                        myViewHolder.todaysCountValueBooked.text =
                            homeApptListModel.apptCount.toString() + ""
                        myViewHolder.bulkCancel.visibility = View.GONE
                        myViewHolder.delayIntimation.visibility = View.GONE
                        myViewHolder.bulkComplete.visibility = View.GONE
                        myViewHolder.bulkCompleteVideo.visibility = View.GONE
                        myViewHolder.dashBoardBookAppointment.visibility =
                            View.VISIBLE // commented 14th
                        myViewHolder.viewAllChat.visibility = View.VISIBLE
                        if (homeApptListModel.patientApptArray.length() > 0) {
                            if (homeApptListModel.patientApptArray.length() == 1) {
                                myViewHolder.leftArrowButtonLayout.isClickable = false
                                myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorGrey1),
                                    PorterDuff.Mode.SRC_IN
                                )
                                myViewHolder.rightArrowButtonLayout.isClickable = false
                                myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorGrey1),
                                    PorterDuff.Mode.SRC_IN
                                )
                            } else {
                                if (isAppointmentNextBackChatClick == 0) {
                                    myViewHolder.leftArrowButtonLayout.isClickable = false
                                    myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorGrey1),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                    myViewHolder.rightArrowButtonLayout.isClickable = true
                                    myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                }
                            }
                            val appointmentDateString =
                                homeApptListModel.patientApptArray.getJSONObject(0)
                                    .getJSONObject("chat_session").getString("expiry_time")
                            val seprateAppointmentString =
                                appointmentDateString.split(" ".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            val appointmentDate = seprateAppointmentString[0]
                            val appointmentDateFormat = appUtilities.changeDateFormat(
                                "yyyy-MM-dd",
                                "dd MMM, yy",
                                appointmentDate
                            )
                            myViewHolder.patientName.text =
                                homeApptListModel.patientApptArray.getJSONObject(0)
                                    .getJSONObject("chat_user").getString("fname")
                            val str =
                                "<a><font color='#000000'> Valid Till: </font> $appointmentDateFormat </a>"
                            myViewHolder.appointmentDate.text = Html.fromHtml(str)
                        }
                    } else {
                        myViewHolder.appointmentAndChatBookedLayout.visibility = View.GONE
                        myViewHolder.noAppointmentAndChatBookedLayout.visibility = View.VISIBLE
                        myViewHolder.homeApptListClinicName.text = homeApptListModel.clinicName
                        myViewHolder.switchClinicText.visibility = View.GONE
                        myViewHolder.noAppointmentText.text = "You have no chat subscribed yet"
                        myViewHolder.bookNewAppointmetText.text = "Book New Chat"
                        myViewHolder.todaysCount.text = "New Message"
                        myViewHolder.todaysCountValue.text =
                            homeApptListModel.apptCount.toString() + ""
                    }
                } else {
                    myViewHolder.appointmentAndChatBookedLayout.visibility = View.GONE
                    myViewHolder.noAppointmentAndChatBookedLayout.visibility = View.GONE
                }
            } else {
                if (homeApptListModel.apptCount > 0) {
                    myViewHolder.appointmentAndChatBookedLayout.visibility = View.VISIBLE
                    myViewHolder.noAppointmentAndChatBookedLayout.visibility = View.VISIBLE
                    myViewHolder.homeApptListClinicNameBooked.text = homeApptListModel.clinicName
                    DashboardFullMode.switchClinicSelectedString = homeApptListModel.clinicName
                    if (AppConstants.clinicCount <= 1) {
                        myViewHolder.switchClinicTextBooked.visibility = View.GONE
                    } else {
                        myViewHolder.switchClinicTextBooked.visibility = View.VISIBLE
                    }
                    if (DashboardFullMode.selectedDuration.equals("Today", ignoreCase = true)) {
                        myViewHolder.todaysCountBooked.text = "Today's Count"
                        myViewHolder.delayIntimation.visibility = View.VISIBLE
                    } else if (DashboardFullMode.selectedDuration.equals(
                            "All",
                            ignoreCase = true
                        )
                    ) {
                        myViewHolder.todaysCountBooked.text = "All Count"
                        myViewHolder.delayIntimation.visibility = View.GONE
                    } else if (DashboardFullMode.selectedDuration.equals(
                            "This Week",
                            ignoreCase = true
                        )
                    ) {
                        myViewHolder.todaysCountBooked.text = "This Week Count"
                        myViewHolder.delayIntimation.visibility = View.GONE
                    } else if (DashboardFullMode.selectedDuration.equals(
                            "This Month",
                            ignoreCase = true
                        )
                    ) {
                        myViewHolder.todaysCountBooked.text = "This Month Count"
                        myViewHolder.delayIntimation.visibility = View.GONE
                    } else if (DashboardFullMode.selectedDuration.equals(
                            "This Year",
                            ignoreCase = true
                        )
                    ) {
                        myViewHolder.todaysCountBooked.text = "This Year Count"
                        myViewHolder.delayIntimation.visibility = View.GONE
                    }
                    myViewHolder.todaysCountValueBooked.text =
                        homeApptListModel.apptCount.toString() + ""
                    myViewHolder.bulkCancel.visibility = View.VISIBLE
                    myViewHolder.bulkComplete.visibility = View.VISIBLE
                    myViewHolder.bulkCompleteVideo.visibility = View.GONE
                    myViewHolder.dashBoardBookAppointment.visibility = View.GONE
                    myViewHolder.viewAllChat.visibility = View.GONE
                    if (homeApptListModel.patientApptArray.length() > 0) {
                        if (homeApptListModel.patientApptArray.length() == 1) {
                            myViewHolder.leftArrowButtonLayout.isClickable = false
                            myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                ContextCompat.getColor(mContext, R.color.colorGrey1),
                                PorterDuff.Mode.SRC_IN
                            )
                            myViewHolder.rightArrowButtonLayout.isClickable = false
                            myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                ContextCompat.getColor(mContext, R.color.colorGrey1),
                                PorterDuff.Mode.SRC_IN
                            )
                        } else {
                            if (isAppointmentNextBackClinicClick == 0) {
                                myViewHolder.leftArrowButtonLayout.isClickable = false
                                myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorGrey1),
                                    PorterDuff.Mode.SRC_IN
                                )
                                myViewHolder.rightArrowButtonLayout.isClickable = true
                                myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                            }
                        }
                        val appointmentDateString =
                            homeApptListModel.patientApptArray.getJSONObject(0)
                                .getString("appt_time")
                        val seprateAppointmentString = appointmentDateString.split(" ".toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        val appointmentDate = seprateAppointmentString[0]
                        val appointmentDateFormat = appUtilities.changeDateFormat(
                            "yyyy-MM-dd",
                            "dd MMM, yy",
                            appointmentDate
                        )
                        val appointmetTime = appUtilities.formatTimeBasedOnPreference(
                            mContext, "HH:mm:ss", seprateAppointmentString[1]
                        )
                        if (DashboardFullMode.selectedDuration.equals("Today", ignoreCase = true)) {
                            myViewHolder.patientName.text =
                                homeApptListModel.patientApptArray.getJSONObject(0)
                                    .getString("patient_name")
                            myViewHolder.appointmentDate.text = "@ $appointmetTime"
                        } else {
                            myViewHolder.patientName.text =
                                homeApptListModel.patientApptArray.getJSONObject(0)
                                    .getString("patient_name")
                            myViewHolder.appointmentDate.text =
                                "On $appointmentDateFormat @ $appointmetTime"
                        }
                    }
                } else {
                    myViewHolder.appointmentAndChatBookedLayout.visibility = View.GONE
                    myViewHolder.noAppointmentAndChatBookedLayout.visibility = View.VISIBLE
                    myViewHolder.homeApptListClinicName.text = homeApptListModel.clinicName
                    if (AppConstants.clinicCount <= 1) {
                        myViewHolder.switchClinicText.visibility = View.GONE
                    } else {
                        myViewHolder.switchClinicText.visibility = View.VISIBLE
                    }
                    myViewHolder.noAppointmentText.text = "You have no appointment for now"
                    myViewHolder.bookNewAppointmetText.text = "Book new Appt."
                    if (DashboardFullMode.selectedDuration.equals("Today", ignoreCase = true)) {
                        myViewHolder.todaysCount.text = "Today's Count"
                    } else if (DashboardFullMode.selectedDuration.equals(
                            "All",
                            ignoreCase = true
                        )
                    ) {
                        myViewHolder.todaysCount.text = "All Count"
                    } else if (DashboardFullMode.selectedDuration.equals(
                            "This Week",
                            ignoreCase = true
                        )
                    ) {
                        myViewHolder.todaysCount.text = "This Week Count"
                    } else if (DashboardFullMode.selectedDuration.equals(
                            "This Month",
                            ignoreCase = true
                        )
                    ) {
                        myViewHolder.todaysCount.text = "This Month Count"
                    } else if (DashboardFullMode.selectedDuration.equals(
                            "This Year",
                            ignoreCase = true
                        )
                    ) {
                        myViewHolder.todaysCount.text = "This Year Count"
                    }
                    myViewHolder.todaysCountValue.text = homeApptListModel.apptCount.toString() + ""
                }
            }
            myViewHolder.leftArrowButtonLayout.setOnClickListener {
                if (homeApptListModel.serviceId == 1) {
                    if (isAppointmentNextBackVideoClick == 0) {
                        isAppointmentNextBackVideoClick = 0
                    } else {
                        isAppointmentNextBackVideoClick--
                    }
                    try {
                        if (homeApptListModel.patientApptArray.length() > 1) {
                            if (isAppointmentNextBackVideoClick == 0) {
                                myViewHolder.leftArrowButtonLayout.isClickable = false
                                myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorGrey1),
                                    PorterDuff.Mode.SRC_IN
                                )
                                myViewHolder.rightArrowButtonLayout.isClickable = true
                                myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                            } else {
                                myViewHolder.leftArrowButtonLayout.isClickable = true
                                myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                                myViewHolder.rightArrowButtonLayout.isClickable = true
                                myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                            }
                            val appointmentDateString =
                                homeApptListModel.patientApptArray.getJSONObject(
                                    isAppointmentNextBackVideoClick
                                ).getString("appt_time")
                            val seprateAppointmentString =
                                appointmentDateString.split(" ".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            val appointmentDate = seprateAppointmentString[0]
                            val appointmentDateFormat = appUtilities.changeDateFormat(
                                "yyyy-MM-dd",
                                "dd MMM, yy",
                                appointmentDate
                            )
                            val appointmetTime = appUtilities.formatTimeBasedOnPreference(
                                mContext, "HH:mm:ss", seprateAppointmentString[1]
                            )
                            if (DashboardFullMode.selectedDuration.equals(
                                    "Today",
                                    ignoreCase = true
                                )
                            ) {
                                myViewHolder.patientName.text =
                                    homeApptListModel.patientApptArray.getJSONObject(
                                        isAppointmentNextBackVideoClick
                                    ).getString("patient_name")
                                myViewHolder.appointmentDate.text = "@ $appointmetTime"
                            } else {
                                myViewHolder.patientName.text =
                                    homeApptListModel.patientApptArray.getJSONObject(
                                        isAppointmentNextBackVideoClick
                                    ).getString("patient_name")
                                myViewHolder.appointmentDate.text =
                                    "On $appointmentDateFormat @ $appointmetTime"
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else if (homeApptListModel.serviceId == 2) {
                    if (isAppointmentNextBackChatClick == 0) {
                        isAppointmentNextBackChatClick = 0
                    } else {
                        isAppointmentNextBackChatClick--
                    }
                    try {
                        if (homeApptListModel.patientApptArray.length() > 1) {
                            if (isAppointmentNextBackChatClick == 0) {
                                myViewHolder.leftArrowButtonLayout.isClickable = false
                                myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorGrey1),
                                    PorterDuff.Mode.SRC_IN
                                )
                                myViewHolder.rightArrowButtonLayout.isClickable = true
                                myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                            } else {
                                myViewHolder.leftArrowButtonLayout.isClickable = true
                                myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                                myViewHolder.rightArrowButtonLayout.isClickable = true
                                myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                            }
                            val appointmentDateString =
                                homeApptListModel.patientApptArray.getJSONObject(
                                    isAppointmentNextBackChatClick
                                ).getJSONObject("chat_session").getString("expiry_time")
                            val seprateAppointmentString =
                                appointmentDateString.split(" ".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            val appointmentDate = seprateAppointmentString[0]
                            val appointmentDateFormat = appUtilities.changeDateFormat(
                                "yyyy-MM-dd",
                                "dd MMM, yy",
                                appointmentDate
                            )
                            myViewHolder.patientName.text =
                                homeApptListModel.patientApptArray.getJSONObject(
                                    isAppointmentNextBackChatClick
                                ).getJSONObject("chat_user").getString("fname")
                            val str =
                                "<a><font color='#000000'> Valid Till: </font> $appointmentDateFormat </a>"
                            myViewHolder.appointmentDate.text = Html.fromHtml(str)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                if (homeApptListModel.serviceId == 3) {
                    if (isAppointmentNextBackClinicClick == 0) {
                        isAppointmentNextBackClinicClick = 0
                    } else {
                        isAppointmentNextBackClinicClick--
                    }
                    try {
                        if (homeApptListModel.patientApptArray.length() > 1) {
                            if (isAppointmentNextBackClinicClick == 0) {
                                myViewHolder.leftArrowButtonLayout.isClickable = false
                                myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorGrey1),
                                    PorterDuff.Mode.SRC_IN
                                )
                                myViewHolder.rightArrowButtonLayout.isClickable = true
                                myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                            } else {
                                myViewHolder.leftArrowButtonLayout.isClickable = true
                                myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                                myViewHolder.rightArrowButtonLayout.isClickable = true
                                myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                    ContextCompat.getColor(mContext, R.color.colorAccent),
                                    PorterDuff.Mode.SRC_IN
                                )
                            }
                            val appointmentDateString =
                                homeApptListModel.patientApptArray.getJSONObject(
                                    isAppointmentNextBackClinicClick
                                ).getString("appt_time")
                            val seprateAppointmentString =
                                appointmentDateString.split(" ".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            val appointmentDate = seprateAppointmentString[0]
                            val appointmentDateFormat = appUtilities.changeDateFormat(
                                "yyyy-MM-dd",
                                "dd MMM, yy",
                                appointmentDate
                            )
                            val appointmetTime = appUtilities.formatTimeBasedOnPreference(
                                mContext, "HH:mm:ss", seprateAppointmentString[1]
                            )
                            if (DashboardFullMode.selectedDuration.equals(
                                    "Today",
                                    ignoreCase = true
                                )
                            ) {
                                myViewHolder.patientName.text =
                                    homeApptListModel.patientApptArray.getJSONObject(
                                        isAppointmentNextBackClinicClick
                                    ).getString("patient_name")
                                myViewHolder.appointmentDate.text = "@ $appointmetTime"
                            } else {
                                myViewHolder.patientName.text =
                                    homeApptListModel.patientApptArray.getJSONObject(
                                        isAppointmentNextBackClinicClick
                                    ).getString("patient_name")
                                myViewHolder.appointmentDate.text =
                                    "On $appointmentDateFormat @ $appointmetTime"
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
            myViewHolder.rightArrowButtonLayout.setOnClickListener {
                when (homeApptListModel.serviceId) {
                    1 -> {
                        isAppointmentNextBackVideoClick++
                        try {
                            if (homeApptListModel.patientApptArray.length() > 1) {
                                if (isAppointmentNextBackVideoClick == homeApptListModel.patientApptArray.length() - 1) {
                                    myViewHolder.rightArrowButtonLayout.isClickable = false
                                    myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorGrey1),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                    myViewHolder.leftArrowButtonLayout.isClickable = true
                                    myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                } else {
                                    myViewHolder.leftArrowButtonLayout.isClickable = true
                                    myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                    myViewHolder.rightArrowButtonLayout.isClickable = true
                                    myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                }
                                val appointmentDateString =
                                    homeApptListModel.patientApptArray.getJSONObject(
                                        isAppointmentNextBackVideoClick
                                    ).getString("appt_time")
                                val seprateAppointmentString =
                                    appointmentDateString.split(" ".toRegex())
                                        .dropLastWhile { it.isEmpty() }
                                        .toTypedArray()
                                val appointmentDate = seprateAppointmentString[0]
                                val appointmentDateFormat = appUtilities.changeDateFormat(
                                    "yyyy-MM-dd",
                                    "dd MMM, yy",
                                    appointmentDate
                                )
                                val appointmetTime = appUtilities.formatTimeBasedOnPreference(
                                    mContext, "HH:mm:ss", seprateAppointmentString[1]
                                )
                                if (DashboardFullMode.selectedDuration.equals(
                                        "Today",
                                        ignoreCase = true
                                    )
                                ) {
                                    myViewHolder.patientName.text =
                                        homeApptListModel.patientApptArray.getJSONObject(
                                            isAppointmentNextBackVideoClick
                                        ).getString("patient_name")
                                    myViewHolder.appointmentDate.text = "@ $appointmetTime"
                                } else {
                                    myViewHolder.patientName.text =
                                        homeApptListModel.patientApptArray.getJSONObject(
                                            isAppointmentNextBackVideoClick
                                        ).getString("patient_name")
                                    myViewHolder.appointmentDate.text =
                                        "On $appointmentDateFormat @ $appointmetTime"
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    2 -> {
                        isAppointmentNextBackChatClick++
                        try {
                            if (homeApptListModel.patientApptArray.length() > 1) {
                                if (isAppointmentNextBackChatClick == homeApptListModel.patientApptArray.length() - 1) {
                                    myViewHolder.rightArrowButtonLayout.isClickable = false
                                    myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorGrey1),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                    myViewHolder.leftArrowButtonLayout.isClickable = true
                                    myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                } else {
                                    myViewHolder.leftArrowButtonLayout.isClickable = true
                                    myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                    myViewHolder.rightArrowButtonLayout.isClickable = true
                                    myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                }
                                val appointmentDateString =
                                    homeApptListModel.patientApptArray.getJSONObject(
                                        isAppointmentNextBackChatClick
                                    ).getJSONObject("chat_session").getString("expiry_time")
                                val seprateAppointmentString =
                                    appointmentDateString.split(" ".toRegex())
                                        .dropLastWhile { it.isEmpty() }
                                        .toTypedArray()
                                val appointmentDate = seprateAppointmentString[0]
                                val appointmentDateFormat = appUtilities.changeDateFormat(
                                    "yyyy-MM-dd",
                                    "dd MMM, yy",
                                    appointmentDate
                                )
                                myViewHolder.patientName.text =
                                    homeApptListModel.patientApptArray.getJSONObject(
                                        isAppointmentNextBackChatClick
                                    ).getJSONObject("chat_user").getString("fname")
                                val str =
                                    "<a><font color='#000000'> Valid Till: </font> $appointmentDateFormat </a>"
                                myViewHolder.appointmentDate.text = Html.fromHtml(str)

                            }
                        } catch (e: JSONException) {
                            e.stackTrace
                        }
                    }
                    3 -> {
                        isAppointmentNextBackClinicClick++
                        try {
                            if (homeApptListModel.patientApptArray.length() > 1) {
                                if (isAppointmentNextBackClinicClick == homeApptListModel.patientApptArray.length() - 1) {
                                    myViewHolder.rightArrowButtonLayout.isClickable = false
                                    myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorGrey1),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                    myViewHolder.leftArrowButtonLayout.isClickable = true
                                    myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                } else {
                                    myViewHolder.leftArrowButtonLayout.isClickable = true
                                    myViewHolder.leftArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                    myViewHolder.rightArrowButtonLayout.isClickable = true
                                    myViewHolder.rightArrowButtonLayout.background.setColorFilter(
                                        ContextCompat.getColor(mContext, R.color.colorAccent),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                }
                                val appointmentDateString =
                                    homeApptListModel.patientApptArray.getJSONObject(
                                        isAppointmentNextBackClinicClick
                                    ).getString("appt_time")
                                val seprateAppointmentString =
                                    appointmentDateString.split(" ".toRegex())
                                        .dropLastWhile { it.isEmpty() }
                                        .toTypedArray()
                                val appointmentDate = seprateAppointmentString[0]
                                val appointmentDateFormat = appUtilities.changeDateFormat(
                                    "yyyy-MM-dd",
                                    "dd MMM, yy",
                                    appointmentDate
                                )
                                val appointmetTime = appUtilities.formatTimeBasedOnPreference(
                                    mContext, "HH:mm:ss", seprateAppointmentString[1]
                                )
                                if (DashboardFullMode.selectedDuration.equals(
                                        "Today",
                                        ignoreCase = true
                                    )
                                ) {
                                    myViewHolder.patientName.text =
                                        homeApptListModel.patientApptArray.getJSONObject(
                                            isAppointmentNextBackClinicClick
                                        ).getString("patient_name")
                                    myViewHolder.appointmentDate.text = "@ $appointmetTime"
                                } else {
                                    myViewHolder.patientName.text =
                                        homeApptListModel.patientApptArray.getJSONObject(
                                            isAppointmentNextBackClinicClick
                                        ).getString("patient_name")
                                    myViewHolder.appointmentDate.text =
                                        "On $appointmentDateFormat @ $appointmetTime"
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            myViewHolder.switchClinicTextBooked.setOnClickListener { v ->
                dashBoardApptClickLitner.onItemClick(
                    v,
                    0,
                    null,
                    "SWITCH_CLINIC",
                    homeApptListModel.clinicName,
                    homeApptListModel.productId,
                    homeApptListModel.serviceId,
                    isAppointmentNextBackVideoClick,
                    isAppointmentNextBackChatClick,
                    isAppointmentNextBackClinicClick,
                    homeApptListModel.productId
                )
            }
            myViewHolder.switchClinicText.setOnClickListener { v ->
                dashBoardApptClickLitner.onItemClick(
                    v,
                    0,
                    null,
                    "SWITCH_CLINIC",
                    homeApptListModel.clinicName,
                    homeApptListModel.productId,
                    homeApptListModel.serviceId,
                    isAppointmentNextBackVideoClick,
                    isAppointmentNextBackChatClick,
                    isAppointmentNextBackClinicClick,
                    homeApptListModel.productId
                )
            }
            myViewHolder.bulkCancel.setOnClickListener { v ->
                dashBoardApptClickLitner.onItemClick(
                    v,
                    0,
                    homeApptListModel.appointmentPendingId,
                    "BULK_CANCEL",
                    homeApptListModel.clinicName,
                    homeApptListModel.productId,
                    homeApptListModel.serviceId,
                    isAppointmentNextBackVideoClick,
                    isAppointmentNextBackChatClick,
                    isAppointmentNextBackClinicClick,
                    homeApptListModel.productId
                )
            }
            myViewHolder.delayIntimation.setOnClickListener { v ->
                dashBoardApptClickLitner.onItemClick(
                    v,
                    0,
                    homeApptListModel.appointmentPendingId,
                    "LATE",
                    homeApptListModel.clinicName,
                    homeApptListModel.productId,
                    homeApptListModel.serviceId,
                    isAppointmentNextBackVideoClick,
                    isAppointmentNextBackChatClick,
                    isAppointmentNextBackClinicClick,
                    homeApptListModel.productId
                )
            }
            myViewHolder.bulkComplete.setOnClickListener { v ->
                dashBoardApptClickLitner.onItemClick(
                    v,
                    0,
                    homeApptListModel.appointmentPendingId,
                    "BULK_COMPLETE",
                    homeApptListModel.clinicName,
                    homeApptListModel.productId,
                    homeApptListModel.serviceId,
                    isAppointmentNextBackVideoClick,
                    isAppointmentNextBackChatClick,
                    isAppointmentNextBackClinicClick,
                    homeApptListModel.productId
                )
            }
            myViewHolder.bulkCompleteVideo.setOnClickListener { v ->
                dashBoardApptClickLitner.onItemClick(
                    v,
                    0,
                    homeApptListModel.appointmentPendingId,
                    "BULK_COMPLETE",
                    homeApptListModel.clinicName,
                    homeApptListModel.productId,
                    homeApptListModel.serviceId,
                    isAppointmentNextBackVideoClick,
                    isAppointmentNextBackChatClick,
                    isAppointmentNextBackClinicClick,
                    homeApptListModel.productId
                )
            }
            myViewHolder.dashBoardBookAppointment.setOnClickListener { v ->
                dashBoardApptClickLitner.onItemClick(
                    v,
                    0,
                    homeApptListModel.appointmentPendingId,
                    "BOOK_APPOINTMENT",
                    homeApptListModel.clinicName,
                    homeApptListModel.productId,
                    homeApptListModel.serviceId,
                    isAppointmentNextBackVideoClick,
                    isAppointmentNextBackChatClick,
                    isAppointmentNextBackClinicClick,
                    homeApptListModel.productId
                )
            }
            myViewHolder.viewAllChat.setOnClickListener { v ->
                dashBoardApptClickLitner.onItemClick(
                    v,
                    0,
                    homeApptListModel.appointmentPendingId,
                    "VIEW_ALL_CHAT",
                    homeApptListModel.clinicName,
                    homeApptListModel.productId,
                    homeApptListModel.serviceId,
                    isAppointmentNextBackVideoClick,
                    isAppointmentNextBackChatClick,
                    isAppointmentNextBackClinicClick,
                    homeApptListModel.productId
                )
            }
            myViewHolder.bookAppointmentButtonLayout.setOnClickListener { v ->
                dashBoardApptClickLitner.onItemClick(
                    v,
                    0,
                    homeApptListModel.appointmentPendingId,
                    "BOOK_APPOINTMENT",
                    homeApptListModel.clinicName,
                    homeApptListModel.productId,
                    homeApptListModel.serviceId,
                    isAppointmentNextBackVideoClick,
                    isAppointmentNextBackChatClick,
                    isAppointmentNextBackClinicClick,
                    homeApptListModel.productId
                )
            }
            myViewHolder.patientAppointmentCardView.setOnClickListener { v ->
                dashBoardApptClickLitner.onItemClick(
                    v,
                    i,
                    homeApptListModel.patientApptArray,
                    "PATIENT_APPOINTMENT_CARD",
                    homeApptListModel.clinicName,
                    homeApptListModel.productId,
                    homeApptListModel.serviceId,
                    isAppointmentNextBackVideoClick,
                    isAppointmentNextBackChatClick,
                    isAppointmentNextBackClinicClick,
                    homeApptListModel.productId
                )
            }
        } catch (e: JSONException) {
            e.stackTrace
        }
    }

    override fun getItemCount(): Int {
        return dashBoardApptListModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var homeApptListClinicName: TextView
        var switchClinicText: TextView
        var noAppointmentText: TextView
        var todaysCount: TextView
        var todaysCountValue: TextView
        var bookNewAppointmetText: TextView
        var noAppointmentAndChatBookedLayout: RelativeLayout
        var appointmentAndChatBookedLayout: RelativeLayout
        var bookAppointmentButtonLayout: RelativeLayout
        var homeApptListClinicNameBooked: TextView
        var switchClinicTextBooked: TextView
        var todaysCountBooked: TextView
        var todaysCountValueBooked: TextView
        var leftArrowButtonLayout: RelativeLayout
        var rightArrowButtonLayout: RelativeLayout
        var patientName: TextView
        var appointmentDate: TextView
        var bulkCancel: RelativeLayout
        var delayIntimation: RelativeLayout
        var bulkComplete: RelativeLayout
        var bulkCompleteVideo: RelativeLayout
        var dashBoardBookAppointment: RelativeLayout
        var viewAllChat: RelativeLayout
        var patientAppointmentCardView: CardView

        init {
            homeApptListClinicName = itemView.findViewById(R.id.homeApptListClinicName)
            switchClinicText = itemView.findViewById(R.id.switchClinicText)
            noAppointmentText = itemView.findViewById(R.id.noAppointmentText)
            todaysCount = itemView.findViewById(R.id.todaysCount)
            bookNewAppointmetText = itemView.findViewById(R.id.bookNewAppointmetText)
            todaysCountValue = itemView.findViewById(R.id.todaysCountValue)
            bookAppointmentButtonLayout = itemView.findViewById(R.id.bookAppointmentButtonLayout)
            homeApptListClinicNameBooked = itemView.findViewById(R.id.homeApptListClinicNameBooked)
            switchClinicTextBooked = itemView.findViewById(R.id.switchClinicTextBooked)
            todaysCountBooked = itemView.findViewById(R.id.todaysCountBooked)
            todaysCountValueBooked = itemView.findViewById(R.id.todaysCountValueBooked)
            appointmentAndChatBookedLayout =
                itemView.findViewById(R.id.appointmentAndChatBookedLayout)
            noAppointmentAndChatBookedLayout =
                itemView.findViewById(R.id.noAppointmentAndChatBookedLayout)
            leftArrowButtonLayout = itemView.findViewById(R.id.leftArrowButtonLayout)
            rightArrowButtonLayout = itemView.findViewById(R.id.rightArrowButtonLayout)
            patientName = itemView.findViewById(R.id.patientName)
            appointmentDate = itemView.findViewById(R.id.appointmentDate)
            bulkCancel = itemView.findViewById(R.id.bulkCancel)
            delayIntimation = itemView.findViewById(R.id.delayIntimation)
            bulkComplete = itemView.findViewById(R.id.bulkComplete)
            bulkCompleteVideo = itemView.findViewById(R.id.bulkCompleteVideo)
            dashBoardBookAppointment = itemView.findViewById(R.id.dashBoardBookAppointment)
            viewAllChat = itemView.findViewById(R.id.viewAllChat)
            patientAppointmentCardView = itemView.findViewById(R.id.patientAppointmentCardView)
        }
    }

    companion object {
        var isAppointmentNextBackClinicClick = 0
        var isAppointmentNextBackVideoClick = 0
        var isAppointmentNextBackChatClick = 0
    }
}