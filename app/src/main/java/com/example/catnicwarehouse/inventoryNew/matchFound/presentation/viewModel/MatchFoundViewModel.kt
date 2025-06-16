package com.example.catnicwarehouse.inventoryNew.matchFound.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.inventoryNew.articles.domain.useCase.GetInventoryItemsUseCase
import com.example.catnicwarehouse.inventoryNew.comment.domain.useCase.UpdateInventoryCommentUseCase
import com.example.catnicwarehouse.inventoryNew.comment.presentation.sealedClasses.InventoryCommentEvent
import com.example.catnicwarehouse.inventoryNew.comment.presentation.sealedClasses.InventoryCommentViewState
import com.example.catnicwarehouse.inventoryNew.matchFound.domain.useCase.InventorizeItemUseCase
import com.example.catnicwarehouse.inventoryNew.matchFound.presentation.sealedClasses.MatchFoundEvent
import com.example.catnicwarehouse.inventoryNew.matchFound.presentation.sealedClasses.MatchFoundViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import com.example.shared.repository.inventory.model.InventorizeItemRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MatchFoundViewModel @Inject constructor(
    private val inventorizeItemUseCase: InventorizeItemUseCase
) : BaseViewModel() {


    private val _matchFoundFlow =
        MutableStateFlow<MatchFoundViewState>(MatchFoundViewState.Empty)
    val matchFoundFlow: StateFlow<MatchFoundViewState> = _matchFoundFlow


    private fun inventorizeItem(
        id: Int?,
        inventorizeItemRequestModel: InventorizeItemRequestModel?
    ) {
        if (hasNetwork()) {
            inventorizeItemUseCase.invoke(id.toString(),  inventorizeItemRequestModel)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _matchFoundFlow.value = MatchFoundViewState.Loading
                        }

                        is Resource.Error -> {
                            _matchFoundFlow.value =
                                MatchFoundViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _matchFoundFlow.value = MatchFoundViewState.ItemInventorized(result.data,id)

                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _matchFoundFlow.value =
                MatchFoundViewState.Error(getString(R.string.no_internet_connection))
        }
    }



    fun onEvent(event: MatchFoundEvent) {
        when (event) {
            MatchFoundEvent.Reset -> _matchFoundFlow.value = MatchFoundViewState.Empty
            is MatchFoundEvent.InventorizeItem -> inventorizeItem(
                event.id,event.inventorizeItemRequestModel
            )
        }
    }
}
