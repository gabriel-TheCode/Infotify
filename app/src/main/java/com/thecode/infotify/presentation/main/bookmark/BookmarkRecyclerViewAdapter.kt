package com.thecode.infotify.presentation.main.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thecode.infotify.R
import com.thecode.infotify.databinding.AdapterNewsLandscapeBinding
import com.thecode.infotify.domain.model.Article


class BookmarkRecyclerViewAdapter(
    private val onDeleteBookmark: (Article) -> Unit,
    private val onOpenNews: (Article) -> Unit,
    private val onOpenNewsInBrowser: (String) -> Unit,
    private val onShareNews: (Article) -> Unit
) :
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
        holder.apply {
            tvNewsTitle.text = news.title
            tvNewsDate.text = news.publishedAt?.split("T")?.get(0) ?: ""
            tvPublisherName.text = news.source.name

            btnShare.setOnClickListener {
                onShareNews(news)
            }

            btnDelete.setOnClickListener {
                onDeleteBookmark(news)
                newsList.removeAt(position)
                notifyItemRemoved(position)
            }

            container.setOnClickListener {
                onOpenNews(news)
            }

            container.setOnLongClickListener {
                onOpenNewsInBrowser(news.url)
                return@setOnLongClickListener true
            }

            Glide.with(itemView.context).load(news.urlToImage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .apply(RequestOptions().centerCrop())
                .into(image)
        }
    }

    fun setArticleListItems(newsList: List<Article>) {
        this.newsList = ArrayList(newsList)
        notifyDataSetChanged()
    }

    class NewsViewHolder(binding: AdapterNewsLandscapeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val container = binding.frameNews
        val tvNewsTitle = binding.textTitle
        val tvPublisherName = binding.textNamePublisher
        val image = binding.imageNews
        val btnShare = binding.btnShare
        val btnDelete = binding.btnDelete
        val tvNewsDate = binding.textChipDate
    }
}
