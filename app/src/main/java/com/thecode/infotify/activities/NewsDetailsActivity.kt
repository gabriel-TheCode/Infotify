package com.thecode.infotify.activities

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thecode.infotify.R
import kotlinx.android.synthetic.main.activity_news_details.*


class NewsDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)


        //RECEIVE OUR DATA
        val i = intent

        val title = i.extras!!.getString("title")
        val description = i.extras!!.getString("description")
        val source = i.extras!!.getString("source")
        val imageUrl = i.extras!!.getString("imageUrl")
        val content = i.extras!!.getString("content")
        val date = i.extras!!.getString("date")
        val url = i.extras!!.getString("url")


        //REFERENCE VIEWS FROM XML
        val img = image_news
        val txtSource = text_source
        val txtContent = text_content
        val txtDate = text_date
        val webview = webview


        //ASSIGN DATA TO THOSE VIEWS
        Glide.with(this).load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .apply(RequestOptions().centerCrop())
            .into(img)
        txtSource.text = source
        txtContent.text = content
        txtDate.text = date



        webview.settings.loadWithOverviewMode = true

        webview.settings.javaScriptEnabled = true
        webview.isClickable = false
        webview.isEnabled = false
        webview.isHorizontalScrollBarEnabled = true
        webview.webChromeClient = WebChromeClient()

        val webSettings: WebSettings = webview.settings
        webview.loadUrl(url)

    }
}
