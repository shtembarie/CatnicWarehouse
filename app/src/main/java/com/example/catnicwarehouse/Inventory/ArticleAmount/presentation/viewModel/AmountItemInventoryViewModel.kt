package com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.sealedClasses.AmountItemEvent
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.sealedClasses.AmountItemViewState
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.domain.useCase.UpdateInventoryItemUseCase
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.CancelPackingListUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.GetDefaultPackingZonesUseCase
import com.example.catnicwarehouse.packing.packingList.domain.useCases.GetAssignedPackingListsUseCase
import com.example.catnicwarehouse.packing.packingList.domain.useCases.GetPackingListUseCase
import com.example.catnicwarehouse.packing.packingList.domain.useCases.GetPackingListsUseCase
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListEvent
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import com.example.shared.repository.inventory.model.SetInventoryItems
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AmountItemInventoryViewModel @Inject constructor(
    private val updateInventoryItemUseCase: UpdateInventoryItemUseCase,
) : BaseViewModel() {


    private val _amountItemFlow =
        MutableStateFlow<AmountItemViewState>(AmountItemViewState.Empty)
    val amountItemFlow: StateFlow<AmountItemViewState> = _amountItemFlow

    fun onEvent(event: AmountItemEvent) {
        when (event) {
            AmountItemEvent.Empty -> {
            }
            is AmountItemEvent.UpdateInventoryItem -> {
                updateInventoryItem(
                    stockyard = event.stockyardId,
                    itemId = event.itemId,
                    setInventoryItems = event.setInventoryItems
                )
            }
        }
    }

    private fun updateInventoryItem(
        stockyard:Int?,
        itemId:Int?,
        setInventoryItems: SetInventoryItems?
    ) {
        if (hasNetwork()) {
            updateInventoryItemUseCase.invoke(stockyard,itemId,setInventoryItems).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _amountItemFlow.value = AmountItemViewState.Loading
                    }

                    is Resource.Error -> {
                        _amountItemFlow.value = AmountItemViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _amountItemFlow.value =
                            AmountItemViewState.InventoryItemUpdated(result.data)
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