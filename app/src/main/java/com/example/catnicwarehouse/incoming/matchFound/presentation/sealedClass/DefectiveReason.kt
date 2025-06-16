package com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass

import android.os.Parcel
import android.os.Parcelable

enum class DefectiveReason(val value: String) :Parcelable{
    PHYSICAL_DAMAGE("Physical damage"),
    MANUFACTURING_DEFECTS("Manufacturing defects"),
    EXPIRATION("Expiration"),
    MARKET_WITHDRAWAL("Market withdrawal"),
    OBSOLESCENCE("Obsolescence"),
    PACKAGING_OR_LABELING_ERRORS("Packaging or labeling errors"),
    FUNCTIONAL_FAILURES("Functional failures");

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<DefectiveReason> {

        fun fromValue(value: String): DefectiveReason? {
            return values().find { it.value == value }
        }
        override fun createFromParcel(parcel: Parcel) = values().first { it.name == parcel.readString() }
        override fun newArray(size: Int) = arrayOfNulls<DefectiveReason?>(size)
    }

}