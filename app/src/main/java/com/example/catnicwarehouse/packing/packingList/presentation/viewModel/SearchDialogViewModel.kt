package com.example.catnicwarehouse.packing.packingList.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.packing.packingList.domain.useCases.SearchPackingListsUseCase
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListEvent
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.SearchPackingListEvent
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.SearchPackingListViewState
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchDialogViewModel @Inject constructor(
    private val searchPackingListsUseCase: SearchPackingListsUseCase
) : ViewModel() {

    private val _searchedPackingListsFlow = MutableStateFlow<SearchPackingListViewState>(SearchPackingListViewState.Empty)
    val searchedPackingListsFlow: StateFlow<SearchPackingListViewState> = _searchedPackingListsFlow

    fun onEvent(event: SearchPackingListEvent) {
        when (event) {
            SearchPackingListEvent.EmptySearch -> _searchedPackingListsFlow.value = SearchPackingListViewState.Empty
            is SearchPackingListEvent.SearchPackingList -> searchPackingLists(query = event.query)
        }
    }


    private fun searchPackingLists(query: String) {
        viewModelScope.launch {
            searchPackingListsUseCase(query).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _searchedPackingListsFlow.value = SearchPackingListViewState.Empty
                    }
                    is Resource.Success -> {
                        _searchedPackingListsFlow.value = if (result.data.isNullOrEmpty()) {
                            SearchPackingListViewState.Empty
                        } else {
                            SearchPackingListViewState.SearchedPackingLists(result.data)
                        }
                    }
                    is Resource.Error -> {
                        _searchedPackingListsFlow.value = SearchPackingListViewState.Error(result.message)
                    }
                }
            }
        }
    }
}
