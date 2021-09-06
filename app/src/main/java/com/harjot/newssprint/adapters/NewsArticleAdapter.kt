package com.harjot.newssprint.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.harjot.newssprint.OnItemClickListener
import com.harjot.newssprint.R
import com.harjot.newssprint.models.Article
import com.harjot.newssprint.viewholders.NewsArticleViewHolder

class NewsArticleAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val fragmentName: String
) :
    RecyclerView.Adapter<NewsArticleViewHolder>() {

    val TAG = "NewsArticleAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsArticleViewHolder {
        return NewsArticleViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_article_preview, parent, false),
            onItemClickListener,
            fragmentName
        )
    }

    override fun onBindViewHolder(holder: NewsArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bindView(article)
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


}