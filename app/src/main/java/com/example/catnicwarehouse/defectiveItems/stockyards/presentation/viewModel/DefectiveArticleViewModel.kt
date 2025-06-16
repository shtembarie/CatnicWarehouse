package com.example.catnicwarehouse.defectiveItems.stockyards.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.GetDefectiveArticleUseCase
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.sealedClasses.GetDefectiveArticlesEvent
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.sealedClasses.GetDefectiveArticlesViewState
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.GetWarehouseStockyardInventoryEntriesUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.defectiveArticles.DefectiveArticleList
import com.example.shared.repository.defectiveArticles.GetDefectiveArticleUIModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by Enoklit on 04.12.2024.
 */
@HiltViewModel
class DefectiveArticleViewModel @Inject constructor(
    private val getDefectiveArticleUseCase: GetDefectiveArticleUseCase,
    private val getWarehouseStockyardInventoryEntriesUseCase: GetWarehouseStockyardInventoryEntriesUseCase,

    ): BaseViewModel() {
    private val _getDefectiveArticle = MutableStateFlow<GetDefectiveArticlesViewState>(
        GetDefectiveArticlesViewState.Empty)
    val getDefectiveArticle: StateFlow<GetDefectiveArticlesViewState> = _getDefectiveArticle

    fun onEvent(event: GetDefectiveArticlesEvent){
        when(event){
            is GetDefectiveArticlesEvent.Loading -> {getDefectiveArticlesList(event.WarehouseCode)}
            GetDefectiveArticlesEvent.Reset -> _getDefectiveArticle.value = GetDefectiveArticlesViewState.Empty
            is GetDefectiveArticlesEvent.GetWarehouseStockyardInventoryEntries -> getWarehouseStockyardInventoryEntries(event.articleId, event.stockyardId, event.warehouseCode, event.titleText)
        }
    }
    private fun getWarehouseStockyardInventoryEntries(
        articleId: String?,
        stockYardId: String?,
        warehouseCode: String?,
        titleText:String
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
                            _getDefectiveArticle.value = GetDefectiveArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _getDefectiveArticle.value = GetDefectiveArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _getDefectiveArticle.value =
                                GetDefectiveArticlesViewState.WarehouseStockyardInventoryEntriesResponse(
                                    warehouseStockyardInventoryEntriesResponse = result.data,
                                    titleText = titleText
                                )
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _getDefectiveArticle.value =
                GetDefectiveArticlesViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getDefectiveArticlesList(WarehouseCode: String?){
        if (hasNetwork()){
            getDefectiveArticleUseCase.getWarehouseStockyard(
                WarehouseCode,
            ).onEach { result ->
                when(result){
                    is Resource.Loading -> {
                        showProgressBar()
                        _getDefectiveArticle.value = GetDefectiveArticlesViewState.Loading
                    }
                    is Resource.Error -> {
                        _getDefectiveArticle.value = GetDefectiveArticlesViewState.Error(result.message)
                    }
                    is Resource.Success -> {
                        val current = result.data
                        if (current != null) {
                            val uiModel = mapDefectiveArticlesDTOUIModelCurrent(current)
                            _getDefectiveArticle.value = GetDefectiveArticlesViewState.DefectiveArticles(uiModel)
                        } else {
                            _getDefectiveArticle.value = GetDefectiveArticlesViewState.Error("")
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }else {
            _getDefectiveArticle.value =
                GetDefectiveArticlesViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    private fun mapDefectiveArticlesDTOUIModelCurrent(getDefectiveArticle: List<DefectiveArticleList>): List<GetDefectiveArticleUIModel>{
        return getDefectiveArticle.map { stockyard ->
        GetDefectiveArticleUIModel(
            id = stockyard.id,
            warehouseStockYardInventoryEntryId = stockyard.warehouseStockYardInventoryEntryId,
            warehouseCode = stockyard.warehouseCode,
            warehouseName = stockyard.warehouseName,
            warehouseStockYardId = stockyard.warehouseStockYardId,
            articleId = stockyard.articleId,
            articleMatchCode = stockyard.articleMatchCode,
            articleDescription = stockyard.articleDescription,
            unitCode = stockyard.unitCode,
            defectiveAmount = stockyard.defectiveAmount,
            restoredAmount = stockyard.restoredAmount,
            originType = stockyard.originType,
            originObjectId = stockyard.originObjectId,
            reportedTimestamp = stockyard.reportedTimestamp,
            reportedBy = stockyard.reportedBy,
            changedTimestamp = stockyard.changedTimestamp,
            changedBy = stockyard.changedBy,
            reason = stockyard.reason,
            comment = stockyard.comment,
            status = stockyard.status
        )

        }
    }

}