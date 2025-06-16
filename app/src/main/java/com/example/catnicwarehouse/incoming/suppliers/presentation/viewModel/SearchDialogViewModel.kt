package com.example.catnicwarehouse.incoming.suppliers.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.suppliers.domain.usecase.SearchVendorUseCase
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorEvent
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchDialogViewModel @Inject constructor(
    private val searchVendorUseCase: SearchVendorUseCase
) : BaseViewModel() {


    private val _searchedVendorsFlow =
        MutableStateFlow<SearchVendorViewState>(SearchVendorViewState.Empty)
    val searchedVendorsFlow: StateFlow<SearchVendorViewState> = _searchedVendorsFlow

    fun onEvent(event: SearchVendorEvent) {
        when (event) {
            is SearchVendorEvent.SearchVendor -> {
                loadSearchedVendors(event.searchTerm)
            }

            SearchVendorEvent.EmptySearch -> _searchedVendorsFlow.value=SearchVendorViewState.Empty
            is SearchVendorEvent.CreateDelivery -> {}
            is SearchVendorEvent.SearchArticle -> {}
        }
    }


    private fun loadSearchedVendors(
        searchTerm: String? = null,
    ) {
        if (hasNetwork()) {
            searchVendorUseCase.invoke(searchTerm = searchTerm)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _searchedVendorsFlow.value = SearchVendorViewState.Loading
                        }

                        is Resource.Error -> {
                            _searchedVendorsFlow.value = SearchVendorViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _searchedVendorsFlow.value =
                                SearchVendorViewState.SearchedVendors(result.data)
                        }

                        else -> {}
                    }
                }.launchIn(viewModelScope)
        } else {
            _searchedVendorsFlow.value =
                SearchVendorViewState.Error(getString(R.string.no_internet_connection))
        }
    }
}