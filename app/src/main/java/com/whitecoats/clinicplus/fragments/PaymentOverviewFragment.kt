package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.wang.avi.AVLoadingIndicatorView
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.ActiveConsultationPagerAdapter
import com.whitecoats.clinicplus.adapters.OnlinePaymentOverViewPagerAdapter
import com.whitecoats.clinicplus.adapters.OverViewFiltersRecyclerViewAdapter
import com.whitecoats.clinicplus.adapters.TotalOverViewPagerAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.clinicplus.viewmodels.PaymentTransactionViewModel
import com.whitecoats.model.IntentServiceResult
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class PaymentOverviewFragment : Fragment() {
    private lateinit var toSwipeOne: ImageView
    private lateinit var toSwipeTwo: ImageView
    private lateinit var onlinePaymentSwipeOne: ImageView
    private lateinit var onlinePaymentSwipeTwo: ImageView
    private lateinit var activeSwipeOne: ImageView
    private lateinit var activeSwipeTwo: ImageView
    private lateinit var activeSwipeThree: ImageView
    private lateinit var activeSwipeFour: ImageView
    private lateinit var activeSwipeFive: ImageView
    private lateinit var activeSwipeSix: ImageView
    private lateinit var activeSwipeSeven: ImageView
    private lateinit var activeSwipeEight: ImageView
    private lateinit var activeSwipeNine: ImageView
    private lateinit var _mViewPager: ViewPager
    private var _adapter: TotalOverViewPagerAdapter? = null
    private lateinit var _mViewPagerOnlinePaymentOverview: ViewPager
    private var _adapterOnlinePaymentOverview: OnlinePaymentOverViewPagerAdapter? = null
    private lateinit var _mViewPagerActiveConsultation: ViewPager
    private var _adapterActiveConsultation: ActiveConsultationPagerAdapter? = null
    private lateinit var viewInstance: View
    private var clinicData: JSONArray? = null
    private var viewPagerCount = 0
    private lateinit var pendingRupeeText: TextView
    private lateinit var successfulRupeeText: TextView
    private lateinit var refundScheduleRupeeText: TextView
    private lateinit var refundPendingRupeeText: TextView
    private lateinit var refundSuccessfulLeftRupeeText: TextView
    var paymentTransactionViewModel: PaymentTransactionViewModel? = null
    var durationClicked = false
    var durationFilter = "All"
    private var selectedFromDate = ""
    private var selectedToDate = ""
    private var sharedPref: SharedPref? = null
    private var filterValue: String? = null
    var filtersStrings = ArrayList<String>()
    private var aviInPaymentOver: AVLoadingIndicatorView? = null
    private lateinit var dialog: Dialog

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewInstance = inflater.inflate(R.layout.fragment_payment_overview, container, false)
        paymentTransactionViewModel =
            ViewModelProvider(this)[PaymentTransactionViewModel::class.java]
        paymentTransactionViewModel!!.init()
        //        totalOverviewModelList=new ArrayList<>();
        toSwipeOne = viewInstance.findViewById(R.id.toSwipeOne)
        toSwipeTwo = viewInstance.findViewById(R.id.toSwipeTwo)
        onlinePaymentSwipeOne = viewInstance.findViewById(R.id.onlinePaymentSwipeOne)
        onlinePaymentSwipeTwo = viewInstance.findViewById(R.id.onlinePaymentSwipeTwo)
        val viewTransactionActiveConsult =
            viewInstance.findViewById<RelativeLayout>(R.id.viewTransactionActiveConsult)
        val viewTransactionOverviewSettlement =
            viewInstance.findViewById<RelativeLayout>(R.id.viewTransactionOverviewSettlement)
        val viewTransactionRefundOverview =
            viewInstance.findViewById<RelativeLayout>(R.id.viewTransactionRefundOverview)
        val filters_recyclerView =
            viewInstance.findViewById<RecyclerView>(R.id.filters_recyclerView)
        if (sharedPref == null) {
            sharedPref = SharedPref(activity)
        }
        activeSwipeOne = viewInstance.findViewById(R.id.activeSwipeOne)
        activeSwipeTwo = viewInstance.findViewById(R.id.activeSwipeTwo)
        activeSwipeThree = viewInstance.findViewById(R.id.activeSwipeThree)
        activeSwipeFour = viewInstance.findViewById(R.id.activeSwipeFour)
        activeSwipeFive = viewInstance.findViewById(R.id.activeSwipeFive)
        activeSwipeSix = viewInstance.findViewById(R.id.activeSwipeSix)
        activeSwipeSeven = viewInstance.findViewById(R.id.activeSwipeSeven)
        activeSwipeEight = viewInstance.findViewById(R.id.activeSwipeEight)
        activeSwipeNine = viewInstance.findViewById(R.id.activeSwipeNine)
        val totalOverviewInfo = viewInstance.findViewById<RelativeLayout>(R.id.totalOverviewInfo)
        val settlementOverviewInfo =
            viewInstance.findViewById<RelativeLayout>(R.id.settlementOverviewInfo)
        val refundOverviewInfo = viewInstance.findViewById<RelativeLayout>(R.id.refundOverviewInfo)
        val apptFilterDurationToday =
            viewInstance.findViewById<RadioButton>(R.id.apptFilterDurationToday)
        pendingRupeeText = viewInstance.findViewById(R.id.pendingRupeeText)
        successfulRupeeText = viewInstance.findViewById(R.id.successfulRupeeText)
        refundScheduleRupeeText = viewInstance.findViewById(R.id.refundScheduleRupeeText)
        refundPendingRupeeText = viewInstance.findViewById(R.id.refundPendingRupeeText)
        refundSuccessfulLeftRupeeText =
            viewInstance.findViewById(R.id.refundSuccessfulLeftRupeeText)
        aviInPaymentOver = viewInstance.findViewById(R.id.aviInPaymentOver)

        filtersStrings.add("All Transaction")
        filtersStrings.add("Custom Date")
        filtersStrings.add("This Week")
        filtersStrings.add("This Month")
        filtersStrings.add("This Year")
        totalOverviewInfo.setOnClickListener {
            val paymentsOverViewInfoBottomSheet = PaymentsOverViewInfoBottomSheet()
            paymentsOverViewInfoBottomSheet.setupConfig(
                this@PaymentOverviewFragment,
                "TotalOverView"
            )
            paymentsOverViewInfoBottomSheet.show(
                requireActivity().supportFragmentManager,
                "Bottom Sheet Dialog Fragment"
            )
        }
        viewTransactionActiveConsult.setOnClickListener {
            EventBus.getDefault().post(IntentServiceResult("ViewTransactionActiveConsult", null))
        }
        viewTransactionOverviewSettlement.setOnClickListener {
            EventBus.getDefault()
                .post(IntentServiceResult("ViewTransactionOverviewSettlement", null))
        }
        viewTransactionRefundOverview.setOnClickListener {
            EventBus.getDefault().post(IntentServiceResult("ViewTransactionRefundOverview", null))
        }
        /* activeConsultationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PaymentsOverViewInfoBottomSheet paymentsOverViewInfoBottomSheet = new PaymentsOverViewInfoBottomSheet();
                paymentsOverViewInfoBottomSheet.setupConfig(PaymentOverviewFragment.this,"ActiveConsultaion");
                paymentsOverViewInfoBottomSheet.show(getActivity().getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");

            }
        });*/settlementOverviewInfo.setOnClickListener {
            val paymentsSettlementOverViewBtmSheet = PaymentsSettlementOverViewBtmSheet()
            paymentsSettlementOverViewBtmSheet.setupConfig(
                this@PaymentOverviewFragment,
                "SettlementOverview"
            )
            paymentsSettlementOverViewBtmSheet.show(
                requireActivity().supportFragmentManager,
                "Bottom Sheet Dialog Fragment"
            )
        }
        refundOverviewInfo.setOnClickListener {
            val paymentsRefundOverViewBtmSheet = PaymentsRefundOverViewBtmSheet()
            paymentsRefundOverViewBtmSheet.setupConfig(
                this@PaymentOverviewFragment,
                "RefundOverview"
            )
            paymentsRefundOverViewBtmSheet.show(
                requireActivity().supportFragmentManager,
                "Bottom Sheet Dialog Fragment"
            )
        }


        // now create instance of the material date picker
        // builder make sure to add the "datePicker" which
        // is normal material date picker which is the first
        // type of the date picker in material design date
        // picker
        val materialDateBuilder: MaterialDatePicker.Builder<*> =
            MaterialDatePicker.Builder.dateRangePicker()

        // now define the properties of the
        // materialDateBuilder that is title text as SELECT A DATE
        materialDateBuilder.setTitleText("SELECT A DATE")

        // now create the instance of the material date
        // picker
        val materialDatePicker = materialDateBuilder.build()

        // handle select date button which opens the
        // material design date picker
        apptFilterDurationToday.setOnClickListener { // getSupportFragmentManager() to
            // interact with the fragments
            // associated with the material design
            // date picker tag is to get any error
            // in logcat
            materialDatePicker.show(
                requireActivity().supportFragmentManager,
                "MATERIAL_DATE_PICKER"
            )
        }

        // now handle the positive button click from the
        // material design date picker
        materialDatePicker.addOnPositiveButtonClickListener {
            val selection = it as Pair<Long, Long>
            val selectedFrom = selection.first
            val firstDate = Date(selectedFrom)
            val selectedTo = selection.second
            val secondDate = Date(selectedTo)
            val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            //format yyyy-MM-dd
            selectedFromDate = sdf2.format(firstDate)
            selectedToDate = sdf2.format(secondDate)
            filterValue = "Specific_" + selectedFromDate + "_" + selectedToDate
            sharedPref!!.savePref("FilterPaymentOverView", filterValue)
            durationFilterChanged()
        }

        materialDatePicker.addOnNegativeButtonClickListener { durationClicked = false }


        val overViewFiltersRecyclerViewAdapter = OverViewFiltersRecyclerViewAdapter(
            requireContext(),
            filtersStrings
        ) { filterType ->
            if (filterType.equals("All Transaction", ignoreCase = true)) {
                durationFilter = filterType
                filterValue = "All"
                sharedPref!!.savePref("FilterPaymentOverView", filterValue)
            } else if (filterType.equals("Custom Date", ignoreCase = true)) {
                materialDatePicker.show(
                    requireActivity().supportFragmentManager,
                    "MATERIAL_DATE_PICKER"
                )
            } else if (filterType.equals("This Week", ignoreCase = true)) {
                durationFilter = filterType
                filterValue = "Week"
                sharedPref!!.savePref("FilterPaymentOverView", filterValue)
            } else if (filterType.equals("This Month", ignoreCase = true)) {
                durationFilter = filterType
                filterValue = "Month"
                sharedPref!!.savePref("FilterPaymentOverView", filterValue)
            } else if (filterType.equals("This Year", ignoreCase = true)) {
                durationFilter = filterType
                filterValue = "Year"
                sharedPref!!.savePref("FilterPaymentOverView", filterValue)
            }
            if (!filterType.equals("Custom Date", ignoreCase = true)) {
                getPaymentOverviewDetails(durationFilter)
            }
        }
        val mLinearLayoutManager = LinearLayoutManager(activity)
        mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        filters_recyclerView.layoutManager = mLinearLayoutManager
        filters_recyclerView.adapter = overViewFiltersRecyclerViewAdapter
        all = viewInstance.findViewById(R.id.apptFilterDurationAll)
        custom = viewInstance.findViewById(R.id.apptFilterDurationToday)
        week = viewInstance.findViewById(R.id.apptFilterDurationWeek)
        month = viewInstance.findViewById(R.id.apptFilterDurationMonth)
        year = viewInstance.findViewById(R.id.apptFilterDurationYear)
        all!!.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.filter_selected_outline)
        all!!.setTextColor(Color.WHITE)
        durationClicked = true
        all!!.setOnClickListener { v ->
            val checked = (v as RadioButton).isChecked
            if (checked) {
                if (durationClicked) {
                    Log.i("response", "clicked")
                } else {
                    durationClicked = true
                    filterValue = "All"
                    sharedPref!!.savePref("FilterPaymentOverView", filterValue)
                    all!!.isChecked = true
                    custom!!.isChecked = false
                    week!!.isChecked = false
                    month!!.isChecked = false
                    year!!.isChecked = false
                    durationFilterChanged()
                }
            }
        }
        custom!!.setOnClickListener { v ->
            val checked = (v as RadioButton).isChecked
            if (checked) {
                if (durationClicked) {
                    Log.i("response", "clicked")
                } else {
                    durationClicked = true
                    all!!.isChecked = false
                    custom!!.isChecked = true
                    week!!.isChecked = false
                    month!!.isChecked = false
                    year!!.isChecked = false
                    // interact with the fragments
                    // associated with the material design
                    // date picker tag is to get any error
                    // in logcat
                    materialDatePicker.show(
                        requireActivity().supportFragmentManager,
                        "MATERIAL_DATE_PICKER"
                    )
                }
            }
        }
        week!!.setOnClickListener { v ->
            val checked = (v as RadioButton).isChecked
            if (checked) {
                if (durationClicked) {
                    Log.i("response", "clicked")
                } else {
                    durationClicked = true
                    all!!.isChecked = false
                    filterValue = "Week"
                    sharedPref!!.savePref("FilterPaymentOverView", filterValue)
                    custom!!.isChecked = false
                    week!!.isChecked = true
                    month!!.isChecked = false
                    year!!.isChecked = false
                    durationFilterChanged()
                }
            }
        }
        month!!.setOnClickListener { v ->
            val checked = (v as RadioButton).isChecked
            if (checked) {
                if (durationClicked) {
                    Log.i("response", "clicked")
                } else {
                    durationClicked = true
                    all!!.isChecked = false
                    custom!!.isChecked = false
                    week!!.isChecked = false
                    filterValue = "Month"
                    sharedPref!!.savePref("FilterPaymentOverView", filterValue)
                    month!!.isChecked = true
                    year!!.isChecked = false
                    durationFilterChanged()
                }
            }
        }
        year!!.setOnClickListener { v ->
            val checked = (v as RadioButton).isChecked
            if (checked) {
                if (durationClicked) {
                    Log.i("response", "clicked")
                } else {
                    durationClicked = true
                    all!!.isChecked = false
                    custom!!.isChecked = false
                    week!!.isChecked = false
                    filterValue = "Year"
                    sharedPref!!.savePref("FilterPaymentOverView", filterValue)
                    month!!.isChecked = false
                    year!!.isChecked = true
                    durationFilterChanged()
                }
            }
        }
        return viewInstance
    }

    fun swipingSelectedUnselected(recordPosition: Int) {
        if (recordPosition == 0) {
            Log.i("pos", "0")
        } else {
            Toast.makeText(activity, "Two", Toast.LENGTH_LONG).show()
        }
    }


