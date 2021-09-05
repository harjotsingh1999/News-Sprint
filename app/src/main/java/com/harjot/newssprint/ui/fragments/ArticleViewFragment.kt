package com.harjot.newssprint.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.utils.widget.MotionLabel
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.harjot.newssprint.R
import com.harjot.newssprint.ui.NewsActivity
import com.harjot.newssprint.ui.NewsViewModel
import com.harjot.newssprint.utils.Constants
import com.harjot.newssprint.utils.ScreenUtils
import kotlinx.android.synthetic.main.content_article_fragment.*
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.item_article_preview.*
import kotlinx.android.synthetic.main.layout_article_bottom_sheet.*

class ArticleViewFragment : Fragment(R.layout.fragment_article) {

    private val args: ArticleViewFragmentArgs by navArgs()
    lateinit var viewModel: NewsViewModel
    lateinit var bottomSheet: MaterialCardView
    lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>
    val TAG = "ArticleViewFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val article = args.article
        viewModel = (activity as NewsActivity).viewModel


        article_web_view.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url ?: "www.google.com")
        }

        fab.setOnClickListener {
            viewModel.bookmarkArticle(article)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_LONG).show()
        }

        bottomSheet = fragment_article_bottom_sheet as MaterialCardView
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.apply {
            peekHeight = ScreenUtils.getScreenHeight(requireContext()) / 2
            isHideable = false
            expandedOffset = ScreenUtils.getScreenHeight(requireContext()) / 10
            isFitToContents = false
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    (fragment_article_content as MotionLayout).progress = slideOffset
                }
            })
        }
        article_fragment_title_text_view.text = article.title
        article_fragment_title_text_view.isSelected = true
        article_fragment_back_image_button.setOnClickListener {
            Log.e(TAG, "onViewCreated: go back simon")
            findNavController().navigateUp()
        }

        article_author_chip.text = article.author ?: article.source?.name ?: "Author"
        article_time_chip.text = Constants.getLocalModifiedTime(article.publishedAt)
        article_favorite_chip.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.e(TAG, "onViewCreated: saved= $isChecked")
            if (isChecked) {
                article_favorite_chip.apply {
                    chipIconTint = ColorStateList.valueOf(resources.getColor(R.color.colorRed))
                    text = "Saved"
                }
            } else {
                article_favorite_chip.apply {
                    chipIconTint = ColorStateList.valueOf(resources.getColor(R.color.colorGrey))
                    text = "Save"
                }
            }
        }
        Glide.with(this).load(article.urlToImage).into(article_fragment_image_view)
    }
}