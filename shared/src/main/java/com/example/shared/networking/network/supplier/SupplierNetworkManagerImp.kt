package com.example.shared.networking.network.supplier

import com.example.shared.networking.network.supplier.model.SearchedVendorDTO
import com.example.shared.networking.services.main.ApiServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SupplierNetworkManagerImp @Inject constructor(private val apiServices: ApiServices) :
    SupplierNetworkManager {
    override fun loadSuppliers(scope: CoroutineScope): Flow<List<SearchedVendorDTO>?> =
        callbackFlow {
//            apiServices.searchVendors("").enqueue(object : Callback<List<SearchedVendorDTO>> {
//                override fun onResponse(
//                    call: Call<List<SearchedVendorDTO>>,
//                    response: Response<List<SearchedVendorDTO>>
//                ) {
//                    scope.launch {
//                        send(response.body())
//                    }
//                }
//
//                override fun onFailure(call: Call<List<SearchedVendorDTO>>, t: Throwable) {
//                    scope.launch {
//                        send(null)
//                    }
//                }
//            })
            awaitClose()
        }
}