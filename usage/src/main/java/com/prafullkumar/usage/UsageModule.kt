package com.prafullkumar.usage

import com.prafullkumar.usage.presentation.screens.appDetail.AppDetailViewModel
import com.prafullkumar.usage.presentation.screens.usageStats.UsageScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val usageModule = module {
    viewModel { UsageScreenViewModel(get()) }
    viewModel { AppDetailViewModel(get(), get()) }
}