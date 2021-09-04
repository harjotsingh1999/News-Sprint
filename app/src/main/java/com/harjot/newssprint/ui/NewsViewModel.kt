package com.harjot.newssprint.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harjot.newssprint.models.Article
import com.harjot.newssprint.models.NewsResponse
import com.harjot.newssprint.repository.NewsRepository
import com.harjot.newssprint.utils.Constants
import com.harjot.newssprint.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    val trendingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var trendingNewsPage = 1
    var trendingNewsResponse: NewsResponse? = null


    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    val TAG = "NewsViewModel"

    init {
        getTrendingNews(Constants.COUNTRY_CODE_INDIA)
    }

    fun getTrendingNews(countryCode: String) {

        Log.e(TAG, "getTrendingNews: page= $trendingNewsPage current saved resp= \n $trendingNewsResponse ")
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
                trendingNewsPage += 1
                // if first response ever
                if (trendingNewsResponse == null) {
                    // save articles if config changes happen
                    trendingNewsResponse = it
                } else {
                    // add new articles to existing articles list
                    trendingNewsResponse?.articles?.addAll(it.articles)
//                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(data = trendingNewsResponse ?: it)
            }
        }
        return Resource.Error(message = response.message())
    }


    fun searchNews(searchQuery: String) {
        Log.e(TAG, "getTrendingNews: page= $searchNewsPage current saved resp= \n $searchNewsResponse ")

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
                searchNewsPage += 1
                // if first response ever
                if (searchNewsResponse == null) {
                    // save articles if config changes happen
                    searchNewsResponse = it
                } else {
                    // add new articles to existing articles list
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = it.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(data = searchNewsResponse ?: it)
            }
        }
        return Resource.Error(message = response.message())
    }


    fun bookmarkArticle(article: Article) {
        viewModelScope.launch {
            newsRepository.upsertArticle(article)
        }
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteSavedArticle(article: Article) {
        viewModelScope.launch {
            newsRepository.deleteArticle(article)
        }
    }


    /**
     * this is done because, if there is data present, and a new search is made,
     * the new results will get added to the prev results,
     * hence reset when edit text is changed
     */
    fun resetSearchNews() {
        searchNewsPage = 1
        searchNewsResponse = null
    }

}