package com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses

import com.example.shared.networking.network.packing.model.packingList.SearchPackingListDTO

sealed class SearchPackingListViewState {
    object Reset : SearchPackingListViewState()
    object Empty : SearchPackingListViewState()
    data class Error(val errorMessage: String?) : SearchPackingListViewState()
    data class SearchedPackingLists(val packingLists: List<SearchPackingListDTO>?) : SearchPackingListViewState()
}
