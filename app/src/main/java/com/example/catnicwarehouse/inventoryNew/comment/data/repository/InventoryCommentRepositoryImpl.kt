package com.example.catnicwarehouse.inventoryNew.comment.data.repository

import com.example.catnicwarehouse.inventoryNew.comment.domain.repository.InventoryCommentRepository
import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.inventory.model.InventoryItem
import retrofit2.Response
import javax.inject.Inject

class InventoryCommentRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): InventoryCommentRepository {
    override suspend fun updateInventoryComment(
        id: String?,
        itemId: String?,
        deliveryNoteRequestModel: DeliveryNoteRequestModel?
    ): Response<Unit> {
        return warehouseApiServices.updateInventoryNote(id,itemId,deliveryNoteRequestModel)
    }

}