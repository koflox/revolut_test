package com.koflox.revoluttest.data.source.remote

import com.koflox.revoluttest.data.entities.rates.response.RatesResponse
import com.koflox.revoluttest.data.source.DataSource
import com.koflox.revoluttest.data.source.Error
import com.koflox.revoluttest.data.source.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteDataSource(
    private val revolutService: RevolutService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataSource {

    override suspend fun getRates(baseCurrency: String): Result<RatesResponse, Error.NetworkError> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(revolutService.getRates(baseCurrency).await())
        } catch (e: Exception) {
            Result.Failure(Error.NetworkError)
        }
    }

}