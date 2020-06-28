package com.koflox.revoluttest.use_cases

import com.koflox.revoluttest.data.entities.rates.displayed.DisplayedCurrencies
import com.koflox.revoluttest.data.entities.rates.displayed.DisplayedCurrency
import com.koflox.revoluttest.data.entities.rates.response.CurrencyExchangeRate
import com.koflox.revoluttest.data.entities.rates.response.RatesResponse
import com.koflox.revoluttest.data.entities.rates.response.RatesResponseAdapter.Companion.BASE_CURRENCY_RATE
import com.koflox.revoluttest.data.entities.rates.response.toDisplayedCurrency
import com.koflox.revoluttest.data.source.Result
import java.math.BigDecimal

class CalculateAmountsUseCase : BaseUseCase<RatesResponse>() {

    companion object {
        private val DEFAULT_INITIAL_AMOUNT = BigDecimal.ONE
    }

    private var baseAmount = DEFAULT_INITIAL_AMOUNT
    private var displayedBaseAmount = DEFAULT_INITIAL_AMOUNT.toPlainString()
    private val displayedCurrencies = mutableListOf<DisplayedCurrency>()

    private lateinit var ratesResponse: RatesResponse

    private var baseRequiresReselection = false

    /**
     * Recalculates new currency amounts keeping the currency order from the first calculation
     */
    override suspend fun run(data: RatesResponse) {
        when {
            ::ratesResponse.isInitialized -> displayedCurrencies.apply {
                clear()
                ratesResponse.rates.forEach { exchangeRate ->
                    data.rates.firstOrNull { exchangeRate.name == it.name }?.let { newExchangeRate ->
                        val isBaseCurrency = newExchangeRate.name == ratesResponse.baseCurrency
                        val requiresReselection = when {
                            isBaseCurrency && baseRequiresReselection -> {
                                baseRequiresReselection = false
                                true
                            }
                            else -> false
                        }
                        val displayedCurrency = newExchangeRate.toDisplayedCurrency(
                            baseAmount,
                            isBaseCurrency = isBaseCurrency,
                            requiresReselection = requiresReselection
                        )
                        add(displayedCurrency)
                    }
                }
            }
            else -> {
                ratesResponse = data
                displayedCurrencies.apply {
                    clear()
                    data.rates.forEach { exchangeRate -> add(exchangeRate.toDisplayedCurrency(baseAmount)) }
                }
            }
        }
        resultChannel.send(Result.Success(DisplayedCurrencies(displayedCurrencies)))
    }

    /**
     * Triggers recalculation of currency amounts
     * @param currency currency to be used for recalculation
     */
    fun onAmountChanged(currency: DisplayedCurrency) {
        if (currency.name != ratesResponse.baseCurrency) return
        val newAmount = when {
            currency.amount.isEmpty() -> BigDecimal.ZERO
            else -> BigDecimal(currency.amount)
        }
        val isBigDecimalDiffersFromDisplayedValue = currency.amount.length - newAmount.toPlainString().length
        if (isBigDecimalDiffersFromDisplayedValue != 0) baseRequiresReselection = true
        displayedBaseAmount = currency.amount
        baseAmount = newAmount
        invoke(ratesResponse, isSync = true)
    }

    /**
     * Calculates new currency exchange rates and saves currency as base
     * Rate is 1.00 for a new base currency and (oldRate / oldBaseCurrencyRate)
     *
     * EUR 1.00                        USD 1.00
     * RUB 2.00  USD chosen as base -> EUR 0.25
     * USD 4.00                        RUB 0.5
     *
     * @param currency - new base currency
     * @return true if new base currency differs from the current one, false otherwise
     */
    fun onBaseCurrencyChanged(currency: DisplayedCurrency): Boolean {
        if (currency.name == ratesResponse.baseCurrency) return false
        displayedBaseAmount = currency.amount
        baseAmount = BigDecimal(currency.amount)
        val newRates = mutableListOf<CurrencyExchangeRate>().apply {
            val newBaseCurrency = ratesResponse.rates.first { it.name == currency.name }
            val oldRates = ratesResponse.rates.toMutableList().apply {
                remove(newBaseCurrency)
            }
            add(CurrencyExchangeRate(newBaseCurrency.name, BASE_CURRENCY_RATE))
            oldRates.forEach {
                val newRate = it.rate / newBaseCurrency.rate
                add(CurrencyExchangeRate(it.name, newRate))
            }
        }
        ratesResponse = RatesResponse(currency.name, newRates)
        invoke(ratesResponse, isSync = true)
        return true
    }

}