package com.whitecoats.clinicplus.adapters

import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.fragments.NewAppointmentFragment.Companion.activePastFilter
import com.whitecoats.clinicplus.interfaces.CaptureNotesMedicationListener
import com.whitecoats.clinicplus.models.CaptureNotesMedicationModel

class CaptureNoteMedicationAdapter(
    private var myList: ArrayList<CaptureNotesMedicationModel>?,
    private val context: Context,
    private val captureNotesMedicationListener: CaptureNotesMedicationListener
) : RecyclerView.Adapter<CaptureNoteMedicationAdapter.RecyclerItemViewHolder>() {
    var mLastPosition = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_capture_notes_medications, parent, false)
        return RecyclerItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        Log.d("onBindViewHoler ", myList!!.size.toString() + "")
        holder.notesTextView.text = myList!![position].medicationName
        holder.medicationDaysValue.text = myList!![position].medicationDays.toString() + ""
        holder.medicationDaysDurationValue.text = myList!![position].medicationDuration
        val content = SpannableString("Discontinue")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        holder.discontinueText.text = content
        if (activePastFilter.equals("past", ignoreCase = true)) {
            holder.discontinueImage.visibility = View.VISIBLE
            val rotateAnimation = AnimationUtils.loadAnimation(
                context, R.anim.rotate_animation
            ) as RotateAnimation
            holder.discontinueImage.animation = rotateAnimation
            holder.discontinueButtonLayout.visibility = View.GONE
        } else {
            if (myList!![position].status.equals("Not Active", ignoreCase = true)) {
                holder.discontinueImage.visibility = View.VISIBLE
                holder.discontinueButtonLayout.visibility = View.GONE
                val rotateAnimation = AnimationUtils.loadAnimation(
                    context, R.anim.rotate_animation
                ) as RotateAnimation
                holder.discontinueImage.animation = rotateAnimation
            } else {
                holder.discontinueImage.visibility = View.GONE
                holder.discontinueButtonLayout.visibility = View.VISIBLE
            }
        }
        mLastPosition = position
        holder.discontinueButtonLayout.setOnClickListener {
            captureNotesMedicationListener.OnCaptureNoteMedicationClick(
                position,
                myList!![position]
            )
        }
    }

    override fun getItemCount(): Int {
        return if (null != myList) myList!!.size else 0
    }

    fun notifyData(myList: ArrayList<CaptureNotesMedicationModel>) {
        Log.d("notifyData ", myList.size.toString() + "")
        this.myList = myList
        notifyDataSetChanged()
    }

    inner class RecyclerItemViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        val notesTextView: TextView
        val medicationDaysValue: TextView
        val medicationDaysDurationValue: TextView
        val discontinueText: TextView
        val discontinueImage: TextView
        val discontinueButtonLayout: RelativeLayout

        init {
            notesTextView = parent.findViewById<View>(R.id.medicationNameText) as TextView
            medicationDaysValue = parent.findViewById<View>(R.id.medicationDaysValue) as TextView
            medicationDaysDurationValue =
                parent.findViewById<View>(R.id.medicationDaysDurationValue) as TextView
            discontinueText = parent.findViewById<View>(R.id.discontinueText) as TextView
            discontinueButtonLayout =
                parent.findViewById<View>(R.id.discontinueButtonLayout) as RelativeLayout
            discontinueImage = parent.findViewById(R.id.discontinue_image)

        }
    }
}