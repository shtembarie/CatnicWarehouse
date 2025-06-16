package com.example.catnicwarehouse.incoming.comment.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.comment.domain.useCase.SetDeliveryNoteUseCase
import com.example.catnicwarehouse.incoming.comment.presentation.fragment.CommentFragment
import com.example.catnicwarehouse.incoming.comment.presentation.sealedClass.CommentEvent
import com.example.catnicwarehouse.incoming.comment.presentation.sealedClass.CommentViewState
import com.example.catnicwarehouse.incoming.matchFound.domain.useCase.CreateDeliveryItemUseCase
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.MatchFoundEvent
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.MatchFoundViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import com.example.shared.networking.network.purchaseOrder.model.CreateDeliveryItemRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val setDeliveryNoteUseCase: SetDeliveryNoteUseCase
) : BaseViewModel() {


    private val _commentFlow =
        MutableStateFlow<CommentViewState>(CommentViewState.Empty)
    val commentFlow: StateFlow<CommentViewState> = _commentFlow


    private fun setDeliveryNote(
        deliveryId: String,
        noteRequestModel: DeliveryNoteRequestModel
    ) {
        if (hasNetwork()) {
            setDeliveryNoteUseCase.invoke(
                deliveryId = deliveryId,
                deliveryNoteRequestModel = noteRequestModel
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _commentFlow.value = CommentViewState.Loading
                    }

                    is Resource.Error -> {
                        _commentFlow.value = CommentViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _commentFlow.value =
                            CommentViewState.DeliveryNoteSaved(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _commentFlow.value = CommentViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    fun onEvent(event: CommentEvent) {
        when (event) {
            is CommentEvent.SaveDeliveryNote -> setDeliveryNote(event.note, event.noteRequestModel)
        }
    }
}