package com.whitecoats.clinicplus

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.opentok.android.*
import com.opentok.android.PublisherKit.PublisherListener
import com.opentok.android.Session.ReconnectionListener
import com.opentok.android.SubscriberKit.*
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.ShowAlertDialog
import org.json.JSONObject

class JoinVideoActivity : AppCompatActivity(), Session.SessionListener, PublisherListener,
    SubscriberListener, ReconnectionListener, StreamListener, VideoListener {
    private var mSession: Session? = null
    private lateinit var mPublisherViewContainer: FrameLayout
    private lateinit var mSubscriberViewContainer: FrameLayout
    private lateinit var fullScreenFrame: FrameLayout
    private var mPublisher: Publisher? = null
    private var mSubscriber: Subscriber? = null
    private lateinit var hangUp: FloatingActionButton
    private lateinit var mic: FloatingActionButton
    private lateinit var video: FloatingActionButton
    private lateinit var switchCamera: FloatingActionButton
    private lateinit var fullText: TextView
    private lateinit var timer: TextView
    private lateinit var patientName: TextView
    private var micState = 1
    private var videoState = 1
    private val permissionsRequired =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    private var mainLayout: RelativeLayout? = null
    private var startTime = 0L
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L
    private val customHandler = Handler()
    private var ruleLayout: LinearLayout? = null
    private var closeRule: ImageView? = null
    private var appointmentApiRequests: AppointmentApiRequests? = null
    private var apptId = 0
    private var isVideoStop = false
    private var isStreamDestroyOnVideoStop = false
    private var isHangUp = false
    private var connectionCount = 0
    private var patientNm: String? = null
    private var mSessionDialog: ProgressDialog? = null
    private var mSubscriberDialog: ProgressDialog? = null
    private var ismSessionDialog = false
    private var ismSubscriberDialog = false
    private var frontCam = true
    private lateinit var requestPhoneStatePermissionLauncher: ActivityResultLauncher<String>
    private var telephonyManager: TelephonyManager? = null
    private var callStateListener: PhoneStateListener? = null
    private var callCurrentState = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_video)
        mainLayout = findViewById(R.id.videoMainLayout)
        fullScreenFrame = findViewById(R.id.videoFullscreenFrame)
        mPublisherViewContainer = findViewById(R.id.videoPublisherContainer)
        mSubscriberViewContainer = findViewById(R.id.videoSubscriberContainer)
        hangUp = findViewById(R.id.videoHangCall)
        mic = findViewById(R.id.videoMic)
        video = findViewById(R.id.videoVideo)
        fullText = findViewById(R.id.videoFullscreenText)
        timer = findViewById(R.id.videoTimer)
        hangUp.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        mic.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        video.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        ruleLayout = findViewById(R.id.appointDetailRulesLayout)
        closeRule = findViewById(R.id.appointDetailHIWIcon)
        appointmentApiRequests = AppointmentApiRequests()
        patientName = findViewById(R.id.videoPatientName)
        mSessionDialog = ProgressDialog(this@JoinVideoActivity)
        mSubscriberDialog = ProgressDialog(this@JoinVideoActivity)
        switchCamera = findViewById(R.id.switchCamera)

        requestPhoneStatePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result: Boolean ->
            if (result) {
                telephonyManager!!.listen(
                    callStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE
                )
            } else {
                ShowAlertDialog().showPopupToMovePermissionPage(this@JoinVideoActivity)
            }
        }
        apptId = intent.getIntExtra("AppointmentId", 0)


        if (intent.hasExtra("callingFrom")) {
            if (intent.getStringExtra("callingFrom")
                    .equals("notificationOpenHandler", ignoreCase = true)
            ) {
                checkIfAnyOnGoingPhoneCall()
            } else {
                getJoinVideoToken(apptId)
            }
        } else {
            getJoinVideoToken(apptId)
        }

        //remove the notification after opening the activity
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        hangUp.setOnClickListener {
            if (mSession != null) {
                mSession!!.disconnect()
            }
            isHangUp = true
            existVideo(apptId)
        }
        mic.setOnClickListener {
            if (mPublisher != null) {
                if (micState == 1) {
                    mPublisher!!.publishAudio = false
                    micState = 0
                    mic.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@JoinVideoActivity,
                            R.drawable.ic_mic_off
                        )
                    )
                } else {
                    mPublisher!!.publishAudio = true
                    micState = 1
                    mic.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@JoinVideoActivity,
                            R.drawable.ic_mic
                        )
                    )
                }
            }
        }
        video.setOnClickListener {
            if (mPublisher != null) {
                if (videoState == 1) {
                    mPublisherViewContainer.removeView(mPublisher!!.view)
                    mPublisher!!.publishVideo = false
                    videoState = 0
                    switchCamera.visibility = View.GONE
                    video.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@JoinVideoActivity,
                            R.drawable.ic_video_off
                        )
                    )
                } else {
                    mPublisherViewContainer.addView(mPublisher!!.view)
                    mPublisher!!.publishVideo = true
                    videoState = 1
                    switchCamera.visibility = View.VISIBLE
                    video.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@JoinVideoActivity,
                            R.drawable.ic_video
                        )
                    )
                }
            }
        }
        switchCamera.setOnClickListener {
            if (mPublisher != null && videoState == 1) {
                mPublisher!!.cycleCamera()
                if (frontCam) {
                    frontCam = false
                    switchCamera.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@JoinVideoActivity,
                            R.drawable.ic_switch_camera_front
                        )
                    )
                } else {
                    frontCam = true
                    switchCamera.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@JoinVideoActivity,
                            R.drawable.ic_switch_camera_back
                        )
                    )
                }
            }
        }
    }

    private fun checkIfAnyOnGoingPhoneCall() {
        telephonyManager =
            this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        callStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    callCurrentState = 1
                    Toast.makeText(
                        this@JoinVideoActivity,
                        "You can't place a video call if you're already on a phone call.",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    callCurrentState = 2
                    Toast.makeText(
                        this@JoinVideoActivity,
                        "You can't place a video call if you're already on a phone call.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (callCurrentState == 1 || callCurrentState == 2) {
                        callCurrentState = 0
                    } else {
                        getJoinVideoToken(apptId)
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= 31) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
            } else {
                telephonyManager!!.listen(
                    callStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE
                )
            }
        } else {
            telephonyManager!!.listen(
                callStateListener,
                PhoneStateListener.LISTEN_CALL_STATE
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val builder1 = AlertDialog.Builder(this@JoinVideoActivity)
        builder1.setMessage("Do You Want To Quit This Session?")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Yes"
        ) { _, _ ->
            if (mSession != null) {
                mSession!!.disconnect()
            }
            isHangUp = true
            existVideo(apptId)
        }
        builder1.setNegativeButton(
            "No"
        ) { dialog, _ -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }

    override fun onStreamCreated(publisherKit: PublisherKit, stream: Stream) {
//        connectionCount++;
//        Log.i("Video Activity", "onStreamCreated");
    }

    override fun onStreamDestroyed(publisherKit: PublisherKit, stream: Stream) {
//        Log.i("Video Activity", "onStreamDestroyed");
        if (isVideoStop) {
            isStreamDestroyOnVideoStop = true
            if (ismSessionDialog) {
                mSessionDialog!!.dismiss()
            } else {
                Toast.makeText(
                    this@JoinVideoActivity,
                    "You have left the consultation",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            if (ismSessionDialog) {
                mSessionDialog!!.dismiss()
            } else {
                Toast.makeText(
                    this@JoinVideoActivity,
                    "You have left the consultation",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onError(publisherKit: PublisherKit, opentokError: OpentokError) {
//        Log.i("Video Activity", "onError");
        Toast.makeText(this@JoinVideoActivity, opentokError.message, Toast.LENGTH_SHORT).show()
    }

    override fun onConnected(subscriberKit: SubscriberKit) {
        Toast.makeText(
            this@JoinVideoActivity,
            "Your patient is now re-connected",
            Toast.LENGTH_SHORT
        ).show()
    }

    // In the implementation of the SubscriberKit.StreamListener interface:
    override fun onDisconnected(subscriber: SubscriberKit) {
        // Display a user interface notification.
//        Log.i(LOGTAG, "Subscriber has been disconnected by connection error");
        showSubscriberReconnectionDialog(true)
    }

    override fun onReconnected(subscriber: SubscriberKit) {
        // Adjust user interface.
//        Log.i(LOGTAG, "Subscriber has been reconnected");
        showSubscriberReconnectionDialog(false)
    }

    override fun onError(subscriberKit: SubscriberKit, opentokError: OpentokError) {
        Toast.makeText(this@JoinVideoActivity, opentokError.message, Toast.LENGTH_SHORT).show()
    }

    //suscriber video enable disable litner
    override fun onVideoDisableWarningLifted(subscriberKit: SubscriberKit) {
//        Log.i("DisableWarningLifted", "onVideoDisableWarningLifted");
    }

    override fun onVideoDisableWarning(subscriberKit: SubscriberKit) {
//        Log.i("onVideoDisableWarning", "onVideoDisableWarning");
    }

    override fun onVideoDataReceived(subscriberKit: SubscriberKit) {
//        Log.i("Video Activity subscriber", "videoDataRecevied");
    }

    override fun onVideoDisabled(subscriberKit: SubscriberKit, s: String) {
//        Log.i("Video Activity subscriber", "videoDisabled");
        Toast.makeText(this@JoinVideoActivity, "Patient has  disabled video", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onVideoEnabled(subscriberKit: SubscriberKit, s: String) {
//        Log.i("Video Activity subscriber", "videoEnabled");
        Toast.makeText(this@JoinVideoActivity, "Patient has  enabled video", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onConnected(session: Session) {
//        Log.i("Video Activity", "Session Connected");
        isVideoStart = true
        startTime = SystemClock.uptimeMillis()
        customHandler.postDelayed(updateTimerThread, 0)
        if (ActivityCompat.checkSelfPermission(
                this@JoinVideoActivity,
                permissionsRequired[0]
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this@JoinVideoActivity,
                permissionsRequired[1]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= 31) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                } else {
                    mPublisher = Publisher.Builder(this)
                        .resolution(Publisher.CameraCaptureResolution.LOW)
                        .frameRate(Publisher.CameraCaptureFrameRate.FPS_15)
                        .build()
                    mPublisher!!.publishAudio = true
                    mPublisher!!.setPublisherListener(this)
                    mPublisherViewContainer.addView(mPublisher!!.view)
                    if (mPublisher!!.view is GLSurfaceView) {
                        (mPublisher!!.view as GLSurfaceView).setZOrderOnTop(true)
                    }
                    mSession!!.publish(mPublisher)
                    connectionCount++
                }
            } else {
                mPublisher = Publisher.Builder(this)
                    .resolution(Publisher.CameraCaptureResolution.LOW)
                    .frameRate(Publisher.CameraCaptureFrameRate.FPS_15)
                    .build()
                mPublisher!!.publishAudio = true
                mPublisher!!.setPublisherListener(this)
                mPublisherViewContainer.addView(mPublisher!!.view)
                if (mPublisher!!.view is GLSurfaceView) {
                    (mPublisher!!.view as GLSurfaceView).setZOrderOnTop(true)
                }
                mSession!!.publish(mPublisher)
                connectionCount++
            }
        } else {
            ActivityCompat.requestPermissions(
                this@JoinVideoActivity,
                permissionsRequired,
                PERMISSION_CALLBACK_CONSTANT
            )
        }
    }

    override fun onDisconnected(session: Session) {
//        Log.i("Video Activity", "Session Disconnected");
        connectionCount--
        if (ismSessionDialog || ismSubscriberDialog) {
            mSessionDialog!!.dismiss()
            mSubscriberDialog!!.dismiss()
            ismSessionDialog = false
            ismSubscriberDialog = false
            if (mSubscriber != null) {
                mSubscriber = null
                mSubscriberViewContainer.removeAllViews()
                /*Added null check condition for mPublisher object to avoid the Crash*/if (mPublisher != null) {
                    mPublisher!!.publishAudio = false
                }
                Toast.makeText(
                    this@JoinVideoActivity,
                    "Lost connection. This could be due to your internet connection or because $patientNm lost their connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // In the implementation of the Session.ReconnectionListener interface
    override fun onReconnecting(session: Session) {
        // Display a user interface notification.
        // Log.i(LOGTAG, "Reconnecting the session "+session.getSessionId());
        showReconnectionDialog(true)
    }

    override fun onReconnected(session: Session) {
        // Adjust user interface.
//        Log.i(LOGTAG, "Session has been reconnected");
        showReconnectionDialog(false)
    }

    override fun onStreamReceived(session: Session, stream: Stream) {
//        Log.i("Video Activity", "Stream Received");
        if (mSubscriber == null) {
//            Log.d("Subscriber", "**************");
            mSubscriber = Subscriber.Builder(this, stream).build()
            mSession!!.subscribe(mSubscriber)
            mSubscriber!!.setVideoListener(this@JoinVideoActivity) // added by dileep 11th may 2020
            mSubscriber!!.setStreamListener(this@JoinVideoActivity)
            mSubscriberViewContainer.addView(mSubscriber!!.view)
            /*Added null check condition for mPublisher object to avoid the Crash*/if (mPublisher != null) {
                mPublisher!!.publishAudio = true
            }
            connectionCount++
            if (connectionCount == 2) {
                Toast.makeText(
                    this@JoinVideoActivity,
                    "You are now securely connected to $patientNm",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            if (isVideoStop) {
                mSubscriber = Subscriber.Builder(this, stream).build()
                mSession!!.subscribe(mSubscriber)
                mSubscriberViewContainer.addView(mSubscriber!!.view)
            }
        }
    }

    override fun onStreamDropped(session: Session, stream: Stream) {
//        Log.i("Video Activity", "Stream Dropped");
        if (mSubscriber != null) {
            mSubscriber = null
            mSubscriberViewContainer.removeAllViews()
            /*Added null check condition for mPublisher object to avoid the Crash*/if (mPublisher != null) {
                mPublisher!!.publishAudio = false
            }
            connectionCount--
            if (ismSubscriberDialog) {
                ismSubscriberDialog = false
                mSubscriberDialog!!.dismiss()
                Toast.makeText(
                    this@JoinVideoActivity,
                    "Your Patient has left the consultation",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onError(session: Session, opentokError: OpentokError) {
//        Log.i("Video Activity", "Stream Dropped onError");
        Toast.makeText(this@JoinVideoActivity, opentokError.message, Toast.LENGTH_SHORT).show()
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
                        mPublisher = Publisher.Builder(this)
                            .resolution(Publisher.CameraCaptureResolution.LOW)
                            .frameRate(Publisher.CameraCaptureFrameRate.FPS_15)
                            .build()
                        mPublisher!!.publishAudio = true
                        mPublisher!!.setPublisherListener(this)
                        mPublisherViewContainer.addView(mPublisher!!.view)
                        if (mPublisher!!.view is GLSurfaceView) {
                            (mPublisher!!.view as GLSurfaceView).setZOrderOnTop(true)
                        }
                        mSession!!.publish(mPublisher)
                    }
                } else {
                    mPublisher = Publisher.Builder(this)
                        .resolution(Publisher.CameraCaptureResolution.LOW)
                        .frameRate(Publisher.CameraCaptureFrameRate.FPS_15)
                        .build()
                    mPublisher!!.publishAudio = true
                    mPublisher!!.setPublisherListener(this)
                    mPublisherViewContainer.addView(mPublisher!!.view)
                    if (mPublisher!!.view is GLSurfaceView) {
                        (mPublisher!!.view as GLSurfaceView).setZOrderOnTop(true)
                    }
                    mSession!!.publish(mPublisher)
                }
            } else {
                Toast.makeText(
                    this@JoinVideoActivity,
                    "You Need To Give Camera And Microphone Permission",
                    Toast.LENGTH_SHORT
                ).show()
                mSession!!.disconnect()
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(
                    this@JoinVideoActivity,
                    permissionsRequired[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Got Permission
                Toast.makeText(
                    this@JoinVideoActivity,
                    "You Have All The Permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getJoinVideoToken(apptId: Int) {
        val url = ApiUrls.getJoinVideoDetails + "/" + apptId
        appointmentApiRequests!!.getApptApiData(url, "", this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    var resObj = JSONObject(result)
                    resObj = resObj.getJSONObject("response")
                    API_KEY = resObj.getString("key")
                    SESSION_ID =
                        resObj.getJSONArray("app").getJSONObject(0).getJSONObject("appointments")
                            .getString("opentok_session_id")
                    TOKEN = resObj.getString("oToken")
                    mSession = Session.Builder(this@JoinVideoActivity, API_KEY, SESSION_ID).build()
                    mSession!!.setSessionListener(this@JoinVideoActivity)
                    mSession!!.setReconnectionListener(this@JoinVideoActivity)
                    mSession!!.connect(TOKEN)
                    patientNm = resObj.getJSONObject("user").getString("fname")
                    //                    Log.d("Patient Name", patientNm);
                    patientName.text = patientNm
                } catch (e: Exception) {
                    e.printStackTrace()
                    showSessionFail()
                }
            }

            override fun onError(err: String) {
                showSessionFail()
            }
        })
    }

    private fun showSessionFail() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Failed To Create Session. Try Again Later.")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Ok"
        ) { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        val alert11 = builder1.create()
        alert11.show()
    }

    private val updateTimerThread: Runnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            var secs = (updatedTime / 1000).toInt()
            val mins = secs / 60
            secs %= 60
            //val milliseconds = (updatedTime % 1000).toInt()
            timer.text = ("" + mins + ":"
                    + String.format("%02d", secs))
            customHandler.postDelayed(this, 0)
        }
    }

    private fun existVideo(apptId: Int) {
        val url = ApiUrls.exitVideo + "/" + apptId
        appointmentApiRequests!!.getApptApiData(url, "", this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                finish()
            }

            override fun onError(err: String) {
                finish()
                errorHandler(this@JoinVideoActivity, err)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (isVideoStop && isStreamDestroyOnVideoStop) {
            mSession!!.connect(TOKEN)
            isStreamDestroyOnVideoStop = false
        } else if (isVideoStop) {
//            mSession.connect(TOKEN);
            if (mSession != null) {
                mSession!!.onResume()
                isVideoStop = false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isHangUp) {
            Log.i("isHangup", "isHangup")
        } else if (OSNotificationOpenedHandler.isNotificationOpen) {
            mSession!!.disconnect()
            isVideoStart = false
            OSNotificationOpenedHandler.isNotificationOpen = false
        } else {
            isVideoStop = true
            /*Checking the null condition to avoid the crash*/if (mSession != null) {
                mSession!!.onPause()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //        mSession.onPause();
        if (isHangUp) {
            Log.i("isHangUp", "isHangUp")
        } else if (OSNotificationOpenedHandler.isNotificationOpen) {
            mSession!!.disconnect()
            isVideoStart = false
            OSNotificationOpenedHandler.isNotificationOpen = false
        } else {
            isVideoStop = true
            /*Checking the null condition to avoid the crash*/if (mSession != null) {
                mSession!!.onPause()
            }
        }
    }

    private fun showReconnectionDialog(show: Boolean) {
        if (show) {
            mSessionDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            mSessionDialog!!.setMessage("Reconnecting. Please wait...")
            mSessionDialog!!.isIndeterminate = true
            mSessionDialog!!.setCanceledOnTouchOutside(false)
            mSessionDialog!!.show()
            ismSessionDialog = true
        } else {
            ismSessionDialog = false
            mSessionDialog!!.dismiss()
            val builder = AlertDialog.Builder(this@JoinVideoActivity)
            builder.setMessage("Session has been re-connected")
                .setPositiveButton(android.R.string.ok, null)
            builder.create()
            builder.show()
        }
    }

    private fun showSubscriberReconnectionDialog(show: Boolean) {
        if (show) {
            mSubscriberDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            mSubscriberDialog!!.setMessage("Patient is reconnecting. Please wait...")
            mSubscriberDialog!!.isIndeterminate = true
            mSubscriberDialog!!.setCanceledOnTouchOutside(false)
            mSubscriberDialog!!.show()
            ismSubscriberDialog = true
        } else {
            ismSubscriberDialog = false
            mSubscriberDialog!!.dismiss()
            /*Added null check condition for mPublisher object to avoid the Crash*/if (mPublisher != null) {
                mPublisher!!.publishAudio = true
            }
            val builder = AlertDialog.Builder(this@JoinVideoActivity)
            builder.setMessage("Patient has been re-connected")
                .setPositiveButton(android.R.string.ok, null)
            builder.create()
            builder.show()
        }
    }

    companion object {
        private var API_KEY = ""
        private var SESSION_ID = ""
        private var TOKEN = ""
        private const val PERMISSION_CALLBACK_CONSTANT = 100
        private const val REQUEST_PERMISSION_SETTING = 101
        var isVideoStart = false
    }
}