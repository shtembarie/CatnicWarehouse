package com.example.catnicwarehouse.checks.shared.presentation.viewModel

import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.checks.shared.presentation.sealedClasses.ChecksSharedViewState
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChecksSharedViewModel @Inject constructor() : BaseViewModel() {

    private val _checksSharedFlow =
        MutableStateFlow<ChecksSharedViewState>(ChecksSharedViewState.Empty)
    val checksSharedFlow: StateFlow<ChecksSharedViewState> = _checksSharedFlow


    var scannedArticle: ArticlesForDeliveryResponseDTO? = null
    var articlesListToSelectFrom: List<WarehouseStockyardInventoryEntriesResponseModel>? = null
    var scannedStockyard: WarehouseStockyardsDTO? = null
    var selectedArticleToShowMatchFoundDetails: WarehouseStockyardInventoryEntriesResponseModel? = null

    fun initViewModel() {
        scannedArticle = null
        scannedStockyard = null
        articlesListToSelectFrom = null
        selectedArticleToShowMatchFoundDetails = null

    }

}
