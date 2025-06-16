package com.example.catnicwarehouse.incoming.articles.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.articles.domain.useCase.FindDeliveryItemsUseCase
import com.example.catnicwarehouse.incoming.articles.presentation.sealedClass.ArticlesEvent
import com.example.catnicwarehouse.incoming.articles.presentation.sealedClass.ArticlesViewState
import com.example.catnicwarehouse.incoming.deliveries.domain.usecase.GetDeliveryUseCase
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerViewState
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
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
class ArticlesViewModel @Inject constructor(
    private val getDeliveryUseCase: GetDeliveryUseCase,
    private val findDeliveryItemsUseCase: FindDeliveryItemsUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase
) : BaseViewModel() {


    private val _articlesFlow = MutableStateFlow<ArticlesViewState>(ArticlesViewState.Empty)
    val articlesFlow: StateFlow<ArticlesViewState> = _articlesFlow

    fun onEvent(event: ArticlesEvent) {
        when (event) {
            is ArticlesEvent.GetDelivery -> getDeliveryById(event.id)
            ArticlesEvent.Reset -> reset()
            is ArticlesEvent.SearchArticle -> searchArticlesForDelivery(event.searchTerm)
        }
    }

    private fun reset() {
        _articlesFlow.value = ArticlesViewState.Empty
    }


    private fun getDeliveryById(
        id: String
    ) {

        if (hasNetwork()) {
            getDeliveryUseCase.invoke(id=id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _articlesFlow.value = ArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _articlesFlow.value = ArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _articlesFlow.value = ArticlesViewState.Delivery(result.data)
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _articlesFlow.value =
                ArticlesViewState.Error(getString(R.string.no_internet_connection))
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
                            _articlesFlow.value = ArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _articlesFlow.value = ArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _articlesFlow.value =
                                ArticlesViewState.ArticlesForDeliveryFound(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _articlesFlow.value =
                ArticlesViewState.Error(getString(R.string.no_internet_connection))
        }
    }

}