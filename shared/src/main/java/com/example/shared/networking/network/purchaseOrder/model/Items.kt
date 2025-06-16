import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2023 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Items (

	@SerializedName("id") val id : String,
	@SerializedName("iposition") val iposition : Int,
	@SerializedName("articleId") val articleId : String,
	@SerializedName("unitCode") val unitCode : String,
	@SerializedName("amount") val amount : Int,
	@SerializedName("description") val description : String,
	@SerializedName("typeCode") val typeCode : String,
	@SerializedName("productBookingGroupCode") val productBookingGroupCode : String,
	@SerializedName("totalPrice") val totalPrice : Int,
	@SerializedName("price") val price : Int,
	@SerializedName("vatRate") val vatRate : Int,
	@SerializedName("rebateRate") val rebateRate : Int,
	@SerializedName("warehouseCode") val warehouseCode : String,
	@SerializedName("profitMarginPercent") val profitMarginPercent : Int,
	@SerializedName("impAccountNumber") val impAccountNumber : String,
	@SerializedName("productionMachineId") val productionMachineId : Int
)