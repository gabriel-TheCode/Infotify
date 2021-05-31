package com.thecode.infotify.presentation.main.headline

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseFragment
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.databinding.FragmentHeadlineBinding
import com.thecode.infotify.databinding.LayoutBadStateBinding
import com.thecode.infotify.presentation.main.NewsOnClickListener
import com.thecode.infotify.presentation.main.NewsRecyclerViewAdapter
import com.thecode.infotify.utils.AppConstants
import com.thecode.infotify.utils.AppConstants.ARG_CATEGORY
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import org.json.JSONException


@AndroidEntryPoint
class HeadlineFragment : BaseFragment(), NewsOnClickListener {

    // ViewModel
    private val viewModel: HeadlineViewModel by viewModels()

    // Listener
    private var newsOnClickListener: NewsOnClickListener = this

    // View Binding
    private var _bindingHeadline: FragmentHeadlineBinding? = null
    private var _bindingLayoutBadState: LayoutBadStateBinding? = null
    private val binding get() = _bindingHeadline!!
    private val bindingLayoutBadState get() = _bindingLayoutBadState!!

    private var category: String = "general"

    // Views
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: NewsRecyclerViewAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var btnRetry: AppCompatButton
    lateinit var layoutBadState: View
    lateinit var supportLayout: LinearLayout
    lateinit var textState: TextView
    lateinit var imgState: ImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bindingHeadline = FragmentHeadlineBinding.inflate(inflater, container, false)
        _bindingLayoutBadState = LayoutBadStateBinding.inflate(inflater, container, false)

        val view = binding.root

        subscribeObserver()
        initViews()

        initRecyclerView()

        btnRetry.setOnClickListener {
            fetchApiNews()
        }

        fetchApiNews()
        recyclerView.scheduleLayoutAnimation()


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindingHeadline = null
        _bindingLayoutBadState = null
    }

    private fun fetchApiNews() {
            viewModel.getHeadlines(
                AppConstants.DEFAULT_LANG,
                category
            )
    }

    private fun showInternetConnectionErrorLayout() {
        if (recyclerAdapter.itemCount > 0) {
            showErrorDialog(
                getString(R.string.error),
                getString(R.string.check_internet)
            )
        } else {
            supportLayout.isVisible = true
            textState.text = getString(R.string.internet_connection_error)
            btnRetry.isVisible = true
        }
    }

    private fun showBadStateLayout() {
        if (recyclerAdapter.itemCount > 0) {
            showErrorDialog(
                getString(R.string.error),
                getString(R.string.service_unavailable)
            )
        } else {
            supportLayout.isVisible = true
            textState.text = getString(R.string.no_result_found)
            btnRetry.isVisible = true
        }
    }

    private fun hideBadStateLayout() {
        supportLayout.isVisible = false
    }

    private fun subscribeObserver() {
        viewModel.headlineState.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Success -> {
                    hideBadStateLayout()
                    hideLoadingProgress()
                    populateRecyclerView(it.data.articles)
                }
                is DataState.Loading -> {
                    showLoadingProgress()
                }
                is DataState.Error -> {
                    Toast.makeText(requireActivity(), it.exception.message, Toast.LENGTH_LONG)
                        .show()
                    hideLoadingProgress()
                    showInternetConnectionErrorLayout()
                    Toast.makeText(
                        context,
                        getString(R.string.internet_connection_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun hideLoadingProgress() {
        refreshLayout.isRefreshing = false
    }

    private fun showLoadingProgress() {
        refreshLayout.isRefreshing = true
    }

    private fun initRecyclerView() {
        recyclerAdapter = NewsRecyclerViewAdapter(newsOnClickListener)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        // recyclerView.adapter = recyclerAdapter
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)
    }

    private fun initViews() {
        refreshLayout = binding.refreshLayout
        recyclerView = binding.recyclerViewNews
        btnRetry = bindingLayoutBadState.btnRetry
        layoutBadState = bindingLayoutBadState.layoutBadState
        imgState = bindingLayoutBadState.imgState
        textState = bindingLayoutBadState.textState
        supportLayout = binding.supportLayout

        refreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorPrimaryDark
        )
        val typedValue = TypedValue()
        val theme: Resources.Theme = requireContext().theme
        theme.resolveAttribute(R.attr.primaryCardBackgroundColor, typedValue, true)
        @ColorInt val color = typedValue.data
        refreshLayout.setProgressBackgroundColorSchemeColor(color)
        refreshLayout.setOnRefreshListener {
            fetchApiNews()
        }
    }

    private fun populateRecyclerView(articles: List<Article>) {
        try {
            if (articles.isEmpty()){
                showBadStateLayout()
            }else{
                val articleArrayList: ArrayList<Article> = ArrayList()
                for (i in articles.indices) {
                    val article = articles[i]
                    articleArrayList.add(article)
                    recyclerAdapter.setArticleListItems(articleArrayList)
                    recyclerView.scheduleLayoutAnimation()
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun saveBookmark(article: Article) {
        viewModel.saveBookmark(article)
        AestheticDialog.showRainbow(
            requireActivity(),
            "Success",
            "Bookmark saved",
            AestheticDialog.SUCCESS
        )
    }


    override fun deleteBookmark(article: Article) {
        TODO("Not yet implemented")
    }

    override fun openNews(article: Article) {
        loadWebviewDialog(article)

    }

    override fun shareNews(article: Article) {
        openSharingIntent(article)
    }
}
