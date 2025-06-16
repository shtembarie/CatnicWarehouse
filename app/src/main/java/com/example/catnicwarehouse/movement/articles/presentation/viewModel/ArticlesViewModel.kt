package com.example.catnicwarehouse.movement.articles.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsViewState
import com.example.catnicwarehouse.movement.articles.domain.useCases.GetWarehouseStockyardInventoryUseCase
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.GetWarehouseStockyardInventoryEntriesUseCase
import com.example.catnicwarehouse.movement.matchFound.presentation.sealedClasses.MatchFoundViewState
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
class ArticlesViewModel @Inject constructor(
    private val getWarehouseStockyardInventoryUseCase: GetWarehouseStockyardInventoryUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
    private val getWarehouseStockyardInventoryEntriesUseCase: GetWarehouseStockyardInventoryEntriesUseCase
) : BaseViewModel() {


    private val _articlesFlow = MutableStateFlow<ArticlesViewState>(ArticlesViewState.Empty)
    val articlesFlow: StateFlow<ArticlesViewState> = _articlesFlow

    fun onEvent(event: ArticlesEvent) {
        when (event) {
            is ArticlesEvent.GetStockyardInventory -> getWarehouseStockyardsByWarehouseCode(id = event.stockyardId)
            ArticlesEvent.Reset -> _articlesFlow.value = ArticlesViewState.Empty
            is ArticlesEvent.SearchArticle -> searchArticlesForDelivery(searchTerm = event.searchTerm)
            is ArticlesEvent.GetWarehouseStockyardInventoryEntries -> getWarehouseStockyardInventoryEntries(
                articleId = event.articleId,
                stockYardId = event.stockyardId,
                warehouseCode = event.warehouseCode,
                isFromUserEntry = event.isFromUserEntry
            )
        }
    }

    private fun getWarehouseStockyardsByWarehouseCode(
        id: String?
    ) {
        if (hasNetwork()) {
            getWarehouseStockyardInventoryUseCase.invoke(
                id = id,
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _articlesFlow.value = ArticlesViewState.Loading
                    }

                    is Resource.Error -> {
                        _articlesFlow.value = ArticlesViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _articlesFlow.value =
                            ArticlesViewState.GetStockyardInventoryResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _articlesFlow.value =
                ArticlesViewState.Error(getString(R.string.no_internet_connection))
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
                            _articlesFlow.value = ArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _articlesFlow.value = ArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _articlesFlow.value = ArticlesViewState.ArticleResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _articlesFlow.value =
                ArticlesViewState.Error(getString(R.string.no_internet_connection))
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
                            _articlesFlow.value = ArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _articlesFlow.value = ArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _articlesFlow.value =
                                ArticlesViewState.WarehouseStockyardInventoryEntriesResponse(
                                    warehouseStockyardInventoryEntriesResponse = result.data,
                                    isFromUserEntry = isFromUserEntry
                                )
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _articlesFlow.value =
                ArticlesViewState.Error(getString(R.string.no_internet_connection))
        }
    }


}