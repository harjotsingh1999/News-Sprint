package com.harjot.newssprint.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.harjot.newssprint.R
import com.harjot.newssprint.adapters.NewsArticleAdapter
import com.harjot.newssprint.ui.NewsActivity
import com.harjot.newssprint.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_bookmarked_news.*

class BookmarkedNewsFragment() : Fragment(R.layout.fragment_bookmarked_news) {

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


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                // get the position of the article swiped
                val position = viewHolder.adapterPosition
                // get the article at that position
                val article = newsArticleAdapter.differ.currentList[position]

                viewModel.deleteSavedArticle(article)

                Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.bookmarkArticle(article)
                    }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvSavedNews)

        viewModel.getSavedNews().observe(viewLifecycleOwner) { articles ->
            newsArticleAdapter.differ.submitList(articles)
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