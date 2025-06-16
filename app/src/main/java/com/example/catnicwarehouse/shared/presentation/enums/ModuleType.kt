package com.example.catnicwarehouse.shared.presentation.enums

import android.os.Parcel
import android.os.Parcelable

enum class ModuleType(val type: String) : Parcelable {
    INCOMING("Incoming"), INVENTORY("Inventory"), MOVEMENTS("Movements"),PACKING_1("Packing1"),PACKING_2("Packing2"),CORRECTIVE_STOCK("CorrectingStock"),DEFECTIVE_ITEMS("DefectiveItems"),CHECKS("Checks");


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ModuleType> {
        override fun createFromParcel(parcel: Parcel) =
            values().first { it.type == parcel.readString() }

        override fun newArray(size: Int) = arrayOfNulls<ModuleType?>(size)
    }
}