package com.koflox.revoluttest.data.entities.rates.displayed

import com.koflox.revoluttest.data.entities.Entity
import com.koflox.revoluttest.data.entities.rates.response.CURSOR_NO_SELECTION

class DisplayedCurrencies(
    val currencies: List<DisplayedCurrency>
) : Entity

data class DisplayedCurrency(
    val name: String,
    val amount: String,
    val cursorSelection: Int = CURSOR_NO_SELECTION
)