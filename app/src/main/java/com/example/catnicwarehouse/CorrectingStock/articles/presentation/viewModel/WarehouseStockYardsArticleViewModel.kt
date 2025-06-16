package com.example.catnicwarehouse.CorrectingStock.articles.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.CorrectingStock.articles.domain.useCase.GetStockYardsArticlesUseCase
import com.example.catnicwarehouse.CorrectingStock.articles.presentation.sealedClasses.GetWarehouseStockYardArticlesEvent
import com.example.catnicwarehouse.CorrectingStock.articles.presentation.sealedClasses.GetWarehouseStockYardsArticleViewState
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.catnicwarehouse.scan.domain.usecase.GetWarehouseStockyardByIdUseCase
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.correctingStock.model.GetWarehouseStockYardsArticlesByIdUIModel
import com.example.shared.repository.correctingStock.model.WarehouseStockYardsArticlesList
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by Enoklit on 13.11.2024.
 */
@HiltViewModel
class WarehouseStockYardsArticleViewModel @Inject constructor(
    private val getStockYardsArticlesUseCase: GetStockYardsArticlesUseCase,
    private val getWarehouseStockyardByIdUseCase: GetWarehouseStockyardByIdUseCase,
): BaseViewModel() {
    private val _getWarehouseStockYardsArticle = MutableStateFlow<GetWarehouseStockYardsArticleViewState>(
        GetWarehouseStockYardsArticleViewState.Empty)
    val getWarehouseStockYardsArticle: StateFlow<GetWarehouseStockYardsArticleViewState> = _getWarehouseStockYardsArticle

    fun onEvent(event : GetWarehouseStockYardArticlesEvent){
        when(event){
            is GetWarehouseStockYardArticlesEvent.Loading -> {getWarehouseStockYardList(event.warehouseEntryId)}
            is GetWarehouseStockYardArticlesEvent.Reset -> reset()
            is GetWarehouseStockYardArticlesEvent.GetWarehouseStockyardById -> {getWarehouseStockyardById(event.id)}
        }
    }
    private fun reset() {
        _getWarehouseStockYardsArticle.value = GetWarehouseStockYardsArticleViewState.Empty
    }
    private fun getWarehouseStockyardById(
        id: String
    ){
        if (hasNetwork()) {
            getWarehouseStockyardByIdUseCase.invoke(id = id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _getWarehouseStockYardsArticle.value = GetWarehouseStockYardsArticleViewState.Loading
                        }

                        is Resource.Error -> {
                            _getWarehouseStockYardsArticle.value = GetWarehouseStockYardsArticleViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _getWarehouseStockYardsArticle.value = GetWarehouseStockYardsArticleViewState.WarehouseStockByIdFound(warehouseStockyard = result.data)

                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _getWarehouseStockYardsArticle.value =
                GetWarehouseStockYardsArticleViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    private fun getWarehouseStockYardList(warehouseEntryId: Int?){
        if (hasNetwork()){
            getStockYardsArticlesUseCase.getWarehouseStockYardsArticle(warehouseEntryId)
                .onEach { result ->
                    when (result){
                        is Resource.Loading -> {
                            showProgressBar()
                            _getWarehouseStockYardsArticle.value = GetWarehouseStockYardsArticleViewState.Loading
                        }
                        is Resource.Error -> {
                            _getWarehouseStockYardsArticle.value = GetWarehouseStockYardsArticleViewState.Error("Not found")
                        }
                        is Resource.Success -> {
                            val correctionStockyard = result.data
                            if (correctionStockyard != null){
                                val uiModel = mapCorrectingStockYardDTOToUIModelCurrent(correctionStockyard)
                                _getWarehouseStockYardsArticle.value = GetWarehouseStockYardsArticleViewState.WarehouseStockArticleFound(uiModel)
                            }else {
                                _getWarehouseStockYardsArticle.value = GetWarehouseStockYardsArticleViewState.Error("")
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _getWarehouseStockYardsArticle.value = GetWarehouseStockYardsArticleViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun mapCorrectingStockYardDTOToUIModelCurrent(correctionStockyards: List<WarehouseStockYardsArticlesList>): List<GetWarehouseStockYardsArticlesByIdUIModel> {
        return correctionStockyards.map { stockyard ->
            GetWarehouseStockYardsArticlesByIdUIModel(
                id = stockyard.id,
                stockYardId = stockyard.stockYardId,
                stockYardName = stockyard.stockYardName,
                articleId = stockyard.articleId,
                articleMatchCode = stockyard.articleMatchCode,
                articleDescription = stockyard.articleDescription,
                amount = stockyard.amount,
                unit = stockyard.unit,
                defectiveArticles = stockyard.defectiveArticles,
                isMoving = stockyard.isMoving,
            )
        }
    }
}