package com.example.catnicwarehouse.Inventory.matchFoundStockYard.presentation.sealedClasses

import com.example.catnicwarehouse.Inventory.matchFoundStockYard.data.model.GetInventoryKPIUIModel

sealed class GetInventoryKPIViewState{
    object Reset : GetInventoryKPIViewState()
    object Empty : GetInventoryKPIViewState()
    object Loading : GetInventoryKPIViewState()
    data class GetInventoriesKPI(val getInventoriesKPI: GetInventoryKPIUIModel) : GetInventoryKPIViewState()
    data class Error(val errorMessage: String?) : GetInventoryKPIViewState()
}
