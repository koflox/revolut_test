package com.koflox.revoluttest.di

import com.koflox.revoluttest.data.source.AppDataRepository
import com.koflox.revoluttest.data.source.DataRepository
import com.koflox.revoluttest.data.source.DataSource
import com.koflox.revoluttest.data.source.remote.RemoteDataSource
import org.koin.dsl.module

val dataModule = module {
    single<DataSource> {
        RemoteDataSource(get())
    }
    single<DataRepository> {
        AppDataRepository(
            remoteDataSource = get()
        )
    }
}