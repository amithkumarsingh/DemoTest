package com.whitecoats.clinicplus.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.telephony.PhoneStateListener
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.opentok.android.*
import com.opentok.android.PublisherKit.PublisherListener
import com.opentok.android.Session.ReconnectionListener
import com.opentok.android.SubscriberKit.*
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.models.VideoCallModel
import com.whitecoats.clinicplus.utils.ShowAlertDialog
import com.whitecoats.clinicplus.viewmodels.VideoCallViewModel

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class VideoScreenActivity : AppCompatActivity(), Session.SessionListener, PublisherListener,
    SubscriberListener, ReconnectionListener, StreamListener, VideoListener {
    private lateinit var subscriberVideoFrame: FrameLayout
    private lateinit var publisherVideoFrame: FrameLayout
    private lateinit var switchCam: FloatingActionButton
    private lateinit var toggleVideo: FloatingActionButton
    private lateinit var toggleMic: FloatingActionButton
    private lateinit var hangUp: FloatingActionButton
    private lateinit var patientName: TextView
    private lateinit var videoTimer: TextView
    private lateinit var emptyText: TextView
    private lateinit var permissionText: TextView
    private lateinit var mainVideoLayout: RelativeLayout
    private lateinit var permissionLayout: RelativeLayout
    private lateinit var givePermission: Button
    private lateinit var loader: ProgressBar
    private lateinit var loader2: ProgressBar
    private var apptId = 0
    private var videoCallViewModel: VideoCallViewModel? = null
    private var frontCam = true
    private var fromBroadcast = false
    private var videoCallModel: VideoCallModel? = null
    private val timeSwapBuff = 0L
    private var updatedTime = 0L
    private var startTime = 0L
    private var customHandler: Handler? = null
    private var sharedpreferences: SharedPreferences? = null
    private var mSession: Session? = null
    private val permissionsRequired =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    private var mPublisher: Publisher? = null
    private var mSubscriber: Subscriber? = null
    private var userClickedYes = false
    private val mHideHandler = Handler()
    private var mContentView: View? = null
    private val mHidePart2Runnable = Runnable { // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mContentView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
    private var bolBroacastRegistred: Boolean? = null;
    private var mControlsView: View? = null
    private val mShowPart2Runnable = Runnable { // Delayed display of UI elements
        val actionBar = supportActionBar
        actionBar?.show()
        mControlsView!!.visibility = View.VISIBLE
    }
    private var mVisible = false
    private val mHideRunnable = Runnable { show() }

    private lateinit var requestPhoneStatePermissionLauncher: ActivityResultLauncher<String>


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {}
        }
        false
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_screen)
        initView()
        apptId = intent.getIntExtra("AppointmentId", 0)
        videoCallViewModel!!.videoCallModelLiveData.observe(
            this
        ) { videoCallModel -> startVideoSession(videoCallModel) }
