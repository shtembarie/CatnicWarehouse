package com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses

import androidx.work.Operation.State.IN_PROGRESS
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingEvent
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel

sealed class PackingListEvent {
    data class GetPackingList(val id: String?) : PackingListEvent()
    data class GetPackingLists(val inProgress: Boolean?) : PackingListEvent()
    data class CancelPackingList(val id: String?,val cancelPackingRequestModel: CancelPackingRequestModel) : PackingListEvent()
    object  GetAssignedPackingLists : PackingListEvent()
    object GetDefaultPackingZones : PackingListEvent()
    object Empty : PackingListEvent()
}