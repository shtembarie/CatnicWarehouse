package com.example.catnicwarehouse.incoming.suppliers.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.deliveryType.domain.useCase.CreateDeliveryUseCase
import com.example.catnicwarehouse.incoming.deliveryType.presentation.sealedClass.DeliveryTypeViewState
import com.example.catnicwarehouse.incoming.suppliers.domain.usecase.SearchCustomerUseCase
import com.example.catnicwarehouse.incoming.suppliers.domain.usecase.SearchVendorUseCase
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerEvent
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerViewState
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorEvent
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorViewState
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchCustomerViewModel @Inject constructor(
    private val searchCustomerUseCase: SearchCustomerUseCase,
    private val createDeliveryUseCase: CreateDeliveryUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
) : BaseViewModel() {

    private val _searchedCustomersFlow =
        MutableStateFlow<SearchCustomerViewState>(SearchCustomerViewState.Empty)
    val searchedCustomersFlow: StateFlow<SearchCustomerViewState> = _searchedCustomersFlow

    fun onEvent(event: SearchCustomerEvent) {
        when (event) {
            is SearchCustomerEvent.SearchCustomer -> {
                loadSearchedCustomers(event.searchTerm)
            }

            SearchCustomerEvent.EmptySearch -> {}
            is SearchCustomerEvent.CreateDelivery -> createDelivery(event.createDeliveryRequestModel)
            is SearchCustomerEvent.SearchArticle -> {searchArticlesForDelivery(event.searchTerm)}
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
                            _searchedCustomersFlow.value = SearchCustomerViewState.Loading
                        }

                        is Resource.Error -> {
                            _searchedCustomersFlow.value =
                                SearchCustomerViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _searchedCustomersFlow.value =
                                SearchCustomerViewState.SearchedCustomers(result.data)
                        }

                        else -> {}
                    }
                }.launchIn(viewModelScope)
        } else {
            _searchedCustomersFlow.value =
                SearchCustomerViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun createDelivery(
        createDeliveryRequestModel: CreateDeliveryRequestModel
    ) {

        if (hasNetwork()) {
            createDeliveryUseCase.invoke(createDeliveryRequestModel = createDeliveryRequestModel)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _searchedCustomersFlow.value = SearchCustomerViewState.Loading
                        }

                        is Resource.Error -> {
                            _searchedCustomersFlow.value =
                                SearchCustomerViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _searchedCustomersFlow.value =
                                SearchCustomerViewState.DeliveryCreated(result.data)
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _searchedCustomersFlow.value =
                SearchCustomerViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun searchArticlesForDelivery(
        searchTerm: String
    ) {
        if (hasNetwork()) {
            searchArticlesForDeliveryUseCase.invoke(searchTerm = searchTerm)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _searchedCustomersFlow.value = SearchCustomerViewState.Loading
                        }

                        is Resource.Error -> {
                            _searchedCustomersFlow.value = SearchCustomerViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _searchedCustomersFlow.value =
                                SearchCustomerViewState.ArticlesForDeliveryFound(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _searchedCustomersFlow.value =
                SearchCustomerViewState.Error(getString(R.string.no_internet_connection))
        }
    }
}