package com.project.news.utils

import android.content.Context
import android.content.res.Configuration
import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.project.news.adapters.NewsAdapter

class MyItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as NewsAdapter.ArticleViewHolder)
                .getItemDetails()
        }
        return null
    }

}

object Utils {
    fun isNightMode(context: Context): Boolean {
        val nightModeFlags: Int =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}