//        videoCallViewModel!!.exitVideoStatus.observe(
//            this
//        ) { aBoolean -> Log.d("Exit Status", "********$aBoolean") }
        if (checkPermission()) {
            if (Build.VERSION.SDK_INT >= 31) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                } else {
                    mainVideoLayout.visibility = View.VISIBLE
                    permissionLayout.visibility = View.GONE
                    videoCallViewModel!!.getVideoSession(this, apptId)
                }
            } else {
                mainVideoLayout.visibility = View.VISIBLE
                permissionLayout.visibility = View.GONE
                videoCallViewModel!!.getVideoSession(this, apptId)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissionsRequired,
                PERMISSION_CALLBACK_CONSTANT
            )
        }
        requestPhoneStatePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result: Boolean ->
            if (result) {
                mainVideoLayout.visibility = View.VISIBLE
                permissionLayout.visibility = View.GONE
                videoCallViewModel!!.getVideoSession(this, apptId)
            } else {
                ShowAlertDialog().showPopupToMovePermissionPage(this@VideoScreenActivity)
            }
        }
        toggleVideo.setOnClickListener {
            if (mPublisher != null) {
                if (mPublisher!!.publishVideo) {
//                    mPublisherViewContainer.removeView(mPublisher.getView());
                    mPublisher!!.publishVideo = false
                    switchCam.visibility = View.GONE
                    toggleVideo.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@VideoScreenActivity,
                            R.drawable.ic_video_off
                        )
                    )
                } else {
//                    mPublisherViewContainer.addView(mPublisher.getView());
                    mPublisher!!.publishVideo = true
                    switchCam.visibility = View.VISIBLE
                    toggleVideo.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@VideoScreenActivity,
                            R.drawable.ic_video
                        )
                    )
                }
            }
        }
        toggleMic.setOnClickListener {
            if (mPublisher != null) {
                if (mPublisher!!.publishAudio) {
                    mPublisher!!.publishAudio = false
                    toggleMic.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@VideoScreenActivity,
                            R.drawable.ic_mic_off
                        )
                    )
                } else {
                    mPublisher!!.publishAudio = true
                    toggleMic.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@VideoScreenActivity,
                            R.drawable.ic_mic
                        )
                    )
                }
            }
        }
        switchCam.setOnClickListener {
            if (mPublisher != null && mPublisher!!.publishVideo) {
                mPublisher!!.cycleCamera()
                if (frontCam) {
                    frontCam = false
                    switchCam.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@VideoScreenActivity,
                            R.drawable.ic_switch_camera_front
                        )
                    )
                } else {
                    frontCam = true
                    switchCam.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@VideoScreenActivity,
                            R.drawable.ic_switch_camera_back
                        )
                    )
                }
            }
        }
        hangUp.setOnClickListener {
            if (mSession != null) {
                showConfirm()
            } else {
                setPreference(false)
                unregisterReceiver(receiver)
                finish()
            }
        }

        // Set up the user interaction to manually show or hide the system UI.
        mContentView!!.setOnClickListener { toggle() }
        givePermission.setOnClickListener {
            if (givePermission.text.toString().contains("Reconnect")) {
                if (videoCallModel != null) {
                    permissionText.text = "Reconnecting..."
                    loader2.visibility = View.VISIBLE
                    givePermission.visibility = View.GONE
                    mSession!!.connect(videoCallModel!!.getoToken())
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    permissionsRequired,
                    PERMISSION_CALLBACK_CONSTANT
                )
            }
        }
        val filter = IntentFilter()
        filter.addAction("NewCallIncoming")
        registerReceiver(receiver, filter)

        bolBroacastRegistred = true;
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        val actionBar = supportActionBar
        actionBar?.hide()
        mControlsView!!.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        mContentView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    private fun initView() {
        mVisible = true
        mControlsView = findViewById(R.id.fullscreen_content_controls)
        mContentView = findViewById(R.id.fullscreen_content)
        subscriberVideoFrame = findViewById(R.id.videoSubscriberContainer)
        publisherVideoFrame = findViewById(R.id.videoPublisherContainer)
        switchCam = findViewById(R.id.switchCamera)
        toggleMic = findViewById(R.id.videoMic)
        toggleVideo = findViewById(R.id.videoVideo)
        hangUp = findViewById(R.id.videoHangCall)
        patientName = findViewById(R.id.videoPatientName)
        videoTimer = findViewById(R.id.videoTimer)
        emptyText = findViewById(R.id.videoFullscreenText)
        mainVideoLayout = findViewById(R.id.mainVideoView)
        permissionLayout = findViewById(R.id.permissionLayout)
        givePermission = findViewById(R.id.givePermission)
        permissionText = findViewById(R.id.permissionText)
        loader = findViewById(R.id.loader)
        loader2 = findViewById(R.id.loader2)
        customHandler = Handler()
        sharedpreferences = getSharedPreferences(AppConstants.appSharedPref, MODE_PRIVATE)
        setPreference(true)
        videoCallViewModel = ViewModelProvider(this)[VideoCallViewModel::class.java]
        videoCallViewModel!!.init()
    }

    @SuppressLint("SetTextI18n")
    private fun startVideoSession(videoCallModel: VideoCallModel?) {
        if (videoCallModel != null) {
            if (videoCallModel.status == 200 && videoCallModel.getoToken() != "") {
                Log.d("Video Creating Session", "*************")
                fromBroadcast = false
                mSession = Session.Builder(
                    this,
                    videoCallModel.key,
                    videoCallModel.app[0].appointments.opentok_session_id
                ).build()
                mSession!!.setSessionListener(this)
                mSession!!.setReconnectionListener(this)
                mSession!!.connect(videoCallModel.getoToken())
                this.videoCallModel = videoCallModel
                patientName.text = videoCallModel.user.fname
            } else {
                Log.d("Video Join Api Error", "***************")
                mainVideoLayout.visibility = View.GONE
                permissionLayout.visibility = View.VISIBLE
                permissionText.text = "Some error has occurred. Please try again later"
                givePermission.visibility = View.GONE
            }
        }
    }

    private fun checkPermission(): Boolean {
        var granted = false
        if (ActivityCompat.checkSelfPermission(
                this,
                permissionsRequired[0]
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                permissionsRequired[1]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            granted = true
        }
        return granted
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            var allgranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }
            if (allgranted) {
                if (Build.VERSION.SDK_INT >= 31) {
                    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                    } else {
                        mainVideoLayout.visibility = View.VISIBLE
                        permissionLayout.visibility = View.GONE
                        videoCallViewModel!!.getVideoSession(this, apptId)
                    }
                } else {
                    mainVideoLayout.visibility = View.VISIBLE
                    permissionLayout.visibility = View.GONE
                    videoCallViewModel!!.getVideoSession(this, apptId)
                }
            } else {
                mainVideoLayout.visibility = View.GONE
                permissionLayout.visibility = View.VISIBLE
                Toast.makeText(
                    this,
                    "You need to give Camera and Microphone permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showConfirm() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Close Session")
        builder.setMessage("Do you want to close the session?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
            videoCallViewModel!!.clearData()
            mSession!!.disconnect()
            setPreference(false)
            userClickedYes = true
            videoCallViewModel!!.exitVideo(this, apptId)
            unregisterReceiver(receiver)
            finish()
        }
        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.show()
    }

    private val updateTimerThread: Runnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            val timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            var secs = (updatedTime / 1000).toInt()
            val mins = secs / 60
            secs %= 60
            videoTimer.text = ("" + mins + ":"
                    + String.format("%02d", secs))
            customHandler!!.postDelayed(this, 0)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // super.getOnBackPressedDispatcher().onBackPressed()
        if (mSession != null) {
            showConfirm()
        } else {
            setPreference(false)
            unregisterReceiver(receiver)
            finish()
        }
    }

    override fun onPause() {
        Log.d("On PAuse", "****************")
        setPreference(false)
//        try {
//            if(receiver !=null) {
//                unregisterReceiver(receiver)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        super.onPause()
    }

    override fun onResume() {
        setPreference(true)
        super.onResume()
    }

    override fun onDestroy() {
        Log.d("On Destroy", "****************")
        //        if(mSession != null) {
//            Log.d("Session On", "****************");
//            mSession.disconnect();
//        }
        videoCallViewModel!!.clearData()
        setPreference(false)
        if (!userClickedYes) {
            videoCallViewModel!!.exitVideo(this, apptId)
        }
        try {
            if (bolBroacastRegistred == true) {
                if (receiver != null) {
                    unregisterReceiver(receiver)
                    bolBroacastRegistred = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    private fun setPreference(state: Boolean) {
        val editor = sharedpreferences!!.edit()
        editor.putBoolean("InVideoSession", state)
        editor.apply()
    }

    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val apptID = intent.getIntExtra("IncomingApptId", 0)
            if (apptID != 0) {
                showIncomingCallPopUp(apptID)
                fromBroadcast = true
            }
        }
    }

    private fun showIncomingCallPopUp(apptId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Call Incoming")
        builder.setMessage("Do you want to close this session and join the second one?")
        builder.setCancelable(false)
        val perviousApptId = this.apptId
        this.apptId = apptId
        builder.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
            videoCallViewModel!!.clearData()
            mSession!!.disconnect()
            videoCallViewModel!!.exitVideo(this, perviousApptId)

            //remove the notification after opening the activity
            val notificationManager =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            videoCallViewModel!!.getVideoSession(this, apptId)
        }
        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.show()
    }

    /**
     * Session Listeners
     */
    @SuppressLint("SetTextI18n")
    override fun onReconnecting(session: Session) {
        Log.d("Video Call", "onReconnecting Session ****************")
        if (mSubscriber != null) {
            subscriberVideoFrame.removeView(mSubscriber!!.view)
        } else {
            subscriberVideoFrame.removeAllViews()
        }
        emptyText.text = "Reconnecting...."
        loader.visibility = View.VISIBLE
        emptyText.visibility = View.VISIBLE
    }

    override fun onReconnected(session: Session) {
        Log.d("Video Call", "onReconnected Session ****************")
        if (mSubscriber != null) {
            subscriberVideoFrame.removeAllViews()
            subscriberVideoFrame.addView(mSubscriber!!.view)
        }
        emptyText.text = ""
        loader.visibility = View.GONE
        emptyText.visibility = View.GONE
    }

    override fun onConnected(session: Session) {
        Toast.makeText(this, "Session Connected", Toast.LENGTH_SHORT).show()
        startTime = SystemClock.uptimeMillis()
        customHandler!!.postDelayed(updateTimerThread, 0)
        mainVideoLayout.visibility = View.VISIBLE
        permissionLayout.visibility = View.GONE
        mPublisher = Publisher.Builder(this)
            .resolution(Publisher.CameraCaptureResolution.LOW)
            .frameRate(Publisher.CameraCaptureFrameRate.FPS_15)
            .build()
        mPublisher!!.publishAudio = true
        mPublisher!!.setPublisherListener(this)
        publisherVideoFrame.addView(mPublisher!!.view)
        if (mPublisher!!.view is GLSurfaceView) {
            (mPublisher!!.view as GLSurfaceView).setZOrderOnTop(true)
        }
        mSession!!.publish(mPublisher)
    }

    @SuppressLint("SetTextI18n")
    override fun onDisconnected(session: Session) {
        Log.d("Video Call", "onDisconnected Session ****************")
        mainVideoLayout.visibility = View.GONE
        permissionLayout.visibility = View.VISIBLE
        loader2.visibility = View.GONE
        givePermission.visibility = View.VISIBLE
        permissionText.text = "Session Disconnected"
        givePermission.text = "Reconnect Session"
        if (fromBroadcast) {
            mainVideoLayout.visibility = View.VISIBLE
            permissionLayout.visibility = View.GONE
            emptyText.text = "Connecting to patient..."
            loader.visibility = View.VISIBLE
            emptyText.visibility = View.VISIBLE
        }
        if (mSubscriber != null) {
            subscriberVideoFrame.removeAllViews()
            mSubscriber = null
        }
    }

    override fun onStreamReceived(session: Session, stream: Stream) {
        Log.d("Video Call", "onStreamReceived Session ****************")
        if (mSubscriber == null) {
            Log.d("Subcriber null", "****************")
            mSubscriber = Subscriber.Builder(this, stream).build()
            mSession!!.subscribe(mSubscriber)
            mSubscriber!!.setVideoListener(this)
            mSubscriber!!.setStreamListener(this)
            subscriberVideoFrame.addView(mSubscriber!!.view)
            emptyText.visibility = View.GONE
            loader.visibility = View.GONE
            Toast.makeText(
                this,
                "You are now securely connected to " + patientName.text.toString(),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Log.d("Subcriber not null", "****************")
            subscriberVideoFrame.removeAllViews()
            subscriberVideoFrame.addView(mSubscriber!!.view)
            emptyText.text = ""
            emptyText.visibility = View.GONE
            loader.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onStreamDropped(session: Session, stream: Stream) {
        Log.d("Video Call", "onStreamDropped Session ****************")
        if (mSubscriber != null) {
            mSubscriber = null
            subscriberVideoFrame.removeAllViews()
            //            mPublisher.setPublishAudio(false);
            emptyText.text = "Patient has left the consultation"
            emptyText.visibility = View.VISIBLE
            Toast.makeText(this, "Patient has left the consultation", Toast.LENGTH_SHORT).show()
        }

//        if (mSubscriber != null) {
//            subscriberVideoFrame.removeAllViews()
//            subscriberVideoFrame.addView(mSubscriber!!.view)
//        }
//        emptyText.text = ""
//        emptyText.visibility = View.GONE
//        loader.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    override fun onError(session: Session, opentokError: OpentokError) {
        Log.d("Video Call", "onError Session ****************" + opentokError.errorCode)
        Log.d("Video Error", opentokError.message)
        Toast.makeText(this, opentokError.message, Toast.LENGTH_LONG).show()
        if (opentokError.errorCode.toString() == "ConnectionFailed") {
            loader2.visibility = View.GONE
            givePermission.visibility = View.VISIBLE
            permissionText.text = "Session Disconnected"
        }
    }
    /** **************************************************************************************  */
    /**
     * Publisher Listener
     */
    override fun onStreamCreated(publisherKit: PublisherKit, stream: Stream) {
        Log.d("Video Call", "onStreamCreated PublisherKit ****************")
    }

    override fun onStreamDestroyed(publisherKit: PublisherKit, stream: Stream) {
        Log.d("Video Call", "onStreamDestroyed PublisherKit ****************")
    }

    override fun onError(publisherKit: PublisherKit, opentokError: OpentokError) {
        Log.d("Video Call", "onError PublisherKit ****************")
    }
    /** **************************************************************************************  */
    /**
     * Subscriber Listener
     */
    override fun onReconnected(subscriberKit: SubscriberKit) {
        Log.d("Video Call", "onReconnected SubscriberKit ****************" + (mSubscriber == null))
        //        if(mSubscriber == null) {
//            mSubscriber = new Subscriber.Builder(this, subscriberKit.getStream()).build();
//            mSession.subscribe(mSubscriber);
//            mSubscriber.setVideoListener(this);
//            mSubscriber.setStreamListener(this);
//            subscriberVideoFrame.addView(mSubscriber.getView());
//
//            emptyText.setText("");
//            emptyText.setVisibility(View.GONE);
//        }
        if (mSubscriber != null) {
            subscriberVideoFrame.removeAllViews()
            subscriberVideoFrame.addView(mSubscriber!!.view)
        }
        emptyText.text = ""
        emptyText.visibility = View.VISIBLE
        loader.visibility = View.GONE
    }

    override fun onConnected(subscriberKit: SubscriberKit) {
        Log.d("Video Call", "onConnected SubscriberKit ****************")
    }

    override fun onDisconnected(subscriberKit: SubscriberKit) {
        Log.d("Video Call", "onDisconnected SubscriberKit ****************")
        if (mSubscriber != null) {
//            mSubscriber = null;
            subscriberVideoFrame.removeAllViews()
        }
    }

    override fun onError(subscriberKit: SubscriberKit, opentokError: OpentokError) {
        Log.d("Video Call", "onError SubscriberKit ****************")
    }

    override fun onVideoDataReceived(subscriberKit: SubscriberKit) {
        Log.d("Video Call", "onVideoDataReceived SubscriberKit ****************")
    }

    @SuppressLint("SetTextI18n")
    override fun onVideoDisabled(subscriberKit: SubscriberKit, s: String) {
        Log.d("Video Call", "onVideoDisabled SubscriberKit ****************")
        if (mSubscriber != null) {
            subscriberVideoFrame.removeView(mSubscriber!!.view)
        }
        emptyText.text = "Patient switch off the camera"
        emptyText.visibility = View.VISIBLE
    }

    override fun onVideoEnabled(subscriberKit: SubscriberKit, s: String) {
        Log.d("Video Call", "onVideoEnabled SubscriberKit ****************")
        if (mSubscriber != null) {
            subscriberVideoFrame.removeAllViews()
            subscriberVideoFrame.addView(mSubscriber!!.view)
        }
        emptyText.text = ""
        emptyText.visibility = View.GONE
    }

    override fun onVideoDisableWarning(subscriberKit: SubscriberKit) {
        Log.d("Video Call", "onVideoDisableWarning SubscriberKit ****************")
    }

    override fun onVideoDisableWarningLifted(subscriberKit: SubscriberKit) {
        Log.d("Video Call", "onVideoDisableWarningLifted SubscriberKit ****************")
    }

    /** **************************************************************************************  */
    companion object {
        private const val PERMISSION_CALLBACK_CONSTANT = 100

        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }

    override fun onStop() {
        super.onStop()
        videoCallViewModel!!.exitVideoStatus.removeObservers(this)
    }
}