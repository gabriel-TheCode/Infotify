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
import com.thecode.infotify.core.domain.Article

interface NewsOnClickListener {

    fun saveBookmark(article: Article)

    fun deleteBookmark(article: Article)

    fun openNews(article: Article)

    fun shareNews(article: Article)
}

class NewsRecyclerViewAdapter(private val listener: NewsOnClickListener) :
    RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder>() {

    private lateinit var binding: AdapterNewsBinding
    var newsList: List<Article> = listOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        binding = AdapterNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.tvNewsTitle.text = news.title
        holder.tvNewsdescription.text = news.description
        holder.tvNewsDate.text = news.publishedAt?.split("T")?.get(0) ?: ""
        holder.tvPublisherName.text = news.source.name ?: "Infotify News"

        Glide.with(holder.itemView.context).load(news.urlToImage)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .apply(RequestOptions().centerCrop())
            .into(holder.image)

        holder.btnShare.setOnClickListener {
            listener.shareNews(news)
        }

        holder.container.setOnLongClickListener {
            listener.saveBookmark(news)
            return@setOnLongClickListener true
        }

        holder.container.setOnClickListener {
            listener.openNews(news)
        }
    }

    fun setArticleListItems(newsList: ArrayList<Article>) {
        this.newsList = emptyList()
        this.newsList = newsList
        notifyDataSetChanged()
    }

    class NewsViewHolder(binding: AdapterNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        val container: FrameLayout = binding.frameNews
        val tvNewsTitle: TextView = binding.textTitle
        val tvNewsdescription: TextView = binding.textDescription
        val tvPublisherName: TextView = binding.textNamePublisher
        val image: ImageView = binding.imageNews
        val btnShare: ImageView = binding.btnShare
        val tvNewsDate: TextView = binding.textChipDate
    }

}
