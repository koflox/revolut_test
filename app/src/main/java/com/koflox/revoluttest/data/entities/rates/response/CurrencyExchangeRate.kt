package com.koflox.revoluttest.data.entities.rates.response

import com.koflox.revoluttest.data.entities.rates.displayed.DisplayedCurrency
import java.math.BigDecimal
import java.math.RoundingMode

data class CurrencyExchangeRate(
    val name: String,
    val rate: Double
)

const val CURSOR_NO_SELECTION = -1

fun CurrencyExchangeRate.toDisplayedCurrency(
    baseAmount: BigDecimal,
    scale: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_UP,
    isBaseCurrency: Boolean = false,
    requiresReselection: Boolean = false
): DisplayedCurrency {
    val amount = baseAmount.multiply(BigDecimal.valueOf(rate))
    val scaledAmount = when {
        isBaseCurrency -> amount.setScale(baseAmount.scale(), roundingMode)
        else -> amount.setScale(scale, roundingMode)
            .stripTrailingZeros()
    }
    val displayedAmount = scaledAmount.toPlainString()
    val cursorSelection = when {
        requiresReselection -> displayedAmount.length
        else -> CURSOR_NO_SELECTION
    }
    return DisplayedCurrency(name, displayedAmount, cursorSelection)
}