/*
    private fun setUpView() {
//        _mViewPager = (ViewPager) getView().findViewById(R.id.imageviewPager);
//        _adapter = new TotalOverViewPagerAdapter(getActivity(), getFragmentManager());
//        _mViewPager.setAdapter(_adapter);
//        _mViewPager.setCurrentItem(0);
//
//        _mViewPagerActiveConsultation = (ViewPager) getView().findViewById(R.id.ActiveConViewPager);
//        _adapterActiveConsultation = new ActiveConsultationPagerAdapter(getActivity(), getFragmentManager());
//        _mViewPagerActiveConsultation.setAdapter(_adapterActiveConsultation);
//        _mViewPagerActiveConsultation.setCurrentItem(0);
//
//        initButton();
    }
*/

    private fun setTab() {
        when (viewPagerCount) {
            1 -> {
                Log.i("tab", "1")
            }
            2 -> {
                activeSwipeOne.visibility = View.VISIBLE
                activeSwipeTwo.visibility = View.VISIBLE
            }
            3 -> {
                activeSwipeOne.visibility = View.VISIBLE
                activeSwipeTwo.visibility = View.VISIBLE
                activeSwipeThree.visibility = View.VISIBLE
            }
            4 -> {
                activeSwipeOne.visibility = View.VISIBLE
                activeSwipeTwo.visibility = View.VISIBLE
                activeSwipeThree.visibility = View.VISIBLE
                activeSwipeFour.visibility = View.VISIBLE
            }
            5 -> {
                activeSwipeOne.visibility = View.VISIBLE
                activeSwipeTwo.visibility = View.VISIBLE
                activeSwipeThree.visibility = View.VISIBLE
                activeSwipeFour.visibility = View.VISIBLE
                activeSwipeFive.visibility = View.VISIBLE
            }
            6 -> {
                activeSwipeOne.visibility = View.VISIBLE
                activeSwipeTwo.visibility = View.VISIBLE
                activeSwipeThree.visibility = View.VISIBLE
                activeSwipeFour.visibility = View.VISIBLE
                activeSwipeFive.visibility = View.VISIBLE
                activeSwipeSix.visibility = View.VISIBLE
            }
            7 -> {
                activeSwipeOne.visibility = View.VISIBLE
                activeSwipeTwo.visibility = View.VISIBLE
                activeSwipeThree.visibility = View.VISIBLE
                activeSwipeFour.visibility = View.VISIBLE
                activeSwipeFive.visibility = View.VISIBLE
                activeSwipeSix.visibility = View.VISIBLE
                activeSwipeSeven.visibility = View.VISIBLE
            }
            8 -> {
                activeSwipeOne.visibility = View.VISIBLE
                activeSwipeTwo.visibility = View.VISIBLE
                activeSwipeThree.visibility = View.VISIBLE
                activeSwipeFour.visibility = View.VISIBLE
                activeSwipeFive.visibility = View.VISIBLE
                activeSwipeSix.visibility = View.VISIBLE
                activeSwipeSeven.visibility = View.VISIBLE
                activeSwipeEight.visibility = View.VISIBLE
            }
            9 -> {
                activeSwipeOne.visibility = View.VISIBLE
                activeSwipeTwo.visibility = View.VISIBLE
                activeSwipeThree.visibility = View.VISIBLE
                activeSwipeFour.visibility = View.VISIBLE
                activeSwipeFive.visibility = View.VISIBLE
                activeSwipeSix.visibility = View.VISIBLE
                activeSwipeSeven.visibility = View.VISIBLE
                activeSwipeEight.visibility = View.VISIBLE
                activeSwipeNine.visibility = View.VISIBLE
            }
        }
        _mViewPager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(position: Int) {}
            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageSelected(position: Int) {
                toSwipeOne.setImageResource(R.drawable.ic_circle_two)
                toSwipeOne.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                toSwipeTwo.setImageResource(R.drawable.ic_circle_two)
                toSwipeTwo.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                btnAction(position)
            }
        })
        _mViewPagerOnlinePaymentOverview.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(position: Int) {}
            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageSelected(position: Int) {
                onlinePaymentSwipeOne.setImageResource(R.drawable.ic_circle_two)
                onlinePaymentSwipeOne.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                onlinePaymentSwipeTwo.setImageResource(R.drawable.ic_circle_two)
                onlinePaymentSwipeTwo.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                       btnActionOnlinePayment(position)
            }
        })
        _mViewPagerActiveConsultation.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(position: Int) {}
            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageSelected(position: Int) {
                activeSwipeOne.setImageResource(R.drawable.ic_circle_two)
                activeSwipeOne.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                activeSwipeTwo.setImageResource(R.drawable.ic_circle_two)
                activeSwipeTwo.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                activeSwipeThree.setImageResource(R.drawable.ic_circle_two)
                activeSwipeThree.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                activeSwipeFour.setImageResource(R.drawable.ic_circle_two)
                activeSwipeFour.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                activeSwipeFive.setImageResource(R.drawable.ic_circle_two)
                activeSwipeFive.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                activeSwipeSix.setImageResource(R.drawable.ic_circle_two)
                activeSwipeSix.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                activeSwipeSeven.setImageResource(R.drawable.ic_circle_two)
                activeSwipeSeven.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                activeSwipeEight.setImageResource(R.drawable.ic_circle_two)
                activeSwipeEight.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                activeSwipeNine.setImageResource(R.drawable.ic_circle_two)
                activeSwipeNine.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
                btnActionActiveConsult(position)
            }
        })
    }

    private fun btnAction(action: Int) {
        when (action) {
            0 -> {
                toSwipeOne.setImageResource(R.drawable.ic_circle)
                toSwipeOne.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
            1 -> {
                toSwipeTwo.setImageResource(R.drawable.ic_circle)
                toSwipeTwo.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    private fun btnActionOnlinePayment(action: Int) {
        when (action) {
            0 -> {
                onlinePaymentSwipeOne.setImageResource(R.drawable.ic_circle)
                onlinePaymentSwipeOne.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
            1 -> {
                onlinePaymentSwipeTwo.setImageResource(R.drawable.ic_circle)
                onlinePaymentSwipeTwo.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    private fun btnActionActiveConsult(action: Int) {
        when (action) {
            0 -> {
                activeSwipeOne.setImageResource(R.drawable.ic_circle)
                activeSwipeOne.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
            1 -> {
                activeSwipeTwo.setImageResource(R.drawable.ic_circle)
                activeSwipeTwo.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
            2 -> {
                activeSwipeThree.setImageResource(R.drawable.ic_circle)
                activeSwipeThree.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
            3 -> {
                activeSwipeFour.setImageResource(R.drawable.ic_circle)
                activeSwipeFour.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
            4 -> {
                activeSwipeFive.setImageResource(R.drawable.ic_circle)
                activeSwipeFive.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
            5 -> {
                activeSwipeSix.setImageResource(R.drawable.ic_circle)
                activeSwipeSix.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
            6 -> {
                activeSwipeSeven.setImageResource(R.drawable.ic_circle)
                activeSwipeSeven.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
            7 -> {
                activeSwipeEight.setImageResource(R.drawable.ic_circle)
                activeSwipeEight.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
            8 -> {
                activeSwipeNine.setImageResource(R.drawable.ic_circle)
                activeSwipeNine.setColorFilter(
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    private fun initButton() {
        toSwipeOne.setImageResource(R.drawable.ic_circle)
        toSwipeOne.setColorFilter(
            ContextCompat.getColor(requireActivity(), R.color.colorAccent),
            PorterDuff.Mode.SRC_IN
        )
        activeSwipeOne.setImageResource(R.drawable.ic_circle)
        activeSwipeOne.setColorFilter(
            ContextCompat.getColor(requireActivity(), R.color.colorAccent),
            PorterDuff.Mode.SRC_IN
        )
        onlinePaymentSwipeOne.setImageResource(R.drawable.ic_circle)
        onlinePaymentSwipeOne.setColorFilter(
            ContextCompat.getColor(requireActivity(), R.color.colorAccent),
            PorterDuff.Mode.SRC_IN
        )
    }


    override fun onResume() {
        super.onResume()
        getPaymentOverviewDetails(durationFilter)
    }

    @SuppressLint("SetTextI18n")
    private fun getPaymentOverviewDetails(dateFilter: String) {
        var url = ""
        try {
            url = if (dateFilter.equals("Specific", ignoreCase = true)) {
                ApiUrls.getPaymentOverviewDetails + "?date_filter=" + "&fromDate=" + selectedFromDate + "&toDate=" + selectedToDate
            } else {
                ApiUrls.getPaymentOverviewDetails + "?date_filter=" + dateFilter + "&fromDate=" + "&toDate="
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        showCustomProgressAlertDialog(
            resources.getString(R.string.please_wait),
            resources.getString(R.string.wait_while_we_fetching)
        )
        aviInPaymentOver!!.visibility = View.VISIBLE
        paymentTransactionViewModel!!.getPaymentOverviewDetailsDetails(activity, url)
            .observe(requireActivity()) { s ->
                Log.i("payMt overview res", s)
                dialog.dismiss()
                aviInPaymentOver!!.visibility = View.GONE
                durationClicked = false
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.PaymentOverviewScreenImpression),
                            null
                        )
                        totalPagerCount = 0
                        totalClinicCountData = 0
                        totalVideoCountData = 0
                        totalChatCountData = 0
                        totalInstantVideoCountData = 0
                        val resObject =
                            response.getJSONObject("response").getJSONObject("response")
                        val totalOverviewObject = resObject.getJSONObject("total_overview")
                        _mViewPager =
                            viewInstance.findViewById<View>(R.id.imageviewPager) as ViewPager
                        _adapter = TotalOverViewPagerAdapter(
                            childFragmentManager,
                            totalOverviewObject
                        )
                        _mViewPager.adapter = _adapter
                        _mViewPager.currentItem = 0
                        _adapter!!.notifyDataSetChanged()
                        val onlinePaymentOverviewObject =
                            resObject.getJSONObject("online_payment_overview")
                        _mViewPagerOnlinePaymentOverview =
                            viewInstance.findViewById<View>(R.id.onlinePaymentImageviewPager) as ViewPager
                        _adapterOnlinePaymentOverview = OnlinePaymentOverViewPagerAdapter(
                            childFragmentManager,
                            onlinePaymentOverviewObject
                        )
                        _mViewPagerOnlinePaymentOverview.adapter =
                            _adapterOnlinePaymentOverview
                        _mViewPagerOnlinePaymentOverview.currentItem = 0
                        _adapter!!.notifyDataSetChanged()
                        val productArray =
                            resObject.getJSONArray("active_consultation_overview")
                        for (i in 0 until productArray.length()) {
                            val productObject = productArray.getJSONObject(i)
                            if (productObject.getString("name")
                                    .contains("Clinic") || productObject.getString("name")
                                    .contains("clinic")
                            ) {
                                totalClinicCountData++
                            }
                            if (productObject.getString("name")
                                    .contains("Video") || productObject.getString("name")
                                    .contains("video")
                            ) {
                                totalVideoCountData++
                            }
                            if (productObject.getString("name")
                                    .contains("Chat") || productObject.getString("name")
                                    .contains("chat")
                            ) {
                                totalChatCountData++
                            }
                            if (productObject.getString("name")
                                    .contains("Instant") || productObject.getString("name")
                                    .contains("instant")
                            ) {
                                totalInstantVideoCountData++
                            }
                        }
                        totalPagerCount =
                            totalClinicCountData + totalVideoCountData + totalChatCountData + totalInstantVideoCountData
                        viewPagerCount = totalPagerCount
                        clinicData = productArray
                        _mViewPagerActiveConsultation =
                            viewInstance.findViewById<View>(R.id.ActiveConViewPager) as ViewPager
                        _adapterActiveConsultation = ActiveConsultationPagerAdapter(
                            childFragmentManager,
                            totalClinicCountData,
                            totalVideoCountData,
                            totalChatCountData,
                            totalInstantVideoCountData,
                            totalPagerCount,
                            clinicData!!
                        )
                        _mViewPagerActiveConsultation.adapter = _adapterActiveConsultation
                        _mViewPagerActiveConsultation.currentItem = 0
                        initButton()
                        setTab()
                        val settlementOverviewObject =
                            resObject.getJSONObject("settlement_overview")
                        pendingRupeeText.text =
                            "" + settlementOverviewObject.getDouble("pending")
                        successfulRupeeText.text =
                            "" + settlementOverviewObject.getDouble("done")
                        val refundOverviewObject = resObject.getJSONObject("refund_overview")
                        refundScheduleRupeeText.text =
                            "" + refundOverviewObject.getDouble("scheduled")
                        refundPendingRupeeText.text =
                            "" + refundOverviewObject.getDouble("pending")
                        refundSuccessfulLeftRupeeText.text =
                            "" + refundOverviewObject.getDouble("done")
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    private fun durationFilterChanged() {
        if (all!!.isChecked) {
            durationFilter = all!!.text.toString()
            //            appointmentActivity.durationFilter = durationFilter;
            all!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
            custom!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            week!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            month!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            year!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            all!!.setTextColor(Color.WHITE)
            custom!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            week!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            month!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            year!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        } else if (custom!!.isChecked) {
            if (custom!!.text.toString().equals("Custom Date", ignoreCase = true)) {
                durationFilter = "Specific"
            }
            //            appointmentActivity.durationFilter = durationFilter;
            custom!!.background =ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
            all!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            week!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            month!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            year!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            custom!!.setTextColor(Color.WHITE)
            all!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            week!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            month!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            year!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        } else if (week!!.isChecked) {
            durationFilter = week!!.text.toString()
            //            appointmentActivity.durationFilter = durationFilter;
            week!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
            all!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            custom!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            month!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            year!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            week!!.setTextColor(Color.WHITE)
            all!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            custom!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            month!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            year!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        } else if (month!!.isChecked) {
            durationFilter = month!!.text.toString()
            //            appointmentActivity.durationFilter = durationFilter;
            month!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
            all!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            custom!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            week!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            year!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            month!!.setTextColor(Color.WHITE)
            all!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            custom!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            week!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            year!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        } else if (year!!.isChecked) {
            durationFilter = year!!.text.toString()
            //            appointmentActivity.durationFilter = durationFilter;
            year!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
            all!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            custom!!.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            week!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            month!!.background =
                ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
            year!!.setTextColor(Color.WHITE)
            all!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            custom!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            week!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            month!!.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        }


//        appointmentApptListModelList.clear();
        getPaymentOverviewDetails(durationFilter)
    }

    companion object {
        var totalClinicCountData = 0
        var totalVideoCountData = 0
        var totalChatCountData = 0
        var totalInstantVideoCountData = 0
        var totalPagerCount = 0

        //filter
        @SuppressLint("StaticFieldLeak")
        var all: RadioButton? = null

        @SuppressLint("StaticFieldLeak")
        var custom: RadioButton? = null

        @SuppressLint("StaticFieldLeak")
        var week: RadioButton? = null

        @SuppressLint("StaticFieldLeak")
        var month: RadioButton? = null

        @SuppressLint("StaticFieldLeak")
        var year: RadioButton? = null

        @JvmStatic
        fun newInstance(): PaymentOverviewFragment {
            val fragment = PaymentOverviewFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(requireActivity())
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}