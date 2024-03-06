package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.whitecoats.clinicplus.R

class MyCustomAlertDialogBox(context: Context?) : AlertDialog(context) {
    private lateinit var tvTitle: TextView
    private lateinit var tvDesc: TextView
    private lateinit var btnLeft: Button
    private lateinit var btnRight: Button
    private lateinit var btnMiddle: Button
    private lateinit var layoutTwoButtons: LinearLayout
    private lateinit var layoutOneButton: LinearLayout
    private lateinit var alertIcon: ImageView
    private var dialogActionListener: DialogActionListener? = null
    @SuppressLint("InflateParams")
    private fun initialize() {
        val view = LayoutInflater.from(context).inflate(R.layout.my_custom_alert_layout, null)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvTitle = view.findViewById(R.id.tv_title)
        tvDesc = view.findViewById(R.id.tv_desc)
        alertIcon = view.findViewById(R.id.alert_icon)
        btnLeft = view.findViewById(R.id.btn_left)
        btnRight = view.findViewById(R.id.btn_right)
        layoutTwoButtons = view.findViewById(R.id.layout_two_button)
        setClickListener(btnLeft, btnRight)
        setView(view)
    }

    private fun setClickListener(vararg views: View) {
        for (view in views) {
            view.setOnClickListener(mOnClickListener)
        }
    }

    private val mOnClickListener = View.OnClickListener { v -> dialogActionListener!!.onAction(v) }

    init {
        initialize()
    }

    fun setOnActionListener(dialogActionListener: DialogActionListener?) {
        this.dialogActionListener = dialogActionListener
    }

    fun setAlertTitle(title: String?) {
        if (title != null) {
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = title
        }
    }

    fun setAlertDesciption(desciption: String?) {
        tvDesc.visibility = View.VISIBLE
        tvDesc.text = desciption
    }

    fun setRightButtonText(text: String?) {
        layoutTwoButtons.visibility = View.VISIBLE
        layoutOneButton.visibility = View.GONE
        btnRight.visibility = View.VISIBLE
        btnRight.text = text
    }

    fun setLeftButtonText(text: String?) {
        layoutTwoButtons.visibility = View.VISIBLE
        layoutOneButton.visibility = View.GONE
        btnLeft.visibility = View.VISIBLE
        btnLeft.text = text
    }

    fun setMiddleButtonText(text: String?) {
        layoutTwoButtons.visibility = View.GONE
        layoutOneButton.visibility = View.VISIBLE
        btnMiddle.text = text
    }

    fun setAlertIcon(drawable: Drawable?) {
        alertIcon.visibility = View.VISIBLE
        alertIcon.background = drawable
    }

    interface DialogActionListener {
        fun onAction(viewId: View?)
    }
}