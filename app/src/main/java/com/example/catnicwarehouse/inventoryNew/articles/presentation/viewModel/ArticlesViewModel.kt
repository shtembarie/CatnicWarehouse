package com.example.catnicwarehouse.inventoryNew.articles.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.Inventory.AddArticle.domain.useCases.AddInventoryArticleUseCase
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.inventoryNew.articles.domain.useCase.GetInventoryItemsUseCase
import com.example.catnicwarehouse.inventoryNew.articles.presentation.sealedClasses.InventoryItemsEvent
import com.example.catnicwarehouse.inventoryNew.articles.presentation.sealedClasses.InventoryItemsViewState
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.GetWarehouseStockyardInventoryEntriesUseCase
import com.example.catnicwarehouse.scan.domain.usecase.GetWarehouseStockyardByIdUseCase
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.inventory.model.InventoryItem
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val getInventoryItemsUseCase: GetInventoryItemsUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
    private val getWarehouseStockyardInventoryEntriesUseCase: GetWarehouseStockyardInventoryEntriesUseCase
) : BaseViewModel() {


    private val _articlesFlow =
        MutableStateFlow<InventoryItemsViewState>(InventoryItemsViewState.Empty)
    val articlesFlow: StateFlow<InventoryItemsViewState> = _articlesFlow


    private fun getInventoryItems(id: String) {
        if (hasNetwork()) {
            getInventoryItemsUseCase.invoke(id = id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _articlesFlow.value = InventoryItemsViewState.Loading
                        }

                        is Resource.Error -> {
                            _articlesFlow.value =
                                InventoryItemsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            val items = result.data ?: return@onEach
                            _articlesFlow.value = InventoryItemsViewState.InventoryItems(items)

                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _articlesFlow.value =
                InventoryItemsViewState.Error(getString(R.string.no_internet_connection))
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
                            _articlesFlow.value = InventoryItemsViewState.Loading
                        }

                        is Resource.Error -> {
                            _articlesFlow.value = InventoryItemsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _articlesFlow.value = InventoryItemsViewState.ArticleResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _articlesFlow.value =
                InventoryItemsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getWarehouseStockyardInventoryEntries(
        articleId: String?,
        stockYardId: String?,
        warehouseCode: String?,
        isFromUserEntry: Boolean
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
                            _articlesFlow.value = InventoryItemsViewState.Loading
                        }

                        is Resource.Error -> {
                            _articlesFlow.value = InventoryItemsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _articlesFlow.value =
                                InventoryItemsViewState.WarehouseStockyardInventoryEntriesResponse(
                                    warehouseStockyardInventoryEntriesResponse = result.data,
                                    isFromUserEntry = isFromUserEntry
                                )
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _articlesFlow.value =
                InventoryItemsViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    fun onEvent(event: InventoryItemsEvent) {
        when (event) {
            is InventoryItemsEvent.InventoryItems -> {
                getInventoryItems(event.id)
            }

            InventoryItemsEvent.Reset -> {
                _articlesFlow.value = InventoryItemsViewState.Empty
            }

            is InventoryItemsEvent.SearchArticle -> {
                searchArticlesForDelivery(event.searchTerm)
            }

            is InventoryItemsEvent.GetWarehouseStockyardInventoryEntries -> {
                getWarehouseStockyardInventoryEntries(
                    event.articleId,
                    event.stockyardId,
                    event.warehouseCode,
                    event.isFromUserEntry
                )

            }
        }
    }
}