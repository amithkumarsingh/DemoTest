package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.DashBoardTrainingListClickListener
import com.whitecoats.clinicplus.models.TrainingScheduleModel

class DashBoardTrainingScheduleListAdapter(
    private val activity: Activity,
    private val trainingScheduleModels: List<TrainingScheduleModel>,
    private val trainingClickListener: DashBoardTrainingListClickListener
) : RecyclerView.Adapter<DashBoardTrainingScheduleListAdapter.MyViewHolder>() {
    private val appUtilities: AppUtilities = AppUtilities()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_dashboard_training, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val trainingModel = trainingScheduleModels[i]
        myViewHolder.topicText.text = trainingModel.topic
        val currentStringStart = trainingModel.topicDate
        val separatedStart = currentStringStart.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val date = separatedStart[0]
        val convertDate = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", date)
        val time = separatedStart[1].substring(0, separatedStart[1].length - 3)
        myViewHolder.trainingTopicDate.text = "$convertDate @ $time"
        myViewHolder.bookTextView.setOnClickListener(View.OnClickListener { v ->
            trainingClickListener.onItemClick(
                v,
                i,
                "CANCEL_TRAINING",
                trainingModel
            )
        })
        myViewHolder.trainingDescriptionInfoImage.setOnClickListener { v ->
            trainingClickListener.onItemClick(
                v,
                i,
                "DESCRIPTION_MORE_INFO",
                trainingModel
            )
        }
    }

    override fun getItemCount(): Int {
//        return trainingScheduleModels.size();
        return 1
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trainingTopicDate: TextView
        val topicText: TextView
        val bookTextView: TextView
        private val cancelLayout: RelativeLayout
        val trainingDescriptionInfoImage: ImageView

        init {
            topicText = itemView.findViewById(R.id.topicText)
            trainingTopicDate = itemView.findViewById(R.id.trainingTopicDate)
            cancelLayout = itemView.findViewById(R.id.cancelLayout)
            trainingDescriptionInfoImage = itemView.findViewById(R.id.trainingDescriptionInfoImage)
            bookTextView = itemView.findViewById(R.id.bookTextView)
        }
    }

    companion object {
        var shareRecordClickFlag = 0
    }
}