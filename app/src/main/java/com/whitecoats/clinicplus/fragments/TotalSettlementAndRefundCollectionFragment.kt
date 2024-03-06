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

class TotalSettlementAndRefundCollectionFragment(totalOverviewObject: JSONObject?) : Fragment() {
    private lateinit var leftSettlementRupeeText: TextView
    var totalSettlementCollected = 0.0
    private lateinit var online_collect_lay: RelativeLayout

    init {
        try {
            totalOverviewObjectData = totalOverviewObject
            totalSettlementCollected =
                totalOverviewObjectData!!.getDouble("total_online_collection")
        } catch (e: JSONException) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.list_row_payment_settlement_overview, container, false)
        leftSettlementRupeeText = view.findViewById(R.id.leftSettlementRupeeText)
        online_collect_lay = view.findViewById(R.id.online_collect_lay)
        leftSettlementRupeeText.text = "" + totalSettlementCollected
        online_collect_lay.setOnClickListener(View.OnClickListener {
            EventBus.getDefault()
                .post(IntentServiceResult("TotalOnlinePayments", totalOverviewObjectData))
        })
        return view
    }

    companion object {
        var totalOverviewObjectData: JSONObject? = null
        fun newInstance(): TotalSettlementAndRefundCollectionFragment {
            val fragment = TotalSettlementAndRefundCollectionFragment(totalOverviewObjectData)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}