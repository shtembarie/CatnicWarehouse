package com.example.shared.networking.services.warehouse

import com.example.shared.networking.network.InventoryOrder.dataModel.PostStockyardItemCommand
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticleUnitsResponseModel
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.customer.model.SearchedCustomerDTOItem
import com.example.shared.networking.network.delivery.model.DeliveryResponseModel
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.networking.network.delivery.model.getDelivery.GetDeliveryResponseModel
import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import com.example.shared.networking.network.delivery.model.updateDeliveryItem.UpdateDeliveryItemRequestModel
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.example.shared.networking.network.packing.model.packingItem.PackingItemsModel
import com.example.shared.networking.network.packing.model.packingList.AssignedPackingListItem
import com.example.shared.networking.network.packing.model.packingList.GetItemsForPackingResponseModelItem
import com.example.shared.networking.network.packing.model.packingList.PackingListStatusResponseModel
import com.example.shared.networking.network.packing.model.packingList.PackingModelItem
import com.example.shared.networking.network.packing.model.packingList.SearchPackingListDTO
import com.example.shared.networking.network.packing.model.packingList.packingListItem.PackingListItem
import com.example.shared.networking.network.packing.model.shippingContainer.ShippingContainerPackingListItemsByShippingContainer.ShippingContainerPackingListItemsByShippingContainerResponseModel
import com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.CreateShippingContainerPackingListItemRequestModel
import com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers.GetShippingContainersResponseModel
import com.example.shared.networking.network.packing.model.shippingContainer.shippingContainerTypes.ShippingContainerTypeResponseModelItem
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import com.example.shared.networking.network.purchaseOrder.model.CreateDeliveryItemRequestModel
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO
import com.example.shared.networking.network.warehouse.FindWarehouseDTO
import com.example.shared.repository.correctingStock.model.*
import com.example.shared.repository.dashboard.WarehousesResponseModelItem
import com.example.shared.repository.defectiveArticles.DefectiveArticleList
import com.example.shared.repository.defectiveArticles.DefectivesArticlesById
import com.example.shared.repository.defectiveArticles.PostDefectiveArticlesModel
import com.example.shared.repository.defectiveArticles.SetAmount
import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryItemRepoModel
import com.example.shared.repository.movements.CreateMovementResponseModel
import com.example.shared.repository.movements.DropOffRequestModel
import com.example.shared.repository.movements.GetMovementsModel
import com.example.shared.repository.movements.PickUpRequestModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel
import com.example.shared.repository.inventory.model.*
import com.example.shared.repository.login.model.RootResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WarehouseApiServices {


    @GET("v0/Bootstrap/initialize")
    suspend fun initialize(
    ): Response<RootResponse>

    @GET("/v0/api/Delivery")
    suspend fun findDelivery(
        @Query("Date") date: String? = null,
        @Query("User") user: String? = null,
        @Query("Status") status: String? = null
    ): Response<List<DeliveryResponseModel>>

    @GET("/v0/api/Delivery/{id}")
    suspend fun getDelivery(
        @Path("id") id: String
    ): Response<GetDeliveryResponseModel>

    @GET("/v0/api/Vendors/SearchVendors")
    suspend fun searchVendors(@Query("SearchTerm") searchTerm: String?): Response<List<SearchedVendorDTO>>

    @GET("v0/api/Customer/SearchCustomers")
    suspend fun searchCustomers(@Query("searchstring") searchTerm: String?): Response<List<SearchedCustomerDTOItem>>

    @POST("/v0/api/Delivery")
    suspend fun createDelivery(@Body createDeliveryRequestModel: CreateDeliveryRequestModel): Response<String>

    @GET("/v0/api/Warehouses")
    suspend fun findWarehouses(@Query("request") searchTerm: String?): Response<List<FindWarehouseDTO>>

    @GET("/v0/api/WarehouseStockYards/PickupAndDropZones")
    suspend fun getDefaultPickupAndDropZoneStockYards(@Query("WarehouseCode") searchTerm: String?): Response<List<WarehouseStockyardsDTO>>

    @GET("/v0/api/Delivery/ArticlesForDelivery")
    suspend fun getArticlesForDelivery(@Query("Searchterm") searchTerm: String?): Response<List<ArticlesForDeliveryResponseDTO>>

    @GET("/v0/api/WarehouseStockYards/{id}")
    suspend fun getWarehouseStockyardById(@Path("id") id: String?): Response<WarehouseStockyardsDTO>

    @GET("/v0/api/WarehouseStockYards/search")
    suspend fun searchWarehouseStockyards(
        @Query("searchTerm") searchTerm: String?,
        @Query("warehouseCode") warehouseCode: String?
    ): Response<List<WarehouseStockyardsDTO>>


    @GET("/v0/api/Delivery/{deliveryId}/items")
    suspend fun findDeliveryItems(@Path("deliveryId") deliveryId: String): Response<List<DeliveryItemRepoModel>>

    @POST("/v0/api/Delivery/{deliveryId}/items")
    suspend fun createDeliveryItem(
        @Path("deliveryId") deliveryId: String,
        @Body createDeliveryItemRequestModel: CreateDeliveryItemRequestModel
    ): Response<Int>

    @PUT("/v0/api/Delivery/{deliveryId}/note")
    suspend fun setDeliveryNote(
        @Path("deliveryId") deliveryId: String,
        @Body deliveryNoteRequestModel: DeliveryNoteRequestModel
    ): Response<Unit>

    @POST("v0/api/Delivery/{deliveryId}/DeliveryWarehousingCompletion")
    suspend fun completeDeliveryWarehousing(
        @Path("deliveryId") deliveryId: String,
    ): Response<Unit>


    @POST("v0/api/Delivery/{deliveryId}/items/{deliveryItemId}/booking")
    suspend fun bookDeliveryItem(
        @Path("deliveryId") deliveryId: String,
        @Path("deliveryItemId") deliveryItemId: String,
    ): Response<Unit>


    @GET("v0/api/Articles/units")
    suspend fun getArticleUnits(
        @Query("id") id: String,
    ): Response<ArticleUnitsResponseModel>


    @POST("v0/api/Delivery/{deliveryId}/items/{deliveryItemId}")
    suspend fun updateDeliveryItem(
        @Path("deliveryId") deliveryId: String,
        @Path("deliveryItemId") deliveryItemId: String,
        @Body updateDeliveryItemRequestModel: UpdateDeliveryItemRequestModel
    ): Response<Unit>


    //----------------------------------------Movement-----------------------------------//
    @GET("v0/api/Movements/movements")
    suspend fun getMovements(
        @Query("Status") status: String?,
        @Query("OnlyMyMovements") onlyMyMovements: Boolean?,
    ): Response<GetMovementsModel>

    @GET("v0/api/Inventory")
    suspend fun getInventoryItems(
        @Query("WarehouseCode") warehouseCode: String,
        @Query("Status") status: String
    ): Response<List<InventoryResponse>>

    @GET("/v0/api/Inventory/{Id}")
    suspend fun getInventoryById(
        @Path("Id") id: Int
    ): Response<GetIdByInventoryModel>

    @PUT("v0/api/Inventory/{id}/items/{itemId}/note")
    suspend fun updateInventoryNote(
        @Path("id") id: String?,
        @Path("itemId") itemId: String?,
        @Body deliveryNoteRequestModel: DeliveryNoteRequestModel?
    ): Response<Unit>

    @POST("v0/api/Inventory/{id}/ItemInventory")
    suspend fun inventorizeItem(
        @Path("id") id: String?,
        @Body inventorizeItemRequestModel: InventorizeItemRequestModel?
    ): Response<InventoriseItemResponseModel>

    @POST("v0/api/Movements")
    suspend fun createMovement(
        @Body body: RequestBody
    ): Response<CreateMovementResponseModel>

    @GET("v0/api/WarehouseStockYards/{WarehouseCode}/stockyards/warehouse")
    suspend fun getWarehouseStockyardsByWarehouseCode(@Path("WarehouseCode") searchTerm: String?): Response<ArrayList<WarehouseStockyardsDTO>>

    //----------------------------------------Inventory-----------------------------------//
    @GET("/v0/api/Inventory/current")
    suspend fun getCurrentInventory(
        @Query("WarehouseCode") WarehouseCode: String?
    ): Response<CurrentInventoryResponseModel>

    @GET("/v0/api/Inventory/{Id}/kpi")
    suspend fun getInventorykpi(
        @Path("Id") id: Int
    ): Response<InventoryKPI>

    @GET("v0/api/WarehouseStockYards/{id}/inventory")
    suspend fun getWarehouseStockyardInventory(@Path("id") id: String?): Response<ArrayList<WarehouseStockyardInventoryResponseModel>>

    @GET("/v0/api/Inventory/{id}/items")
    suspend fun getInventoryItems(
        @Path("id") id: String,
        @Query("Inventoried") Inventoried: Boolean? = true,
        @Query("WarehouseStockYardId") WarehouseStockYardId: Int? = null,
        @Query("ChangedBy") ChangedBy: String? = null
    ): Response<List<InventoryItem>>

    @POST("/v0/api/Inventory/{id}/stockyards/{stockyardId}/items")
    suspend fun postItemToStockyard(
        @Path("id") id: Int,
        @Path("stockyardId") stockyardId: Int,
        @Body command: PostStockyardItemCommand
    ): Response<String>


    @GET("v0/api/WarehouseStockYards/inventory/search")
    suspend fun getWarehouseStockyardInventoryEntries(
        @Query("articleId") articleId: String?,
        @Query("stockYardId") stockYardId: String?,
        @Query("warehouseCode") warehouseCode: String?
    ): Response<ArrayList<WarehouseStockyardInventoryEntriesResponseModel>>

    @POST("v0/api/Movements/{id}/pickUp")
    suspend fun pickUp(
        @Path("id") id: String?,
        @Body pickUpRequestModel: PickUpRequestModel?
    ): Response<Unit>

    @POST("v0/api/Movements/{id}/dropOff")
    suspend fun dropOff(
        @Path("id") id: String?,
        @Body dropOffFRequestModel: DropOffRequestModel?
    ): Response<Unit>

    @POST("v0/api/Movements/{id}/closing")
    suspend fun closeMovement(
        @Path("id") id: String?,
    ): Response<Unit>

    @GET("v0/api/Warehouses")
    suspend fun getWarehouses(): Response<List<WarehousesResponseModelItem>>

    @PUT("/v0/api/Inventory/{id}/items/{itemId}")
    suspend fun setInventoryItemId(
        @Path("id") id: Int?,
        @Path("itemId") itemId: Int?,
        @Body setInventoryItems: SetInventoryItems?
    ): Response<Unit>

    //----------------------------------------Packing-----------------------------------//

    @GET("v0/api/PackingLists/{id}")
    suspend fun getPackingList(@Path("id") id: String?): Response<PackingModelItem>

    @GET("v0/api/PackingLists/{id}/items")
    suspend fun getPackingItems(@Path("id") id: String?): Response<PackingItemsModel>

    @PUT("v0/api/PackingLists/{id}/packing/start")
    suspend fun startPacking(@Path("id") id: String?): Response<Unit>


    @GET("v0/api/PackingLists")
    suspend fun getPackingLists(@Query("inProgress") inProgress: Boolean?): Response<PackingListItem>

    @GET("v0/api/PackingLists/PackingListsforPicking")
    suspend fun getAssignedPackingLists(): Response<List<AssignedPackingListItem>>

    @PUT("v0/api/PackingLists/{id}/packing/pause")
    suspend fun pausePacking(@Path("id") id: String?): Response<Unit>

    @PATCH("v0/api/PackingLists/{packingListId}/items/{itemId}/PickAmount")
    suspend fun pickAmount(
        @Path("packingListId") packingListId: String?,
        @Path("itemId") itemId: String?,
        @Body pickAmountRequestModel: PickAmountRequestModel?
    ): Response<Unit>

    @PATCH("v0/api/PackingLists/{packingListId}/items/{itemId}/ChangePackedAmount/{packedAmount}")
    suspend fun changePackedAmount(
        @Path("packingListId") packingListId: String?,
        @Path("itemId") itemId: String?,
        @Path("packedAmount") packedAmount: Int?
    ): Response<Unit>

    @PATCH("v0/api/PackingLists/{packingListId}/finalize")
    suspend fun finalizePackingList(@Path("packingListId") packingListId: String?): Response<Unit>

    @PATCH("v0/api/PackingLists/{packingListId}/terminate")
    suspend fun cancelPackingList(
        @Path("packingListId") packingListId: String?,
        @Body cancelPackingRequestModel: CancelPackingRequestModel
    ): Response<Unit>

    @GET("v0/api/WarehouseStockYards/PackingZones")
    suspend fun getDefaultPackingZones(@Query("WarehouseCode") warehouseCode: String?): Response<List<DefaultPackingZoneResultModel>>

    @GET("v0/api/PackingLists/search/{query}")
    suspend fun searchPackingLists(@Path("query") query: String): Response<List<SearchPackingListDTO>>

    //-------------------Correcting Stock-------------------------//
    @GET("/v0/api/WarehouseStockYards")
    suspend fun getWarehouseStockYards(
        @Query("warehouseCode") WarehouseCode: String? = null,
        @Query("warehouseTemplateId") WarehouseTemplateId: Int? = null
    ): Response<List<WarehouseStockYardsList>>

    @GET("/v0/api/WarehouseStockYards/{id}/inventory")
    suspend fun getStockYardsArticles(
        @Path("id") WarehouseStockYardId: Int?,
        @Query("searchTerm") searchTerm: String? = null
    ): Response<List<WarehouseStockYardsArticlesList>>

    @PUT("/v0/api/WarehouseStockYards/{warehouseStockYardId}/correctInventory/{entryId}")
    suspend fun updateCorrectingStockItems(
        @Path("warehouseStockYardId") warehouseStockYardId: Int?,
        @Path("entryId") entryId: Int?,
        @Body correctInventoryItems: CorrectInventoryItems
    ): Response<Unit>

    @GET("v0/api/Articles/units")
    suspend fun getArticleUnit(
        @Query("id") id: String
    ): Response<ArticleUnitsResponse>

    //-------------------Defective Articles-------------------------//
    @GET("/v0/api/DefectiveArticles")
    suspend fun getDefectiveArticles(
        @Query("WarehouseCode") WarehouseCode: String? = null,
        @Query("WarehouseStockYardId") WarehouseStockYardId: Int? = null,
        @Query("Status") Status: String? = null
    ): Response<List<DefectiveArticleList>>

    @GET("/v0/api/DefectiveArticles/{id}")
    suspend fun getDefectiveArticlesById(
        @Path("id") id: Int? = null
    ): Response<DefectivesArticlesById>

    @PUT("/v0/api/DefectiveArticles/{id}/split")
    suspend fun updateAmount(
        @Path("id") id: Int?,
        @Body setAmount: SetAmount
    ): Response<Unit>

    @POST("/v0/api/DefectiveArticles")
    suspend fun postDefectiveItems(
        @Body postDefectiveArticlesModel: PostDefectiveArticlesModel?
    ): Response<Unit>

    @GET("v0/api/PackingLists/{packingListId}/packingItems")
    suspend fun getItemsForPacking(@Path("packingListId") packingListId: String): Response<List<GetItemsForPackingResponseModelItem>>

    @GET("v0/api/PackingLists/{packingListId}/shippingcontainers")
    suspend fun getShippingContainers(@Path("packingListId") packingListId: String?): Response<GetShippingContainersResponseModel>

    @GET("v0/api/PackingLists/GetshippingContainerTypes")
    suspend fun getShippingContainerTypes(): Response<List<ShippingContainerTypeResponseModelItem>>

    @GET("v0/api/PackingLists/{packingListId}/{shippingContainierId}")
    suspend fun getShippingContainerPackingListItemsByShippingContainer(
        @Path("packingListId") packingListId: String?,
        @Path("shippingContainierId") shippingContainierId: String?
    ): Response<ShippingContainerPackingListItemsByShippingContainerResponseModel>

    @POST("v0/api/PackingLists/{packingListId}/items")
    suspend fun createShippingContainerPackingListItem(
        @Path("packingListId") packingListId: String?,
        @Body createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel
    ): Response<Unit>

    @PUT("v0/api/PackingLists/{packingListId}/items")
    suspend fun updateShippingContainerPackingListItem(
        @Path("packingListId") packingListId: String?,
        @Body createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel
    ): Response<Unit>

    @GET("v0/api/PackingLists/{packingListId}/comment")
    suspend fun getPackingListComment(@Path("packingListId") packingListId: String?): Response<String>

    @GET("v0/api/PackingLists/{packingListId}/status")
    suspend fun getPackingListStatus(@Path("packingListId") packingListId: String?): Response<PackingListStatusResponseModel>



}