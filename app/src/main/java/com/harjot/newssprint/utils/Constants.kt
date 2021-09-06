package com.harjot.newssprint.utils

import java.text.SimpleDateFormat
import java.util.*

object Constants {

    const val QUERY_PAGE_SIZE: Int = 20
    const val API_KEY = "6cfa8cbc28be4e57999c67c6f617f0b9";
    const val BASE_URL = "https://newsapi.org"
    const val SEARCH_NEWS_TIME_DELAY = 500L
    const val COUNTRY_CODE_INDIA = "us"
    fun getLocalModifiedTime(articleTime: String?): String? {
        try {
            val parser =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = parser.parse(articleTime!!)
            val formatter =
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            return formatter.format(date!!)
        } catch (e: Exception) {
            return articleTime
        }
    }
}