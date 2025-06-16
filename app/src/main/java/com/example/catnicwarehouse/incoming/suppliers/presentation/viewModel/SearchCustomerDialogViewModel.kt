package com.example.catnicwarehouse.incoming.suppliers.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.suppliers.domain.usecase.SearchCustomerUseCase
import com.example.catnicwarehouse.incoming.suppliers.domain.usecase.SearchVendorUseCase
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerEvent
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerViewState
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
class SearchCustomerDialogViewModel  @Inject constructor(
    private val searchCustomerUseCase: SearchCustomerUseCase
) : BaseViewModel() {


    private val _searchedCustomerFlow =
        MutableStateFlow<SearchCustomerViewState>(SearchCustomerViewState.Empty)
    val searchedCustomerFlow: StateFlow<SearchCustomerViewState> = _searchedCustomerFlow

    fun onEvent(event: SearchCustomerEvent) {
        when (event) {
            is SearchCustomerEvent.SearchCustomer -> {
                loadSearchedCustomers(event.searchTerm)
            }

            SearchCustomerEvent.EmptySearch -> _searchedCustomerFlow.value= SearchCustomerViewState.Empty
            is SearchCustomerEvent.CreateDelivery -> {}
            is SearchCustomerEvent.SearchArticle -> {}
        }
    }


    private fun loadSearchedCustomers(
        searchTerm: String? = null,
    ) {
        if (hasNetwork()) {
            searchCustomerUseCase.invoke(searchTerm = searchTerm)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _searchedCustomerFlow.value = SearchCustomerViewState.Loading
                        }

                        is Resource.Error -> {
                            _searchedCustomerFlow.value = SearchCustomerViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _searchedCustomerFlow.value =
                                SearchCustomerViewState.SearchedCustomers(result.data)
                        }

                        else -> {}
                    }
                }.launchIn(viewModelScope)
        } else {
            _searchedCustomerFlow.value =
                SearchCustomerViewState.Error(getString(R.string.no_internet_connection))
        }
    }
}