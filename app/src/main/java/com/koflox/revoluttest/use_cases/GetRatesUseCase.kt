package com.koflox.revoluttest.use_cases

import com.koflox.revoluttest.data.entities.rates.response.STUB_RATES_RESPONSE
import com.koflox.revoluttest.data.source.DataRepository
import com.koflox.revoluttest.data.source.Result
import com.koflox.revoluttest.data.source.isSucceeded
import com.koflox.revoluttest.util.startCoroutineTimer
import kotlinx.coroutines.Job

class GetRatesUseCase(
    private val dataRepository: DataRepository
) : BaseUseCase<String>() {

    companion object {
        const val DEFAULT_REPEAT_INTERVAL_MILLIS = 1_000L

        const val DEFAULT_BASE_CURRENCY = "EUR"
    }

    @Volatile
    var isFirstRequestSucceeded = true

    @Volatile
    private var baseCurrency = DEFAULT_BASE_CURRENCY

    private var timerStartTime: Long = -1L
    private var interval = DEFAULT_REPEAT_INTERVAL_MILLIS

    private var ratesTimerJob: Job? = null

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun run(baseCurrency: String) {
        resultChannel.run {
            send(Result.State.Loading(STUB_RATES_RESPONSE))
            val ratesResult = dataRepository.getRates(baseCurrency)
            send(ratesResult)
            when {
                ratesResult.isSucceeded() -> {
                    send(Result.State.Loaded(STUB_RATES_RESPONSE))
                    isFirstRequestSucceeded = false
                }
            }
        }
    }

    fun startRatesFetching(
        intervalMillis: Long = DEFAULT_REPEAT_INTERVAL_MILLIS,
        delayMillis: Long = 0
    ) {
        stopRatesFetching()
        this.interval = intervalMillis
        timerStartTime = System.currentTimeMillis()
        ratesTimerJob = startCoroutineTimer(
            delayMillis = delayMillis,
            repeatMillis = intervalMillis,
            block = { invoke(baseCurrency, isSync = false) })
    }

    fun stopRatesFetching() {
        ratesTimerJob?.cancel()
        ratesTimerJob = null
    }

    fun onBaseCurrencyChanged(currencyName: String) {
        baseCurrency = currencyName
        val delayUntilNextFetch = interval - ((System.currentTimeMillis() - timerStartTime) / interval)
        startRatesFetching(delayMillis = delayUntilNextFetch)
    }

}