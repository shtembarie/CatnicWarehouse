package com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses

sealed class SearchPackingListEvent {
    object EmptySearch : SearchPackingListEvent()
    data class SearchPackingList(val query: String) : SearchPackingListEvent()
}
