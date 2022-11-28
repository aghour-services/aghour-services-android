package com.aghourservices.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aghourservices.ui.viewModel.CategoriesViewModel

class CategoriesVIewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            return CategoriesViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}