package com.thecode.infotify.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thecode.infotify.R
import com.thecode.infotify.databinding.AdapterNewsBinding
import com.thecode.infotify.domain.model.Article


class NewsRecyclerViewAdapter(
    private val onSaveBookmark: (Article) -> Unit,
    private val onOpenNews: (Article) -> Unit,
    private val onOpenNewsInBrowser: (String) -> Unit,
    private val onShareNews: (Article) -> Unit
) : RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder>() {

    private lateinit var binding: AdapterNewsBinding
    private var newsList: List<Article> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        binding = AdapterNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.apply {
            val news = newsList[position]
            tvNewsTitle.text = news.title
            tvNewsDescription.text = news.description
            tvNewsDate.text = news.publishedAt?.split("T")?.get(0) ?: ""
            tvPublisherName.text = news.source.name

            Glide.with(holder.itemView.context).load(news.urlToImage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .apply(RequestOptions().centerCrop())
                .into(holder.image)

            btnShare.setOnClickListener {
                onShareNews(news)
            }

            container.setOnLongClickListener {
                onOpenNewsInBrowser(news.url)
                return@setOnLongClickListener true
            }

            btnBookmark.setOnClickListener {
                onSaveBookmark(news)
            }

            container.setOnClickListener {
                onOpenNews(news)
            }
        }

    }

    fun setArticleListItems(newsList: List<Article>) {
        this.newsList = emptyList()
        this.newsList = newsList
        notifyDataSetChanged()
    }

    class NewsViewHolder(binding: AdapterNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        val container: FrameLayout = binding.frameNews
        val tvNewsTitle: TextView = binding.textTitle
        val tvNewsDescription: TextView = binding.textDescription
        val tvPublisherName: TextView = binding.textNamePublisher
        val image: ImageView = binding.imageNews
        val btnBookmark: ImageView = binding.btnBookmark
        val btnShare: ImageView = binding.btnShare
        val tvNewsDate: TextView = binding.textChipDate
    }
}
