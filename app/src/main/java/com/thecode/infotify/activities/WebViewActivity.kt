package com.thecode.infotify.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.thecode.infotify.R
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val i = intent

        val url = i.extras!!.getString("url")

        val webView = webview
        webView.settings.loadWithOverviewMode = true
        webView.settings.javaScriptEnabled = true
        webView.isVerticalScrollBarEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return true
            }
        }
        webView.loadUrl(url)
    }
}
