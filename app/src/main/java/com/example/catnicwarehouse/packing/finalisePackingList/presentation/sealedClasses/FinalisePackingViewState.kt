package com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses

import android.support.v4.os.IResultReceiver.Default
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsViewState
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.example.shared.networking.network.packing.model.packingItem.PackingItemsModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

sealed class FinalisePackingViewState {
    object Reset : FinalisePackingViewState()
    object Empty : FinalisePackingViewState()
    object Loading : FinalisePackingViewState()
    data class Error(val errorMessage: String?) : FinalisePackingViewState()
    data class ArticleResult(val articles: List<ArticlesForDeliveryResponseDTO>?) : FinalisePackingViewState()
    data class WarehouseStockyardInventoryEntriesResponse(val warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>?, val isFromUserEntry:Boolean): FinalisePackingViewState()
    data class PausePackingResult(val isPackingPaused: Boolean?) : FinalisePackingViewState()
    data class GetPackingItemsResult(val packingItems: PackingItemsModel?) : FinalisePackingViewState()
    data class GetFinalizePackingListResult(val isPackingListFinalized: Boolean?) : FinalisePackingViewState()
    data class GetCancelPackingListResult(val isPackingListCancelled: Boolean?) : FinalisePackingViewState()
    data class GetDefaultPackingZonesResult(val defaultPackingZones:List<DefaultPackingZoneResultModel>?) : FinalisePackingViewState()
    data class GetPackingListComment(val comment: String?) : FinalisePackingViewState()
}