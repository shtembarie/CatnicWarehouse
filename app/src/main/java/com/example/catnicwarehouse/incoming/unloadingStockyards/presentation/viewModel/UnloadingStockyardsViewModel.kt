package com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.unloadingStockyards.domain.useCase.DefaultWarehouseStockyardsUseCase
import com.example.catnicwarehouse.incoming.unloadingStockyards.domain.useCase.FindWarehousesUseCase
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsEvent
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsViewState
import com.example.catnicwarehouse.scan.domain.usecase.GetWarehouseStockyardByIdUseCase
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
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
class UnloadingStockyardsViewModel @Inject constructor(
    private val findWarehousesUseCase: FindWarehousesUseCase,
    private val defaultWarehouseStockyardsUseCase: DefaultWarehouseStockyardsUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
    private val getWarehouseStockyardByIdUseCase: GetWarehouseStockyardByIdUseCase
) : BaseViewModel() {


    private val _unloadingStockyardsFlow = MutableStateFlow<UnloadingStockyardsViewState>(UnloadingStockyardsViewState.Empty)
    val unloadingStockyardsFlow: StateFlow<UnloadingStockyardsViewState> = _unloadingStockyardsFlow

    fun onEvent(event: UnloadingStockyardsEvent) {
        when (event) {
            UnloadingStockyardsEvent.Reset -> reset()
            is UnloadingStockyardsEvent.FindWarehouses -> findWarehouses(event.searchTerm)
            is UnloadingStockyardsEvent.FindDefaultPickUpAndDropZones -> getDefaultPickupAndDropZones(event.warehouseCode)
            is UnloadingStockyardsEvent.GetWarehouseStockyardById -> getWarehouseStockyardById(event.id)
            is UnloadingStockyardsEvent.SearchArticle -> searchArticlesForDelivery(event.searchTerm)
        }
    }

    private fun reset() {
        _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.Empty
    }

    private fun findWarehouses(
        searchTerm: String? = null
    ) {

        if (hasNetwork()) {
            findWarehousesUseCase.invoke(searchTerm=searchTerm)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.Loading
                        }

                        is Resource.Error -> {
                            _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.WarehousesFound(result.data)

                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _unloadingStockyardsFlow.value =
                UnloadingStockyardsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getDefaultPickupAndDropZones(
        warehouseCode: String
    ) {

        if (hasNetwork()) {
            defaultWarehouseStockyardsUseCase.invoke(warehouseCode = warehouseCode)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.Loading
                        }

                        is Resource.Error -> {
                            _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.DefaultPickUpAndDropZonesFound(result.data)

                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _unloadingStockyardsFlow.value =
                UnloadingStockyardsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun searchArticlesForDelivery(
        searchTerm: String
    ) {
        if (hasNetwork()) {
            searchArticlesForDeliveryUseCase.invoke(searchTerm = searchTerm)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.Loading
                        }

                        is Resource.Error -> {
                            _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _unloadingStockyardsFlow.value =
                                UnloadingStockyardsViewState.ArticlesForDeliveryFound(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _unloadingStockyardsFlow.value =
                UnloadingStockyardsViewState.Error(getString(R.string.no_internet_connection))
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
                            _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.Loading
                        }

                        is Resource.Error -> {
                            _unloadingStockyardsFlow.value = UnloadingStockyardsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _unloadingStockyardsFlow.value =
                                UnloadingStockyardsViewState.WarehouseStockByIdFound(warehouseStockyard = result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _unloadingStockyardsFlow.value =
                UnloadingStockyardsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

}