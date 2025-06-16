package com.example.catnicwarehouse.inventoryNew.amount.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.domain.useCase.UpdateInventoryItemUseCase
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.amountItem.domain.useCases.GetArticleUnitsUseCase
import com.example.catnicwarehouse.inventoryNew.amount.presentation.sealedClasses.AmountItemEvent
import com.example.catnicwarehouse.inventoryNew.amount.presentation.sealedClasses.AmountItemViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.inventory.model.SetInventoryItems
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AmountViewModel @Inject constructor(
    private val getArticleUnitsUseCase: GetArticleUnitsUseCase
) : BaseViewModel() {


    private val _amountItemFlow =
        MutableStateFlow<AmountItemViewState>(AmountItemViewState.Empty)
    val amountItemFlow: StateFlow<AmountItemViewState> = _amountItemFlow

    fun onEvent(event: AmountItemEvent) {
        when (event) {
            AmountItemEvent.Reset -> {_amountItemFlow.value = AmountItemViewState.Empty}
            is AmountItemEvent.GetArticleUnits -> {getArticleUnits(event.articleId)}
        }
    }

    private fun getArticleUnits(
        articleId: String,
    ) {
        if (hasNetwork()) {
            getArticleUnitsUseCase.invoke(
                articleId = articleId,
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _amountItemFlow.value = AmountItemViewState.Loading
                    }

                    is Resource.Error -> {
                        _amountItemFlow.value = AmountItemViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _amountItemFlow.value =
                            AmountItemViewState.ArticleUnits(result.data?.articleUnits)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _amountItemFlow.value =
                AmountItemViewState.Error(getString(R.string.no_internet_connection))
        }
    }
}