package com.whitecoats.clinicplus.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.activities.FeedActivity
import com.whitecoats.clinicplus.models.FeedsModel
import com.whitecoats.clinicplus.utils.AppUtilities

class FeedsListRecyclerAdapter(
    private val feedsModels: List<FeedsModel.Data>,
    private val feedsInterface: FeedsInterface,
    activity: Activity
) : RecyclerView.Adapter<FeedsListRecyclerAdapter.MyViewHolder>() {
    private val appUtilities: AppUtilities = AppUtilities()
    private val activity: Activity

    init {
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View? = when (viewType) {
            Footer -> LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_feeds_footer, parent, false)
            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_feeds, parent, false)
        }
        return MyViewHolder(itemView!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Normal -> {
                val feedsModel = feedsModels[position]
                holder.feedsTitle?.text = feedsModel.content_title
                holder.feedsBy?.text = feedsModel.content_created!!.name
                holder.feedsDate?.text = appUtilities.changeDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    "dd MMM, YYYY",
                    feedsModel.published_on
                )
                holder.feedsDes?.text = feedsModel.content_desc
                holder.typeIcon?.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.ic_video
                    )
                )
                holder.feedTypeAction?.text = activity.getString(R.string.view)
                if (feedsModel.content_type == "text") {
                    holder.typeIcon?.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.ic_book_open
                        )
                    )
                    holder.feedTypeAction?.text = activity.getString(R.string.read)
                } else if (feedsModel.content_type == "pdf") {
                    holder.typeIcon?.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.ic_pdf
                        )
                    )
                    holder.feedTypeAction?.text = activity.getString(R.string.read)
                }
                holder.feedsCard!!.setOnClickListener {
                    feedsInterface.onCardClick(
                        feedsModel
                    )
                }
            }
            Footer -> feedsInterface.onLoadMore()
            0 -> holder.feedsCard!!.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return feedsModels.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position != FeedActivity.totalFeeds) {
            if (position == feedsModels.size) Footer else Normal
        } else 0
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var feedsCard: CardView? = null
        var feedsTitle: TextView? = null
        var feedsBy: TextView? = null
        var feedsDate: TextView? = null
        var feedsDes: TextView? = null
        var feedTypeAction: TextView? = null
        var typeIcon: ImageView? = null
        var loader: ProgressBar? = null

        init {
            feedsCard = itemView.findViewById(R.id.feedsCardView)
            feedsTitle = itemView.findViewById(R.id.feedsTitle)
            feedsBy = itemView.findViewById(R.id.feedsBy)
            feedsDate = itemView.findViewById(R.id.feedsDate)
            feedsDes = itemView.findViewById(R.id.feedsDescription)
            typeIcon = itemView.findViewById(R.id.feedsTypeIcon)
            feedTypeAction = itemView.findViewById(R.id.feedsTypeText)
            loader = itemView.findViewById(R.id.progressLoader)
        }
    }

    interface FeedsInterface {
        fun onCardClick(feedsModel: FeedsModel.Data?)
        fun onLoadMore()
    }

    companion object {
        const val Normal = 1
        const val Footer = 3
    }
}