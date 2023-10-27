package com.project.news.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.project.news.databinding.ItemArticleBinding
import com.project.news.models.Article
import javax.inject.Inject

/**
 * Common recycler view implemented with diffUtil , to list breaking news, saved news, search news.
 */

class NewsAdapter @Inject constructor() :
    ListAdapter<Article, NewsAdapter.ArticleViewHolder>(DiffUtilCallback()) {
    init {
        setHasStableIds(true)
    }

    var tracker: SelectionTracker<Long>? = null

    inner class ArticleViewHolder(val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
            return object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int {
                    return bindingAdapterPosition
                }

                override fun getSelectionKey(): Long {
                    return itemId
                }
            }
        }

        fun bind(article: Article, isActivated: Boolean) {
            binding.apply {
                tvSource.text = article.source?.name
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvPublishedAt.text = article.publishedAt
            }

            itemView.isActivated = isActivated
            Glide.with(itemView.context).load(article.urlToImage)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progress.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progress.visibility = View.GONE
                        return false
                    }

                }).into(binding.ivArticleImage)

        }

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                val article = currentList[bindingAdapterPosition]
                onItemClickListener?.invoke(article)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = currentList[position]
        tracker?.let {
            holder.bind(article, it.isSelected(position.toLong()))
        }


    }

    var onItemClickListener: ((Article) -> Unit)? = null
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}

private class DiffUtilCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return (oldItem.url == newItem.url)
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}
