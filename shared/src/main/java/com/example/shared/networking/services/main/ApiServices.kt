package com.example.shared.networking.services.main

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.delivery.model.DeliveryResponseModel
import com.example.shared.networking.network.delivery.model.SearchArticleForDeliveryResponseModel
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryResponseModel
import com.example.shared.networking.network.delivery.model.createDeliveryItem.AssignDeliveryItemToPurchaseRequestModel
import com.example.shared.networking.network.delivery.model.updateDeliveryItemForPurchase.UpdateDeliveryItemToPurchaseRequestModel
import com.example.shared.networking.network.purchaseOrder.model.CreateDeliveryItemRequestModel
import com.example.shared.networking.network.purchaseOrder.model.CreatePurchaseOrderRequest
import com.example.shared.networking.network.purchaseOrder.model.CreatePurchaseOrderResponse
import com.example.shared.networking.network.purchaseOrder.model.PurchaseOrderNetworkModel
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO
import com.example.shared.networking.network.warehouse.FindWarehouseDTO
import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryItemRepoModel
import com.example.shared.repository.purchaseOrder.model.ValidPurchaseOrderItemRepoModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiServices {

    @GET("/v0/api/Delivery/{deliveryId}/items")
    fun loadDeliveryItems(@Path("deliveryId") deliveryId: String): Call<List<DeliveryItemRepoModel>>

    @GET("/v0/api/Delivery/ArticlesForDelivery")
    fun loadDeliveryForArticle(@Query("Searchterm") searchterm: String): Call<List<SearchArticleForDeliveryResponseModel>>



    @POST("/v0/api/Delivery/{deliveryId}/items")
    fun createDeliveryItem(
        @Path("deliveryId") deliveryId: String,
        @Body createDeliveryItemRequestModel: CreateDeliveryItemRequestModel
    ): Call<ResponseBody>

    @POST("/v0/api/Delivery/{deliveryId}/items/{deliveryItemId}/AutomaticAssignment")
    fun automaticAssignment(
        @Path("deliveryId") deliveryId: String, @Path("deliveryItemId") deliveryItemId: String
    ): Call<ResponseBody>

    @POST("/v0/api/Delivery/{deliveryId}/items/{deliveryItemId}/Assignments")
    fun assignDeliveryItemToPurchase(
        @Path("deliveryId") deliveryId: String,
        @Path("deliveryItemId") deliveryItemId: String,
        @Body createDeliveryItemRequestModel: AssignDeliveryItemToPurchaseRequestModel
    ): Call<ResponseBody>

    @PUT("/v0/api/Delivery/{deliveryId}/items/{deliveryItemId}/Assignments/{purchaseOrderItemReference}")
    fun updateDeliveryItemToPurchase(
        @Path("deliveryId") deliveryId: String,
        @Path("deliveryItemId") deliveryItemId: String,
        @Path("purchaseOrderItemReference") purchaseOrderItemReference: String,
        @Body createDeliveryItemRequestModel: UpdateDeliveryItemToPurchaseRequestModel
    ): Call<ResponseBody>

    @GET("/v0/api/Delivery/ValidPurchaseOrdersForDelivery")
    fun getValidPurchaseOrdersForDelivery(
        @Query("VendorId") vendorId: String,
        @Query("ArticleId") ArticleId: String,
    ): Call<List<ValidPurchaseOrderItemRepoModel>>

    @PUT("/v0/api/Delivery/{deliveryId}/Booking")
    fun completeDelivery(
        @Path("deliveryId") deliveryId: String
    ): Call<ResponseBody>



    @POST("/v0/api/PurchaseOrders")
    fun createPurchaseOrder(@Body createPurchaseOrder: CreatePurchaseOrderRequest): Call<CreatePurchaseOrderResponse>

    @GET("/v0/api/PurchaseOrders/{id}")
    fun getPurchaseOrder(@Path("id") id: String): Call<ResponseBody>

    @GET("/v0/api/PurchaseOrders/{id}/items")
    fun getPurchaseOrderItems(@Path("id") id: String): Call<PurchaseOrderNetworkModel>

}

