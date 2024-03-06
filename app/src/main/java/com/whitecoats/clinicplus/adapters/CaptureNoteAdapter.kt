package com.whitecoats.clinicplus.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.NewAppointmentFragment.Companion.activePastFilter
import com.whitecoats.clinicplus.interfaces.CaptureNotesClickListner
import com.whitecoats.clinicplus.models.CaptureNotesModel
import org.json.JSONArray
import org.json.JSONException

class CaptureNoteAdapter(
    private val mCtx: Context,
    private var myList: ArrayList<CaptureNotesModel>?,
    private val mListner: CaptureNotesClickListner
) : RecyclerView.Adapter<CaptureNoteAdapter.RecyclerItemViewHolder>() {
    var mLastPosition = 0

    init {
        lastEditNoteList = ArrayList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_row_capture_notes, parent, false)
        return RecyclerItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        Log.d("onBindViewHoler ", myList!!.size.toString() + "")
        holder.notesText.text = myList!![position].notesText
        if (activePastFilter.equals("past", ignoreCase = true)) {
            holder.editImageLayout.visibility = View.GONE
            holder.bulletPointImageView.visibility = View.VISIBLE
            holder.notesTextCheckBox.visibility = View.GONE
        } else {
            holder.editImageLayout.visibility = View.VISIBLE
            holder.bulletPointImageView.visibility = View.GONE
            holder.notesTextCheckBox.visibility = View.VISIBLE
        }
        mLastPosition = position
        holder.editImageLayout.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, mCtx.getString(
                    R.string.CaptureNotesScreenEditNotes
                ), null
            )
            isEditClick = true
            mListner.OnCaptureNoteClick(
                myList!![position].captureNoteId,
                myList!![position].notesText
            )
        }
        holder.notesTextCheckBox.tag = position
        holder.notesTextCheckBox.setOnClickListener {
            val pos = holder.notesTextCheckBox.tag as Int
            //                Toast.makeText(mCtx, selectedNotedIdArray+" SelectedPosition", Toast.LENGTH_SHORT).show();
            try {
                if (myList!![pos].isSelected) {
                    myList!![pos].isSelected = false
                    for (i in 0 until selectedNotedIdArray.length()) {
                        if (selectedNotedIdArray[i] == myList!![pos].captureNoteId) {
                            selectedNotedIdArray.remove(i)
                        }
                    }
                    //                        Toast.makeText(mCtx, selectedNotedIdArray.toString() + " SelectedPosition", Toast.LENGTH_SHORT).show();
                } else {
                    myList!![pos].isSelected = true
                    selectedNotedIdArray.put(myList!![pos].captureNoteId)
                    //                        Toast.makeText(mCtx, selectedNotedIdArray.toString() + " SelectedPosition", Toast.LENGTH_SHORT).show();
                }
            } catch (e: JSONException) {
            }
        }
    }

    override fun getItemCount(): Int {
        return if (null != myList) myList!!.size else 0
    }

    fun notifyData(myList: ArrayList<CaptureNotesModel>) {
        Log.d("notifyData ", myList.size.toString() + "")
        this.myList = myList
        notifyDataSetChanged()
    }

    inner class RecyclerItemViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        val notesTextCheckBox: CheckBox
        val notesText: TextView
        val editImageLayout: RelativeLayout
        val bulletPointImageView: ImageView

        init {
            notesText = parent.findViewById<View>(R.id.notesText) as TextView
            notesTextCheckBox = parent.findViewById<View>(R.id.notesTextCheckBox) as CheckBox
            editImageLayout = parent.findViewById<View>(R.id.editImageLayout) as RelativeLayout
            bulletPointImageView = parent.findViewById<View>(R.id.bulletPointImageView) as ImageView
            //            etDescriptionTextView = (TextView) parent.findViewById(R.id.txtDescription);
//            crossImage = (ImageView) parent.findViewById(R.id.crossImage);
//            mainLayout = (LinearLayout) parent.findViewById(R.id.mainLayout);
////            mainLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(itemView.getContext(), "Position:" + Integer.toString(getPosition()), Toast.LENGTH_SHORT).show();
//                }
//            });
//            crossImage.setOnClickListener(new AdapterView.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mListner.OnRemoveClick(getAdapterPosition()
//                    );
//                }
//            });
        }
    }

    companion object {
        lateinit var lastEditNoteList: ArrayList<CaptureNotesModel>
        @JvmField
        var selectedNotedIdArray = JSONArray()
        @JvmField
        var isEditClick = false
    }
}