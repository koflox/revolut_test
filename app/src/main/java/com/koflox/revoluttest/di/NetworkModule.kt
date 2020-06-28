package com.koflox.revoluttest.di

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.koflox.revoluttest.BuildConfig
import com.koflox.revoluttest.data.entities.rates.response.RatesResponse
import com.koflox.revoluttest.data.entities.rates.response.RatesResponseAdapter
import com.koflox.revoluttest.data.source.remote.RevolutService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        createLoggingInterceptor()
    }
    single {
        createOkHttpClient(get())
    }
    single {
        createWebService<RevolutService>(get(), BuildConfig.BASE_URL_REVOLUT)
    }
}

private fun createOkHttpClient(
    httpLoggingInterceptor: HttpLoggingInterceptor
): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(httpLoggingInterceptor)
    .build()

private fun createLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = when {
        BuildConfig.DEBUG -> HttpLoggingInterceptor.Level.BODY
        else -> HttpLoggingInterceptor.Level.NONE
    }
}

private fun createGsonConverterFactory() = GsonConverterFactory.create(GsonBuilder().run {
    registerTypeAdapter(
        RatesResponse::class.java,
        RatesResponseAdapter()
    )
    create()
})

private inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(createGsonConverterFactory())
        .addCallAdapterFactory(CoroutineCallAdapterFactory()).build()
    return retrofit.create(T::class.java)
}