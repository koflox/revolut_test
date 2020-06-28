package com.koflox.revoluttest.di

import com.koflox.revoluttest.use_cases.CalculateAmountsUseCase
import com.koflox.revoluttest.use_cases.GetRatesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory {
        GetRatesUseCase(dataRepository = get())
    }
    factory {
        CalculateAmountsUseCase()
    }
}