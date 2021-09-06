package com.harjot.newssprint.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harjot.newssprint.models.Article
import com.harjot.newssprint.utils.Constants
import com.harjot.newssprint.utils.OnItemClickListener
import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsArticleViewHolder(
    itemView: View,
    private val onItemClickListener: OnItemClickListener,
    private val fragmentName: String
) :
    RecyclerView.ViewHolder(itemView) {
    fun bindView(article: Article) {
        itemView.apply {
            Glide.with(this).load(article.urlToImage).into(article_image_view)
            itemView.transitionName = fragmentName + article.url
            article_source_text_view.text = article.source?.name
            article_title_text_view.text = article.title
            article_description_text_view.text = article.description

            article_time_text_view.text =
                Constants.getLocalModifiedTime(article.publishedAt ?: "time")

            setOnClickListener {
                onItemClickListener.onItemClick(itemView, adapterPosition, article)
            }
        }
    }
}