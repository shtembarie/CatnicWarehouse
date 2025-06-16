package com.example.catnicwarehouse.packing.packingItem.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.packing.packingItem.domain.useCases.GetPackingItemsUseCase
import com.example.catnicwarehouse.packing.packingItem.domain.useCases.GetPackingListCommentUseCase
import com.example.catnicwarehouse.packing.packingItem.domain.useCases.StartPackingUseCase
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsEvent
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsViewState
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class PackingItemsViewModel @Inject constructor(
    private val getPackingItemsUseCase: GetPackingItemsUseCase,
    private val startPackingUseCase: StartPackingUseCase,
    private val getPackingListCommentUseCase: GetPackingListCommentUseCase
) : BaseViewModel() {


    private val _packingItemsFlow = MutableStateFlow<PackingItemsViewState>(PackingItemsViewState.Empty)
    val packingItemsFlow: StateFlow<PackingItemsViewState> = _packingItemsFlow

    fun onEvent(event: PackingItemsEvent) {
        when (event) {
            is PackingItemsEvent.GetPackingItems -> getPackingItems(event.id)
            is PackingItemsEvent.StartPacking -> startPacking(event.id)
            PackingItemsEvent.Empty -> _packingItemsFlow.value = PackingItemsViewState.Empty
            is PackingItemsEvent.GetPackingListComment -> getPackingListComment(event.id)
        }
    }

    private fun getPackingItems(
        id: String?,
    ) {
        if (hasNetwork()) {
            getPackingItemsUseCase.invoke(
                id = id,
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _packingItemsFlow.value = PackingItemsViewState.Loading
                    }

                    is Resource.Error -> {
                        _packingItemsFlow.value = PackingItemsViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _packingItemsFlow.value =
                            PackingItemsViewState.GetPackingItemsResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _packingItemsFlow.value =
                PackingItemsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun startPacking(
        id: String?
    ) {
        if (hasNetwork()) {
            startPackingUseCase.invoke(
                id = id
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _packingItemsFlow.value = PackingItemsViewState.Loading
                    }

                    is Resource.Error -> {
                        _packingItemsFlow.value = PackingItemsViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _packingItemsFlow.value =
                            PackingItemsViewState.StartPackingResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _packingItemsFlow.value =
                PackingItemsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getPackingListComment(
        id: String?,
    ) {
        if (hasNetwork()) {
            getPackingListCommentUseCase.invoke(id).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _packingItemsFlow.value = PackingItemsViewState.Loading
                    }

                    is Resource.Error -> {
                        _packingItemsFlow.value = PackingItemsViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _packingItemsFlow.value =
                            PackingItemsViewState.GetPackingListComment(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _packingItemsFlow.value =
                PackingItemsViewState.Error(getString(R.string.no_internet_connection))
        }
    }






}