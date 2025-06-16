package com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses

import com.example.catnicwarehouse.packing.addPackingItems.presentation.sealedClasses.AddPackingItemsViewState
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsViewState
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListViewState
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.example.shared.networking.network.packing.model.packingItem.PackingItemsModel
import com.example.shared.networking.network.packing.model.packingList.AssignedPackingListItem
import com.example.shared.networking.network.packing.model.packingList.GetItemsForPackingResponseModelItem
import com.example.shared.networking.network.packing.model.packingList.PackingListStatusResponseModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

sealed class PackingArticlesViewState {
    object Reset : PackingArticlesViewState()
    object Empty : PackingArticlesViewState()
    object Loading : PackingArticlesViewState()
    data class Error(val errorMessage: String?) : PackingArticlesViewState()
    data class PickAmountResult(val isAmountPicked: Boolean?) : PackingArticlesViewState()
    data class ChangePackedAmountResult(val isAmountUpdated: Boolean?) : PackingArticlesViewState()
    data class GetItemsForPackingResponse(val itemsForPackingItems: List<GetItemsForPackingResponseModelItem>?) : PackingArticlesViewState()
    data class GetPackingListStatusResponse(val packingListStatus: PackingListStatusResponseModel?) : PackingArticlesViewState()
    data class GetDefaultPackingZonesResult(val defaultPackingZones:List<DefaultPackingZoneResultModel>?) : PackingArticlesViewState()
    data class GetCancelPackingListResult(val isPackingListCancelled: Boolean?): PackingArticlesViewState()
    data class GetPackingItemsResult(val packingItems: PackingItemsModel?) : PackingArticlesViewState()
    data class WarehouseStockyardInventoryEntriesResponse(val warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>?, val isFromUserEntry:Boolean): PackingArticlesViewState()
}