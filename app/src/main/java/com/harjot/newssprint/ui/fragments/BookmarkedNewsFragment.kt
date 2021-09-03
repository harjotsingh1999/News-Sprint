package com.harjot.newssprint.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.harjot.newssprint.R
import com.harjot.newssprint.adapters.NewsArticleAdapter
import com.harjot.newssprint.ui.NewsActivity
import com.harjot.newssprint.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_bookmarked_news.*

class BookmarkedNewsFragment : Fragment(R.layout.fragment_bookmarked_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsArticleAdapter: NewsArticleAdapter

    val TAG = "BookmarkedNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()

        newsArticleAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_bookmarkedNewsFragment_to_articleViewFragment,
                bundle
            )
        }
    }

    private fun setUpRecyclerView() {
        newsArticleAdapter = NewsArticleAdapter()
        rvSavedNews.apply {
            adapter = newsArticleAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}