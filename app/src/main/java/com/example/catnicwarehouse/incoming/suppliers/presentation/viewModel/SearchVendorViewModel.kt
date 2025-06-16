package com.example.catnicwarehouse.incoming.suppliers.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.deliveryType.domain.useCase.CreateDeliveryUseCase
import com.example.catnicwarehouse.incoming.suppliers.domain.usecase.SearchVendorUseCase
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerViewState
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorEvent
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorViewState
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsViewState
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
class SearchVendorViewModel @Inject constructor(
    private val searchVendorUseCase: SearchVendorUseCase,
    private val createDeliveryUseCase: CreateDeliveryUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
) : BaseViewModel() {

    private val _searchedVendorsFlow =
        MutableStateFlow<SearchVendorViewState>(SearchVendorViewState.Empty)
    val searchedVendorsFlow: StateFlow<SearchVendorViewState> = _searchedVendorsFlow

    fun onEvent(event: SearchVendorEvent) {
        when (event) {
            is SearchVendorEvent.SearchVendor -> {
                loadSearchedVendors(event.searchTerm)
            }

            SearchVendorEvent.EmptySearch -> {}
            is SearchVendorEvent.CreateDelivery -> createDelivery(event.createDeliveryRequestModel)
            is SearchVendorEvent.SearchArticle -> {searchArticlesForDelivery(event.searchTerm)}
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

    private fun createDelivery(
        createDeliveryRequestModel: CreateDeliveryRequestModel
    ) {

        if (hasNetwork()) {
            createDeliveryUseCase.invoke(createDeliveryRequestModel = createDeliveryRequestModel)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _searchedVendorsFlow.value = SearchVendorViewState.Loading
                        }

                        is Resource.Error -> {
                            _searchedVendorsFlow.value =
                                SearchVendorViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _searchedVendorsFlow.value =
                                SearchVendorViewState.DeliveryCreated(result.data)
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _searchedVendorsFlow.value =
                SearchVendorViewState.Error(getString(R.string.no_internet_connection))
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
                            _searchedVendorsFlow.value = SearchVendorViewState.Loading
                        }

                        is Resource.Error -> {
                            _searchedVendorsFlow.value = SearchVendorViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _searchedVendorsFlow.value =
                                SearchVendorViewState.ArticlesForDeliveryFound(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _searchedVendorsFlow.value =
                SearchVendorViewState.Error(getString(R.string.no_internet_connection))
        }
    }

}