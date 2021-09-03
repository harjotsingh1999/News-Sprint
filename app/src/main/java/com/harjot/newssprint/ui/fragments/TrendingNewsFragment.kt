package com.harjot.newssprint.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.harjot.newssprint.R
import com.harjot.newssprint.adapters.NewsArticleAdapter
import com.harjot.newssprint.ui.NewsActivity
import com.harjot.newssprint.ui.NewsViewModel
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
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        newsArticleAdapter.differ.submitList(it.articles)
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
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        newsArticleAdapter = NewsArticleAdapter()
        rvBreakingNews.apply {
            adapter = newsArticleAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}