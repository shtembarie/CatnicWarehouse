package com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.sealedClasses.MatchFoundInventoryItemEvent
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.domain.useCase.UpdateInventoryItemUseCase
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.sealedClasses.MatchFoundInventoryViewState
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.inventory.model.SetInventoryItems
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchFoundInventoryViewModel @Inject constructor(
    private val updateInventoryItemUseCase: UpdateInventoryItemUseCase,
) : BaseViewModel() {

    private val _matchFoundFlow =
        MutableStateFlow<GetInventoryByIdViewState>(GetInventoryByIdViewState.Empty)
    val matchFoundFlow: StateFlow<GetInventoryByIdViewState> = _matchFoundFlow

    private suspend fun updateInventoryItems(
        stockyardId: Int,
        inventoryItemsList: ArrayList<Pair<Int, SetInventoryItems>>
    ): Boolean {
        return try {
            coroutineScope {
                val deferredResults = inventoryItemsList.map { (itemId, setInventoryItems) ->
                    async {
                        if (hasNetwork()) {
                            updateInventoryItemUseCase.invoke(
                                id = stockyardId,
                                itemId = itemId,
                                setInventoryItems = setInventoryItems
                            ).collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        _matchFoundFlow.value = GetInventoryByIdViewState.Loading
                                    }
                                    is Resource.Error -> {
                                        _matchFoundFlow.value = GetInventoryByIdViewState.Error(result.message)
                                    }
                                    is Resource.Success -> {
                                    }
                                    else -> {}
                                }
                            }
                        } else {
                            _matchFoundFlow.value = GetInventoryByIdViewState.Error(getString(R.string.no_internet_connection))
                        }
                    }
                }


                deferredResults.awaitAll()


                true
            }
        } catch (e: Exception) {
            // Handle the error case
            _matchFoundFlow.value = GetInventoryByIdViewState.Error(e.message ?: "Unknown error")
            false
        }
    }


    fun onEvent(event: MatchFoundInventoryItemEvent) {
        when (event) {
            is MatchFoundInventoryItemEvent.UpdateInventoryItems -> {

                viewModelScope.launch {
                    val success = updateInventoryItems(event.id, event.inventoryItemsList)
                    if (success) {
                        _matchFoundFlow.value = GetInventoryByIdViewState.InventoryItemUpdated(success)
                    }
                }
            }

            MatchFoundInventoryItemEvent.Reset -> _matchFoundFlow.value =
                GetInventoryByIdViewState.Reset

            MatchFoundInventoryItemEvent.LoadInventory -> _matchFoundFlow.value =
                GetInventoryByIdViewState.Loading
        }
    }

}
