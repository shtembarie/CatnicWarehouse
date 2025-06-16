package com.example.catnicwarehouse.shared.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.DefectiveReason
import com.example.catnicwarehouse.shared.presentation.model.ArticleItemUI
import com.example.catnicwarehouse.shared.presentation.enums.DeliveryType
import com.example.catnicwarehouse.shared.presentation.model.VendorOrCustomerInfo

import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.delivery.model.getDelivery.GetDeliveryResponseModel

sealed class SharedEvent {
    data class UpdateSupplierInfo(val vendorCustomerInfo: VendorOrCustomerInfo?) : SharedEvent()
    data class UpdateDeliveryId(val deliveryId: String) : SharedEvent()
    data class UpdateDeliveryType(val deliveryType: DeliveryType) : SharedEvent()
    data class UpdateSelectedWarehouseStockyard(val selectedWarehouseStockyard: Int) : SharedEvent()
    data class UpdateSelectedWarehouseStockyardName(val selectedWarehouseStockyardName: String) : SharedEvent()
    data class UpdateSelectedArticleItemModel(val articleItem: ArticleItemUI?) : SharedEvent()
    data class UpdateArticleItemModelListFromSearchedArticle(val articleItemList: List<ArticlesForDeliveryResponseDTO>?) : SharedEvent()
    data class UpdateSelectedQty(val selectedQty: String) : SharedEvent()
    data class UpdateSelectedQtyUnit(val selectedQuantityUnit: String) : SharedEvent()
    data class UpdateSelectedDefectiveQty(val selectedDefectiveQty: String?) : SharedEvent()
    data class UpdateSelectedDefectiveReason(val selectedDefectiveReason: DefectiveReason) : SharedEvent()
    data class UpdateSelectedDefectiveComment(val selectedDefectiveComment: String?) : SharedEvent()
    data class UpdateSelectedDefectiveUnit(val selectedDefectiveUnit: String?) : SharedEvent()
    data class UpdateGetDeliveryResponseModel(val deliveryResponseModel: GetDeliveryResponseModel?) : SharedEvent()
    data class UpdateDeliveryCompleteStatus(val isDeliveryCompleted: Boolean?) : SharedEvent()
    data class UpdateDeliveryNote(val deliveryNote: String?) : SharedEvent()
    object Reset : SharedEvent()

    companion object {
        fun mapArticleItemForDeliveryToArticleItemUI(articleItem: ArticlesForDeliveryResponseDTO): ArticleItemUI {
            return ArticleItemUI(
                articleId = articleItem.articleId,
                matchCode = articleItem.matchCode ?: "",
                unitCode = articleItem.unitCode ?: "",
                description = articleItem.description

            )
        }
    }
}

