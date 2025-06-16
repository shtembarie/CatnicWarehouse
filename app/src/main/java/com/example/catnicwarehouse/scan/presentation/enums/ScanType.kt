package com.example.catnicwarehouse.scan.presentation.enums

import android.os.Parcel
import android.os.Parcelable

enum class ScanType(val type: String) : Parcelable {
    STOCKYARD("stockyard"),
    ARTICLE("article"),
    ONLY_STOCKYARD("only_stockyard");

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ScanType> {
        override fun createFromParcel(parcel: Parcel) = values().first { it.type == parcel.readString() }
        override fun newArray(size: Int) = arrayOfNulls<ScanType?>(size)
        fun fromType(type: String): ScanType? {
            return values().firstOrNull { it.type == type }
        }
    }

}
