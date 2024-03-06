package com.whitecoats.clinicplus.interfaces

import android.widget.LinearLayout

interface PaymentTypeClick {
    fun onItemClick(pos: Int, itemName:String, view1:LinearLayout?,view2: LinearLayout?)
}