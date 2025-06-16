package com.example.catnicwarehouse.scan.presentation.enums

import android.os.Parcel
import android.os.Parcelable

enum class ScanOptionEnum(val option: String) : Parcelable {
    BARCODE("barcode"),
    CAMERA("camera"),
    MANUAL("manual");

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(option)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ScanOptionEnum> {
        override fun createFromParcel(parcel: Parcel) = values().first { it.option == parcel.readString() }
        override fun newArray(size: Int) = arrayOfNulls<ScanOptionEnum?>(size)
    }
}