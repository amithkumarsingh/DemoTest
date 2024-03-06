package com.whitecoats.clinicplus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.whitecoats.clinicplus.R
import com.whitecoats.model.IntentServiceResult
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

class TotalPaymentAndApptCollectionFragment(totalOverviewObject: JSONObject?) : Fragment() {
    var totalPaymentCollected = 0.0
    var totalAwaitingAppointments = 0.0
    private lateinit var leftRupeeText: TextView
    private lateinit var rightRupeeText: TextView
    private lateinit var total_collect_lay: RelativeLayout
    private lateinit var offline_collect_lay: RelativeLayout

    init {
        try {
            totalOverviewObjectData = totalOverviewObject
            totalPaymentCollected = totalOverviewObjectData!!.getDouble("total_payment_collection")
            totalAwaitingAppointments =
                totalOverviewObjectData!!.getDouble("total_offline_collection")
        } catch (e: JSONException) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.list_row_payment_overview, container, false)
        leftRupeeText = view.findViewById(R.id.leftRupeeText)
        rightRupeeText = view.findViewById(R.id.rightRupeeText)
        total_collect_lay = view.findViewById(R.id.total_collect_lay)
        offline_collect_lay = view.findViewById(R.id.offline_collect_lay)
        leftRupeeText.text = "" + totalPaymentCollected
        rightRupeeText.text = "" + totalAwaitingAppointments
        total_collect_lay.setOnClickListener(View.OnClickListener { view1: View? ->
            EventBus.getDefault()
                .post(IntentServiceResult("TotalPayments", totalOverviewObjectData))
        })
        offline_collect_lay.setOnClickListener(View.OnClickListener { view12: View? ->
            EventBus.getDefault()
                .post(IntentServiceResult("TotalOfflinePayments", totalOverviewObjectData))
        })
        return view
    }

    companion object {
        var totalOverviewObjectData: JSONObject? = null
        fun newInstance(): TotalPaymentAndApptCollectionFragment {
            val fragment = TotalPaymentAndApptCollectionFragment(totalOverviewObjectData)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}