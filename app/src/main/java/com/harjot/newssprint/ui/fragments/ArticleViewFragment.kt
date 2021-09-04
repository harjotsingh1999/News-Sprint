package com.harjot.newssprint.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.harjot.newssprint.R
import com.harjot.newssprint.adapters.NewsArticleAdapter
import com.harjot.newssprint.models.Article
import com.harjot.newssprint.ui.NewsActivity
import com.harjot.newssprint.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleViewFragment : Fragment(R.layout.fragment_article) {

    private val args: ArticleViewFragmentArgs by navArgs()
    lateinit var viewModel: NewsViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val article = args.article
        viewModel = (activity as NewsActivity).viewModel

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url ?: "www.google.com")
        }

        fab.setOnClickListener {
            viewModel.bookmarkArticle(article)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_LONG).show()
        }
    }
}