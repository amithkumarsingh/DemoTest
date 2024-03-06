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

class OnlinePaymentActiveAndSettlementFragment(totalOverviewObject: JSONObject?) : Fragment() {
    var totalPaymentCollected = 0.0
    var totalAwaitingAppointments = 0.0
    private var leftRupeeText: TextView? = null
    private var rightRupeeText: TextView? = null
    private var total_active_consultation_lay: RelativeLayout? = null
    private var total_settlements_lay: RelativeLayout? = null

    init {
        try {
            totalOverviewObjectData = totalOverviewObject
            totalPaymentCollected = totalOverviewObjectData!!.getDouble("total_active_consultation")
            totalAwaitingAppointments =
                totalOverviewObjectData!!.getDouble("total_settlement_collection")
        } catch (e: JSONException) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.list_row_online_active_settlement_payment_overview,
            container,
            false
        )

        leftRupeeText = view.findViewById(R.id.leftRupeeText)
        rightRupeeText = view.findViewById(R.id.rightRupeeText)
        total_active_consultation_lay = view.findViewById(R.id.total_active_consultation_lay)
        total_settlements_lay = view.findViewById(R.id.total_settlements_lay)
        leftRupeeText?.setText("" + totalPaymentCollected)
        rightRupeeText?.setText("" + totalAwaitingAppointments)
        total_active_consultation_lay?.setOnClickListener(View.OnClickListener {
            EventBus.getDefault()
                .post(IntentServiceResult("TotalActiveConsultation", totalOverviewObjectData))
        })
        total_settlements_lay?.setOnClickListener(View.OnClickListener {
            EventBus.getDefault()
                .post(IntentServiceResult("TotalSettlement", totalOverviewObjectData))
        })

        return view
    }

    companion object {
        var totalOverviewObjectData: JSONObject? = null
        fun newInstance(): OnlinePaymentActiveAndSettlementFragment {
            val fragment = OnlinePaymentActiveAndSettlementFragment(totalOverviewObjectData)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}