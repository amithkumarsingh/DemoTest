package com.whitecoats.clinicplus.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.whitecoats.clinicplus.models.FeedsModel
import com.whitecoats.clinicplus.repositories.FeedsListRepository

class FeedsListViewModel : ViewModel() {
    private var feedsListRepository: FeedsListRepository? = null
    fun init() {
        feedsListRepository = FeedsListRepository.instance
    }

    fun getFeedsList(activity: Activity?, pageNo: Int) {
        feedsListRepository!!.feedsList(activity, pageNo)
    }

    fun getMediaURL(activity: Activity?, contentPath: String?) {
        feedsListRepository!!.feedMediaURL(activity, contentPath)
    }

    fun feedList(): LiveData<FeedsModel?> {
        return feedsListRepository!!.feedsList
    }

    fun mediaURL(): LiveData<String?> {
        return feedsListRepository!!.feedMediaURL
    }

    fun clearFeedsList() {
        feedsListRepository!!.clearFeedsList()
    }
/*
    fun clearMediaURL() {
        feedsListRepository!!.clearMediaURL()
    }*/
}