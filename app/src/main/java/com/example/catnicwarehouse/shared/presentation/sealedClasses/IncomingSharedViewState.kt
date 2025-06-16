package com.example.catnicwarehouse.shared.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.DefectiveReason
import com.example.catnicwarehouse.shared.presentation.model.ArticleItemUI
import com.example.catnicwarehouse.shared.presentation.enums.DeliveryType
import com.example.catnicwarehouse.shared.presentation.model.VendorOrCustomerInfo
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.delivery.model.getDelivery.GetDeliveryResponseModel


sealed class IncomingSharedViewState {
    object Empty : IncomingSharedViewState()
    object Loading : IncomingSharedViewState()
    data class Error(val errorMessage: String?) : IncomingSharedViewState()
    data class SupplierInfoResult(val vendorCustomerInfo: VendorOrCustomerInfo?) : IncomingSharedViewState()
    data class DeliveryIdResult(val deliveryId:String?) : IncomingSharedViewState()
    data class DeliveryTypeResult(val deliveryType: DeliveryType?) : IncomingSharedViewState()
    data class SelectedWarehouseStockyardIdResult(val selectedWarehouseStockyardId: Int) : IncomingSharedViewState()
    data class SelectedWarehouseStockyardNameResult(val selectedWarehouseStockyardName: String?) : IncomingSharedViewState()
    data class ArticleItemListModelResultForSearchedArticle(val articleItemModelListFromSearchedArticle: List<ArticlesForDeliveryResponseDTO>?) : IncomingSharedViewState()
    data class SelectedArticleItemModelResult(val selectedArticleItemModel: ArticleItemUI?) : IncomingSharedViewState()
    data class SelectedArticleQtyResult(val selectedQty: String?) : IncomingSharedViewState()
    data class SelectedSelectedDefectiveCommentResult(val selectedDefectiveComment: String?) : IncomingSharedViewState()
    data class SelectedDefectiveQtyResult(val selectedDefectiveQty: String?) : IncomingSharedViewState()
    data class SelectedDefectiveReasonResult(val selectedDefectiveReason: DefectiveReason?) : IncomingSharedViewState()
    data class SelectedDefectiveUnitResult(val selectedDefectiveUnit: String?) : IncomingSharedViewState()
    data class SelectedQuantityUnitResult(val selectedQuantityUnit: String?) : IncomingSharedViewState()
    data class GetDeliveryResponseModelResult(val deliveryResponseModel: GetDeliveryResponseModel?) : IncomingSharedViewState()
    data class IsDeliveryCompletedResult(val isDeliveryCompleted: Boolean?) : IncomingSharedViewState()
    data class GetDeliveryNoteResult(val deliveryNote: String?) : IncomingSharedViewState()

}