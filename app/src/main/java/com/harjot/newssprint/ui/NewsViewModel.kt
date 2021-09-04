package com.harjot.newssprint.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.harjot.newssprint.NewsApplication
import com.harjot.newssprint.models.Article
import com.harjot.newssprint.models.NewsResponse
import com.harjot.newssprint.repository.NewsRepository
import com.harjot.newssprint.utils.Constants
import com.harjot.newssprint.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(private val newsRepository: NewsRepository, application: Application) :
    AndroidViewModel(
        application
    ) {

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

        Log.e(
            TAG,
            "getTrendingNews: page= $trendingNewsPage current saved resp= \n $trendingNewsResponse "
        )
        viewModelScope.launch {

            // first emit the loading state
//            trendingNews.postValue(Resource.Loading())

            // make network request from repository
//            val response = newsRepository.getTrendingNews(countryCode, trendingNewsPage)
//            trendingNews.postValue(handleTrendingNewsResponse(response))

            // replace all above with a safe function call
            safeTrendingNewsCall(countryCode)
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
        Log.e(
            TAG,
            "getTrendingNews: page= $searchNewsPage current saved resp= \n $searchNewsResponse "
        )

        viewModelScope.launch {

            // first emit the loading state
//            searchNews.postValue(Resource.Loading())

            // make network request from repository
//            val response = newsRepository.searchNews(searchQuery, searchNewsPage)
//            searchNews.postValue(handleSearchNewsResponse(response))
            safeSearchNewsCall(searchQuery)
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

    private suspend fun safeTrendingNewsCall(countryCode: String) {
        trendingNews.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getTrendingNews(countryCode, trendingNewsPage)
                trendingNews.postValue(handleTrendingNewsResponse(response))
            } else {
                trendingNews.postValue(Resource.Error(message = "No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> trendingNews.postValue(Resource.Error(message = "Network failure"))
                else -> trendingNews.postValue(Resource.Error(message = "cConversion error"))

            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error(message = "No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error(message = "Network failure"))
                else -> searchNews.postValue(Resource.Error(message = "cConversion error"))

            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<NewsApplication>()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}