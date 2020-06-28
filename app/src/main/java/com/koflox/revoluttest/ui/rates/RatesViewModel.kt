package com.koflox.revoluttest.ui.rates

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.koflox.revoluttest.R
import com.koflox.revoluttest.data.entities.Entity
import com.koflox.revoluttest.data.entities.rates.displayed.DisplayedCurrencies
import com.koflox.revoluttest.data.entities.rates.displayed.DisplayedCurrency
import com.koflox.revoluttest.data.entities.rates.response.RatesResponse
import com.koflox.revoluttest.data.source.Error
import com.koflox.revoluttest.data.source.Result
import com.koflox.revoluttest.ui.base.BaseViewModel
import com.koflox.revoluttest.use_cases.CalculateAmountsUseCase
import com.koflox.revoluttest.use_cases.GetRatesUseCase
import com.koflox.revoluttest.util.Event
import com.koflox.revoluttest.util.Network
import com.koflox.revoluttest.util.mergeChannels
import kotlinx.coroutines.channels.ReceiveChannel

class RatesViewModel(
    private val app: Application,
    private val getRatesUseCase: GetRatesUseCase,
    private val calculateAmountsUseCase: CalculateAmountsUseCase
) : BaseViewModel(app) {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _rates = MutableLiveData<List<DisplayedCurrency>>()
    val rates: LiveData<List<DisplayedCurrency>> = _rates

    private val _baseCurrencyExchangeRate = MutableLiveData<Event<DisplayedCurrency>>()
    val baseDisplayedCurrency: LiveData<Event<DisplayedCurrency>> = _baseCurrencyExchangeRate

    private val _isGettingRates = MutableLiveData<Boolean>()
    val isGettingRates: LiveData<Boolean> = _isGettingRates

    init {
        _title.value = app.getString(R.string.title_rates)
    }

    override val receiveChannel: ReceiveChannel<Result<Entity, Error>>
        get() = mergeChannels(getRatesUseCase.receiveChannel, calculateAmountsUseCase.receiveChannel)

    override fun handleResult(result: Result<Entity, Error>) {
        result.handle(::handleSuccess, ::handleFailure, ::handleState)
    }

    fun onStart() {
        getRatesUseCase.startRatesFetching()
    }

    fun onStop() {
        getRatesUseCase.stopRatesFetching()
    }

    fun onAmountChanged(currency: DisplayedCurrency) {
        calculateAmountsUseCase.onAmountChanged(currency)
    }

    fun onCurrencyChosen(currency: DisplayedCurrency) {
        val isChanged = calculateAmountsUseCase.onBaseCurrencyChanged(currency)
        if (isChanged) _baseCurrencyExchangeRate.value = Event(currency)
        getRatesUseCase.onBaseCurrencyChanged(currency.name)
    }

    private fun handleSuccess(data: Entity) {
        when (data) {
            is RatesResponse -> calculateAmountsUseCase.invoke(data, isSync = true)
            is DisplayedCurrencies -> _rates.value = data.currencies
        }
    }

    private fun handleFailure(error: Error) {
        if (Network.isNetworkAvailable())
            _userMessage.value = Event(app.getString(R.string.text_rates_general_error_desc))
    }

    private fun handleState(state: Result.State<Entity>) {
        when (state) {
            is Result.State.Loading -> when (state.stub) {
                is RatesResponse -> if (getRatesUseCase.isFirstRequestSucceeded) _isGettingRates.value = true
            }
            is Result.State.Loaded -> when (state.stub) {
                is RatesResponse -> _isGettingRates.value = false
            }
        }
    }

}