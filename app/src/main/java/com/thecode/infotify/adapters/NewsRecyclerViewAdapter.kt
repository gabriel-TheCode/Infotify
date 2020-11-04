package com.thecode.infotify.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.infotify.R
import com.thecode.infotify.entities.Article
import com.thecode.infotify.entities.Source
import com.thecode.infotify.utils.CustomProgressBar
import io.realm.Realm
import kotlinx.android.synthetic.main.adapter_news.view.*


class NewsRecyclerViewAdapter(val context: Context) : RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder>() {

    var newsList : List<Article> = listOf()
    private val progressBar: CustomProgressBar = CustomProgressBar()
    val realm: Realm = Realm.getDefaultInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_news,parent,false)
        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.tvNewsTitle.text = newsList[position].title
        holder.tvNewsdescription.text = newsList[position].description
        holder.tvNewsDate.text = newsList[position].publishedAt?.split("T")?.get(0) ?: ""
        holder.tvPublisherName.text = newsList[position].source?.name ?: "Infotify News"

        Glide.with(context).load(newsList[position].urlToImage)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .apply(RequestOptions().centerCrop())
            .into(holder.image)
        holder.btnShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                newsList[position].title + " - via Infotify News App\n" + newsList[position].url
            )
            sendIntent.type = "text/plain"
            context.startActivity(sendIntent)
        }

        holder.btnBookmark.setOnClickListener {
            saveIntoDatabase(newsList[position])

        }

        //WHEN ITEM IS CLICKED
        holder.container.setOnClickListener{
            //INTENT OBJ
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

            //show article content inside a dialog
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
                    if(progressBar.dialog.isShowing){
                        progressBar.dialog.dismiss()
                    }
                    AestheticDialog.showRainbow(context as Activity?, "ERROR", "Sorry, the link of the article is not reachable", AestheticDialog.ERROR)
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
                    }else{
                        progressBar.dialog.dismiss()
                    }
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar.show(context,"Loading...")
                }
            }
            newsView.loadUrl(url)
            newsView.isClickable = false
            newsView.isEnabled = false

        }
    }

    fun setArticleListItems(newsList: List<Article>){
        this.newsList = emptyList()
        this.newsList = newsList
        notifyDataSetChanged()
    }



    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val container: FrameLayout = itemView.frame_news
        val tvNewsTitle: TextView = itemView.text_title
        val tvNewsdescription: TextView = itemView.text_description
        val tvPublisherName: TextView = itemView.text_name_publisher
        val image: ImageView = itemView.image_news
        val btnShare: ImageView = itemView.btnShare
        val btnBookmark: ImageView = itemView.btnBookmark
        val tvNewsDate : TextView = itemView.text_chip_date
    }


    private fun saveIntoDatabase(article: Article) {

        realm.executeTransactionAsync({ realm ->
            val articleBookmark: Article = realm.createObject(Article::class.java)
            val sourceArticle: Source = realm.createObject(Source::class.java)

            articleBookmark.urlToImage = article.urlToImage
            articleBookmark.url = article.url
            articleBookmark.author = article.author
            articleBookmark.content = article.content
            articleBookmark.description = article.description
            articleBookmark.publishedAt = article.publishedAt
            articleBookmark.title = article.title
            sourceArticle.name = article.source?.name
            articleBookmark.source = sourceArticle
        }, {
            // Transaction was a success.
            Log.v("database", "Stored ok")
            AestheticDialog.showRainbow(
                context as Activity?,
                "Success",
                "Bookmark saved",
                AestheticDialog.SUCCESS
            )

        }, {
            // Transaction failed and was automatically canceled.
            AestheticDialog.showToaster(
                context as Activity?,
                "Error",
                "Something went wrong",
                AestheticDialog.ERROR
            )

        })
    }


}