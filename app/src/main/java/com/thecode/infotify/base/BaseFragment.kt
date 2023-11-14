package com.thecode.infotify.base

import android.content.Intent
import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.thecode.infotify.R
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.presentation.newsdetails.NewsDetailsActivity
import com.thecode.infotify.utils.CustomProgressBar

open class BaseFragment : Fragment() {

    private val progressBar: CustomProgressBar = CustomProgressBar()

    fun showErrorDialog(title: String, description: String) {
        AestheticDialog.Builder(requireActivity(), DialogStyle.RAINBOW, DialogType.ERROR)
            .setTitle(title)
            .setMessage(description)
            .setDuration(2000)
            .show()
    }

    fun showSuccessDialog(title: String, description: String) {
        AestheticDialog.Builder(requireActivity(), DialogStyle.RAINBOW, DialogType.SUCCESS)
            .setTitle(title)
            .setMessage(description)
            .setDuration(2000)
            .show()
    }

    fun openSharingIntent(article: Article) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            article.title + " - via Infotify News App\n" + article.url
        )
        sendIntent.type = "text/plain"
        requireActivity().startActivity(sendIntent)
    }

    fun openDetailsActivity(article: Article) {

        val i = Intent(context, NewsDetailsActivity::class.java)

        // ADD DATA TO OUR INTENT
        i.putExtra("title", article.title)
        i.putExtra("description", article.description)
        i.putExtra("imageUrl", article.urlToImage)
        i.putExtra("source", article.source.name)
        i.putExtra("date", article.publishedAt)
        i.putExtra("content", article.content)
        i.putExtra("url", article.url)

        // START DETAIL ACTIVITY
        requireActivity().startActivity(i)
    }

    fun loadWebviewDialog(article: Article) {
        // show article content inside a dialog
        val newsView = WebView(requireActivity())
        var failedLoading = false
        newsView.settings.loadWithOverviewMode = true

        val title: String = article.title.toString()
        val url: String = article.url

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
                dismissDialog()
                showErrorDialog(
                    getString(R.string.web_error),
                    getString(R.string.link_not_reachable)
                )
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (!failedLoading) {
                    dismissDialog()
                    val alertDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
                    alertDialog.setTitle(title)
                    alertDialog.setView(newsView)
                    alertDialog.setButton(
                        AlertDialog.BUTTON_NEUTRAL, "OK"
                    ) { _, _ -> dismissDialog() }
                    alertDialog.show()
                } else {
                    dismissDialog()
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.show(requireContext(), getString(R.string.please_wait))
            }
        }
        newsView.loadUrl(url)
        newsView.isClickable = false
        newsView.isEnabled = false
    }

    private fun dismissDialog() {
        try {
            if (progressBar.dialog.isShowing) {
                progressBar.dialog.dismiss()
            }
        } catch (ex: Exception) {
            showErrorDialog(
                getString(R.string.web_error),
                getString(R.string.link_not_reachable)
            )
        }
    }
}
