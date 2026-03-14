package com.prafullkumar.usage.presentation.screens.appDetail

import android.content.Context
import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

class AppDetailViewModel(
    private val packageName: String,
    private val context: Context
) : ViewModel(), KoinComponent {


}