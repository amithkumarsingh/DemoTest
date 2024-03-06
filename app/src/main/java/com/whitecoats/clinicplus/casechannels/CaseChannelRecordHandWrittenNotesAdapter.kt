package com.whitecoats.clinicplus.casechannels

import android.app.Activity
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.model.PatientRecordsModel

class CaseChannelRecordHandWrittenNotesAdapter(
    activity: Activity,
    patientRecordsModels: List<PatientRecordsModel>?,
    caseDoctorOrganisationClickListener: CaseDoctorOrganisationClickListener
) : RecyclerView.Adapter<CaseChannelRecordHandWrittenNotesAdapter.ViewHolder>() {
    private val activity: Activity
    private val patientRecordsModels: List<PatientRecordsModel>?
    private val appUtilities: AppUtilities = AppUtilities()
    private val caseDoctorOrganisationClickListener: CaseDoctorOrganisationClickListener

    init {
        this.activity = activity
        this.patientRecordsModels = patientRecordsModels
        this.caseDoctorOrganisationClickListener = caseDoctorOrganisationClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_case_channel_record_hand_written, viewGroup, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val recordsModel = patientRecordsModels!![i]
        viewHolder.caseChannelRecordsHandWrittenCardView.visibility = View.VISIBLE
        val temp = appUtilities.changeDateFormat(
            "yyyy-MM-dd mm:HH:ss",
            "dd MMM, yy mm:HH",
            recordsModel.hnValidTill
        )
        if (temp.equals("", ignoreCase = true)) {
            viewHolder.presValidDate.text = "-"
        } else {
            viewHolder.presValidDate.text = temp
        }
        if (recordsModel.hnDesp.equals("", ignoreCase = true)) {
            viewHolder.caeChannelRecordNotesDescription.text = "-"
        } else {
            viewHolder.caeChannelRecordNotesDescription.text = recordsModel.hnDesp
        }
        viewHolder.medPresState.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_close))
        viewHolder.medPresState.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(activity,R.color.colorDanger),
            PorterDuff.Mode.SRC_IN
        )
        if (recordsModel.hnMedPres.equals("1", ignoreCase = true)) {
            viewHolder.medPresState.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_tick))
            viewHolder.medPresState.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(activity,R.color.colorSuccess),
                PorterDuff.Mode.SRC_IN
            )
        }
        viewHolder.testPresState.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_close))
        viewHolder.testPresState.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(activity,R.color.colorDanger),
            PorterDuff.Mode.SRC_IN
        )
        if (recordsModel.hnMedPres.equals("1", ignoreCase = true)) {
            viewHolder.testPresState.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_tick))
            viewHolder.testPresState.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(activity,R.color.colorSuccess),
                PorterDuff.Mode.SRC_IN
            )
        }
        viewHolder.caseChannelRecordHandWrittenDoctorName.text = recordsModel.createdUser
        val createDate = appUtilities.changeDateFormat(
            "yyyy-MM-dd mm:HH:ss",
            "dd MMM, yy mm:HH",
            recordsModel.created_At
        )
        if (createDate.equals("", ignoreCase = true)) {
            viewHolder.presValidDate.text = "-"
        } else {
            viewHolder.caseChannelRecordHandWrittenDate.text = createDate
            viewHolder.caseChannelRecordCreateOn.text = createDate
        }
        viewHolder.viewNotes.setOnClickListener { view ->
            caseDoctorOrganisationClickListener.onItemClick(
                view,
                i,
                "",
                recordsModel.hnAttachURL
            )
        }
    }

    override fun getItemCount(): Int {
        return patientRecordsModels?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //written notes
        val viewNotes: TextView
        val presValidDate: TextView
        val caeChannelRecordNotesDescription: TextView
        val caseChannelRecordCreateOn: TextView
        val caseChannelRecordHandWrittenDoctorName: TextView
        val caseChannelRecordHandWrittenDate: TextView
        val medPresState: ImageView
        val testPresState: ImageView
        var caseChannelRecordsHandWrittenCardView: CardView

        init {
            caseChannelRecordsHandWrittenCardView =
                itemView.findViewById(R.id.caseChannelRecordsHandWrittenCardView)
            medPresState = itemView.findViewById(R.id.caseChannelRecordNotesMedPresIcon)
            testPresState = itemView.findViewById(R.id.caseChannelRecordNotesTestPresIcon)
            viewNotes = itemView.findViewById(R.id.caseChannelRecordView)
            presValidDate = itemView.findViewById(R.id.caseChannelRecordNotesPresValid)
            caeChannelRecordNotesDescription =
                itemView.findViewById(R.id.caeChannelRecordNotesDescription)
            caseChannelRecordCreateOn = itemView.findViewById(R.id.caseChannelRecordCreateOn)
            caseChannelRecordHandWrittenDoctorName =
                itemView.findViewById(R.id.caseChannelRecordHandWrittenDoctorName)
            caseChannelRecordHandWrittenDate =
                itemView.findViewById(R.id.caseChannelRecordHandWrittenDate)
        }
    }
}