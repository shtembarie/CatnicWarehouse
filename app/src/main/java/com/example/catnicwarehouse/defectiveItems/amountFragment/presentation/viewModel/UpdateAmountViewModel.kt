package com.example.catnicwarehouse.defectiveItems.amountFragment.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.defectiveItems.amountFragment.domain.useCases.UpdatetAmountUseCase
import com.example.catnicwarehouse.defectiveItems.amountFragment.presentation.sealedClasses.UpdateAmountEvent
import com.example.catnicwarehouse.defectiveItems.amountFragment.presentation.sealedClasses.UpdateAmountViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.defectiveArticles.SetAmount
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by Enoklit on 05.12.2024.
 */
@HiltViewModel
class UpdateAmountViewModel @Inject constructor(
    private val updatetAmountUseCase: UpdatetAmountUseCase
): BaseViewModel(){
    private val _updatedAmount = MutableStateFlow<UpdateAmountViewState>(UpdateAmountViewState.Empty)
    val updatedAmount: StateFlow<UpdateAmountViewState> = _updatedAmount

    fun onEvent(event: UpdateAmountEvent){
        when(event){
            is UpdateAmountEvent.Empty -> _updatedAmount.value = UpdateAmountViewState.Empty
            is UpdateAmountEvent.UpdatetAmount -> {updatingAmount(event.id, event.setAmount)}
        }
    }
    private fun updatingAmount(
        id: Int?,
        setAmount: SetAmount
    ){
        if (hasNetwork()){
            updatetAmountUseCase.invoke(
                id = id,
                setAmount = setAmount
            ).onEach { result ->
                when(result){
                    is Resource.Loading -> {
                        _updatedAmount.value = UpdateAmountViewState.Loading
                    }
                    is Resource.Error -> {
                        _updatedAmount.value = UpdateAmountViewState.Error(result.message)
                    }
                    is Resource.Success -> {
                        _updatedAmount.value = UpdateAmountViewState.UpdatedAmountResult(result.data)
                    }
                }
            }.launchIn(viewModelScope)
        }else {
            _updatedAmount.value = UpdateAmountViewState.Error(getString(R.string.no_internet_connection))
        }
    }
}