package com.koflox.revoluttest.data.source

import com.koflox.revoluttest.data.entities.rates.response.RatesResponse

interface DataRepository {

    suspend fun getRates(baseCurrency: String): Result<RatesResponse, Error>

}