package com.thecode.infotify.base

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
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
import com.thecode.infotify.domain.model.Article
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
            .setDuration(1000)
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

    fun loadWebviewDialog(article: Article) {
        // show article content inside a dialog
        val newsView = WebView(requireActivity())
        var failedLoading = false

        val title: String = article.title.toString()
        val url: String = article.url

        newsView.apply {
            settings.loadWithOverviewMode = true
            isHorizontalScrollBarEnabled = false
            isClickable = false
            isEnabled = false
            webViewClient = object : WebViewClient() {

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
                        val alertDialog: AlertDialog =
                            AlertDialog.Builder(requireContext()).create()
                        alertDialog.apply {
                            setTitle(title)
                            setView(newsView)
                            setButton(
                                AlertDialog.BUTTON_NEUTRAL, "OK"
                            ) { _, _ -> dismissDialog() }
                            show()
                        }
                    } else {
                        dismissDialog()
                    }
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar.show(requireContext(), getString(R.string.please_wait))
                }
            }
            loadUrl(url)
        }
    }

    private fun dismissDialog() {
        try {
            if (progressBar.dialog.isShowing) {
                progressBar.dialog.dismiss()
            }
        } catch (ex: Exception) {
            Log.d("WebView Error", ex.message.toString())
        }
    }
}
