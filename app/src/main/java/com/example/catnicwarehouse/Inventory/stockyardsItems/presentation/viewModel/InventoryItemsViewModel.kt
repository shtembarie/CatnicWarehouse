package com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.Inventory.AddArticle.domain.useCases.AddInventoryArticleUseCase
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.catnicwarehouse.inventoryNew.articles.domain.useCase.GetInventoryItemsUseCase
import com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.sealedClasses.InventoryItemsEvent
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.scan.domain.usecase.GetWarehouseStockyardByIdUseCase
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.InventoryOrder.dataModel.PostStockyardItemCommand
import com.example.shared.repository.inventory.model.InventoryItem
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class InventoryItemsViewModel @Inject constructor(
    private val getInventoryItemsUseCase: GetInventoryItemsUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
    private val addInventoryArticleUseCase: AddInventoryArticleUseCase,
    private val getWarehouseStockyardByIdUseCase: GetWarehouseStockyardByIdUseCase,
) : BaseViewModel() {
    private val _getInventoryItems =
        MutableStateFlow<GetInventoryByIdViewState>(GetInventoryByIdViewState.Empty)
    val getInventoryItems: StateFlow<GetInventoryByIdViewState> = _getInventoryItems
    private val _totalActualStock = MutableLiveData<Int>()
    val totalActualStock: LiveData<Int> get() = _totalActualStock
    private val _totalTargetStock = MutableLiveData<Int>()
    val totalTargetStock: LiveData<Int> get() = _totalTargetStock


    private fun resetItems() {
        _getInventoryItems.value = GetInventoryByIdViewState.Empty
    }
    private fun searchArticlesForInventory(
        searchTerm: String
    ) {
        if (hasNetwork()) {
            searchArticlesForDeliveryUseCase.invoke(searchTerm = searchTerm)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _getInventoryItems.value = GetInventoryByIdViewState.Loading
                        }

                        is Resource.Error -> {
                            _getInventoryItems.value = GetInventoryByIdViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _getInventoryItems.value =
                                GetInventoryByIdViewState.ArticlesForInventoryFound(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _getInventoryItems.value =
                GetInventoryByIdViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    fun getInventoryItems(id: Int) {
        if (hasNetwork()) {
            getInventoryItemsUseCase.invoke(id = id.toString())
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _getInventoryItems.value = GetInventoryByIdViewState.Loading
                        }

                        is Resource.Error -> {
                            _getInventoryItems.value =
                                GetInventoryByIdViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            val item = result.data ?: return@onEach
                            _getInventoryItems.value =
                                GetInventoryByIdViewState.GetInventoriesItems(item)
                            calculateTotalActualStock(item)
                            sortInventoryItems(item)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _getInventoryItems.value =
                GetInventoryByIdViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    private fun addInventoryArticle(id: Int, stockyardId: Int, command: PostStockyardItemCommand) {
        if (hasNetwork()) {
            addInventoryArticleUseCase.invoke(id = id, stockyardId = stockyardId, command = command)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _getInventoryItems.value = GetInventoryByIdViewState.Loading
                        }

                        is Resource.Error -> {
                            _getInventoryItems.value =
                                GetInventoryByIdViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _getInventoryItems.value =
                                GetInventoryByIdViewState.InventoryArticleItemAdded
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _getInventoryItems.value =
                GetInventoryByIdViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    private fun calculateTotalActualStock(items: List<InventoryItem>) {
        val total = items.sumOf { it.actualStock }
        _totalActualStock.postValue(total)
        val totaltarget = items.sumOf { it.targetStock.toDouble() }.toInt()
        _totalTargetStock.postValue(totaltarget.toInt())
    }
    private fun sortInventoryItems(items: List<InventoryItem>) {
        val sortedItems = items.sortedWith(compareByDescending<InventoryItem> { it.inventoried }
            .thenBy { false })
        _getInventoryItems.value = GetInventoryByIdViewState.GetInventoriesItems(sortedItems)
    }
    private fun getWarehouseStockyardById(
        id: String
    ){
        if (hasNetwork()) {
            getWarehouseStockyardByIdUseCase.invoke(id = id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _getInventoryItems.value = GetInventoryByIdViewState.Loading
                        }

                        is Resource.Error -> {
                            _getInventoryItems.value = GetInventoryByIdViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _getInventoryItems.value = GetInventoryByIdViewState.WarehouseStockByIdFound(warehouseStockyard = result.data)

                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _getInventoryItems.value =
                GetInventoryByIdViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    fun onItemEvent(event: InventoryItemsEvent) {
        when (event) {
            InventoryItemsEvent.LoadInventory -> {}
            InventoryItemsEvent.Reset -> resetItems()
            is InventoryItemsEvent.AddInventoryArticle -> addInventoryArticle(id = event.inventoryId, stockyardId = event.stockyardId, command = event.command)
            is InventoryItemsEvent.SearchArticle -> { searchArticlesForInventory(event.searchTerm) }
            is InventoryItemsEvent.GetWarehouseStockyardById -> { getWarehouseStockyardById(event.id) }
        }
    }
}