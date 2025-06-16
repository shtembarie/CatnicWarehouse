package com.example.catnicwarehouse.scan.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.GetWarehouseStockyardInventoryEntriesUseCase
import com.example.catnicwarehouse.scan.domain.usecase.GetWarehouseStockyardByIdUseCase
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.catnicwarehouse.scan.domain.usecase.SearchWarehouseStockyardsUseCase
import com.example.catnicwarehouse.scan.presentation.sealedClass.manualInput.ManualInputEvent
import com.example.catnicwarehouse.scan.presentation.sealedClass.manualInput.ManualInputViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ManualInputViewModel @Inject constructor(
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
    private val getWarehouseStockyardByIdUseCase: GetWarehouseStockyardByIdUseCase,
    private val searchWarehouseStockyardsUseCase: SearchWarehouseStockyardsUseCase,
    private val getWarehouseStockyardInventoryEntriesUseCase: GetWarehouseStockyardInventoryEntriesUseCase
) : BaseViewModel() {


    private val _manualInputFlow =
        MutableStateFlow<ManualInputViewState>(ManualInputViewState.Empty)
    val manualInputFlow: StateFlow<ManualInputViewState> = _manualInputFlow


    private fun searchArticlesForDelivery(
        searchTerm: String
    ) {
        if (hasNetwork()) {
            searchArticlesForDeliveryUseCase.invoke(searchTerm = searchTerm)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _manualInputFlow.value = ManualInputViewState.Loading
                        }

                        is Resource.Error -> {
                            _manualInputFlow.value = ManualInputViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _manualInputFlow.value =
                                ManualInputViewState.ArticlesForDeliveryFound(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _manualInputFlow.value =
                ManualInputViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getWarehouseStockyardById(
        id: String
    ) {
        if (hasNetwork()) {
            getWarehouseStockyardByIdUseCase.invoke(id = id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _manualInputFlow.value = ManualInputViewState.Loading
                        }

                        is Resource.Error -> {
                            _manualInputFlow.value = ManualInputViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _manualInputFlow.value =
                                ManualInputViewState.WarehouseStockByIdFound(warehouseStockyard = result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _manualInputFlow.value =
                ManualInputViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun searchWarehouseStockyards(
        searchTerm: String?="",
        warehouseCode:String?="",
        isFromUserSearch:Boolean
    ) {
        if (hasNetwork()) {
            searchWarehouseStockyardsUseCase.invoke(searchTerm,warehouseCode)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _manualInputFlow.value = ManualInputViewState.Loading
                        }

                        is Resource.Error -> {
                            _manualInputFlow.value = ManualInputViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _manualInputFlow.value =
                                ManualInputViewState.WarehouseStockyardsFound(result.data,isFromUserSearch)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _manualInputFlow.value =
                ManualInputViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getWarehouseStockyardInventoryEntries(
        articleId: String?,
        stockYardId: String?,
        warehouseCode: String?,
        isFromUserEntry:Boolean
    ) {
        if (hasNetwork()) {
            getWarehouseStockyardInventoryEntriesUseCase.invoke(
                id = articleId,
                stockyardId = stockYardId,
                warehouseCode = warehouseCode
            )
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _manualInputFlow.value = ManualInputViewState.Loading
                        }

                        is Resource.Error -> {
                            _manualInputFlow.value = ManualInputViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _manualInputFlow.value =
                                ManualInputViewState.WarehouseStockyardInventoryEntriesResponse(
                                    warehouseStockyardInventoryEntriesResponse = result.data,
                                    isFromUserEntry = isFromUserEntry
                                )
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _manualInputFlow.value =
                ManualInputViewState.Error(getString(R.string.no_internet_connection))
        }
    }




    fun onEvent(event: ManualInputEvent) {
        when (event) {
            is ManualInputEvent.GetWarehouseStockyardById -> {getWarehouseStockyardById(event.id)}
            is ManualInputEvent.SearchArticle -> {searchArticlesForDelivery(event.searchTerm)}
            is ManualInputEvent.SearchWarehouseStockyards -> searchWarehouseStockyards(event.searchTerm,event.warehouseCode,event.isFromUserSearch)
            is ManualInputEvent.GetWarehouseStockyardInventoryEntries -> getWarehouseStockyardInventoryEntries(
                articleId = event.articleId,
                stockYardId = event.stockyardId,
                warehouseCode = event.warehouseCode,
                isFromUserEntry = event.isFromUserEntry
            )
        }
    }

}