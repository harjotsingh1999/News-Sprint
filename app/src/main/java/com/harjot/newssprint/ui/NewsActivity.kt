package com.harjot.newssprint.ui

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.harjot.newssprint.R
import com.harjot.newssprint.db.ArticleDatabase
import com.harjot.newssprint.repository.NewsRepository
import kotlinx.android.synthetic.main.activity_main.*

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    val TAG = "NewsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository, application)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottom_navigation_view.setupWithNavController(navController)
//        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.e(
                TAG,
                "on destination changed: $destination , arguments= $arguments, ${controller.currentDestination}"
            )
            if (destination.id == R.id.articleViewFragment) {
                bottom_navigation_view.visibility = View.GONE
            } else bottom_navigation_view.visibility = View.VISIBLE
        }
    }
}
