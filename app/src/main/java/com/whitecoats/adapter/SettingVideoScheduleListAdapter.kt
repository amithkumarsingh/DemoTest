package com.whitecoats.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.ScheduleItemClickListener
import com.whitecoats.clinicplus.SettingsFormActivity
import com.whitecoats.model.SettingVideoScheduleModel

class SettingVideoScheduleListAdapter(
    mContext: Context,
    settingVideoSchedModelListObj: List<SettingVideoScheduleModel>,
    scheduleItemClickLitner: ScheduleItemClickListener
) : RecyclerView.Adapter<SettingVideoScheduleListAdapter.MyViewHolder>() {
    // private val settingVideoSchedModelListRes: List<SettingVideoScheduleModel?>
    private val mContext: Context
    private val scheduleItemClickLitner: ScheduleItemClickListener
    private val appUtilities: AppUtilities

    //    private SettingServiceViewItemClickListener serviceListener;
    init {
        // this.settingVideoSchedModelListRes = settingVideoSchedModelListObj
        this.mContext = mContext
        this.scheduleItemClickLitner = scheduleItemClickLitner
        appUtilities = AppUtilities()
        settingVideoSchedModelList = settingVideoSchedModelListObj
        //        this.serviceListener = serviceListener;
    }

    companion object {
        var settingVideoSchedModelList: List<SettingVideoScheduleModel> = ArrayList()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_setting_video_schedule, viewGroup, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        myViewHolder: MyViewHolder,
        @SuppressLint("RecyclerView") i: Int
    ) {
        if (i == 0) {
            val spanString = SpannableString(settingVideoSchedModelList[i]!!.dayName)
            spanString.setSpan(UnderlineSpan(), 0, spanString.length, 0)
            spanString.setSpan(StyleSpan(Typeface.BOLD), 0, spanString.length, 0)
            spanString.setSpan(StyleSpan(Typeface.ITALIC), 0, spanString.length, 0)
            //text.setText(spanString);
            myViewHolder.dayText.text = spanString

            myViewHolder.dayText.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.colorPrimary
                )
            )
            myViewHolder.slotOneText.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.colorPrimary
                )
            )
            myViewHolder.slotOneText.text = settingVideoSchedModelList[i]?.slotOneTime
            myViewHolder.slotTwoText.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.colorPrimary
                )
            )
            myViewHolder.slotTwoText.text = settingVideoSchedModelList[i]?.slotTwoTime
        } else {
            myViewHolder.dayText.text = settingVideoSchedModelList[i]?.dayName
            myViewHolder.slotOneText.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.colorSuccess
                )
            )
            if (settingVideoSchedModelList[i]?.slotOneTime.equals(
                    "00:00:00-00:00:00",
                    ignoreCase = true
                ) || settingVideoSchedModelList[i]?.slotOneTime.equals(
                    "",
                    ignoreCase = true
                )
            ) {
                myViewHolder.slotOneText.text = "----"
            } else {
                val currentString = settingVideoSchedModelList[i]?.slotOneTime
                val separated =
                    currentString?.split("-".toRegex())?.dropLastWhile { it.isEmpty() }!!
                        .toTypedArray()
                var left = ""
                var right = ""
                if (separated[0].length > 5) {
                    if (appUtilities.timeFormatPreferences(mContext) == 12) {
                        left = appUtilities.changeDateFormat(
                            "HH:mm",
                            "hh:mm a",
                            separated[0].substring(0, separated[0].length - 3)
                        )
                    } else {
                        left = separated[0].substring(
                            0,
                            separated[0].length - 3
                        )
                    }
                } else {
                    if (appUtilities.timeFormatPreferences(mContext) == 12) {
                        left = appUtilities.changeDateFormat(
                            "HH:mm",
                            "hh:mm a",
                            separated[0].substring(0, separated[0].length)
                        )
                        //                        }
                    } else {
                        left = separated[0].substring(0, separated[0].length)
                    }
                }
                /*Added the condition to check the arraylist size to avoid the ArrayIndexOutOfBound Exception*/if (separated.size > 1) {
                    if (separated[1].length > 5) {
                        if (appUtilities.timeFormatPreferences(mContext) == 12) {
                            right = appUtilities.changeDateFormat(
                                "HH:mm",
                                "hh:mm a",
                                separated[1].substring(0, separated[1].length - 3)
                            )
                        } else {
                            right = separated[1].substring(
                                0,
                                separated[1].length - 3
                            )
                        }
                    } else {
                        if (appUtilities.timeFormatPreferences(mContext) == 12) {
                            right = appUtilities.changeDateFormat(
                                "HH:mm",
                                "hh:mm a",
                                separated[1].substring(0, separated[1].length)
                            )
                        } else {
                            right = separated[1].substring(0, separated[1].length)
                        }
                    }
                }
                myViewHolder.slotOneText.text = "$left-$right"
            }
            myViewHolder.slotTwoText.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.colorSuccess
                )
            )
            if (settingVideoSchedModelList[i]?.slotTwoTime.toString()
                    .equals(
                        "00:00:00-00:00:00",
                        ignoreCase = true
                    ) || settingVideoSchedModelList[i]?.slotTwoTime.toString()
                    .equals("", ignoreCase = true)
            ) {
                myViewHolder.slotTwoText.text = "----"
            } else {
                val currentString = settingVideoSchedModelList[i]?.slotTwoTime
                val separated =
                    currentString?.split("-".toRegex())?.dropLastWhile { it.isEmpty() }!!
                        .toTypedArray()
                var left = ""
                var right = ""
                if (separated[0].length > 5) {
                    if (appUtilities.timeFormatPreferences(mContext) == 12) {

                        left = appUtilities.changeDateFormat(
                            "HH:mm",
                            "hh:mm a",
                            separated[0].substring(0, separated[0].length - 3)
                        )
                    } else {
                        left = separated[0].substring(
                            0,
                            separated[0].length - 3
                        )
                    }
                } else {
                    if (appUtilities.timeFormatPreferences(mContext) == 12) {

                        left = appUtilities.changeDateFormat(
                            "HH:mm",
                            "hh:mm a",
                            separated[0].substring(0, separated[0].length)
                        )
                    } else {
                        left = separated[0].substring(0, separated[0].length)
                    }
                }
                /*Added the condition to check the arraylist size to avoid the ArrayIndexOutOfBound Exception*/if (separated.size > 1) {
                    if (separated[1].length > 5) {
                        if (appUtilities.timeFormatPreferences(mContext) == 12) {
                            right = appUtilities.changeDateFormat(
                                "HH:mm",
                                "hh:mm a",
                                separated[1].substring(0, separated[1].length - 3)
                            )
                        } else {
                            right = separated[1].substring(
                                0,
                                separated[1].length - 3
                            )
                        }
                    } else {
                        if (appUtilities.timeFormatPreferences(mContext) == 12) {

                            right = appUtilities.changeDateFormat(
                                "HH:mm",
                                "hh:mm a",
                                separated[1].substring(0, separated[1].length)
                            )
                        } else {
                            right = separated[1].substring(0, separated[1].length)
                        }
                    }
                }

                myViewHolder.slotTwoText.text = "$left-$right"
                Log.d("timeSlotLeftRight", "timeSlotLeftRight$left-$right")
            }
        }
        myViewHolder.serviceCardViewLayout.setOnClickListener {
            //Toast.makeText(mContext,"clicked on card:"+i,Toast.LENGTH_LONG).show();
            // scheduleItemClickLitner.onItemClick(v, i);
        }
        myViewHolder.dayLayout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                if (i == 0) {
                    if (SettingsFormActivity.videoSetupFlagClick == 1) {
                        scheduleItemClickLitner.onItemClick(
                            view,
                            SettingsFormActivity.videoSetupFlagClick
                        )
                    } else if (SettingsFormActivity.clinicSetupFlagClick == 1) {
                        scheduleItemClickLitner.onItemClick(
                            view,
                            SettingsFormActivity.clinicSetupFlagClick
                        )
                    } else {
                        scheduleItemClickLitner.onItemClick(view, i)
                    }
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return settingVideoSchedModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceCardViewLayout: CardView
        val dayText: TextView
        val slotOneText: TextView
        val slotTwoText: TextView
        val dayLayout: RelativeLayout

        init {
            serviceCardViewLayout = itemView.findViewById(R.id.serviceCardViewLayout)
            dayText = itemView.findViewById(R.id.dayText)
            slotOneText = itemView.findViewById(R.id.slotOneText)
            slotTwoText = itemView.findViewById(R.id.slotTwoText)
            dayLayout = itemView.findViewById(R.id.dayLayout)
        }
    }


}