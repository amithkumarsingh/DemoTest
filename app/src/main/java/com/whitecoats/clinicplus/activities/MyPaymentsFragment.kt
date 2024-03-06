package com.whitecoats.clinicplus.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.fragments.PaymentOverviewFragment
import com.whitecoats.clinicplus.fragments.TransactionsFragment
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.model.IntentServiceResult
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MyPaymentsFragment : AppCompatActivity() {
    lateinit var back_button: ImageButton
    lateinit var settings_button: ImageButton
    lateinit var overviewTab: RelativeLayout
    lateinit var transactionTab: RelativeLayout
    var selectedFragment: Fragment? = null
    private lateinit var overViewTabView: View
    private lateinit var transactionsTabView: View
    lateinit var overviewText: TextView
    lateinit var transactionsText: TextView
    private var modePaymentAllFilter = ""
    private var status = ""
    private var pendingSettlementFilter = 0
    private var settlementDoneFilter = 0
    private var pendingRefundFilter = 0
    private var refundCompletedFilter = 0
    private var partialSettlementDoneFilter = 0
    private var partialSettlePendingFilter = 0
    private var scheduledRefundFilter = 0
    private var paymentCompletedFilter = 0
    private var filterCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_payments)
        initialize()
    }

    fun initialize() {
        back_button = findViewById(R.id.toolbarPayments_back)
        settings_button = findViewById(R.id.toolbarSettingsIcon)
        overviewTab = findViewById(R.id.paymentsOverviewTab)
        transactionTab = findViewById(R.id.paymentsTransactionsTab)
        overViewTabView = findViewById(R.id.overViewTab)
        transactionsTabView = findViewById(R.id.transactionsTab)
        overviewText = findViewById(R.id.paymentsOverviewText)
        transactionsText = findViewById(R.id.paymentsTransactionsText)
        selectedFragment = PaymentOverviewFragment.newInstance()
        if (selectedFragment != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout_payments, selectedFragment!!)
            fragmentTransaction.commit()
        }
        back_button.setOnClickListener { finish() }
        settings_button.setOnClickListener {
            val settingsIntent = Intent(applicationContext, PaymentSettingsActivity::class.java)
            startActivity(settingsIntent)
        }
        overviewTab.setOnClickListener { v: View? ->
            selectedFragment = PaymentOverviewFragment.newInstance()
            overViewTabView.visibility = View.VISIBLE
            transactionsTabView.visibility = View.GONE
            overviewText.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
            transactionsText.setTextColor(ContextCompat.getColor(this,R.color.colorGreyText))
            if (selectedFragment != null) {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frameLayout_payments, selectedFragment!!)
                fragmentTransaction.commit()
            }
        }
        transactionTab.setOnClickListener { v: View? ->
            modePaymentAllFilter = ""
            status = ""
            pendingSettlementFilter = 0
            settlementDoneFilter = 0
            pendingRefundFilter = 0
            refundCompletedFilter = 0
            partialSettlementDoneFilter = 0
            partialSettlePendingFilter = 0
            scheduledRefundFilter = 0
            paymentCompletedFilter = 0
            filterCount = 0
            transactionTabNavigation(
                modePaymentAllFilter,
                status,
                pendingSettlementFilter,
                settlementDoneFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                partialSettlementDoneFilter,
                partialSettlePendingFilter,
                scheduledRefundFilter,
                paymentCompletedFilter,
                filterCount
            )
        }
    }

    override fun onDestroy() {
        SharedPref.getPrefsHelper().delete("FilterPaymentOverView")
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(intentServiceResult: IntentServiceResult) {
        if (intentServiceResult.type.equals("TotalPayments", ignoreCase = true)) {
            modePaymentAllFilter = ""
            status = ""
            pendingSettlementFilter = 0
            settlementDoneFilter = 0
            pendingRefundFilter = 0
            refundCompletedFilter = 0
            partialSettlementDoneFilter = 0
            partialSettlePendingFilter = 0
            scheduledRefundFilter = 0
            paymentCompletedFilter = 1
            filterCount = 6
            transactionTabNavigation(
                modePaymentAllFilter,
                status,
                pendingSettlementFilter,
                settlementDoneFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                partialSettlementDoneFilter,
                partialSettlePendingFilter,
                scheduledRefundFilter,
                paymentCompletedFilter,
                filterCount
            )
        } else if (intentServiceResult.type.equals("TotalOfflinePayments", ignoreCase = true)) {
            modePaymentAllFilter = "Offline"
            status = ""
            pendingSettlementFilter = 0
            settlementDoneFilter = 0
            pendingRefundFilter = 0
            refundCompletedFilter = 0
            partialSettlementDoneFilter = 0
            partialSettlePendingFilter = 0
            scheduledRefundFilter = 0
            paymentCompletedFilter = 1
            filterCount = 6
            transactionTabNavigation(
                modePaymentAllFilter,
                status,
                pendingSettlementFilter,
                settlementDoneFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                partialSettlementDoneFilter,
                partialSettlePendingFilter,
                scheduledRefundFilter,
                paymentCompletedFilter,
                filterCount
            )
        } else if (intentServiceResult.type.equals("TotalOnlinePayments", ignoreCase = true)) {
            modePaymentAllFilter = "Online"
            status = ""
            pendingSettlementFilter = 0
            settlementDoneFilter = 0
            pendingRefundFilter = 0
            refundCompletedFilter = 0
            partialSettlementDoneFilter = 0
            partialSettlePendingFilter = 0
            scheduledRefundFilter = 0
            paymentCompletedFilter = 1
            filterCount = 6
            transactionTabNavigation(
                modePaymentAllFilter,
                status,
                pendingSettlementFilter,
                settlementDoneFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                partialSettlementDoneFilter,
                partialSettlePendingFilter,
                scheduledRefundFilter,
                paymentCompletedFilter,
                filterCount
            )
        } else if (intentServiceResult.type.equals("TotalActiveConsultation", ignoreCase = true)) {
            modePaymentAllFilter = ""
            status = "Active"
            pendingSettlementFilter = 0
            settlementDoneFilter = 0
            pendingRefundFilter = 0
            refundCompletedFilter = 0
            partialSettlementDoneFilter = 0
            partialSettlePendingFilter = 0
            scheduledRefundFilter = 0
            paymentCompletedFilter = 0
            filterCount = 5
            transactionTabNavigation(
                modePaymentAllFilter,
                status,
                pendingSettlementFilter,
                settlementDoneFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                partialSettlementDoneFilter,
                partialSettlePendingFilter,
                scheduledRefundFilter,
                paymentCompletedFilter,
                filterCount
            )
        } else if (intentServiceResult.type.equals("TotalSettlement", ignoreCase = true)) {
            modePaymentAllFilter = ""
            status = ""
            pendingSettlementFilter = 1
            settlementDoneFilter = 1
            pendingRefundFilter = 0
            refundCompletedFilter = 0
            partialSettlementDoneFilter = 0
            partialSettlePendingFilter = 0
            scheduledRefundFilter = 0
            paymentCompletedFilter = 0
            filterCount = 6
            transactionTabNavigation(
                modePaymentAllFilter,
                status,
                pendingSettlementFilter,
                settlementDoneFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                partialSettlementDoneFilter,
                partialSettlePendingFilter,
                scheduledRefundFilter,
                paymentCompletedFilter,
                filterCount
            )
        } else if (intentServiceResult.type.equals("TotalRefundCompleted", ignoreCase = true)) {
            modePaymentAllFilter = ""
            status = ""
            pendingSettlementFilter = 0
            settlementDoneFilter = 0
            pendingRefundFilter = 1
            refundCompletedFilter = 1
            partialSettlementDoneFilter = 1
            partialSettlePendingFilter = 1
            scheduledRefundFilter = 1
            paymentCompletedFilter = 0
            filterCount = 10
            transactionTabNavigation(
                modePaymentAllFilter,
                status,
                pendingSettlementFilter,
                settlementDoneFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                partialSettlementDoneFilter,
                partialSettlePendingFilter,
                scheduledRefundFilter,
                paymentCompletedFilter,
                filterCount
            )
        } else if (intentServiceResult.type.equals(
                "ViewTransactionActiveConsult",
                ignoreCase = true
            )
        ) {
            modePaymentAllFilter = "Online"
            status = "Active"
            pendingSettlementFilter = 0
            settlementDoneFilter = 0
            pendingRefundFilter = 0
            refundCompletedFilter = 0
            partialSettlementDoneFilter = 0
            partialSettlePendingFilter = 0
            scheduledRefundFilter = 0
            paymentCompletedFilter = 0
            filterCount = 5
            transactionTabNavigation(
                modePaymentAllFilter,
                status,
                pendingSettlementFilter,
                settlementDoneFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                partialSettlementDoneFilter,
                partialSettlePendingFilter,
                scheduledRefundFilter,
                paymentCompletedFilter,
                filterCount
            )
        } else if (intentServiceResult.type.equals(
                "ViewTransactionOverviewSettlement",
                ignoreCase = true
            )
        ) {
            modePaymentAllFilter = ""
            status = ""
            pendingSettlementFilter = 1
            settlementDoneFilter = 1
            pendingRefundFilter = 0
            refundCompletedFilter = 0
            partialSettlementDoneFilter = 0
            partialSettlePendingFilter = 0
            scheduledRefundFilter = 0
            paymentCompletedFilter = 0
            filterCount = 7
            transactionTabNavigation(
                modePaymentAllFilter,
                status,
                pendingSettlementFilter,
                settlementDoneFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                partialSettlementDoneFilter,
                partialSettlePendingFilter,
                scheduledRefundFilter,
                paymentCompletedFilter,
                filterCount
            )
        } else if (intentServiceResult.type.equals(
                "ViewTransactionRefundOverview",
                ignoreCase = true
            )
        ) {
            modePaymentAllFilter = ""
            status = ""
            pendingSettlementFilter = 0
            settlementDoneFilter = 0
            pendingRefundFilter = 1
            refundCompletedFilter = 1
            partialSettlementDoneFilter = 1
            partialSettlePendingFilter = 1
            scheduledRefundFilter = 1
            paymentCompletedFilter = 0
            filterCount = 10
            transactionTabNavigation(
                modePaymentAllFilter,
                status,
                pendingSettlementFilter,
                settlementDoneFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                partialSettlementDoneFilter,
                partialSettlePendingFilter,
                scheduledRefundFilter,
                paymentCompletedFilter,
                filterCount
            )
        }
    }

    private fun transactionTabNavigation(
        modePaymentAllFilter: String,
        status: String,
        pendingSettlementFilter: Int,
        settlementDoneFilter: Int,
        pendingRefundFilter: Int,
        refundCompletedFilter: Int,
        partialSettlementDoneFilter: Int,
        partialSettlePendingFilter: Int,
        scheduledRefundFilter: Int,
        paymentCompletedFilter: Int,
        filterCount: Int
    ) {
        selectedFragment = TransactionsFragment.newInstance(
            modePaymentAllFilter,
            status,
            pendingSettlementFilter,
            settlementDoneFilter,
            pendingRefundFilter,
            refundCompletedFilter,
            partialSettlementDoneFilter,
            partialSettlePendingFilter,
            scheduledRefundFilter,
            paymentCompletedFilter,
            filterCount
        )
        overViewTabView.visibility = View.GONE
        transactionsTabView.visibility = View.VISIBLE
        overviewText.setTextColor(ContextCompat.getColor(this,R.color.colorGreyText))
        transactionsText.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
        if (selectedFragment != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout_payments, selectedFragment!!)
            fragmentTransaction.commit()
        }
    }
}