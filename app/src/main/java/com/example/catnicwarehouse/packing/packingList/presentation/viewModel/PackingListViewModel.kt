package com.example.catnicwarehouse.packing.packingList.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.CancelPackingListUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.GetDefaultPackingZonesUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.catnicwarehouse.packing.packingItem.domain.useCases.GetPackingListCommentUseCase
import com.example.catnicwarehouse.packing.packingList.domain.useCases.GetAssignedPackingListsUseCase
import com.example.catnicwarehouse.packing.packingList.domain.useCases.GetPackingListUseCase
import com.example.catnicwarehouse.packing.packingList.domain.useCases.GetPackingListsUseCase
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListEvent
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
class PackingListViewModel @Inject constructor(
    private val getPackingListUseCase: GetPackingListUseCase,
    private val getPackingListsUseCase: GetPackingListsUseCase,
    private val getAssignedPackingListsUseCase: GetAssignedPackingListsUseCase,
    private val defaultPackingZonesUseCase: GetDefaultPackingZonesUseCase,
    private val cancelPackingListUseCase: CancelPackingListUseCase
) : BaseViewModel() {


    private val _packingListFlow =
        MutableStateFlow<PackingListViewState>(PackingListViewState.Empty)
    val packingListFlow: StateFlow<PackingListViewState> = _packingListFlow

    fun onEvent(event: PackingListEvent) {
        when (event) {
            is PackingListEvent.GetPackingList -> getPackingList(event.id)
            is PackingListEvent.GetPackingLists -> getPackingLists(event.inProgress)
            PackingListEvent.GetAssignedPackingLists -> getAssignedPackingList()
            PackingListEvent.Empty -> _packingListFlow.value = PackingListViewState.Empty
            PackingListEvent.GetDefaultPackingZones -> getDefaultPackingZones()
            is PackingListEvent.CancelPackingList -> cancelPackingList(event.id,event.cancelPackingRequestModel)
        }
    }

    private fun getAssignedPackingList(
    ) {
        if (hasNetwork()) {
            getAssignedPackingListsUseCase.invoke().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _packingListFlow.value = PackingListViewState.Loading
                    }

                    is Resource.Error -> {
                        _packingListFlow.value = PackingListViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _packingListFlow.value =
                            PackingListViewState.GetAssignedPackingLists(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _packingListFlow.value =
                PackingListViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun getPackingList(
        id: String?,
    ) {
        if (hasNetwork()) {
            getPackingListUseCase.invoke(
                id = id,
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _packingListFlow.value = PackingListViewState.Loading
                    }

                    is Resource.Error -> {
                        _packingListFlow.value = PackingListViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _packingListFlow.value =
                            PackingListViewState.GetPackingList(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _packingListFlow.value =
                PackingListViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getPackingLists(
        inProgress: Boolean?,
    ) {
        if (hasNetwork()) {
            getPackingListsUseCase.invoke(
                inProgress = inProgress,
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _packingListFlow.value = PackingListViewState.Loading
                    }

                    is Resource.Error -> {
                        _packingListFlow.value = PackingListViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _packingListFlow.value =
                            PackingListViewState.GetPackingLists(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _packingListFlow.value =
                PackingListViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getDefaultPackingZones() {
        if (hasNetwork()) {
            defaultPackingZonesUseCase.invoke(IncomingConstants.WarehouseParam)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _packingListFlow.value = PackingListViewState.Loading
                        }

                        is Resource.Error -> {
                            _packingListFlow.value =
                                PackingListViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _packingListFlow.value =
                                PackingListViewState.GetDefaultPackingZonesResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _packingListFlow.value =
                PackingListViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun cancelPackingList(
        id: String?,
        cancelPackingRequestModel: CancelPackingRequestModel
    ) {
        if (hasNetwork()) {
            cancelPackingListUseCase.invoke(id, cancelPackingRequestModel)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _packingListFlow.value = PackingListViewState.Loading
                        }

                        is Resource.Error -> {
                            _packingListFlow.value =
                                PackingListViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _packingListFlow.value =
                                PackingListViewState.GetCancelPackingListResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _packingListFlow.value =
                PackingListViewState.Error(getString(R.string.no_internet_connection))
        }
    }
}