package com.example.catnicwarehouse.scan.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.scan.domain.usecase.SearchWarehouseStockyardsUseCase
import com.example.catnicwarehouse.scan.presentation.sealedClass.StockyardTree.StockyardTreeEvent
import com.example.catnicwarehouse.scan.presentation.sealedClass.StockyardTree.StockyardTreeViewState
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
class StockyardTreeViewModel  @Inject constructor(
    private val searchWarehouseStockyardsUseCase: SearchWarehouseStockyardsUseCase
) : BaseViewModel() {


    private val _stockyardTreeFlow =
        MutableStateFlow<StockyardTreeViewState>(StockyardTreeViewState.Empty)
    val stockyardTreeFlow: StateFlow<StockyardTreeViewState> = _stockyardTreeFlow



    private fun searchWarehouseStockyards(
        searchTerm: String?="",
        warehouseCode:String?="",
        isFromUserSearch:Boolean
    ) {
        if (hasNetwork()) {
            searchWarehouseStockyardsUseCase.invoke(searchTerm,warehouseCode)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _stockyardTreeFlow.value = StockyardTreeViewState.Loading
                        }

                        is Resource.Error -> {
                            _stockyardTreeFlow.value = StockyardTreeViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _stockyardTreeFlow.value =
                                StockyardTreeViewState.WarehouseStockyardsFound(result.data,isFromUserSearch)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _stockyardTreeFlow.value =
                StockyardTreeViewState.Error(getString(R.string.no_internet_connection))
        }
    }



    fun onEvent(event: StockyardTreeEvent) {
        when (event) {
            is StockyardTreeEvent.SearchWarehouseStockyards -> searchWarehouseStockyards(event.searchTerm,event.warehouseCode,event.isFromUserSearch)
            StockyardTreeEvent.Reset -> _stockyardTreeFlow.value = StockyardTreeViewState.Reset
        }
    }

}