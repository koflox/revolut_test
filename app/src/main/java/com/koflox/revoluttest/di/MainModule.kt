package com.koflox.revoluttest.di

import com.koflox.revoluttest.ui.rates.RatesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        RatesViewModel(
            app = get(),
            getRatesUseCase = get(),
            calculateAmountsUseCase = get()
        )
    }
}

val revolutModules = listOf(viewModelModule, networkModule, dataModule, useCaseModule)