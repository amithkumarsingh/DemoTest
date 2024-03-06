package com.whitecoats.clinicplus.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.FeedsListRecyclerAdapter
import com.whitecoats.clinicplus.adapters.FeedsListRecyclerAdapter.FeedsInterface
import com.whitecoats.clinicplus.models.FeedsModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.EMRCreateRecordsFormViewModel
import com.whitecoats.clinicplus.viewmodels.FeedsListViewModel
import org.json.JSONObject

class FeedActivity : AppCompatActivity(), FeedsInterface {
    private lateinit var toolbar: Toolbar
    private lateinit var feedsRecycleView: RecyclerView
    private lateinit var feedsRecycleViewLayout: RelativeLayout
    private lateinit var emptyTextLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var feedsListRecyclerAdapter: FeedsListRecyclerAdapter
    private var feedsModels: MutableList<FeedsModel.Data>? = null
    private var emrCreateRecordsFormViewModel: EMRCreateRecordsFormViewModel? = null
    var globalClass: MyClinicGlobalClass? = null
    private var feedsListViewModel: FeedsListViewModel? = null
    private var pageNo = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_feeds)
        initView()
        progressBar.visibility = View.VISIBLE
        emrCreateRecordsFormViewModel =
            ViewModelProvider(this)[EMRCreateRecordsFormViewModel::class.java]
        emrCreateRecordsFormViewModel!!.init()
        feedsListViewModel!!.feedList().observe(
            this
        ) { feedsModel ->
            if (feedsModel != null) {
                totalFeeds = feedsModel.total
                progressBar.visibility = View.GONE
                if (totalFeeds != 0) {
                    emptyTextLayout.visibility = View.GONE
                    feedsRecycleViewLayout.visibility = View.VISIBLE
                    feedsModels!!.addAll(feedsModel.data!!)
                    feedsListRecyclerAdapter.notifyDataSetChanged()
                } else {
                    emptyTextLayout.visibility = View.VISIBLE
                    feedsRecycleViewLayout.visibility = View.GONE
                }
            }
        }
        pullToRefresh!!.setOnRefreshListener {
            if (globalClass!!.isOnline) {
                pageNo = 1
                feedsModels!!.clear()
                feedsListViewModel!!.getFeedsList(this@FeedActivity, pageNo)
            } else {
                globalClass!!.noInternetConnection.showDialog(this@FeedActivity)
            }
        }
    }

    private fun initView() {
        toolbar = findViewById(R.id.feedsToolbar)
        feedsRecycleView = findViewById(R.id.feedsRecycleView)
        feedsRecycleViewLayout = findViewById(R.id.feedsRecycleViewLayout)
        emptyTextLayout = findViewById(R.id.feedsEmptyView)
        progressBar = findViewById(R.id.feedsRecycleLoader)
        feedsModels = ArrayList()
        globalClass = applicationContext as MyClinicGlobalClass
        pullToRefresh = findViewById(R.id.pullToRefresh)
        feedsListRecyclerAdapter = FeedsListRecyclerAdapter(feedsModels!!, this, this@FeedActivity)
        val layoutManager = LinearLayoutManager(this@FeedActivity)
        feedsRecycleView.layoutManager = layoutManager
        feedsRecycleView.adapter = feedsListRecyclerAdapter
        feedsListViewModel = ViewModelProvider(this)[FeedsListViewModel::class.java]
        feedsListViewModel!!.init()
    }

    override fun onCardClick(feedsModel: FeedsModel.Data?) {
        when (feedsModel!!.content_type) {
            "text", "internal" -> {
                isFeedDetailsClick = 1
                val intent = Intent(this@FeedActivity, FeedDetailsActivity::class.java)
                intent.putExtra("FeedDetails", feedsModel)
                startActivity(intent)
            }
            "pdf" -> {
                progressBar.visibility = View.VISIBLE
                isFeedDetailsClick = 1
                emrCreateRecordsFormViewModel!!.getImagePath(
                    this@FeedActivity,
                    feedsModel.content_path
                )
                    .observe(this) { s ->
                        try {
                            val response = JSONObject(s)
                            if (response.getInt("status_code") == 200) {
                                val completeUrl =
                                    response.getJSONObject("response").getString("response")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(completeUrl))
                                startActivity(Intent.createChooser(intent, "Browse with"))
                            } else {
                                errorHandler(this@FeedActivity, s)
                            }
                            progressBar.visibility = View.GONE
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            }
            else -> {
                val intent = Intent(this@FeedActivity, FeedsYoutubePlayerActivity::class.java)
                isFeedDetailsClick = 1
                intent.putExtra("FeedDetails", feedsModel)
                startActivity(intent)
            }
        }
    }


    override fun onLoadMore() {
        Log.d("Total ", totalFeeds.toString() + "," + feedsModels!!.size)
        if (totalFeeds != feedsModels!!.size) {
            pageNo++
            feedsListViewModel!!.getFeedsList(this@FeedActivity, pageNo)
        }
    }

    public override fun onResume() {
        super.onResume()
        if (isFeedDetailsClick == 1) {
            isFeedDetailsClick = 0
        } else {
            pageNo = 1
            feedsModels!!.clear()
            feedsListViewModel!!.getFeedsList(this@FeedActivity, pageNo)
        }
    }

   /* override fun onDetach() {
//        super.onDetach();
        totalFeeds = 0
        feedsListViewModel!!.clearFeedsList()
    }*/

    override fun onDestroy() {
        super.onDestroy()
        totalFeeds = 0
        feedsListViewModel!!.clearFeedsList()
    }

    companion object {
        var totalFeeds = 0
        @JvmField
        var isFeedDetailsClick = 0
        var pullToRefresh: SwipeRefreshLayout?=null

    }
}