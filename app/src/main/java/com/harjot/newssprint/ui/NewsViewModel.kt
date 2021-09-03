package com.harjot.newssprint.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harjot.newssprint.models.NewsResponse
import com.harjot.newssprint.repository.NewsRepository
import com.harjot.newssprint.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    val trendingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private val trendingNewsPage = 1


    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private val searchNewsPage = 1

    init {
        getTrendingNews("us")
    }

    private fun getTrendingNews(countryCode: String) {
        viewModelScope.launch {

            // first emit the loading state
            trendingNews.postValue(Resource.Loading())

            // make network request from repository
            val response = newsRepository.getTrendingNews(countryCode, trendingNewsPage)
            trendingNews.postValue(handleTrendingNewsResponse(response))
        }
    }

    private fun handleTrendingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(data = it)
            }
        }
        return Resource.Error(message = response.message())
    }


    fun searchNews(searchQuery: String) {
        viewModelScope.launch {

            // first emit the loading state
            searchNews.postValue(Resource.Loading())

            // make network request from repository
            val response = newsRepository.searchNews(searchQuery, searchNewsPage)
            searchNews.postValue(handleSearchNewsResponse(response))
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(data = it)
            }
        }
        return Resource.Error(message = response.message())
    }
}