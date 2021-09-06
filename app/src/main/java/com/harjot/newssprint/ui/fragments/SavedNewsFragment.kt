package com.harjot.newssprint.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.harjot.newssprint.R
import com.harjot.newssprint.adapters.NewsArticleAdapter
import com.harjot.newssprint.models.Article
import com.harjot.newssprint.ui.NewsActivity
import com.harjot.newssprint.ui.NewsViewModel
import com.harjot.newssprint.utils.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment() : Fragment(R.layout.fragment_saved_news), OnItemClickListener {

    lateinit var viewModel: NewsViewModel
    lateinit var newsArticleAdapter: NewsArticleAdapter

    val TAG = "SavedNewsFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()


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

                Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_SHORT)
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
        newsArticleAdapter = NewsArticleAdapter(this, TAG)
        rvSavedNews.apply {
            adapter = newsArticleAdapter
            layoutManager = LinearLayoutManager(activity)
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
            R.id.action_bookmarkedNewsFragment_to_articleViewFragment,
            bundle,
            null,
            extras
        )
    }
}