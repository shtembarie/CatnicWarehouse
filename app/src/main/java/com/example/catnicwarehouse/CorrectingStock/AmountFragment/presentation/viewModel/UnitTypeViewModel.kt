package com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.domain.useCase.GetArticleUnitsUseCase
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses.GetArticleUnitsEvent
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses.GetArticleUnitsViewState
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.correctingStock.model.GetArticleUnitsParams
import com.example.shared.repository.correctingStock.model.GetArticleUnitsParamsByUIModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by Enoklit on 19.11.2024.
 */
@HiltViewModel
class UnitTypeViewModel @Inject constructor(
    private val getArticleUnitsUseCase: GetArticleUnitsUseCase
): BaseViewModel(){
    private val _getUnitCode = MutableStateFlow<GetArticleUnitsViewState>(
        GetArticleUnitsViewState.Empty)
    val getUnitCode: StateFlow<GetArticleUnitsViewState> = _getUnitCode

    fun onEvent(event: GetArticleUnitsEvent){
        when (event){
            is GetArticleUnitsEvent.Reset -> {
                _getUnitCode.value = GetArticleUnitsViewState.Empty
            }
            is GetArticleUnitsEvent.Loading -> {getArticleUnitsList(event.articleId)}
        }
    }
    private fun getArticleUnitsList(articleId : String){
        if (hasNetwork()){
            getArticleUnitsUseCase.invoke(
                articleId = articleId,
            ).onEach { result ->
                when(result) {
                    is Resource.Loading -> {
                        _getUnitCode.value = GetArticleUnitsViewState.Loading
                    }
                    is Resource.Error -> {
                        _getUnitCode.value = GetArticleUnitsViewState.Error(result.message)
                    }
                    is Resource.Success -> {
                        _getUnitCode.value = GetArticleUnitsViewState.ArticleUnitId(result.data?.articleUnits)
                    }
                    else -> {}

                }
            }.launchIn(viewModelScope)
        }else {
            _getUnitCode.value = GetArticleUnitsViewState.Error(getString(R.string.no_internet_connection))
        }


    }
}