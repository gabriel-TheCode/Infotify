package com.thecode.infotify.presentation.main

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
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.infotify.R
import com.thecode.infotify.databinding.AdapterNewsBinding
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.utils.CustomProgressBar

class NewsRecyclerViewAdapter(val context: Context, viewModel: ViewModel) :
    RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder>() {

    private lateinit var binding: AdapterNewsBinding
    var newsList: List<Article> = listOf()
    private val progressBar: CustomProgressBar = CustomProgressBar()
    private val viewModel: MainViewModel = viewModel


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
        holder.tvPublisherName.text = news.source?.name ?: "Infotify News"

        Glide.with(context).load(news.urlToImage)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .apply(RequestOptions().centerCrop())
            .into(holder.image)
        holder.btnShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                news.title + " - via Infotify News App\n" + news.url
            )
            sendIntent.type = "text/plain"
            context.startActivity(sendIntent)
        }

        holder.btnBookmark.setOnClickListener {
            saveIntoDatabase(news)
        }

        // WHEN ITEM IS CLICKED
        holder.container.setOnClickListener {
            // INTENT OBJ
            /*val i = Intent(context, NewsDetailsActivity::class.java)

            //ADD DATA TO OUR INTENT
            i.putExtra("title", newsList[position].title)
            i.putExtra("description", newsList[position].description)
            i.putExtra("imageUrl", newsList[position].urlToImage)
            i.putExtra("source", newsList[position].source!!.name)
            i.putExtra("date", newsList[position].publishedAt)
            i.putExtra("content", newsList[position].content)
            i.putExtra("url", newsList[position].url)

            //START DETAIL ACTIVITY
            context.startActivity(i)*/

            // show article content inside a dialog
            val newsView = WebView(context)
            var failedLoading = false
            newsView.settings.loadWithOverviewMode = true

            val title: String = newsList[position].title.toString()
            val content: String = newsList[position].content.toString()
            val url: String = newsList[position].url.toString()

            newsView.settings.javaScriptEnabled = false
            newsView.isHorizontalScrollBarEnabled = false
            newsView.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
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
        val btnBookmark: ImageView = binding.btnBookmark
        val tvNewsDate: TextView = binding.textChipDate
    }

    private fun saveIntoDatabase(article: Article) {
            viewModel.saveBookmark(article)

            // Transaction was a success.
            Log.v("database", "Stored ok")
            AestheticDialog.showRainbow(
                context as Activity?,
                "Success",
                "Bookmark saved",
                AestheticDialog.SUCCESS
            )
        }
    }
