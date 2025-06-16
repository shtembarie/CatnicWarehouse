package com.example.catnicwarehouse.inventoryNew.stockyards.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.inventoryNew.stockyards.domain.useCase.GetCurrentInventoryUseCase
import com.example.catnicwarehouse.inventoryNew.stockyards.presentation.sealedClasses.StockyardEvent
import com.example.catnicwarehouse.inventoryNew.stockyards.presentation.sealedClasses.StockyardsViewState
import com.example.catnicwarehouse.movement.stockyards.domain.useCases.GetWarehouseStockyardsByWarehouseCodeUseCase
import com.example.catnicwarehouse.scan.domain.usecase.GetWarehouseStockyardByIdUseCase
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
class StockyardsViewModel @Inject constructor(
    private val getWarehouseStockyardsByWarehouseCodeUseCase: GetWarehouseStockyardsByWarehouseCodeUseCase,
    private val getWarehouseStockyardByIdUseCase: GetWarehouseStockyardByIdUseCase,
    private val getCurrentInventoryUseCase: GetCurrentInventoryUseCase
) : BaseViewModel() {


    private val _stockyardsFlow = MutableStateFlow<StockyardsViewState>(StockyardsViewState.Empty)
    val stockyardsFlow: StateFlow<StockyardsViewState> = _stockyardsFlow

    fun onEvent(event: StockyardEvent) {
        when (event) {
            is StockyardEvent.GetWarehouseStockyardsByWarehouseCode -> getWarehouseStockyardsByWarehouseCode(
                event.warehouseCode
            )

            StockyardEvent.Reset -> _stockyardsFlow.value = StockyardsViewState.Empty
            is StockyardEvent.GetWarehouseStockyardById -> getWarehouseStockyardById(
                event.id,
                event.isFromUserInteraction
            )

            is StockyardEvent.GetCurrentInventory -> getCurrentInventory(event.warehouseCode)
        }
    }

    private fun getWarehouseStockyardsByWarehouseCode(
        warehouseCode: String?
    ) {
        if (hasNetwork()) {
            getWarehouseStockyardsByWarehouseCodeUseCase.invoke(
                warehouseCode = warehouseCode,
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _stockyardsFlow.value = StockyardsViewState.Loading
                    }

                    is Resource.Error -> {
                        _stockyardsFlow.value = StockyardsViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _stockyardsFlow.value =
                            StockyardsViewState.GetWarehouseStockyardsByWarehouseCodeResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _stockyardsFlow.value =
                StockyardsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getWarehouseStockyardById(
        id: String,
        fromUserInteraction: Boolean
    ) {
        if (hasNetwork()) {
            getWarehouseStockyardByIdUseCase.invoke(id = id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _stockyardsFlow.value = StockyardsViewState.Loading
                        }

                        is Resource.Error -> {
                            _stockyardsFlow.value = StockyardsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _stockyardsFlow.value =
                                StockyardsViewState.WarehouseStockByIdFound(
                                    warehouseStockyard = result.data,
                                    fromUserInteraction = fromUserInteraction
                                )
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _stockyardsFlow.value =
                StockyardsViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun getCurrentInventory(warehouseCode: String?) {
        if (hasNetwork()) {
            getCurrentInventoryUseCase.getCurrentInventory(warehouseCode)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _stockyardsFlow.value = StockyardsViewState.Loading
                        }

                        is Resource.Error -> {
                            _stockyardsFlow.value = StockyardsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _stockyardsFlow.value =
                                StockyardsViewState.GetCurrentInventoriesResult(currentInventory = result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _stockyardsFlow.value =
                StockyardsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

}