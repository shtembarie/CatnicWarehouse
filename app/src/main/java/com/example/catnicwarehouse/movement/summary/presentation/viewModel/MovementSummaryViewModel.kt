package com.example.catnicwarehouse.movement.summary.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.movement.summary.domain.usecase.DropOffUseCase
import com.example.catnicwarehouse.movement.summary.presentation.sealedClasses.MovementSummaryEvent
import com.example.catnicwarehouse.movement.summary.presentation.sealedClasses.MovementSummaryViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.movements.DropOffRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MovementSummaryViewModel @Inject constructor(
    private val dropOffUseCase: DropOffUseCase

) : BaseViewModel() {


    private val _movementSummaryFlow = MutableStateFlow<MovementSummaryViewState>(MovementSummaryViewState.Empty)
    val movementSummaryFlow: StateFlow<MovementSummaryViewState> = _movementSummaryFlow

    fun onEvent(event: MovementSummaryEvent) {
        when (event) {
            is MovementSummaryEvent.DropOff -> dropOff(event.id,event.dropOffRequestModel)
            MovementSummaryEvent.Reset -> _movementSummaryFlow.value = MovementSummaryViewState.Empty
        }
    }

    private fun dropOff(
        id: String?,
        dropOffRequestModel: DropOffRequestModel?
    ) {
        if (hasNetwork()) {
            dropOffUseCase.invoke(
                id = id,
                dropOffRequestModel = dropOffRequestModel
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _movementSummaryFlow.value = MovementSummaryViewState.Loading
                    }

                    is Resource.Error -> {
                        _movementSummaryFlow.value = MovementSummaryViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _movementSummaryFlow.value =
                            MovementSummaryViewState.DropOffResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _movementSummaryFlow.value =
                MovementSummaryViewState.Error(getString(R.string.no_internet_connection))
        }
    }


}