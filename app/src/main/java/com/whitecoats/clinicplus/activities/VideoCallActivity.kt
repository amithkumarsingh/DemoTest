package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.JoinVideoActivity
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.viewmodels.AppointmentViewModel
import com.whitecoats.clinicplus.viewmodels.VideoCallViewModel

class VideoCallActivity : AppCompatActivity() {
    private var patientName: TextView? = null
    private var emptyText: TextView? = null
    private var pickUp: Button? = null
    private var ignoreCall: Button? = null
    private var closeBtn: Button? = null
    private var mediaPlayer: MediaPlayer? = null
    private var startTime = 0L
    private var customHandler: Handler? = null
    private var sharedpreferences: SharedPreferences? = null
    private var videoCallViewModel: VideoCallViewModel? = null
    private var appointmentViewModel: AppointmentViewModel? = null
    private var apptID = 0
    private var patientID = 0
    private lateinit var dialog:Dialog
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)
        apptID = intent.getIntExtra("AppointmentId", 0)
        patientID = intent.getIntExtra("PatientID", 0)
        initView()
        pickUp!!.setOnClickListener {
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
            }
            customHandler!!.removeCallbacks(updateTimerThread)
            videoCallViewModel!!.clearPatientData()
            appointmentViewModel!!.clearStatus()
            if (AppUtilities.isOnline(this)) {
                val intent = Intent(this, VideoScreenActivity::class.java)
                intent.putExtra("AppointmentId", apptID)
                startActivity(intent)
                unregisterReceiver(receiver)
                setPreference(false)
                finish()
            } else {
                showAlert("No Internet", "Please check your internet connection", 2)
            }
        }
        ignoreCall!!.setOnClickListener {
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
            }
            customHandler!!.removeCallbacks(updateTimerThread)
            videoCallViewModel!!.clearPatientData()
            appointmentViewModel!!.clearStatus()
            unregisterReceiver(receiver)
            setPreference(false)
            finish()
        }
        closeBtn!!.setOnClickListener {
            videoCallViewModel!!.clearPatientData()
            appointmentViewModel!!.clearStatus()
            customHandler!!.removeCallbacks(updateTimerThread)
            unregisterReceiver(receiver)
            setPreference(false)
            finish()
        }
        showCustomProgressAlertDialog("","Fetching Data")
        appointmentViewModel!!.apptStatus(this, apptID)
        appointmentViewModel!!.apptStatus.observe(this
        ) { integer ->
            Log.d("Interger Value", integer.toString() + "")
            if (integer != null) {
                if (integer <= 2) {
                    startTime = SystemClock.uptimeMillis()
                    customHandler!!.postDelayed(updateTimerThread, 0)
                    videoCallViewModel!!.getPatientData(this@VideoCallActivity, patientID)
                } else {
                    dialog.dismiss()
                    showAlert("Appointment Invalid", "Appointment is not valid for now", 1)
                }
            }
        }
        videoCallViewModel!!.patientData.observe(this
        ) { patientModel ->
            Log.d("Patient Data", patientModel.toString() + "")
            dialog.dismiss()
            if (patientModel != null) {
                emptyText!!.visibility = View.VISIBLE
                if (patientModel.status != 200) {
                    emptyText!!.text = "Some error has occurred on the server side"
                    if (mediaPlayer != null) {
                        mediaPlayer!!.stop()
                    }
                    customHandler!!.removeCallbacks(updateTimerThread)
                    pickUp!!.visibility = View.GONE
                    ignoreCall!!.visibility = View.GONE
                    closeBtn!!.visibility = View.VISIBLE
                } else {
                    patientName!!.text = patientModel.patientDetails.fname
                }
            }
        }
        val filter = IntentFilter()
        filter.addAction("EndCallingVideo")
        registerReceiver(receiver, filter)
    }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //remove the notification after opening the activity
            val notificationManager =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initView() {
        patientName = findViewById(R.id.patientName)
        pickUp = findViewById(R.id.videoPickUp)
        ignoreCall = findViewById(R.id.videoIgnore)
        emptyText = findViewById(R.id.emptyText)
        closeBtn = findViewById(R.id.onErrorClose)
        customHandler = Handler()
        sharedpreferences = getSharedPreferences(AppConstants.appSharedPref, MODE_PRIVATE)
        setPreference(true)
        videoCallViewModel = ViewModelProvider(this@VideoCallActivity)[VideoCallViewModel::class.java]
        videoCallViewModel!!.init()
        appointmentViewModel = ViewModelProvider(this@VideoCallActivity)[AppointmentViewModel::class.java]
        appointmentViewModel!!.init()

        //remove the notification after opening the activity
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        try {
            val alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(this, alert)
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_RING)
                mediaPlayer!!.isLooping = true
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
        }
        customHandler!!.removeCallbacks(updateTimerThread)
        videoCallViewModel!!.clearPatientData()
        appointmentViewModel!!.clearStatus()
        unregisterReceiver(receiver)
        setPreference(false)
        finish()
    }

    override fun onDestroy() {
        setPreference(false)
        super.onDestroy()
    }

    /**
     * a small timer of 30sec to show the video calling activity.
     * after which we end the activity with a missed call notification
     */
    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            val timeSwapBuff = 0L
            var updatedTime = 0L
            val timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            var secs = (updatedTime / 1000).toInt()
            secs %= 60
            Log.d("Seconds Past", secs.toString() + "")
            if (secs == 30) {
                Log.d("Timer Cancel", "***************")
                customHandler!!.removeCallbacks(this)
                if (mediaPlayer != null) {
                    mediaPlayer!!.stop()
                }
                setUpNotification()
                videoCallViewModel!!.clearPatientData()
                appointmentViewModel!!.clearStatus()
                unregisterReceiver(receiver)
                setPreference(false)
                finish()
            } else {
                Log.d("Timer Tick", "***************")
                customHandler!!.postDelayed(this, 1000)
            }
        }
    }

    /**
     * if the call is missed we show a notification to the doc
     */
    private fun setUpNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "MissedCalledChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "MissedCalledChannel",
                NotificationManager.IMPORTANCE_HIGH
            )

            // Configure the notification channel.
            notificationChannel.description = "Video Missed Call"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val intent = Intent(this, JoinVideoActivity::class.java)
        intent.putExtra("AppointmentId", apptID)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        //        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_missed_call)
            .setContentIntent(pendingIntent) //                .setTicker("Hearty365")
            //     .setPriority(Notification.PRIORITY_MAX)
            .setContentTitle(patientName!!.text.toString())
            .setContentText("You have a missed call")
        //                .setContentInfo("Info");
        notificationManager.notify( /*notification id*/1, notificationBuilder.build())
    }

    private fun showAlert(title: String, msg: String, type: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton("Ok") { dialog: DialogInterface, _: Int ->
            if (type == 1) {
                appointmentViewModel!!.clearStatus()
                dialog.dismiss()
                unregisterReceiver(receiver)
                setPreference(false)
                finish()
            } else {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun setPreference(state: Boolean) {
        val editor = sharedpreferences!!.edit()
        editor.putBoolean("InVideoCalling", state)
        editor.apply()
    }
    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = android.app.AlertDialog.Builder(this@VideoCallActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(this@VideoCallActivity)
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}