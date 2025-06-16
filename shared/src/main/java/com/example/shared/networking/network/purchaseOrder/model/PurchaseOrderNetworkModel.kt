
package com.example.shared.networking.network.purchaseOrder.model

import Items
import com.google.gson.annotations.SerializedName


data class PurchaseOrderNetworkModel(

	@SerializedName("items") val items : List<Items>,
	@SerializedName("netValue") val netValue : Int,
	@SerializedName("grossValue") val grossValue : Int
)