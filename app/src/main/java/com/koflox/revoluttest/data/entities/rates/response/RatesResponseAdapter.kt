package com.koflox.revoluttest.data.entities.rates.response

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class RatesResponseAdapter : TypeAdapter<RatesResponse>() {

    companion object {
        private const val FIELD_NAME_BASE_CURRENCY = "baseCurrency"
        private const val FIELD_NAME_RATES = "rates"
        const val BASE_CURRENCY_RATE: Double = 1.0
    }

    override fun write(writer: JsonWriter, response: RatesResponse) {
        writer.run {
            beginObject()
            name(FIELD_NAME_BASE_CURRENCY).value(response.baseCurrency)
            name(FIELD_NAME_RATES)
            beginObject()
            response.rates.forEach { (currency, rate) ->
                name(currency).value(rate)
            }
            endObject()
            endObject()
        }
    }

    override fun read(reader: JsonReader): RatesResponse {
        var baseCurrency = ""
        val rates = mutableListOf<CurrencyExchangeRate>()
        reader.run {
            beginObject()
            while (hasNext()) {
                val name = nextName()
                when {
                    name == FIELD_NAME_BASE_CURRENCY && peek() != JsonToken.NULL -> {
                        val base = nextString().also { baseCurrency = it }
                        rates.add(CurrencyExchangeRate(base, BASE_CURRENCY_RATE))
                    }
                    name == FIELD_NAME_RATES && peek() != JsonToken.NULL -> {
                        beginObject()
                        parseCurrency@ while (peek() != JsonToken.END_OBJECT) {
                            val currencyName = nextName()
                            val rateValue = when {
                                peek() != JsonToken.NULL -> nextDouble()
                                else -> {
                                    skipValue()
                                    continue@parseCurrency
                                }
                            }
                            rates.add(CurrencyExchangeRate(currencyName, rateValue))
                        }
                        endObject()
                    }
                    else -> skipValue()
                }
            }
            endObject()
        }
        return RatesResponse(baseCurrency, rates)
    }

}