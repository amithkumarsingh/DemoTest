package com.whitecoats.clinicplus.models

import android.os.Parcel
import android.os.Parcelable

data class UpiIdDetails(
    val id: Int,
    var upi_id: String,
    val upi_scan_image_url: String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(upi_id)
        parcel.writeString(upi_scan_image_url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UpiIdDetails> {
        override fun createFromParcel(parcel: Parcel): UpiIdDetails {
            return UpiIdDetails(parcel)
        }

        override fun newArray(size: Int): Array<UpiIdDetails?> {
            return arrayOfNulls(size)
        }
    }
}