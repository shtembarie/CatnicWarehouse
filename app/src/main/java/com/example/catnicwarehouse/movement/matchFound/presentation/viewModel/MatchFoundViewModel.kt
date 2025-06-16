package com.example.catnicwarehouse.movement.matchFound.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.GetWarehouseStockyardInventoryEntriesUseCase
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.PickUpArticleUseCase
import com.example.catnicwarehouse.movement.matchFound.presentation.sealedClasses.MatchFoundEvent
import com.example.catnicwarehouse.movement.matchFound.presentation.sealedClasses.MatchFoundViewState
import com.example.catnicwarehouse.movement.stockyards.presentation.sealedClasses.StockyardsViewState
import com.example.catnicwarehouse.scan.domain.usecase.GetWarehouseStockyardByIdUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.movements.PickUpRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MatchFoundViewModel @Inject constructor(
    private val pickUpArticleUseCase: PickUpArticleUseCase,
    private val getWarehouseStockyardByIdUseCase: GetWarehouseStockyardByIdUseCase,
    private val getWarehouseStockyardInventoryEntriesUseCase: GetWarehouseStockyardInventoryEntriesUseCase
) : BaseViewModel() {


    private val _matchFoundFlow = MutableStateFlow<MatchFoundViewState>(MatchFoundViewState.Empty)
    val matchFoundFlow: StateFlow<MatchFoundViewState> = _matchFoundFlow

    fun onEvent(event: MatchFoundEvent) {
        when (event) {
            is MatchFoundEvent.PickUp -> pickUp(event.id,event.pickUpRequestModel)
            MatchFoundEvent.Reset -> _matchFoundFlow.value = MatchFoundViewState.Empty
            is MatchFoundEvent.GetWarehouseStockyardById -> getWarehouseStockyardById(event.id)
            is MatchFoundEvent.GetWarehouseStockyardInventoryEntries -> getWarehouseStockyardInventoryEntries(event.articleId,event.stockyardId,event.warehouseCode)
        }
    }

    private fun pickUp(
        id: String?,
        prickUpRequestModel: PickUpRequestModel?
    ) {
        if (hasNetwork()) {
            pickUpArticleUseCase.invoke(
                id = id,
                pickUpRequestModel = prickUpRequestModel
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _matchFoundFlow.value = MatchFoundViewState.Loading
                    }

                    is Resource.Error -> {
                        _matchFoundFlow.value = MatchFoundViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _matchFoundFlow.value =
                            MatchFoundViewState.PickUpResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _matchFoundFlow.value =
                MatchFoundViewState.Error(getString(R.string.no_internet_connection))
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
                            _matchFoundFlow.value = MatchFoundViewState.Loading
                        }

                        is Resource.Error -> {
                            _matchFoundFlow.value = MatchFoundViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _matchFoundFlow.value =
                                MatchFoundViewState.WarehouseStockByIdFound(warehouseStockyard = result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _matchFoundFlow.value =
                MatchFoundViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getWarehouseStockyardInventoryEntries(
        articleId: String?,
        stockYardId:String?,
        warehouseCode:String?,

    ) {
        if (hasNetwork()) {
            getWarehouseStockyardInventoryEntriesUseCase.invoke(id = articleId, stockyardId = stockYardId, warehouseCode = warehouseCode)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _matchFoundFlow.value = MatchFoundViewState.Loading
                        }

                        is Resource.Error -> {
                            _matchFoundFlow.value = MatchFoundViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _matchFoundFlow.value =
                                MatchFoundViewState.WarehouseStockyardInventoryEntriesResponse(warehouseStockyardInventoryEntriesResponse = result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _matchFoundFlow.value =
                MatchFoundViewState.Error(getString(R.string.no_internet_connection))
        }
    }




}