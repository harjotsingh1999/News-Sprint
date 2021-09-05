package com.harjot.newssprint.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harjot.newssprint.R
import com.harjot.newssprint.models.Article
import com.harjot.newssprint.utils.Constants
import kotlinx.android.synthetic.main.item_article_preview.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NewsArticleAdapter : RecyclerView.Adapter<NewsArticleAdapter.NewsArticleViewHolder>() {

    val TAG = "NewsArticleAdapter"

    inner class NewsArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsArticleViewHolder {
        return NewsArticleViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_article_preview, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(article_image_view)
            article_source_text_view.text = article.source?.name
            article_title_text_view.text = article.title
            article_description_text_view.text = article.description
            article_time_text_view.text =
                Constants.getLocalModifiedTime(article.publishedAt ?: "time")

            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

}