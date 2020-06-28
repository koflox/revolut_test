package com.koflox.revoluttest.data.source

class AppDataRepository(
    private val remoteDataSource: DataSource
) : DataRepository {

    override suspend fun getRates(baseCurrency: String) = remoteDataSource.getRates(baseCurrency)

}