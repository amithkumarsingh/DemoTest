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

class OnlinePaymentRefundFragment(totalOverviewObject: JSONObject?) : Fragment() {
    var totalPaymentCollected = 0.0
    var totalAwaitingAppointments = 0.0
    private var leftRupeeText: TextView? = null
    private val rightRupeeText: TextView? = null
    private var total_refund_completed: RelativeLayout? = null

    init {
        try {
            totalOverviewObjectData = totalOverviewObject
            totalPaymentCollected = totalOverviewObjectData!!.getDouble("total_refund_completed")
            //            totalAwaitingAppointments = totalOverviewObjectData.getInt("total_active_appts");
        } catch (e: JSONException) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.list_row_online_payment_refund_overview, container, false)

//        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        leftRupeeText = view.findViewById(R.id.leftSettlementRupeeText)
        total_refund_completed = view.findViewById(R.id.total_refund_completed)
        //        rightRupeeText = view.findViewById(R.id.rightRupeeText);
        leftRupeeText?.setText("" + totalPaymentCollected)
        total_refund_completed?.setOnClickListener(View.OnClickListener {
            EventBus.getDefault()
                .post(IntentServiceResult("TotalRefundCompleted", totalOverviewObjectData))
        })
        //        rightRupeeText.setText(""+totalAwaitingAppointments);
//
//        transactionsListAdapter = new TransactionsListAdapter(getActivity());
//        transactionsRecycleView.setAdapter(transactionsListAdapter);
        return view
    }

    companion object {
        //    private TransactionsListAdapter transactionsListAdapter;
        //    private RecyclerView transactionsRecycleView;
        //    private RecyclerView.LayoutManager mLayoutManager;
        var totalOverviewObjectData: JSONObject? = null
        fun newInstance(): OnlinePaymentRefundFragment {
            val fragment = OnlinePaymentRefundFragment(totalOverviewObjectData)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}