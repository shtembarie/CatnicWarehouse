package com.example.catnicwarehouse.packing.packingList.presentation.utils

import com.example.shared.networking.network.packing.model.packingList.SearchPackingListDTO

interface PackingListSelectionListener {
    fun onPackingListSelected(selectedPackingList: SearchPackingListDTO)
}