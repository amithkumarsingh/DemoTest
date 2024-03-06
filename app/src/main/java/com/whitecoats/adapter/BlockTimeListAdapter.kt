package com.whitecoats.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.SettingBlockTimeClickListener
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.model.BlockTimeListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONException
import org.json.JSONObject

class BlockTimeListAdapter(
    private val activity: Activity,
    private val blockTimeListModelList: List<BlockTimeListModel>?,
    private val settingBlockListener: SettingBlockTimeClickListener
) : RecyclerView.Adapter<BlockTimeListAdapter.MyViewHolder>() {
    var value: JSONObject? = null
    private val appPreference: SharedPreferences =
        activity.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_block_time, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, @SuppressLint("RecyclerView") i: Int) {
        val blockTimeListModel = blockTimeListModelList!![i]
        // String temp = "1 Jan, 2019 \n1:20";
        myViewHolder.startDate.text = blockTimeListModel.startDate
        myViewHolder.endDate.text = blockTimeListModel.endDate
        myViewHolder.startTime.text = blockTimeListModel.startTime
        myViewHolder.endTime.text = blockTimeListModel.endTime
        if (blockTimeListModel.serviceType == 0) {
            myViewHolder.serviceName.text = buildString {
        append("All")
    }
            myViewHolder.serviceIcon.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_all))
        }
        if (blockTimeListModel.serviceType == 1) {
            myViewHolder.serviceName.text = buildString { append("Video Service") }
            myViewHolder.serviceIcon.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_video))
        }
        if (blockTimeListModel.serviceType == 2) {
            myViewHolder.serviceName.text = buildString {
        append("Chat Service")
    }
            myViewHolder.serviceIcon.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_all))
        }
        if (blockTimeListModel.serviceType == 3) {
            myViewHolder.serviceName.text = buildString {
        append("Clinic Service")
    }
            myViewHolder.serviceIcon.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_clinic))
        }
        myViewHolder.timeBlockStatus.isChecked = blockTimeListModel.active
        myViewHolder.statusDelete.setOnClickListener { view ->
            try {
                value = JSONObject()
                value!!.put("id", blockTimeListModel.id)
            } catch (e: JSONException) {
                e.message
            }
            ZohoSalesIQ.Tracking.setPageTitle("Settings - Time Block - Delete Time Block")
            settingBlockListener.onItemClick(view, i, value, "DELETE")
        }
        myViewHolder.timeBlockStatus.setOnClickListener{ view ->
            if ((view as SwitchMaterial).isChecked) {
                try {
                    ZohoSalesIQ.Tracking.setPageTitle("Settings - Time Block - Made Active")
                    value = JSONObject()
                    value!!.put("id", blockTimeListModel.id)
                    value!!.put("status", 1)
                    value!!.put("active", true)
                } catch (e: JSONException) {
                    e.message
                }
            } else {
                try {
                    ZohoSalesIQ.Tracking.setPageTitle("Settings - Time Block - Made Off")
                    value = JSONObject()
                    value!!.put("id", blockTimeListModel.id)
                    value!!.put("status", 0)
                    value!!.put("active", false)
                } catch (e: JSONException) {
                    e.message
                }
            }
            settingBlockListener.onItemClick(view, i, value, "SWITCH")
        }
        myViewHolder.timeBlockStatus.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                myViewHolder.timeBlockStatus.viewTreeObserver.removeOnGlobalLayoutListener(this)
                showGuide(2, i, myViewHolder)
            }
        })
    }

    override fun getItemCount(): Int {
        return blockTimeListModelList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var startDate: TextView
        var endDate: TextView
        var serviceName: TextView
        var startTime: TextView
        var endTime: TextView
        var statusDelete: ImageView
        var timeBlockStatus: SwitchMaterial
        val dateLayout: LinearLayout
        val serviceIcon:ImageView

        init {
            startDate = itemView.findViewById(R.id.blockTimeStartDate)
            endDate = itemView.findViewById(R.id.blockTimeEndDate)
            serviceName = itemView.findViewById(R.id.blockTimeServiceName)
            statusDelete = itemView.findViewById(R.id.timeBlockStatusDelete)
            timeBlockStatus = itemView.findViewById(R.id.timeBlockStatusSwitch)
            startTime = itemView.findViewById(R.id.blockTimeStartTime)
            endTime = itemView.findViewById(R.id.blockTimeEndTime)
            dateLayout = itemView.findViewById(R.id.blockTimeDateLayout)
            serviceIcon=itemView.findViewById(R.id.serviceIcon)
        }
    }

    private fun showGuide(section: Int, position: Int, myViewHolder: MyViewHolder) {
        if (position == 0) {
            when (section) {
                1 -> if (!appPreference.getBoolean("TimeBlock", false)) {
                    MaterialIntroView.Builder(activity)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Here is the name of the service for which you have block all your timings")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(myViewHolder.serviceName)
                        .setUsageId("intro_block_serviceName") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                            showGuide(2, position, myViewHolder)
                            val editor = appPreference.edit()
                            editor.putBoolean("TimeBlock", true)
                            editor.apply()
                        }
                        .show()
                }
                2 -> MaterialIntroView.Builder(activity)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("Easily toggle to activate or deactivate your time blocks")
                    .setShape(ShapeType.CIRCLE)
                    .setTarget(myViewHolder.timeBlockStatus)
                    .setUsageId("intro_block_toggle") //THIS SHOULD BE UNIQUE ID
                    .setListener { showGuide(4, position, myViewHolder) }
                    .show()
                3 -> MaterialIntroView.Builder(activity)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("See from what date till what date you have blocked all your timings for your service")
                    .setShape(ShapeType.RECTANGLE)
                    .setTarget(myViewHolder.dateLayout)
                    .setUsageId("intro_block_dateLayout") //THIS SHOULD BE UNIQUE ID
                    .setListener { }
                    .show()
                4 -> MaterialIntroView.Builder(activity)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("Delete the time block if no longer needed")
                    .setShape(ShapeType.CIRCLE)
                    .setTarget(myViewHolder.statusDelete)
                    .setUsageId("intro_block_statusDelete") //THIS SHOULD BE UNIQUE ID
                    .setListener { }
                    .show()
            }
        }
    }
}