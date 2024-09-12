package com.thecode.infotify.presentation.main.headline

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseFragment
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.databinding.FragmentHeadlineBinding
import com.thecode.infotify.presentation.main.NewsRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter


@AndroidEntryPoint
class HeadlineFragment : BaseFragment() {

    // ViewModel
    private val viewModel: HeadlineViewModel by viewModels()

    // View Binding
    private var _binding: FragmentHeadlineBinding? = null
    private val binding get() = _binding!!

    private var category: String = "General"

    // Views
    lateinit var recyclerAdapter: NewsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHeadlineBinding.inflate(inflater, container, false)

        subscribeObservers()
        initViews()
        initRecyclerView()
        fetchNews()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchNews() {
        viewModel.getHeadlines(
            category.lowercase()
        )
        binding.recyclerViewNews.scheduleLayoutAnimation()
    }

    private fun showInternetConnectionErrorLayout() {
        if (recyclerAdapter.itemCount > 0) {
            showErrorDialog(
                getString(R.string.network_error),
                getString(R.string.check_internet)
            )
        } else {
            binding.included.apply {
                layoutBadState.isVisible = true
                textState.text = getString(R.string.internet_connection_error)
                btnRetry.isVisible = true
            }
        }
    }

    private fun showBadStateLayout() {
        if (recyclerAdapter.itemCount > 0) {
            showErrorDialog(
                getString(R.string.error),
                getString(R.string.service_unavailable)
            )
        } else {
            binding.included.apply {
                layoutBadState.isVisible = true
                textState.text = getString(R.string.no_result_found)
                btnRetry.isVisible = true
            }
        }
    }

    private fun hideBadStateLayout() {
        binding.included.layoutBadState.isVisible = false
    }

    private fun subscribeObservers() {
        viewModel.headlineState.observe(
            viewLifecycleOwner,
            {
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
                        hideLoadingProgress()
                        showInternetConnectionErrorLayout()
                    }
                }
            }
        )
    }

    private fun hideLoadingProgress() {
        binding.refreshLayout.isRefreshing = false
    }

    private fun showLoadingProgress() {
        binding.refreshLayout.isRefreshing = true
    }

    private fun initRecyclerView() {
        recyclerAdapter = NewsRecyclerViewAdapter(
            onSaveBookmark = {
                saveBookmark(it)
            },
            onOpenNews = {
                openNews(it)
            },
            onShareNews = {
                shareNews(it)
            },
            onOpenNewsInBrowser = {
                openNewsInBrowser(it)
            }
        )
        binding.recyclerViewNews.layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewNews.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)
    }

    private fun initViews() {
        binding.apply {
            included.btnRetry.setOnClickListener { fetchNews() }
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
                fetchNews()
            }

            themedButtonGroup.selectButton(btnGeneral)

            themedButtonGroup.setOnSelectListener {
                when (it) {
                    btnGeneral -> {
                        category = getString(R.string.general_category)
                    }

                    btnScience -> {
                        category = getString(R.string.science_category)
                    }

                    btnEntertainment -> {
                        category = getString(R.string.entertainment_category)
                    }

                    btnTechnology -> {
                        category = getString(R.string.technology_category)
                    }

                    btnSports -> {
                        category = getString(R.string.sports_category)
                    }
                }
                fetchNews()
            }
        }
    }

    private fun populateRecyclerView(articles: List<Article>) {
        if (articles.isEmpty()) {
            showBadStateLayout()
        } else {
            recyclerAdapter.setArticleListItems(articles)
            binding.recyclerViewNews.scheduleLayoutAnimation()
        }
    }

    fun saveBookmark(article: Article) {
        viewModel.saveBookmark(article)
        showSuccessDialog(
            getString(R.string.success),
            getString(R.string.bookmark_saved)
        )
    }

    fun openNews(article: Article) {
        loadWebviewDialog(article)
    }

    fun openNewsInBrowser(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun shareNews(article: Article) {
        openSharingIntent(article)
    }
}

