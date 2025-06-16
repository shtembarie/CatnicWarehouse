package com.example.catnicwarehouse.packing.shared

enum class PackingItemStatus(val type: String) {
    OPE("Open"),
    PAU("Paused"),
    PRO("In Progress"),
    END("Ended"),
    CLS("Closed"),
    DLV("Delivered"),
    CNR("Cancellation Requested"),
    CNC("Cancelled");


    override fun toString(): String {
        return type
    }
}