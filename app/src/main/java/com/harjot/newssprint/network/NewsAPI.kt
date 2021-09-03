package com.harjot.newssprint.network

import com.harjot.newssprint.models.NewsResponse
import com.harjot.newssprint.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


//https://newsapi.org/v2/top-headlines
interface NewsAPI {


    @GET("/v2/top-headlines")
    suspend fun getTrendingNews(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = Constants.API_KEY
    ): Response<NewsResponse>


    @GET("/v2/everything")
    suspend fun searchTopic(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = Constants.API_KEY
    ): Response<NewsResponse>
}