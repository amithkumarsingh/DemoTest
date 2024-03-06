package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.PatientListClickListner
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.models.DashBoardAutoFollowUpModel

class DashBoardAutoFollowUpListAdapter(
    private val activity: Activity,
    private val patientRecordsModels: List<DashBoardAutoFollowUpModel>,
    private val patientRecordListener: PatientListClickListner
) : RecyclerView.Adapter<DashBoardAutoFollowUpListAdapter.MyViewHolder>() {
    private val appUtilities: AppUtilities = AppUtilities()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_home_auto_follow_up_new, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {

        try {

            val recordsModel = patientRecordsModels[i]

//        //getting patient name from getRecordsName Method
            myViewHolder.patientMessage.text = recordsModel.patientMessage
            val timeFormat: String = if (appUtilities.timeFormatPreferences(activity) == 12) {
                "dd MMM, yy hh:mm aa"
            } else {
                "dd MMM, yy HH:mm"
            }
            val apptDateformat = appUtilities.changeDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                timeFormat,
                recordsModel.submissionDate
            )
            myViewHolder.appointmentDate.text = apptDateformat
            myViewHolder.clinicName.text = recordsModel.clinicName
            myViewHolder.patientName.text = recordsModel.patientName
            myViewHolder.followUpDate.visibility = View.VISIBLE
            val followUpDateformat = appUtilities.changeDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                timeFormat,
                recordsModel.submissionDate
            )
            myViewHolder.followUpDate.text = followUpDateformat
            if (recordsModel.isApptBooked == 1) {
                myViewHolder.bookAppointmentLayout.visibility = View.GONE //added by dileep 8_7_2020
                myViewHolder.followUpDateLabelText.visibility = View.VISIBLE
                myViewHolder.followDateValue.visibility = View.VISIBLE
                val submissionDateformat = appUtilities.changeDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    timeFormat,
                    recordsModel.followUpDate
                )
                myViewHolder.followDateValue.text = submissionDateformat
            } else {
                myViewHolder.followUpDateLabelText.visibility = View.GONE
                myViewHolder.bookAppointmentLayout.visibility =
                    View.VISIBLE //added by dileep 8_7_2020
                myViewHolder.followDateValue.visibility = View.GONE
            }
            if (recordsModel.conditionStatus == 1) {
                myViewHolder.condition.text = "Same"
                myViewHolder.condition.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorInfo
                    )
                )
                myViewHolder.patientConditionLayout.background.setColorFilter(
                    ContextCompat.getColor(
                        activity, R.color.colorInfo
                    ), PorterDuff.Mode.SRC_IN
                )
            } else if (recordsModel.conditionStatus == 2) {
                myViewHolder.condition.text = "Better"
                myViewHolder.condition.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorGreen3
                    )
                )
                myViewHolder.patientConditionLayout.background.setColorFilter(
                    ContextCompat.getColor(
                        activity, R.color.colorGreen3
                    ), PorterDuff.Mode.SRC_IN
                )
            } else if (recordsModel.conditionStatus == 3) {
                myViewHolder.condition.text = "Worse"
                myViewHolder.condition.setTextColor(activity.resources.getColor(R.color.colorDanger))
                myViewHolder.patientConditionLayout.background.setColorFilter(
                    ContextCompat.getColor(
                        activity, R.color.colorDanger
                    ), PorterDuff.Mode.SRC_IN
                )
            } else {
                myViewHolder.condition.text = "-"
                myViewHolder.condition.setTextColor(activity.resources.getColor(R.color.colorDanger))
                myViewHolder.patientConditionLayout.background.setColorFilter(
                    ContextCompat.getColor(
                        activity, R.color.colorDanger
                    ), PorterDuff.Mode.SRC_IN
                )
            }
            if (recordsModel.followInstructionStatus == 1) {
                myViewHolder.followInstruction.text = "Completely"
                myViewHolder.followInstruction.setTextColor(activity.resources.getColor(R.color.colorGreen3))
                myViewHolder.patientInstructionLayout.background.setColorFilter(
                    ContextCompat.getColor(
                        activity, R.color.colorGreen3
                    ), PorterDuff.Mode.SRC_IN
                )
            } else if (recordsModel.followInstructionStatus == 2) {
                myViewHolder.followInstruction.text = "Partially"
                myViewHolder.followInstruction.setTextColor(activity.resources.getColor(R.color.colorInfo))
                myViewHolder.patientInstructionLayout.background.setColorFilter(
                    activity.resources.getColor(
                        R.color.colorInfo
                    ), PorterDuff.Mode.SRC_IN
                )
            } else if (recordsModel.followInstructionStatus == 3) {
                myViewHolder.followInstruction.text = "No"
                myViewHolder.followInstruction.setTextColor(activity.resources.getColor(R.color.colorDanger))
                myViewHolder.patientInstructionLayout.background.setColorFilter(
                    ContextCompat.getColor(
                        activity, R.color.colorDanger
                    ), PorterDuff.Mode.SRC_IN
                )
            } else {
                myViewHolder.followInstruction.text = "-"
                myViewHolder.followInstruction.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorGreen3
                    )
                )
                myViewHolder.patientInstructionLayout.background.setColorFilter(
                    ContextCompat.getColor(
                        activity, R.color.colorGreen3
                    ), PorterDuff.Mode.SRC_IN
                )
            }
            if (recordsModel.fileUrl.toString().equals("null", ignoreCase = true)) {
                myViewHolder.attachmentIcon.visibility = View.GONE
                myViewHolder.report.text = "NA"
                myViewHolder.report.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorGreyText
                    )
                )
            } else {
                myViewHolder.attachmentIcon.visibility = View.VISIBLE
                myViewHolder.report.text = "View"
                myViewHolder.report.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorAccent
                    )
                )
            }
            if (recordsModel.patientMessage.toString().equals("null", ignoreCase = true)) {
                myViewHolder.patientMessage.text = "No Message"
                myViewHolder.patientMessage.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorGreyText
                    )
                )
                myViewHolder.patientInfoIcon.visibility = View.GONE
            } else {
                myViewHolder.patientMessage.text = recordsModel.patientMessage
                myViewHolder.patientMessage.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorBlack
                    )
                )
                myViewHolder.patientInfoIcon.visibility = View.VISIBLE
            }
            myViewHolder.report.setOnClickListener { view ->
                if (myViewHolder.report.text.toString().equals("View", ignoreCase = true)) {
                    patientRecordListener.onItemClick(
                        view,
                        "FILE_URL",
                        recordsModel.fileUrl,
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
            myViewHolder.clinicAddress.setOnClickListener {
                val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {}
                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }
                val builder = AlertDialog.Builder(
                    activity
                )
                builder.setMessage("Address: " + recordsModel.clinicAddress)
                    .setPositiveButton("Cancel", dialogClickListener)
                    .show()
            }
            myViewHolder.bookAppointmentLayout.setOnClickListener { view ->
                // new added on 7-8-2020
                patientRecordListener.onItemClick(
                    view,
                    "BOOK_APPT",
                    recordsModel.clinicName + "_" + recordsModel.followUpId,
                    0,
                    recordsModel.productServiceId,
                    0,
                    0,
                    recordsModel.patientId,
                    recordsModel.patientName,
                    null,
                    null
                )
            }
            myViewHolder.patientMsgMore.setOnClickListener {
                androidx.appcompat.app.AlertDialog.Builder(activity)
                    .setTitle(activity.resources.getString(R.string.patient_msg))
                    .setMessage(recordsModel.patientMessage)
                    .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                    .show()
            }
            myViewHolder.patientInfoIcon.setOnClickListener {
                androidx.appcompat.app.AlertDialog.Builder(activity)
                    .setTitle(activity.resources.getString(R.string.patient_msg))
                    .setMessage(recordsModel.patientMessage)
                    .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                    .show()
            }

        }
        catch (e:Exception)
        {
         e.printStackTrace().toString();
        }
    }

    override fun getItemCount(): Int {
        return patientRecordsModels.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientName: TextView
        val appointmentDate: TextView
        val clinicName: TextView
        val followUpDate: TextView
        private val submissionDate: TextView
        val followDateValue: TextView
        val condition: TextView
        val followInstruction: TextView
        val patientMessage: TextView
        private val homeFollowUpDateText: TextView
        val report: TextView
        private val apptNotes: TextView? = null
        val patientMsgMore: TextView
        val clinicAddress: ImageView
        val patientInfoIcon: ImageView
        private val apptBookButton: TextView
        val patientConditionLayout: RelativeLayout
        val patientInstructionLayout: RelativeLayout
        val bookAppointmentLayout: RelativeLayout
        val followUpDateLabelText: TextView
        val attachmentIcon: ImageView
        private val sharedRecordCardView: CardView? = null

        init {
            patientName = itemView.findViewById(R.id.homeAutoFollowUpPatientName)
            appointmentDate = itemView.findViewById(R.id.homeAutoFollowUpApptDate)
            clinicName = itemView.findViewById(R.id.homeAutoFollowUpClinicName)
            clinicAddress = itemView.findViewById(R.id.infoImageIcon)
            followUpDate = itemView.findViewById(R.id.followUpDate)
            submissionDate = itemView.findViewById(R.id.submissionDateData)
            condition = itemView.findViewById(R.id.conditionData)
            followInstruction = itemView.findViewById(R.id.followedInstructionData)
            patientMessage = itemView.findViewById(R.id.patientMessageData)
            apptBookButton = itemView.findViewById(R.id.apptBookButton)
            homeFollowUpDateText = itemView.findViewById(R.id.homeFollowUpDateText)
            report = itemView.findViewById(R.id.reportData)
            patientMsgMore = itemView.findViewById(R.id.followUpPatientMsgMore)
            patientConditionLayout = itemView.findViewById(R.id.patientConditionLayout)
            patientInstructionLayout = itemView.findViewById(R.id.patientInstructionLayout)
            patientInfoIcon = itemView.findViewById(R.id.patientInfoIcon)
            followDateValue = itemView.findViewById(R.id.followDateValue)
            bookAppointmentLayout = itemView.findViewById(R.id.bookAppointmentLayout)
            followUpDateLabelText = itemView.findViewById(R.id.followUpDateLabelText)
            attachmentIcon = itemView.findViewById(R.id.attachmentIcon)
        }
    }

    companion object {
        var shareRecordClickFlag = 0
    }
}