package com.example.catnicwarehouse.packing.addPackingItems.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.packing.addPackingItems.domain.useCase.GetItemsForPackingUseCase
import com.example.catnicwarehouse.packing.addPackingItems.presentation.sealedClasses.AddPackingItemsEvent
import com.example.catnicwarehouse.packing.addPackingItems.presentation.sealedClasses.AddPackingItemsViewState
import com.example.catnicwarehouse.packing.matchFound.domain.useCases.PickAmountUseCase
import com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses.PackingArticlesEvent
import com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses.PackingArticlesViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.networking.network.packing.model.packingList.GetItemsForPackingResponseModelItem
import com.example.shared.networking.network.packing.model.packingList.WarehouseStockYardPicking
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AddPackingItemsViewModel @Inject constructor(
    private val getItemsForPackingUseCase: GetItemsForPackingUseCase
) : BaseViewModel() {


    private val _addPackingItemsFlow =
        MutableStateFlow<AddPackingItemsViewState>(AddPackingItemsViewState.Empty)
    val addPackingItemsFlow: StateFlow<AddPackingItemsViewState> = _addPackingItemsFlow

    fun onEvent(event: AddPackingItemsEvent) {
        when (event) {
            is AddPackingItemsEvent.GetItemsForPacking -> getItemsForPacking(event.packingListId)
            AddPackingItemsEvent.Reset -> _addPackingItemsFlow.value = AddPackingItemsViewState.Empty
        }
    }

    private fun getItemsForPacking(
        packingListId: String,
    ) {
        if (hasNetwork()) {
            getItemsForPackingUseCase.invoke(packingListId)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _addPackingItemsFlow.value = AddPackingItemsViewState.Loading
                        }

                        is Resource.Error -> {
                            _addPackingItemsFlow.value = AddPackingItemsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _addPackingItemsFlow.value = AddPackingItemsViewState.GetItemsForPackingResponse(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _addPackingItemsFlow.value =
                AddPackingItemsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    fun getDummyPackingItems(): List<GetItemsForPackingResponseModelItem> {
        return listOf(
            GetItemsForPackingResponseModelItem(
                id = "9eb29d12-0352-40b7-8f4c-2a769e170947",
                articleId = "030",
                unitCode = "BUND",
                packedAmount = 6,
                itemAmount = 10,
                warehouseStockYardPickings = listOf(
                    WarehouseStockYardPicking(
                        unitCode = "BUND",
                        amount = 5f,
                        warehouseStockYardId = 1,
                        warehouseStockYardName = "Stockyard A"
                    ),
                    WarehouseStockYardPicking(
                        unitCode = "BUND",
                        amount = 1f,
                        warehouseStockYardId = 2,
                        warehouseStockYardName = "Stockyard B"
                    )
                )
            ),
            GetItemsForPackingResponseModelItem(
                id = "e1b807e1-57a2-4b07-b2f8-7b6bcf391b92",
                articleId = "031",
                unitCode = "BUND",
                packedAmount = 0,
                itemAmount = 10,
                warehouseStockYardPickings = listOf(
                    WarehouseStockYardPicking(
                        unitCode = "BUND",
                        amount = 4f,
                        warehouseStockYardId = 3,
                        warehouseStockYardName = "Stockyard C"
                    )
                )
            )
        )
    }



}