package com.example.catnicwarehouse.shared.presentation.enums

import android.os.Parcel
import android.os.Parcelable
import com.example.catnicwarehouse.scan.presentation.enums.ScanType

enum class ItemType(val type: String) : Parcelable {
    QTY("QTY"), DEFECTIVE("DEFECTIVE");

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ScanType> {
        override fun createFromParcel(parcel: Parcel) =
            ScanType.values().first { it.type == parcel.readString() }

        override fun newArray(size: Int) = arrayOfNulls<ScanType?>(size)
    }
}