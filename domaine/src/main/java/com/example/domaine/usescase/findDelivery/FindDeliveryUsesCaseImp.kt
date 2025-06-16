package com.example.domaine.usescase.findDelivery

import com.example.domain.repository.delivery.DeliveryRepository
import com.example.domain.tools.data.DataState
import com.example.domaine.usescase.findDelivery.mapper.DeliveryUiModelMapper
import com.example.domaine.usescase.findDelivery.model.DeliveryUiModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FindDeliveryUsesCaseImp @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val deliveryUiModelMapper: DeliveryUiModelMapper
) : FindDeliveryUsesCase {
    override fun loadDelivery(): Flow<DataState<List<DeliveryUiModel>>> = callbackFlow{
        deliveryRepository.loadDelivery().collect{
            when(it){
                is DataState.Success->{
                    send(DataState.Success(deliveryUiModelMapper.mapDomainToDTO(it.data)))
                }
                is DataState.Error ->{
                    send(it)
                }
                else ->{
                    // send idle if we want
                }
            }
        }

        awaitClose()
    }

}