package com.koflox.revoluttest.data.source.remote

import com.koflox.revoluttest.data.entities.rates.response.RatesResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

@Suppress("DeferredIsResult")
interface RevolutService {

    @GET("latest")
    fun getRates(
        @Query("base") baseCurrency: String
    ): Deferred<RatesResponse>

}