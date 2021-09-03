package com.harjot.newssprint.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.harjot.newssprint.R
import com.harjot.newssprint.models.Article
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleViewFragment : Fragment(R.layout.fragment_article) {

    private val args: ArticleViewFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val article = args.article

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
    }
}