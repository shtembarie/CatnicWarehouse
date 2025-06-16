package com.example.shared.networking.network.packing.model.packingItem

import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt

data class PackingItem(
    val packingListId: String?,
    val position: String?,
    val orderItemLgid: String?,
    val orderItemOrderId: String?,
    val orderItemPosition: String?,
    val typeCode: String?,
    val articleId: String?,
    val description: String?,
    val unitCode: String?,

    // Use the same field names as the API but store them in private properties
    @SerializedName("amount")
    private val _amount: Double?,

    @SerializedName("packedAmount")
    private val _packedAmount: Double?,

    val packedStatus: String?,
    val gtin: String?,
    val iposition: Int?,
    val lgid: String?,
    val shippingContainers: List<ShippingContainer>?,

    // Possibly some other int-based field, if needed
    var amountToPack: Int?
) {
    // Expose them as Int in your domain logic
    val amount: Int
        get() = (_amount ?: 0.0).toInt()

    val packedAmount: Int
        get() = (_packedAmount ?: 0.0).toInt()
}

data class ShippingContainer(
    val shippingContainerId: String,
    val packingListId: String,
    val packingListItemPosition: String,
    val packedAmount: Int,
    val paI_Lgid: String
)