package com.whitecoats.clinicplus.activities

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.models.FeedsModel
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.viewmodels.FeedsListViewModel
import java.util.*
import kotlin.math.max

class FeedDetailsActivity : AppCompatActivity() {
    private lateinit var headerLayout: FrameLayout
    private lateinit var imageView: ImageView
    private lateinit var feedBy: TextView
    private lateinit var feedDate: TextView
    private lateinit var feedTitle: TextView
    private lateinit var feedDes: TextView
    private var feedData: FeedsModel.Data? = null
    private var appUtilities: AppUtilities? = null
    private val STATE_RESUME_WINDOW = "resumeWindow"
    private val STATE_RESUME_POSITION = "resumePosition"
    private val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
    private var mExoPlayerFullscreen = false
    private lateinit var mFullScreenIcon: ImageView
    private var mFullScreenDialog: Dialog? = null
    private var dataSourceFactory: DataSource.Factory? = null
    private var feedsListViewModel: FeedsListViewModel? = null
    private var exoPlayer: StyledPlayerView? = null
    var player: ExoPlayer? = null
    private var mResumeWindow = 0
    private var mResumePosition: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_details)
        feedData = intent.getSerializableExtra("FeedDetails") as FeedsModel.Data?
        initView()
        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
        }
        feedsListViewModel!!.mediaURL().observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                if (feedData!!.content_type != "text") {
                    val haveResumePosition = mResumeWindow != C.INDEX_UNSET
                    if (haveResumePosition) {
                        Log.i("DEBUG", " haveResumePosition ")
                        player!!.seekTo(mResumeWindow, mResumePosition)
                    }
                    val mediaItem = MediaItem.fromUri(Uri.parse(value))
                    player!!.setMediaItem(mediaItem)
                    player!!.prepare()
                    player!!.playWhenReady = true
                } else {
                    Glide.with(applicationContext)
                        .load(value)
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any,
                                target: Target<Drawable?>,
                                isFirstResource: Boolean
                            ): Boolean {
                                imageView.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any,
                                target: Target<Drawable?>,
                                dataSource: com.bumptech.glide.load.DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                imageView.visibility = View.VISIBLE
                                return false
                            }
                        })
                        .into(imageView)
                }
            }
        })
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow)
        outState.putLong(STATE_RESUME_POSITION, mResumePosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen)
        super.onSaveInstanceState(outState)
    }

    public override fun onResume() {
        super.onResume()
        if (mExoPlayerFullscreen) {
            (exoPlayer!!.parent as ViewGroup).removeView(exoPlayer)
            mFullScreenDialog!!.addContentView(
                exoPlayer!!,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            mFullScreenIcon.setImageDrawable(
                AppUtilities.changeIconColor(
                    Objects.requireNonNull(
                        ContextCompat.getDrawable(
                            applicationContext, R.drawable.ic_fullscreen_skrink
                        )
                    ), this, R.color.colorWhite
                )
            )
            mFullScreenDialog!!.show()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (exoPlayer != null && player != null) {
            mResumeWindow = player!!.currentWindowIndex
            mResumePosition = max(0, player!!.contentPosition)
            player!!.release()
        }
        if (mFullScreenDialog != null) mFullScreenDialog!!.dismiss()
    }

    private fun initView() {
        headerLayout = findViewById(R.id.feedHeaderLayout)
        imageView = findViewById(R.id.imageHeader)
        feedBy = findViewById(R.id.feedBy)
        feedDate = findViewById(R.id.feedDate)
        feedTitle = findViewById(R.id.feedTitle)
        feedDes = findViewById(R.id.feedDesp)
        appUtilities = AppUtilities()
        exoPlayer = findViewById(R.id.exoPlay)
        initFullscreenDialog()
        initFullscreenButton()
        feedsListViewModel = ViewModelProvider(this)[FeedsListViewModel::class.java]
        feedsListViewModel!!.init()
        initExoPlayer()
        feedTitle.text = feedData!!.content_title
        feedDes.text = feedData!!.content_value
        feedDate.text = appUtilities!!.changeDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            "dd MMM, YYYY",
            feedData!!.published_on
        )
        feedBy.text = feedData!!.content_created!!.name
        Log.d("Feed Type", feedData!!.content_type!!)
        headerLayout.visibility = View.VISIBLE
        if (feedData!!.content_type == "text") {
            exoPlayer!!.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            if (feedData!!.content_image == "") {
                imageView.visibility = View.GONE
                headerLayout.visibility = View.GONE
            } else {
                feedsListViewModel!!.getMediaURL(this, feedData!!.content_image)
            }
        } else {
            exoPlayer!!.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            feedsListViewModel!!.getMediaURL(this, feedData!!.content_path)
        }
    }

    private fun initExoPlayer() {
        player = ExoPlayer.Builder(this).build()
        exoPlayer!!.player = player
        dataSourceFactory = DefaultDataSourceFactory(
            this, Util.getUserAgent(this, getString(R.string.app_name))
        )
    }

    private fun initFullscreenDialog() {
        mFullScreenDialog =
            object : Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                @Deprecated("Deprecated in Java")
                override fun onBackPressed() {
                    closeFullscreenDialog()
                }
            }
    }

    private fun openFullscreenDialog() {
        Log.d("Opening Fullscreen", "****************")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (exoPlayer!!.parent as ViewGroup).removeView(exoPlayer)
        mFullScreenDialog!!.addContentView(
            exoPlayer!!,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mFullScreenDialog!!.show()
    }

    private fun closeFullscreenDialog() {
        Log.d("Closing Fullscreen", "****************")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (exoPlayer!!.parent as ViewGroup).removeView(exoPlayer)
        (findViewById<View>(R.id.feedHeaderLayout) as FrameLayout).addView(exoPlayer)
        mFullScreenDialog!!.dismiss()
    }

    private fun initFullscreenButton() {
        exoPlayer!!.setFullscreenButtonClickListener { isFullScreen -> if (!isFullScreen) closeFullscreenDialog() else openFullscreenDialog() }
    }
}