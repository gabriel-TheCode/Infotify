package com.thecode.infotify.presentation.main.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thecode.infotify.R
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.databinding.AdapterNewsLandscapeBinding

interface BookmarkOnClickListener {

    fun deleteBookmark(article: Article)

    fun openNews(article: Article)

    fun shareNews(article: Article)
}

class BookmarkRecyclerViewAdapter(private val listener: BookmarkOnClickListener) :
    RecyclerView.Adapter<BookmarkRecyclerViewAdapter.NewsViewHolder>() {

    private lateinit var binding: AdapterNewsLandscapeBinding
    private var newsList: ArrayList<Article> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        binding =
            AdapterNewsLandscapeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.tvNewsTitle.text = news.title
        holder.tvNewsDate.text = news.publishedAt?.split("T")?.get(0) ?: ""
        holder.tvPublisherName.text = news.source.name

        Glide.with(holder.itemView.context).load(news.urlToImage)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .apply(RequestOptions().centerCrop())
            .into(holder.image)
        holder.btnShare.setOnClickListener {
            listener.shareNews(news)
        }

        holder.btnDelete.setOnClickListener {
            listener.deleteBookmark(news)
            this.newsList.removeAt(position)
            notifyItemRemoved(position)
        }

        holder.container.setOnClickListener {
            listener.openNews(news)
        }
    }

    fun setArticleListItems(newsList: ArrayList<Article>) {
        this.newsList = newsList
        notifyDataSetChanged()
    }

    class NewsViewHolder(binding: AdapterNewsLandscapeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val container: FrameLayout = binding.frameNews
        val tvNewsTitle: TextView = binding.textTitle
        val tvPublisherName: TextView = binding.textNamePublisher
        val image: ImageView = binding.imageNews
        val btnShare: ImageView = binding.btnShare
        val btnDelete: ImageView = binding.btnDelete
        val tvNewsDate: TextView = binding.textChipDate
    }
}
