package com.example.catnicwarehouse.defectiveItems.matchFoundFragment.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.useCase.GetDefectiveArticleByIdUseCase
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.useCase.PostArticlesUseCase
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.presentation.sealedClasses.GetDefectiveArticlesByIdEvent
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.presentation.sealedClasses.GetDefectiveArticlesByIdViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.defectiveArticles.DefectivesArticlesById
import com.example.shared.repository.defectiveArticles.GetDefectiveArticleByIdUIModel
import com.example.shared.repository.defectiveArticles.PostDefectiveArticlesModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by Enoklit on 05.12.2024.
 */
@HiltViewModel
class DefectiveArticleByIdViewModel @Inject constructor(
    private val getDefectiveArticleByIdUseCase: GetDefectiveArticleByIdUseCase,
    private val postArticlesUseCase : PostArticlesUseCase
): BaseViewModel(){
    private val _getDefectiveArticleById = MutableStateFlow<GetDefectiveArticlesByIdViewState>(
        GetDefectiveArticlesByIdViewState.Empty)
    val getDefectiveArticleById: StateFlow<GetDefectiveArticlesByIdViewState> = _getDefectiveArticleById

    fun onEvent(event: GetDefectiveArticlesByIdEvent){
        when(event){
            is GetDefectiveArticlesByIdEvent.Loading -> {getDefectiveArticleByIdList(event.id)}
            GetDefectiveArticlesByIdEvent.Reset -> _getDefectiveArticleById.value = GetDefectiveArticlesByIdViewState.Empty
            is GetDefectiveArticlesByIdEvent.PostArticles -> {postArticles(event.postDefectiveArticlesModel)}
        }
    }
    private fun postArticles(
        postDefectiveArticlesModel: PostDefectiveArticlesModel?
    ){
        if (hasNetwork()){
            postArticlesUseCase.invoke(
                postDefectiveArticlesModel = postDefectiveArticlesModel
            ).onEach { result ->
                when(result){
                    is Resource.Loading -> {
                        _getDefectiveArticleById.value = GetDefectiveArticlesByIdViewState.Loading
                    }
                    is Resource.Error -> {
                        _getDefectiveArticleById.value = GetDefectiveArticlesByIdViewState.Error(result.message)
                    }
                    is Resource.Success -> {
                        _getDefectiveArticleById.value =
                            GetDefectiveArticlesByIdViewState.CreateNewDefectiveItem(result.data)
                    }
                }
            }.launchIn(viewModelScope)
        }else {
            _getDefectiveArticleById.value =
                GetDefectiveArticlesByIdViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    private fun getDefectiveArticleByIdList(id: Int?){
        if (hasNetwork()){
            getDefectiveArticleByIdUseCase.getWarehouseStockyardById(
                id
            ).onEach { result ->
                when(result){
                    is Resource.Loading -> {
                        showProgressBar()
                        _getDefectiveArticleById.value = GetDefectiveArticlesByIdViewState.Loading
                    }
                    is Resource.Error -> {
                        _getDefectiveArticleById.value = GetDefectiveArticlesByIdViewState.Error(result.message)
                    }
                    is Resource.Success -> {
                        val currentId = result.data
                        if (currentId != null){
                            val uiModel = mapDefectiveArticlesByIdDTOUIModelCurrent(currentId)
                            _getDefectiveArticleById.value = GetDefectiveArticlesByIdViewState.DefectiveArticlesById(uiModel)
                        }else {
                            _getDefectiveArticleById.value = GetDefectiveArticlesByIdViewState.Error("")
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }else {
            _getDefectiveArticleById.value =
                GetDefectiveArticlesByIdViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    private fun mapDefectiveArticlesByIdDTOUIModelCurrent(getDefectiveArticleById: DefectivesArticlesById): GetDefectiveArticleByIdUIModel {
        return GetDefectiveArticleByIdUIModel(
            id = getDefectiveArticleById.id,
            warehouseStockYardInventoryEntryId = getDefectiveArticleById.warehouseStockYardInventoryEntryId,
            warehouseCode = getDefectiveArticleById.warehouseCode,
            warehouseName = getDefectiveArticleById.warehouseName,
            warehouseStockYardId = getDefectiveArticleById.warehouseStockYardId,
            articleId = getDefectiveArticleById.articleId,
            articleMatchCode = getDefectiveArticleById.articleMatchCode,
            articleDescription = getDefectiveArticleById.articleDescription,
            unitCode = getDefectiveArticleById.unitCode,
            defectiveAmount = getDefectiveArticleById.defectiveAmount,
            restoredAmount = getDefectiveArticleById.restoredAmount,
            originType = getDefectiveArticleById.originType,
            originObjectId = getDefectiveArticleById.originObjectId,
            reportedTimestamp = getDefectiveArticleById.reportedTimestamp,
            reportedBy = getDefectiveArticleById.reportedBy,
            changedTimestamp = getDefectiveArticleById.changedTimestamp,
            changedBy = getDefectiveArticleById.changedBy,
            reason = getDefectiveArticleById.reason,
            comment = getDefectiveArticleById.comment,
            status = getDefectiveArticleById.status
        )
    }

}
