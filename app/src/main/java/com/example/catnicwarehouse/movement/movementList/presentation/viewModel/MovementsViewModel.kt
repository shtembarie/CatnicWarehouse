package com.example.catnicwarehouse.movement.movementList.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.dashboard.domain.useCase.GetMovementsUseCase
import com.example.catnicwarehouse.dashboard.presentation.sealedClasses.DashboardEvent
import com.example.catnicwarehouse.dashboard.presentation.sealedClasses.DashboardViewState
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.GetWarehouseStockyardInventoryEntriesUseCase
import com.example.catnicwarehouse.movement.movementList.domain.useCases.CloseMovementUseCase
import com.example.catnicwarehouse.movement.movementList.presentation.sealedClasses.MovementsEvent
import com.example.catnicwarehouse.movement.movementList.presentation.sealedClasses.MovementsViewState
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
class MovementsViewModel @Inject constructor(
    private val getMovementsUseCase: GetMovementsUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
    private val getWarehouseStockyardInventoryEntriesUseCase: GetWarehouseStockyardInventoryEntriesUseCase,
    private val closeMovementUseCase: CloseMovementUseCase
) : BaseViewModel() {


    private val _movementsFlow = MutableStateFlow<MovementsViewState>(MovementsViewState.Empty)
    val movementsFlow: StateFlow<MovementsViewState> = _movementsFlow

    fun onEvent(event: MovementsEvent) {
        when (event) {
            is MovementsEvent.GetMovements -> getMovements(event.status, event.onlyMyMovements)
            is MovementsEvent.SearchArticle -> searchArticlesForDelivery(event.searchTerm)
            is MovementsEvent.GetWarehouseStockyardInventoryEntries -> getWarehouseStockyardInventoryEntries(event.articleId,event.stockyardId,event.warehouseCode,event.isFromUserEntry)
            is MovementsEvent.CloseMovement -> closeMovement(event.id)
        }
    }

    private fun getMovements(
        status: String?,
        onlyMyMovements: Boolean? = true
    ) {
        if (hasNetwork()) {
            getMovementsUseCase.invoke(
                status = status,
                onlyMyMovements = onlyMyMovements
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _movementsFlow.value = MovementsViewState.Loading
                    }

                    is Resource.Error -> {
                        _movementsFlow.value = MovementsViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _movementsFlow.value =
                            MovementsViewState.GetMovementsResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _movementsFlow.value =
                MovementsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun closeMovement(
        id: String?,
    ) {
        if (hasNetwork()) {
            closeMovementUseCase.invoke(
                id = id
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _movementsFlow.value = MovementsViewState.Loading
                    }

                    is Resource.Error -> {
                        _movementsFlow.value = MovementsViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _movementsFlow.value =
                            MovementsViewState.CloseMovementResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _movementsFlow.value =
                MovementsViewState.Error(getString(R.string.no_internet_connection))
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
                            _movementsFlow.value = MovementsViewState.Loading
                        }

                        is Resource.Error -> {
                            _movementsFlow.value = MovementsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _movementsFlow.value = MovementsViewState.ArticleResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _movementsFlow.value =
                MovementsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getWarehouseStockyardInventoryEntries(
        articleId: String?,
        stockYardId: String?,
        warehouseCode: String?,
        isFromUserEntry:Boolean
    ) {
        if (hasNetwork()) {
            getWarehouseStockyardInventoryEntriesUseCase.invoke(
                id = articleId,
                stockyardId = stockYardId,
                warehouseCode = warehouseCode
            )
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _movementsFlow.value = MovementsViewState.Loading
                        }

                        is Resource.Error -> {
                            _movementsFlow.value = MovementsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _movementsFlow.value =
                                MovementsViewState.WarehouseStockyardInventoryEntriesResponse(
                                    warehouseStockyardInventoryEntriesResponse = result.data,
                                    isFromUserEntry = isFromUserEntry
                                )
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _movementsFlow.value =
                MovementsViewState.Error(getString(R.string.no_internet_connection))
        }
    }
}