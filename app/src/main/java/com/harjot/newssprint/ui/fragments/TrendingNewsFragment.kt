package com.harjot.newssprint.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harjot.newssprint.R
import com.harjot.newssprint.adapters.NewsArticleAdapter
import com.harjot.newssprint.ui.NewsActivity
import com.harjot.newssprint.ui.NewsViewModel
import com.harjot.newssprint.utils.Constants
import com.harjot.newssprint.utils.Resource
import kotlinx.android.synthetic.main.fragment_trending_news.*

class TrendingNewsFragment : Fragment(R.layout.fragment_trending_news) {


    lateinit var viewModel: NewsViewModel
    lateinit var newsArticleAdapter: NewsArticleAdapter
    val TAG = "TrendingNewsFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()

        newsArticleAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_trendingNewsFragment_to_articleViewFragment,
                bundle
            )
        }
        viewModel.trendingNews.observe(viewLifecycleOwner) { response ->

            Log.e(TAG, "onViewCreated: observe changes response= $response")
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        newsArticleAdapter.differ.submitList(it.articles.toList())

                        // check if more pages are left using the news response
                        val totalPages = it.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.trendingNewsPage == totalPages

                        if (isLastPage) {
                            rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Log.e(TAG, "onViewCreated: error= $it")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        }
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItems = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotAtLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItems >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginate =
                isNotLoadingAndNotAtLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            Log.e(
                TAG,
                "onScrolled: $isNotLoadingAndNotAtLastPage $isAtLastItem $isNotAtBeginning $isTotalMoreThanVisible $shouldPaginate",
            )
            if (shouldPaginate) {
                viewModel.getTrendingNews(Constants.COUNTRY_CODE_INDIA)
                isScrolling = false
            }
        }
    }

    private fun setUpRecyclerView() {
        newsArticleAdapter = NewsArticleAdapter()
        rvBreakingNews.apply {
            adapter = newsArticleAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@TrendingNewsFragment.scrollListener)
        }
    }
}