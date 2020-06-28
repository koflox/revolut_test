package com.koflox.revoluttest.data.entities.rates.response

import com.koflox.revoluttest.data.entities.Entity

val STUB_RATES_RESPONSE = RatesResponse("", listOf())

data class RatesResponse(
    val baseCurrency: String,
    val rates: List<CurrencyExchangeRate>
) : Entity