package com.harjot.newssprint.repository

import com.harjot.newssprint.db.ArticleDatabase
import com.harjot.newssprint.network.RetrofitInstance

class NewsRepository(val db: ArticleDatabase) {

    suspend fun getTrendingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getTrendingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchTopic(searchQuery, pageNumber)

}