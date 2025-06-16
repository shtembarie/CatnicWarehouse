package com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses

import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.example.shared.networking.network.packing.model.packingList.AssignedPackingListItem
import com.example.shared.networking.network.packing.model.packingList.PackingModelItem
import com.example.shared.networking.network.packing.model.packingList.packingListItem.PackingListItem

sealed class PackingListViewState {
    object Reset : PackingListViewState()
    object Empty : PackingListViewState()
    object Loading : PackingListViewState()
    data class Error(val errorMessage: String?) : PackingListViewState()
    data class GetPackingList(val packingList: PackingModelItem?) : PackingListViewState()
    data class GetPackingLists(val packingList: PackingListItem?) : PackingListViewState()
    data class GetDefaultPackingZonesResult(val defaultPackingZones:List<DefaultPackingZoneResultModel>?) : PackingListViewState()
    data class GetAssignedPackingLists(val packingList: List<AssignedPackingListItem>?) : PackingListViewState()
    data class GetCancelPackingListResult(val isPackingListCancelled: Boolean?): PackingListViewState()

}