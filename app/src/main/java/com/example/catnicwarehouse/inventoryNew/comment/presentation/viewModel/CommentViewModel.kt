package com.example.catnicwarehouse.inventoryNew.comment.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.inventoryNew.articles.domain.useCase.GetInventoryItemsUseCase
import com.example.catnicwarehouse.inventoryNew.articles.presentation.sealedClasses.InventoryItemsEvent
import com.example.catnicwarehouse.inventoryNew.articles.presentation.sealedClasses.InventoryItemsViewState
import com.example.catnicwarehouse.inventoryNew.comment.domain.useCase.UpdateInventoryCommentUseCase
import com.example.catnicwarehouse.inventoryNew.comment.presentation.sealedClasses.InventoryCommentEvent
import com.example.catnicwarehouse.inventoryNew.comment.presentation.sealedClasses.InventoryCommentViewState
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.GetWarehouseStockyardInventoryEntriesUseCase
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val updateInventoryCommentUseCase: UpdateInventoryCommentUseCase,
    private val getInventoryItemsUseCase: GetInventoryItemsUseCase
) : BaseViewModel() {


    private val _commentFlow =
        MutableStateFlow<InventoryCommentViewState>(InventoryCommentViewState.Empty)
    val commentFlow: StateFlow<InventoryCommentViewState> = _commentFlow


    private fun updateComment(
        id: String?,
        itemId: String?,
        deliveryNoteRequestModel: DeliveryNoteRequestModel?
    ) {
        if (hasNetwork()) {
            updateInventoryCommentUseCase.invoke(id, itemId, deliveryNoteRequestModel)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _commentFlow.value = InventoryCommentViewState.Loading
                        }

                        is Resource.Error -> {
                            _commentFlow.value =
                                InventoryCommentViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _commentFlow.value = InventoryCommentViewState.CommentUpdated(deliveryNoteRequestModel?.note)

                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _commentFlow.value =
                InventoryCommentViewState.Error(getString(R.string.no_internet_connection))
        }
    }



    fun onEvent(event: InventoryCommentEvent) {
        when (event) {
            InventoryCommentEvent.Reset -> _commentFlow.value = InventoryCommentViewState.Empty
            is InventoryCommentEvent.UpdateComment -> updateComment(
                event.id, event.itemId, event.deliveryNoteRequestModel
            )
        }
    }
}
