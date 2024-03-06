package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.PatientRecordListener
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.fragments.PaymentOverviewFragment.Companion.newInstance
import com.whitecoats.clinicplus.models.PaymentTotalOverviewModel

class PaymentTotalOverviewListAdapter(
    private val activity: Activity,
    private val paymentTotalOverviewModel: List<PaymentTotalOverviewModel>,
    private val patientRecordListener: PatientRecordListener
) : RecyclerView.Adapter<PaymentTotalOverviewListAdapter.MyViewHolder>() {
    private val appUtilities: AppUtilities

    init {
        appUtilities = AppUtilities()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_payment_overview, viewGroup, false)
        Log.d("position=", "position=$i")
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val paymenttotalOverviewModel = paymentTotalOverviewModel[i]
        Log.d("positionOnBind=", "positionOnbind=$i")
        myViewHolder.leftRupeeText.text = paymenttotalOverviewModel.collectedPayment
        myViewHolder.rightRupeeText.text = paymenttotalOverviewModel.awaitingPayment
        if (i == 0) {
            val appointmentActivity = newInstance()
            appointmentActivity.swipingSelectedUnselected(0)
        } else if (i == 1) {
            val appointmentActivity = newInstance()
            appointmentActivity.swipingSelectedUnselected(0)
        } else if (i == 2) {
            val appointmentActivity = newInstance()
            appointmentActivity.swipingSelectedUnselected(0)
        }

//        myViewHolder.terValue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (myViewHolder.terValue.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.view))) {
//                    patientRecordListener.onItemClick(view, recordsModel.getFileUrl(), "FILE_URL", "");
//                }
//            }
//        });
//
    }

    override fun getItemCount(): Int {
        return paymentTotalOverviewModel.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftRupeeText: TextView
        val rightRupeeText: TextView
        private val sharedRecordCardView: CardView? = null

        init {
            leftRupeeText = itemView.findViewById(R.id.leftRupeeText)
            rightRupeeText = itemView.findViewById(R.id.rightRupeeText)
            //
//            viewRecord = itemView.findViewById(R.id.homeSharedRecordsView);
//            priLabel = itemView.findViewById(R.id.homeSharedRecordsPriLabel);
//            secLabel = itemView.findViewById(R.id.homeSharedRecordsSecLabel);
//            terLabel = itemView.findViewById(R.id.homeSharedRecordsTerLabel);
//            priValue = itemView.findViewById(R.id.homeSharedRecordsPriValue);
//            secValue = itemView.findViewById(R.id.homeSharedRecordsSecValue);
//            terValue = itemView.findViewById(R.id.homeSharedRecordsTerValue);
//            sharedRecordCardView = itemView.findViewById(R.id.sharedRecordCardView);
//            createdDateText = itemView.findViewById(R.id.createdDateText);
        }
    }

    companion object {
        var shareRecordClickFlag = 0
    }
}