package com.example.catnicwarehouse.movement.movementList.domain.repository

import com.example.shared.repository.movements.PickUpRequestModel
import retrofit2.Response

interface MovementListRepository {
    suspend fun closeMovement(
        id: String?,
    ): Response<Unit>
}