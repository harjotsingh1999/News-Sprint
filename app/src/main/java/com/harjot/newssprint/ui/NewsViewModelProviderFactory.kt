package com.harjot.newssprint.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.harjot.newssprint.repository.NewsRepository

class NewsViewModelProviderFactory(
    private val newsRepository: NewsRepository,
    val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository, app) as T
    }
}