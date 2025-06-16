package com.example.catnicwarehouse.defectiveItems.amountFragment.domain.useCases

import com.example.catnicwarehouse.defectiveItems.amountFragment.domain.repository.UpdateAmountRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.defectiveArticles.SetAmount
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Enoklit on 05.12.2024.
 */
class UpdatetAmountUseCase @Inject constructor(
    private val updateAmountRepository: UpdateAmountRepository
) {
    operator fun invoke(
        id: Int?,
        setAmount: SetAmount
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val response = updateAmountRepository.updatedeAmount(
                id = id,
                setAmount = setAmount
            )
            if (response.isSuccessful){
                emit(Resource.Success(true))
            }else{
                val errorMessage = response.parseError()
                emit((Resource.Error(errorMessage)))
            }
        }catch (ex:Exception){
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }
}
