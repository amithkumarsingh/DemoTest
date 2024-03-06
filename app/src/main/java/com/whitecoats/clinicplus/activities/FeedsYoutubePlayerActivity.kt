package com.whitecoats.clinicplus.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import com.whitecoats.clinicplus.BuildConfig
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.models.FeedsModel
import com.whitecoats.clinicplus.utils.AppUtilities

class FeedsYoutubePlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
    private var youTubePlayerView: YouTubePlayerView? = null
    private var feedData: FeedsModel.Data? = null
    private var appUtilities: AppUtilities? = null
    private var feedBy: TextView? = null
    private var feedDate: TextView? = null
    private var feedTitle: TextView? = null
    private var feedDesp: TextView? = null
    private val youTubePlayerProvider: YouTubePlayer.Provider?
        get() = youTubePlayerView
    private var fullScreen: Boolean = false
    private var youTubeView: YouTubePlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feeds_youtube_player)
        feedData = intent.getSerializableExtra("FeedDetails") as FeedsModel.Data?
        initView()
    }

    private fun initView() {
        feedBy = findViewById(R.id.feedBy)
        feedDate = findViewById(R.id.feedDate)
        feedTitle = findViewById(R.id.feedTitle)
        feedDesp = findViewById(R.id.feedDesp)
        appUtilities = AppUtilities()
        youTubePlayerView = findViewById(R.id.youtubePlayerView)
        youTubePlayerView!!.initialize(BuildConfig.YOUTUBE_VIDEO_API_KEY, this)
        feedTitle!!.text = feedData!!.content_title
        feedDesp!!.text = feedData!!.content_desc
        feedDate!!.text = appUtilities!!.changeDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            "dd MMM, YYYY",
            feedData!!.published_on
        )
        feedBy!!.text = feedData!!.content_created!!.name
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            youTubePlayerProvider!!.initialize(BuildConfig.YOUTUBE_VIDEO_API_KEY, this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (fullScreen) {
            youTubeView!!.setFullscreen(false)
        } else {
            finish()
        }
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider,
        youTubePlayer: YouTubePlayer,
        wasRestored: Boolean
    ) {
        if (!wasRestored) {
            var youtubeData = ""
            youtubeData = if (feedData!!.content_path!!.contains("=")) {
                feedData!!.content_path!!.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1]
            } else {
                val splitArr =
                    feedData!!.content_path!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                splitArr[splitArr.size - 1]
            }
            youTubeView = youTubePlayer
            youTubePlayer.setOnFullscreenListener {
                    isFullScreen -> fullScreen = isFullScreen }
            youTubePlayer.loadVideo(youtubeData)
            // Hiding player controls
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider,
        youTubeInitializationResult: YouTubeInitializationResult
    ) {
        if (youTubeInitializationResult.isUserRecoverableError) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage = "Error"
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val RECOVERY_DIALOG_REQUEST = 1
    }
}