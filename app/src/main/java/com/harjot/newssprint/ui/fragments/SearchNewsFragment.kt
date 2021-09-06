package com.harjot.newssprint.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialElevationScale
import com.harjot.newssprint.R
import com.harjot.newssprint.adapters.NewsArticleAdapter
import com.harjot.newssprint.models.Article
import com.harjot.newssprint.ui.NewsActivity
import com.harjot.newssprint.ui.NewsViewModel
import com.harjot.newssprint.utils.Constants
import com.harjot.newssprint.utils.OnItemClickListener
import com.harjot.newssprint.utils.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news), OnItemClickListener {

    lateinit var viewModel: NewsViewModel
    lateinit var newsArticleAdapter: NewsArticleAdapter

    val TAG = "SearchNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel

        setUpRecyclerView()


        var job: Job? = null
        search_edit_text.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.resetSearchNews()
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }


        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            Log.e(TAG, "onViewCreated: observe changes response= $response")
            when (response) {
                is Resource.Success -> {
                    hidePaginatingProgressBar()
                    hideLoading()
                    response.data?.let {
                        newsArticleAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage)
                            rvSearchNews.setPadding(0, 0, 0, 0)
                    }
                }
                is Resource.Error -> {
                    hidePaginatingProgressBar()
                    hideLoading()
                    response.message?.let {
                        Log.e(TAG, "onViewCreated: error= $it")
                        Toast.makeText(context, "An error occurred $it", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    if (newsArticleAdapter.differ.currentList.isEmpty()) {
                        showLoading()
                        hidePaginatingProgressBar()
                    } else {
                        hideLoading()
                        showPaginatingProgressBar()
                    }
                }
            }

        }
    }

    private fun showLoading() {
        loading_progress_indicator.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loading_progress_indicator.visibility = View.INVISIBLE
    }

    private fun hidePaginatingProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showPaginatingProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                Log.e(TAG, "onScrollStateChanged: scrolling")
                isScrolling = true
            }
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
                "onScrolled: $isNotLoadingAndNotAtLastPage $isAtLastItem $isNotAtBeginning $isTotalMoreThanVisible $isScrolling $shouldPaginate",
            )
            if (shouldPaginate) {
                viewModel.searchNews(search_edit_text.text.toString())
                isScrolling = false
            }
        }
    }

    private fun setUpRecyclerView() {
        newsArticleAdapter = NewsArticleAdapter(this, TAG)
        rvSearchNews.apply {
            adapter = newsArticleAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    override fun onItemClick(view: View, position: Int, article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article", article)
        }
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
        val extras =
            FragmentNavigatorExtras(view to getString(R.string.article_image_transition))
        findNavController().navigate(
            R.id.action_searchNewsFragment_to_articleViewFragment,
            bundle, null, extras
        )
    }
}
