package com.harjot.newssprint.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.harjot.newssprint.R
import com.harjot.newssprint.ui.NewsActivity
import com.harjot.newssprint.ui.NewsViewModel

class BookmarkedNewsFragment : Fragment(R.layout.fragment_bookmarked_news) {

    lateinit var viewModel: NewsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
    }
}