package com.whitecoats.clinicplus.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiUrls

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private var i = 1
    private var appDatabaseManager: AppDatabaseManager? = null
    private lateinit var appPreference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding2)
        viewPager = findViewById(R.id.onBoardingViewPager)
        val next = findViewById<Button>(R.id.next)
        val skip = findViewById<Button>(R.id.skip)
        appDatabaseManager = AppDatabaseManager(this)
        appPreference = getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        val tabAdapter = OnBoardingTabAdapter(supportFragmentManager, 6)
        viewPager.adapter = tabAdapter
        viewPager.beginFakeDrag()
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId,
            getString(R.string.IntroScreen),
            null
        )
        next.setOnClickListener {
            i++
            if (i == 6) {
                val editor = appPreference.edit()
                editor.putBoolean("OnBoardingDone", true)
                editor.apply()
                val appUserManagers = appDatabaseManager!!.userData
                if (appUserManagers.size > 0) {
                    ApiUrls.loginToken = appUserManagers[0].token
                    if (ApiUrls.loginToken == null || ApiUrls.loginToken == "") {
                        val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val i = Intent(this@OnBoardingActivity, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                } else {
                    val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                viewPager.currentItem = i
            }
        }
        skip.setOnClickListener {
            val editor = appPreference.edit()
            editor.putBoolean("OnBoardingDone", true)
            editor.apply()
            val appUserManagers = appDatabaseManager!!.userData
            if (appUserManagers.size > 0) {
                ApiUrls.loginToken = appUserManagers[0].token
                if (ApiUrls.loginToken == null || ApiUrls.loginToken == "") {
                    val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val i = Intent(this@OnBoardingActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
            } else {
                val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}