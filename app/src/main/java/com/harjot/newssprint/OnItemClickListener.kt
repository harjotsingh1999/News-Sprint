package com.harjot.newssprint

import android.view.View
import com.harjot.newssprint.models.Article

interface OnItemClickListener {
    fun onItemClick(view: View, position: Int, article: Article)
}