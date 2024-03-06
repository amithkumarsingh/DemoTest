package com.whitecoats.clinicplus.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.activities.MyCustomAlertDialogBox
import com.whitecoats.clinicplus.activities.MyCustomAlertDialogBox.DialogActionListener
import com.whitecoats.clinicplus.onboarding.OnBoardingActivity
import org.json.JSONObject

object ErrorHandlerClass {
    fun errorHandler(context: Context, error: String) {
        try {
            val resObj = JSONObject(error)
            if (resObj.has("status_code")) {
                if (resObj.getString("status_code").equals(
                        ErrorCode.FourEightyNine.getError().toString(),
                        ignoreCase = true
                    )
                ) {
                    val myAlertDialog = MyCustomAlertDialogBox(context)
                    myAlertDialog.setAlertDesciption(resObj.getString("message"))
                    myAlertDialog.setCancelable(false)
                    myAlertDialog.setOnActionListener(object : DialogActionListener {
                        override fun onAction(viewId: View?) {
                            when (viewId!!.id) {
                                R.id.btn_left -> {
                                    ClearLoginData().clearUserData(context,"accountDelete")
                                    val i = Intent(
                                        context,
                                        OnBoardingActivity::class.java
                                    )
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    context.startActivity(i)
                                }
                            }

                        }
                    })
                    myAlertDialog.show()
                } else if (resObj.getString("status_code")
                        .equals(ErrorCode.FourNotOne.getError().toString(), ignoreCase = true) &&
                    resObj.getString("message").equals("Token has expired", ignoreCase = true)
                ) {
                    val myAlertDialog = MyCustomAlertDialogBox(context)
                    myAlertDialog.setAlertTitle("Session Expired")
                    myAlertDialog.setAlertDesciption("Your session has been expired, Please login again.")
                    myAlertDialog.setCancelable(false)
                    myAlertDialog.setOnActionListener(object : DialogActionListener {
                        override fun onAction(viewId: View?) {
                            when (viewId!!.id) {
                                R.id.btn_left -> {
                                    ClearLoginData().clearUserData(context,"tokenExpired")
                                    val i = Intent(context, LoginActivity::class.java)
                                    context.startActivity(i)
                                    (context as Activity).finish()
                                }
                            }

                        }
                    })
                    myAlertDialog.show()
                } else {
                    if (resObj.has("message")) {
                        displayMessage(resObj.getString("message"), context)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            displayMessage(error, context)
        }
    }

    fun displayMessage(toastString: String?, context: Context) {
        Toast.makeText(context, toastString, Toast.LENGTH_LONG).show()
    }

}