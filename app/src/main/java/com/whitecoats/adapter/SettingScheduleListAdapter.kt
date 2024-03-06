package com.whitecoats.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.model.SettingScheduleModel
import java.util.*

class SettingScheduleListAdapter(
    private val mContext: Context,
    private val settingSchedModelList: List<SettingScheduleModel>
) : RecyclerView.Adapter<SettingScheduleListAdapter.MyViewHolder>() {
    // Initializing a timeString as 24 hours format Array
    private val spinnerTime = arrayOf(
        "Select",
        "00:00",
        "00:30",
        "01:00",
        "01:30",
        "02:00",
        "02:30",
        "03:00",
        "03:30",
        "04:00",
        "04:30",
        "05:00",
        "05:30",
        "06:00",
        "06:30",
        "07:00",
        "07:30",
        "08:00",
        "08:30",
        "09:00",
        "09:30",
        "10:00",
        "10:30",
        "11:00",
        "11:30",
        "12:00",
        "12:30",
        "13:00",
        "13:30",
        "14:00",
        "14:30",
        "15:00",
        "15:30",
        "16:00",
        "16:30",
        "17:00",
        "17:30",
        "18:00",
        "18:30",
        "19:00",
        "19:30",
        "20:00",
        "20:30",
        "21:00",
        "21:30",
        "22:00",
        "22:30",
        "23:00",
        "23:30"
    )

    // Initializing a 12 hours timeString Array
    private val spinner12HoursTime = arrayOf(
        "Select",
        "12:00 AM",
        "12:30 AM",
        "01:00 AM",
        "01:30 AM",
        "02:00 AM",
        "02:30 AM",
        "03:00 AM",
        "03:30 AM",
        "04:00 AM",
        "04:30 AM",
        "05:00 AM",
        "05:30 AM",
        "06:00 AM",
        "06:30 AM",
        "07:00 AM",
        "07:30 AM",
        "08:00 AM",
        "08:30 AM",
        "09:00 AM",
        "09:30 AM",
        "10:00 AM",
        "10:30 AM",
        "11:00 AM",
        "11:30 AM",
        "12:00 PM",
        "12:30 PM",
        "01:00 PM",
        "01:30 PM",
        "02:00 PM",
        "02:30 PM",
        "03:00 PM",
        "03:30 PM",
        "04:00 PM",
        "04:30 PM",
        "05:00 PM",
        "05:30 PM",
        "06:00 PM",
        "06:30 PM",
        "07:00 PM",
        "07:30 PM",
        "08:00 PM",
        "08:30 PM",
        "09:00 PM",
        "09:30 PM",
        "10:00 PM",
        "10:30 PM",
        "11:00 PM",
        "11:30 PM"
    )
    private var spinnerTimeList: List<String>
    private val spinner12HoursTimeList: List<String>
    var myArrayListVideoMonday = IntArray(4)
    var myArrayListVideoTuesday = IntArray(4)
    var myArrayListVideoWednesday = IntArray(4)
    var myArrayListVideoThursday = IntArray(4)
    var myArrayListVideoFriday = IntArray(4)
    var myArrayListVideoSaturday = IntArray(4)
    var myArrayListVideoSunday = IntArray(4)
    var spinnerOnePosition = -1
    var spinnerTwoPosition = -1
    private var prefs: SharedPreferences
    private val appUtilities: AppUtilities

    init {
        spinnerTimeList = ArrayList(listOf(*spinnerTime))
        spinner12HoursTimeList = ArrayList(listOf(*spinner12HoursTime))
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        appUtilities = AppUtilities()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_row_setting_schedule, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        myViewHolder: MyViewHolder,
        @SuppressLint("RecyclerView") positionList: Int
    ) {
        myViewHolder.daysText.text = settingSchedModelList[positionList].daysName
        if (appUtilities.timeFormatPreferences(mContext) == 12) {
            spinnerTimeList = spinner12HoursTimeList
        }
        val slotOneFromSpinnerArrayAdapter = ArrayAdapter(
            mContext, android.R.layout.simple_spinner_item, spinnerTimeList
        )
        slotOneFromSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        myViewHolder.slotOneFromSpinner.adapter = slotOneFromSpinnerArrayAdapter
        val slotOneToSpinnerArrayAdapter = ArrayAdapter(
            mContext, android.R.layout.simple_spinner_item, spinnerTimeList
        )
        slotOneToSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        myViewHolder.slotOneToSpinner.adapter = slotOneToSpinnerArrayAdapter
        val slotTwoFromSpinnerArrayAdapter = ArrayAdapter(
            mContext, android.R.layout.simple_spinner_item, spinnerTimeList
        )
        slotTwoFromSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        myViewHolder.slotTwoFromSpinner.adapter = slotTwoFromSpinnerArrayAdapter
        val slotTwoToSpinnerArrayAdapter = ArrayAdapter(
            mContext, android.R.layout.simple_spinner_item, spinnerTimeList
        )
        slotTwoToSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        myViewHolder.slotTwoToSpinner.adapter = slotTwoToSpinnerArrayAdapter
        for (i in spinnerTimeList.indices) {
            var getSlotOneStartString: String
            var getSlotOneEndString: String
            var getSlotTwoStartString: String
            var getSlotTwoEndString: String
            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                getSlotOneStartString = if (settingSchedModelList[positionList].slotOneStart.equals(
                        "00:00:00",
                        ignoreCase = true
                    )
                ) {
                    "12:00 AM"
                } else if (settingSchedModelList[positionList].slotOneStart.equals(
                        "00:30:00",
                        ignoreCase = true
                    )
                ) {
                    "12:30 AM"
                } else {
                    appUtilities.changeDateFormat(
                        "HH:mm:ss",
                        "hh:mm a",
                        settingSchedModelList[positionList].slotOneStart
                    )
                }
                getSlotOneEndString = if (settingSchedModelList[positionList].slotOneEnd.equals(
                        "00:00:00",
                        ignoreCase = true
                    )
                ) {
                    "12:00 AM"
                } else if (settingSchedModelList[positionList].slotOneEnd.equals(
                        "00:30:00",
                        ignoreCase = true
                    )
                ) {
                    "12:30 AM"
                } else {
                    appUtilities.changeDateFormat(
                        "HH:mm:ss",
                        "hh:mm a",
                        settingSchedModelList[positionList].slotOneEnd
                    )
                }
                getSlotTwoStartString = if (settingSchedModelList[positionList].slotTwoStart.equals(
                        "00:00:00",
                        ignoreCase = true
                    )
                ) {
                    "12:00 AM"
                } else if (settingSchedModelList[positionList].slotTwoStart.equals(
                        "00:30:00",
                        ignoreCase = true
                    )
                ) {
                    "12:30 AM"
                } else {
                    appUtilities.changeDateFormat(
                        "HH:mm:ss",
                        "hh:mm a",
                        settingSchedModelList[positionList].slotTwoStart
                    )
                }
                getSlotTwoEndString = if (settingSchedModelList[positionList].slotTwoEnd.equals(
                        "00:00:00",
                        ignoreCase = true
                    )
                ) {
                    "12:00 AM"
                } else if (settingSchedModelList[positionList].slotTwoEnd.equals(
                        "00:30:00",
                        ignoreCase = true
                    )
                ) {
                    "12:30 AM"
                } else {
                    appUtilities.changeDateFormat(
                        "HH:mm:ss",
                        "hh:mm a",
                        settingSchedModelList[positionList].slotTwoEnd
                    )
                }
            } else {
                getSlotOneStartString = settingSchedModelList[positionList].slotOneStart
                getSlotOneEndString = settingSchedModelList[positionList].slotOneEnd
                getSlotTwoStartString = settingSchedModelList[positionList].slotTwoStart
                getSlotTwoEndString = settingSchedModelList[positionList].slotTwoEnd
            }
            if (getSlotOneStartString.contains(spinnerTimeList[i])) {
                if (i == 1) {
                    if (getSlotOneStartString.equals(getSlotOneEndString, ignoreCase = true)) {
                        if (positionList == 0) {
                            if (myArrayListVideoMonday[0] == 1) {
                                myViewHolder.slotOneFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotOneFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 1) {
                            if (myArrayListVideoTuesday[0] == 1) {
                                myViewHolder.slotOneFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotOneFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 2) {
                            if (myArrayListVideoWednesday[0] == 1) {
                                myViewHolder.slotOneFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotOneFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 3) {
                            if (myArrayListVideoThursday[0] == 1) {
                                myViewHolder.slotOneFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotOneFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 4) {
                            if (myArrayListVideoFriday[0] == 1) {
                                myViewHolder.slotOneFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotOneFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 5) {
                            if (myArrayListVideoSaturday[0] == 1) {
                                myViewHolder.slotOneFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotOneFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 6) {
                            if (myArrayListVideoSunday[0] == 1) {
                                myViewHolder.slotOneFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotOneFromSpinner.setSelection(0)
                            }
                        }
                    } else {
                        myViewHolder.slotOneFromSpinner.setSelection(i)
                    }
                } else {
                    myViewHolder.slotOneFromSpinner.setSelection(i)
                }
            }
            if (getSlotOneEndString.contains(spinnerTimeList[i])) {
                if (i == 1) {
                    if (positionList == 0) {
                        if (myArrayListVideoMonday[1] == 1) {
                            myViewHolder.slotOneToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotOneToSpinner.setSelection(0)
                        }
                    } else if (positionList == 1) {
                        if (myArrayListVideoTuesday[1] == 1) {
                            myViewHolder.slotOneToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotOneToSpinner.setSelection(0)
                        }
                    } else if (positionList == 2) {
                        if (myArrayListVideoWednesday[1] == 1) {
                            myViewHolder.slotOneToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotOneToSpinner.setSelection(0)
                        }
                    } else if (positionList == 3) {
                        if (myArrayListVideoThursday[1] == 1) {
                            myViewHolder.slotOneToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotOneToSpinner.setSelection(0)
                        }
                    } else if (positionList == 4) {
                        if (myArrayListVideoFriday[1] == 1) {
                            myViewHolder.slotOneToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotOneToSpinner.setSelection(0)
                        }
                    } else if (positionList == 5) {
                        if (myArrayListVideoSaturday[1] == 1) {
                            myViewHolder.slotOneToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotOneToSpinner.setSelection(0)
                        }
                    } else if (positionList == 6) {
                        if (myArrayListVideoSunday[1] == 1) {
                            myViewHolder.slotOneToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotOneToSpinner.setSelection(0)
                        }
                    }
                } else {
                    myViewHolder.slotOneToSpinner.setSelection(i)
                }
            }
            if (getSlotTwoStartString.contains(spinnerTimeList[i])) {
                if (i == 1) {
                    if (getSlotTwoStartString.equals(getSlotTwoEndString, ignoreCase = true)) {
                        if (positionList == 0) {
                            if (myArrayListVideoMonday[2] == 1) {
                                myViewHolder.slotTwoFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotTwoFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 1) {
                            if (myArrayListVideoTuesday[2] == 1) {
                                myViewHolder.slotTwoFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotTwoFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 2) {
                            if (myArrayListVideoWednesday[2] == 1) {
                                myViewHolder.slotTwoFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotTwoFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 3) {
                            if (myArrayListVideoThursday[2] == 1) {
                                myViewHolder.slotTwoFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotTwoFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 4) {
                            if (myArrayListVideoFriday[2] == 1) {
                                myViewHolder.slotTwoFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotTwoFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 5) {
                            if (myArrayListVideoSaturday[2] == 1) {
                                myViewHolder.slotTwoFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotTwoFromSpinner.setSelection(0)
                            }
                        } else if (positionList == 6) {
                            if (myArrayListVideoSunday[2] == 1) {
                                myViewHolder.slotTwoFromSpinner.setSelection(1)
                            } else {
                                myViewHolder.slotTwoFromSpinner.setSelection(0)
                            }
                        }
                    } else {
                        myViewHolder.slotTwoFromSpinner.setSelection(i)
                    }
                } else {
                    myViewHolder.slotTwoFromSpinner.setSelection(i)
                }
            }
            if (getSlotTwoEndString.contains(spinnerTimeList[i])) {
                if (i == 1) {
                    if (positionList == 0) {
                        if (myArrayListVideoMonday[3] == 1) {
                            myViewHolder.slotTwoToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotTwoToSpinner.setSelection(0)
                        }
                    } else if (positionList == 1) {
                        if (myArrayListVideoTuesday[3] == 1) {
                            myViewHolder.slotTwoToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotTwoToSpinner.setSelection(0)
                        }
                    } else if (positionList == 2) {
                        if (myArrayListVideoWednesday[3] == 1) {
                            myViewHolder.slotTwoToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotTwoToSpinner.setSelection(0)
                        }
                    } else if (positionList == 3) {
                        if (myArrayListVideoThursday[3] == 1) {
                            myViewHolder.slotTwoToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotTwoToSpinner.setSelection(0)
                        }
                    } else if (positionList == 4) {
                        if (myArrayListVideoFriday[3] == 1) {
                            myViewHolder.slotTwoToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotTwoToSpinner.setSelection(0)
                        }
                    } else if (positionList == 5) {
                        if (myArrayListVideoSaturday[3] == 1) {
                            myViewHolder.slotTwoToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotTwoToSpinner.setSelection(0)
                        }
                    } else if (positionList == 6) {
                        if (myArrayListVideoSunday[3] == 1) {
                            myViewHolder.slotTwoToSpinner.setSelection(1)
                        } else {
                            myViewHolder.slotTwoToSpinner.setSelection(0)
                        }
                    }
                } else {
                    myViewHolder.slotTwoToSpinner.setSelection(i)
                }
            }
        }
        myViewHolder.slotOneFromSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedTextSpinnerOne = spinnerTimeList[position]
                    spinnerOnePosition = position
                    if (positionList == 0) {
                        /*Android : video nd Clinic time validation is not happening*/
                        if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayMonday[0] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayMonday[0] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerOne
                                )
                            } else {
                                myArrayMonday[0] = selectedTextSpinnerOne
                            }
                        }
                        if (selectedTextSpinnerOne.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoMonday[0] = 1
                        } else if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayListVideoMonday[0] = 0
                        }
                    }
                    if (positionList == 1) {
                        if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayTuesday[0] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayTuesday[0] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerOne
                                )
                            } else {
                                myArrayTuesday[0] = selectedTextSpinnerOne
                            }
                        }
                        if (selectedTextSpinnerOne.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoTuesday[0] = 1
                        } else if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayListVideoTuesday[0] = 0
                        }
                    }
                    if (positionList == 2) {
                        if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayWednesday[0] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayWednesday[0] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerOne
                                )
                            } else {
                                myArrayWednesday[0] = selectedTextSpinnerOne
                            }
                        }
                        if (selectedTextSpinnerOne.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoWednesday[0] = 1
                        } else if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayListVideoWednesday[0] = 0
                        }
                    }
                    if (positionList == 3) {
                        if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayThursday[0] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayThursday[0] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerOne
                                )
                            } else {
                                myArrayThursday[0] = selectedTextSpinnerOne
                            }
                        }
                        if (selectedTextSpinnerOne.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoThursday[0] = 1
                        } else if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayListVideoThursday[0] = 0
                        }
                    }
                    if (positionList == 4) {
                        if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayFriday[0] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayFriday[0] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerOne
                                )
                            } else {
                                myArrayFriday[0] = selectedTextSpinnerOne
                            }
                        }
                        if (selectedTextSpinnerOne.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoFriday[0] = 1
                        } else if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayListVideoFriday[0] = 0
                        }
                    }
                    if (positionList == 5) {
                        if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArraySaturday[0] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArraySaturday[0] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerOne
                                )
                            } else {
                                myArraySaturday[0] = selectedTextSpinnerOne
                            }
                        }
                        if (selectedTextSpinnerOne.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoSaturday[0] = 1
                        } else if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayListVideoSaturday[0] = 0
                        }
                    }
                    if (positionList == 6) {
                        if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArraySunday[0] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArraySunday[0] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerOne
                                )
                            } else {
                                myArraySunday[0] = selectedTextSpinnerOne
                            }
                        }
                        if (selectedTextSpinnerOne.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoSunday[0] = 1
                        } else if (selectedTextSpinnerOne.equals("select", ignoreCase = true)) {
                            myArrayListVideoSunday[0] = 0
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        myViewHolder.slotOneToSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedTextSpinnerTwo = spinnerTimeList[position]
                    spinnerTwoPosition = position
                    if (positionList == 0) {
                        if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayMonday[1] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayMonday[1] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerTwo
                                )
                            } else {
                                myArrayMonday[1] = selectedTextSpinnerTwo
                            }
                        }
                        if (selectedTextSpinnerTwo.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoMonday[1] = 1
                        } else if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayListVideoMonday[1] = 0
                        }
                    }
                    if (positionList == 1) {
                        if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayTuesday[1] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayTuesday[1] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerTwo
                                )
                            } else {
                                myArrayTuesday[1] = selectedTextSpinnerTwo
                            }
                        }
                        if (selectedTextSpinnerTwo.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoTuesday[1] = 1
                        } else if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayListVideoTuesday[1] = 0
                        }
                    }
                    if (positionList == 2) {
                        if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayWednesday[1] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayWednesday[1] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerTwo
                                )
                            } else {
                                myArrayWednesday[1] = selectedTextSpinnerTwo
                            }
                        }
                        if (selectedTextSpinnerTwo.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoWednesday[1] = 1
                        } else if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayListVideoWednesday[1] = 0
                        }
                    }
                    if (positionList == 3) {
                        if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayThursday[1] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayThursday[1] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerTwo
                                )
                            } else {
                                myArrayThursday[1] = selectedTextSpinnerTwo
                            }
                        }
                        if (selectedTextSpinnerTwo.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoThursday[1] = 1
                        } else if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayListVideoThursday[1] = 0
                        }
                    }
                    if (positionList == 4) {
                        if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayFriday[1] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayFriday[1] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerTwo
                                )
                            } else {
                                myArrayFriday[1] = selectedTextSpinnerTwo
                            }
                        }
                        if (selectedTextSpinnerTwo.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoFriday[1] = 1
                        } else if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayListVideoFriday[1] = 0
                        }
                    }
                    if (positionList == 5) {
                        if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArraySaturday[1] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArraySaturday[1] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerTwo
                                )
                            } else {
                                myArraySaturday[1] = selectedTextSpinnerTwo
                            }
                        }
                        if (selectedTextSpinnerTwo.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoSaturday[1] = 1
                        } else if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayListVideoSaturday[1] = 0
                        }
                    }
                    if (positionList == 6) {
                        if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArraySunday[1] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArraySunday[1] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerTwo
                                )
                            } else {
                                myArraySunday[1] = selectedTextSpinnerTwo
                            }
                        }
                        if (selectedTextSpinnerTwo.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoSunday[1] = 1
                        } else if (selectedTextSpinnerTwo.equals("select", ignoreCase = true)) {
                            myArrayListVideoSunday[1] = 0
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        myViewHolder.slotTwoFromSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedTextSpinnerThree = spinnerTimeList[position]
                    if (positionList == 0) {
                        if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayMonday[2] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayMonday[2] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerThree
                                )
                            } else {
                                myArrayMonday[2] = selectedTextSpinnerThree
                            }
                        }
                        if (selectedTextSpinnerThree.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoMonday[2] = 1
                        } else if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayListVideoMonday[2] = 0
                        }
                    }
                    if (positionList == 1) {
                        if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayTuesday[2] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayTuesday[2] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerThree
                                )
                            } else {
                                myArrayTuesday[2] = selectedTextSpinnerThree
                            }
                        }
                        if (selectedTextSpinnerThree.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoTuesday[2] = 1
                        } else if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayListVideoTuesday[2] = 0
                        }
                    }
                    if (positionList == 2) {
                        if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayWednesday[2] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayWednesday[2] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerThree
                                )
                            } else {
                                myArrayWednesday[2] = selectedTextSpinnerThree
                            }
                        }
                        if (selectedTextSpinnerThree.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoWednesday[2] = 1
                        } else if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayListVideoWednesday[2] = 0
                        }
                    }
                    if (positionList == 3) {
                        if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayThursday[2] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayThursday[2] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerThree
                                )
                            } else {
                                myArrayThursday[2] = selectedTextSpinnerThree
                            }
                        }
                        if (selectedTextSpinnerThree.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoThursday[2] = 1
                        } else if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayListVideoThursday[2] = 0
                        }
                    }
                    if (positionList == 4) {
                        if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayFriday[2] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayFriday[2] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerThree
                                )
                            } else {
                                myArrayFriday[2] = selectedTextSpinnerThree
                            }
                        }
                        if (selectedTextSpinnerThree.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoFriday[2] = 1
                        } else if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayListVideoFriday[2] = 0
                        }
                    }
                    if (positionList == 5) {
                        if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArraySaturday[2] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArraySaturday[2] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerThree
                                )
                            } else {
                                myArraySaturday[2] = selectedTextSpinnerThree
                            }
                        }
                        if (selectedTextSpinnerThree.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoSaturday[2] = 1
                        } else if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayListVideoSaturday[2] = 0
                        }
                    }
                    if (positionList == 6) {
                        if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArraySunday[2] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArraySunday[2] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerThree
                                )
                            } else {
                                myArraySunday[2] = selectedTextSpinnerThree
                            }
                        }
                        if (selectedTextSpinnerThree.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoSunday[2] = 1
                        } else if (selectedTextSpinnerThree.equals("select", ignoreCase = true)) {
                            myArrayListVideoSunday[2] = 0
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        myViewHolder.slotTwoToSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedTextSpinnerFour = spinnerTimeList[position]
                    if (positionList == 0) {
                        if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayMonday[3] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayMonday[3] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerFour
                                )
                            } else {
                                myArrayMonday[3] = selectedTextSpinnerFour
                            }
                        }
                        if (selectedTextSpinnerFour.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoMonday[3] = 1
                        } else if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayListVideoMonday[3] = 0
                        }
                    }
                    if (positionList == 1) {
                        if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayTuesday[3] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayTuesday[3] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerFour
                                )
                            } else {
                                myArrayTuesday[3] = selectedTextSpinnerFour
                            }
                        }
                        if (selectedTextSpinnerFour.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoTuesday[3] = 1
                        } else if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayListVideoTuesday[3] = 0
                        }
                    }
                    if (positionList == 2) {
                        if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayWednesday[3] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayWednesday[3] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerFour
                                )
                            } else {
                                myArrayWednesday[3] = selectedTextSpinnerFour
                            }
                        }
                        if (selectedTextSpinnerFour.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoWednesday[3] = 1
                        } else if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayListVideoWednesday[3] = 0
                        }
                    }
                    if (positionList == 3) {
                        if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayThursday[3] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayThursday[3] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerFour
                                )
                            } else {
                                myArrayThursday[3] = selectedTextSpinnerFour
                            }
                        }
                        if (selectedTextSpinnerFour.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoThursday[3] = 1
                        } else if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayListVideoThursday[3] = 0
                        }
                    }
                    if (positionList == 4) {
                        if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayFriday[3] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArrayFriday[3] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerFour
                                )
                            } else {
                                myArrayFriday[3] = selectedTextSpinnerFour
                            }
                        }
                        if (selectedTextSpinnerFour.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoFriday[3] = 1
                        } else if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayListVideoFriday[3] = 0
                        }
                    }
                    if (positionList == 5) {
                        if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArraySaturday[3] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArraySaturday[3] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerFour
                                )
                            } else {
                                myArraySaturday[3] = selectedTextSpinnerFour
                            }
                        }
                        if (selectedTextSpinnerFour.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoSaturday[3] = 1
                        } else if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayListVideoSaturday[3] = 0
                        }
                    }
                    if (positionList == 6) {
                        if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArraySunday[3] = ""
                        } else {
                            if (appUtilities.timeFormatPreferences(mContext) == 12) {
                                myArraySunday[3] = appUtilities.changeDateFormat(
                                    "hh:mm a",
                                    "HH:mm",
                                    selectedTextSpinnerFour
                                )
                            } else {
                                myArraySunday[3] = selectedTextSpinnerFour
                            }
                        }
                        if (selectedTextSpinnerFour.equals("00:00", ignoreCase = true)) {
                            myArrayListVideoSunday[3] = 1
                        } else if (selectedTextSpinnerFour.equals("select", ignoreCase = true)) {
                            myArrayListVideoSunday[3] = 0
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    override fun getItemCount(): Int {
        return settingSchedModelList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val daysText: TextView
        var slotOneFromSpinner: Spinner
        var slotOneToSpinner: Spinner
        var slotTwoFromSpinner: Spinner
        var slotTwoToSpinner: Spinner

        init {
            daysText = itemView.findViewById(R.id.daysText)
            slotOneFromSpinner = itemView.findViewById(R.id.spinnerSlotOneFrom)
            slotOneToSpinner = itemView.findViewById(R.id.spinnerSlotOneTo)
            slotTwoFromSpinner = itemView.findViewById(R.id.spinnerSlotTwoFrom)
            slotTwoToSpinner = itemView.findViewById(R.id.spinnerSlotTwoTo)
        }
    }

    companion object {
        var myArrayMonday = arrayOfNulls<String>(4)
        var myArrayTuesday = arrayOfNulls<String>(4)
        var myArrayWednesday = arrayOfNulls<String>(4)
        var myArrayThursday = arrayOfNulls<String>(4)
        var myArrayFriday = arrayOfNulls<String>(4)
        var myArraySaturday = arrayOfNulls<String>(4)
        var myArraySunday = arrayOfNulls<String>(4)
    }
}