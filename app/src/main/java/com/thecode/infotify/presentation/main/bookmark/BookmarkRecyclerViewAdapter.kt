package com.thecode.infotify.presentation.main.bookmark

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.infotify.R
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.databinding.AdapterNewsLandscapeBinding
import com.thecode.infotify.utils.CustomProgressBar

class BookmarkRecyclerViewAdapter(val context: Context, val viewModel: BookmarkViewModel) :
    RecyclerView.Adapter<BookmarkRecyclerViewAdapter.NewsViewHolder>() {

    private lateinit var binding: AdapterNewsLandscapeBinding
    private var newsList: MutableList<ArticleEntity> = mutableListOf()
    private val progressBar: CustomProgressBar = CustomProgressBar()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        binding =
            AdapterNewsLandscapeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article: ArticleEntity = newsList[position]
        val title: String? = article.title
        val url: String = article.url
        val publishAt = article.publishedAt
        val urlToImage = article.urlToImage
        val sourceName = article.source?.name

        holder.tvNewsTitle.text = title
        holder.tvNewsDate.text = publishAt?.split("T")?.get(0) ?: ""

        if (sourceName.isNullOrEmpty()) {
            holder.tvPublisherName.text = "Infotify News"
        } else {
            holder.tvPublisherName.text = sourceName
        }

        Glide.with(context).load(article.urlToImage)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .apply(RequestOptions().centerCrop())
            .into(holder.image)
        holder.btnShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "$title - via Infotify News App\n$url"
            )
            sendIntent.type = "text/plain"
            context.startActivity(sendIntent)
        }

        // WHEN ITEM IS CLICKED
        holder.btnBookmark.setOnClickListener {
            deleteFromDatabase(holder.adapterPosition, title.toString())
        }

        // WHEN ITEM IS CLICKED
        holder.container.setOnClickListener {

            val newsView = WebView(context)
            var failedLoading = false
            newsView.settings.loadWithOverviewMode = true
            newsView.settings.javaScriptEnabled = false
            newsView.isHorizontalScrollBarEnabled = false
            newsView.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return true
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    failedLoading = true
                    if (progressBar.dialog.isShowing) {
                        progressBar.dialog.dismiss()
                    }
                    AestheticDialog.showRainbow(
                        context as Activity?,
                        "ERROR",
                        "Sorry, the link of the article is not reachable",
                        AestheticDialog.ERROR
                    )
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    if (!failedLoading) {
                        progressBar.dialog.dismiss()
                        val alertDialog: AlertDialog = AlertDialog.Builder(context).create()
                        alertDialog.setTitle(title)
                        alertDialog.setView(newsView)
                        alertDialog.setButton(
                            AlertDialog.BUTTON_NEUTRAL, "OK"
                        ) { dialog, _ -> dialog.dismiss() }
                        alertDialog.show()
                    } else {
                        progressBar.dialog.dismiss()
                    }
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar.show(context, "Loading...")
                }
            }
            newsView.loadUrl(url)
            newsView.isClickable = false
            newsView.isEnabled = false
        }
    }

    fun setArticleListItems(newsList: MutableList<ArticleEntity>) {
        this.newsList = newsList
    }

    class NewsViewHolder(binding: AdapterNewsLandscapeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val container: FrameLayout = binding.frameNews
        val tvNewsTitle: TextView = binding.textTitle
        val tvPublisherName: TextView = binding.textNamePublisher
        val image: ImageView = binding.imageNews
        val btnShare: ImageView = binding.btnShare
        val btnBookmark: ImageView = binding.btnBookmark
        val tvNewsDate: TextView = binding.textChipDate
    }

    private fun deleteFromDatabase(position: Int, url: String) {
            remove(position)
            viewModel.deleteBookmark(url)
            Log.v("database", "Delete ok")
            Toast.makeText(context, "Delete successfully", Toast.LENGTH_LONG).show()
    }

    private fun remove(position: Int) {
        if (itemCount > 1) {
            newsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}