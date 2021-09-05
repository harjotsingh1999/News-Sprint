package com.harjot.newssprint.utils

import android.content.Context
import android.util.DisplayMetrics

object ScreenUtils {
    /**
     * @param context
     * @return the Screen height in DP
     */
    fun getHeightDp(context: Context): Float {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.heightPixels / displayMetrics.density
    }

    /**
     * @param context
     * @return the screen width in dp
     */
    fun getWidthDp(context: Context): Float {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels / displayMetrics.density
    }

    fun getScreenHeight(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.heightPixels
    }

    fun getScreenWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels
    }

    fun pixelsToDp(context: Context, pixels: Int): Float {
        val displayMetrics = context.resources.displayMetrics
        return pixels / displayMetrics.density
    }

    fun dpToPixels(context: Context, dp: Int): Float {
        val displayMetrics = context.resources.displayMetrics
        return dp * displayMetrics.density
    }
}
