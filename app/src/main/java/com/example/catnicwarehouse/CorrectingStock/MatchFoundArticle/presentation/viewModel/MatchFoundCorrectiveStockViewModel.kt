package com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.domain.useCase.CorrectInventoryItemUseCase
import com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.presentation.sealedClasses.CorrectEvent
import com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.presentation.sealedClasses.CorrectInventorViewState
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.correctingStock.model.CorrectInventoryItems
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by Enoklit on 18.11.2024.
 */
@HiltViewModel
class MatchFoundCorrectiveStockViewModel @Inject constructor(
    private val correctInventoryItemUseCase: CorrectInventoryItemUseCase
): BaseViewModel() {
    private val _matchFoundFlow =
        MutableStateFlow<CorrectInventorViewState>(CorrectInventorViewState.Empty)
    val matchFoundFlow: StateFlow<CorrectInventorViewState> = _matchFoundFlow


    private fun correctInventoryItems(
        warehouseStockYardId: Int?,
        entryId: Int?,
        correctInventoryItems: CorrectInventoryItems
    ){
        if (hasNetwork()){
            correctInventoryItemUseCase.invoke(
                warehouseStockYardId = warehouseStockYardId,
                entryId = entryId,
                correctInventoryItems = correctInventoryItems
            ).onEach { result ->
                when (result){
                    is Resource.Loading -> {
                        _matchFoundFlow.value = CorrectInventorViewState.Loading
                    }
                    is Resource.Error -> {
                        _matchFoundFlow.value = CorrectInventorViewState.Error(result.message)
                    }
                    is Resource.Success -> {
                        _matchFoundFlow.value = CorrectInventorViewState.CorrectedInventorySaved(result.data)
                    }
                    else -> {}
                }
            }.launchIn(viewModelScope)
        }else {
            _matchFoundFlow.value = CorrectInventorViewState.Error(getString(R.string.no_internet_connection))
        }

    }

    fun onEvent(event : CorrectEvent){
        when(event){
            is CorrectEvent.CorrectInventoryAmountUnit -> correctInventoryItems(event.warehouseStockYardId, event.entryId, event.correctInventoryItems)
        }
    }

}