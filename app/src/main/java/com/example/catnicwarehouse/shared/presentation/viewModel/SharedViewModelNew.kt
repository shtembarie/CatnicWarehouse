package com.example.catnicwarehouse.shared.presentation.viewModel

import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.DefectiveReason
import com.example.catnicwarehouse.shared.presentation.model.ArticleItemUI
import com.example.catnicwarehouse.shared.presentation.enums.DeliveryType
import com.example.catnicwarehouse.shared.presentation.model.VendorOrCustomerInfo
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.sealedClasses.IncomingSharedViewState
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.delivery.model.getDelivery.GetDeliveryResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModelNew @Inject constructor() : BaseViewModel() {


    private val _incomingSharedFlow =
        MutableStateFlow<IncomingSharedViewState>(IncomingSharedViewState.Empty)
    val incomingSharedFlow: StateFlow<IncomingSharedViewState> = _incomingSharedFlow






    private var vendorCustomerInfo: VendorOrCustomerInfo? = null
    private var deliveryId: String? = null
    private var deliveryType: DeliveryType = DeliveryType.PUR
    private var selectedWarehouseStockyardId: Int = 0
    private var selectedWarehouseStockyardName: String?= null
    private var selectedArticleItemModel: ArticleItemUI? = null
    private var articleItemModelListFromSearchedArticle: List<ArticlesForDeliveryResponseDTO>? = null
    private var selectedQty: String = "1"
    private var selectedDefectiveComment: String? = null
    private var selectedDefectiveQty: String? = null
    private var selectedDefectiveReason: DefectiveReason? = null
    private var selectedDefectiveUnit: String? = null
    private var selectedQuantityUnit: String? = null
    private var deliveryResponseModel: GetDeliveryResponseModel? = null
    private var isDeliveryCompleted: Boolean? = false
    private var deliveryNote: String? = null


    fun getSupplierInfo() = vendorCustomerInfo
    fun getDeliveryId() = deliveryId
    fun getDeliveryType() = deliveryType
    fun getSelectedWarehouseStockyardId() = selectedWarehouseStockyardId
    fun getSelectedWarehouseStockyardName() = selectedWarehouseStockyardName
    fun getSelectedArticleItemModel() = selectedArticleItemModel
    fun getSelectedQty() = selectedQty
    fun getSelectedDefectiveComment() = selectedDefectiveComment
    fun getSelectedDefectiveQty() = selectedDefectiveQty
    fun getSelectedDefectiveReason() = selectedDefectiveReason
    fun getSelectedDefectiveUnit() = selectedDefectiveUnit
    fun getSelectedQuantityUnit() = selectedQuantityUnit
    fun getDeliveryResponseModel() = deliveryResponseModel
    fun isDeliveryCompleted() = isDeliveryCompleted
    fun getDeliveryNote() = deliveryNote
    fun getArticleItemModelListFromSearchedArticle() = articleItemModelListFromSearchedArticle


    fun onEvents(vararg events: SharedEvent) {
        events.fold(InitialSharedViewState()) { state, event ->
            when (event) {
                is SharedEvent.UpdateSupplierInfo -> {
                    vendorCustomerInfo = event.vendorCustomerInfo
                    state.copy(vendorCustomerInfo = vendorCustomerInfo).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.SupplierInfoResult(vendorCustomerInfo = vendorCustomerInfo)
                    }
                }

                is SharedEvent.UpdateDeliveryId -> {
                    deliveryId = event.deliveryId
                    state.copy(deliveryId = deliveryId).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.DeliveryIdResult(deliveryId = deliveryId)
                    }
                }

                is SharedEvent.UpdateDeliveryType -> {
                    deliveryType = event.deliveryType
                    state.copy(deliveryType = deliveryType).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.DeliveryTypeResult(deliveryType = deliveryType)
                    }
                }

                is SharedEvent.UpdateSelectedWarehouseStockyard -> {
                    selectedWarehouseStockyardId = event.selectedWarehouseStockyard
                    state.copy(selectedWarehouseStockyardId = selectedWarehouseStockyardId).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.SelectedWarehouseStockyardIdResult(
                                selectedWarehouseStockyardId = selectedWarehouseStockyardId
                            )
                    }
                }

                is SharedEvent.UpdateSelectedWarehouseStockyardName -> {
                    selectedWarehouseStockyardName = event.selectedWarehouseStockyardName
                    state.copy(selectedWarehouseStockyardName = selectedWarehouseStockyardName).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.SelectedWarehouseStockyardNameResult(
                                selectedWarehouseStockyardName = selectedWarehouseStockyardName
                            )
                    }
                }

                is SharedEvent.UpdateArticleItemModelListFromSearchedArticle-> {
                    articleItemModelListFromSearchedArticle = event.articleItemList
                    state.copy(articleItemModelListFromSearchedArticle = articleItemModelListFromSearchedArticle).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.ArticleItemListModelResultForSearchedArticle(
                                articleItemModelListFromSearchedArticle = articleItemModelListFromSearchedArticle
                            )
                    }
                }

                is SharedEvent.UpdateSelectedArticleItemModel -> {
                    selectedArticleItemModel = event.articleItem
                    state.copy(selectedArticleItemModel = selectedArticleItemModel).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.SelectedArticleItemModelResult(
                                selectedArticleItemModel = selectedArticleItemModel
                            )
                    }
                }

                is SharedEvent.UpdateSelectedQty -> {
                    selectedQty = event.selectedQty
                    state.copy(selectedQty = selectedQty).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.SelectedArticleQtyResult(
                                selectedQty = selectedQty
                            )
                    }
                }

                is SharedEvent.UpdateSelectedDefectiveComment -> {
                    selectedDefectiveComment = event.selectedDefectiveComment
                    state.copy(selectedDefectiveComment = selectedDefectiveComment).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.SelectedSelectedDefectiveCommentResult(
                                selectedDefectiveComment = selectedDefectiveComment
                            )
                    }
                }

                is SharedEvent.UpdateSelectedDefectiveQty -> {
                    selectedDefectiveQty = event.selectedDefectiveQty
                    state.copy(selectedDefectiveQty = selectedDefectiveQty).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.SelectedDefectiveQtyResult(
                                selectedDefectiveQty = selectedDefectiveQty
                            )
                    }
                }

                is SharedEvent.UpdateSelectedDefectiveReason -> {
                    selectedDefectiveReason = event.selectedDefectiveReason
                    state.copy(selectedDefectiveReason = selectedDefectiveReason).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.SelectedDefectiveReasonResult(
                                selectedDefectiveReason = selectedDefectiveReason
                            )
                    }
                }

                is SharedEvent.UpdateSelectedDefectiveUnit -> {
                    selectedDefectiveUnit = event.selectedDefectiveUnit
                    state.copy(selectedDefectiveUnit = selectedDefectiveUnit).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.SelectedDefectiveUnitResult(
                                selectedDefectiveUnit = selectedDefectiveUnit
                            )
                    }
                }

                is SharedEvent.UpdateSelectedQtyUnit -> {
                    selectedQuantityUnit = event.selectedQuantityUnit
                    state.copy(selectedQuantityUnit = selectedQuantityUnit).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.SelectedQuantityUnitResult(
                                selectedQuantityUnit = selectedQuantityUnit
                            )
                    }
                }

                is SharedEvent.UpdateGetDeliveryResponseModel -> {
                    deliveryResponseModel = event.deliveryResponseModel
                    state.copy(deliveryResponseModel = deliveryResponseModel).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.GetDeliveryResponseModelResult(
                                deliveryResponseModel = deliveryResponseModel
                            )
                    }
                }

                is SharedEvent.UpdateDeliveryCompleteStatus -> {
                    isDeliveryCompleted = event.isDeliveryCompleted
                    state.copy(isDeliveryCompleted = isDeliveryCompleted).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.IsDeliveryCompletedResult(
                                isDeliveryCompleted = isDeliveryCompleted
                            )
                    }
                }

                is SharedEvent.UpdateDeliveryNote ->{
                    deliveryNote = event.deliveryNote
                    state.copy(deliveryNote = deliveryNote).also {
                        _incomingSharedFlow.value =
                            IncomingSharedViewState.GetDeliveryNoteResult(
                                deliveryNote = deliveryNote
                            )
                    }
                }

                SharedEvent.Reset -> {
                    vendorCustomerInfo = null
                    deliveryId = null
                    deliveryType = DeliveryType.PUR
                    selectedWarehouseStockyardId = 0
                    selectedArticleItemModel = null
                    selectedQty = "1"
                    selectedDefectiveComment = null
                    selectedDefectiveQty = null
                    selectedDefectiveReason = null
                    selectedDefectiveUnit = null
                    selectedQuantityUnit = null
                    deliveryResponseModel = null
                    isDeliveryCompleted = false
                    deliveryNote = null

                    // Optionally reset the state to reflect a clean slate
                    _incomingSharedFlow.value = IncomingSharedViewState.Empty

                    // Return the initial state after reset
                    InitialSharedViewState()
                }
            }
        }
    }

    data class InitialSharedViewState(
        val vendorCustomerInfo: VendorOrCustomerInfo? = null,
        val deliveryId: String? = null,
        val deliveryType: DeliveryType? = null,
        val selectedWarehouseStockyardId: Int = 0,
        val selectedArticleItemModel: ArticleItemUI? = null,
        val selectedQty: String = "0",
        val selectedDefectiveComment: String? = null,
        val selectedDefectiveQty: String? = null,
        val selectedDefectiveReason: DefectiveReason? = null,
        val selectedDefectiveUnit: String? = null,
        val selectedQuantityUnit: String? = null,
        val deliveryResponseModel: GetDeliveryResponseModel? = null,
        val isDeliveryCompleted: Boolean? = false,
        val deliveryNote: String? = null,
        val articleItemModelListFromSearchedArticle: List<ArticlesForDeliveryResponseDTO>?=null,
        val selectedWarehouseStockyardName: String?=null
    )

}