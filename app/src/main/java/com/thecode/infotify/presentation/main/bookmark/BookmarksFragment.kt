package com.thecode.infotify.presentation.main.bookmark

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
import androidx.recyclerview.widget.RecyclerView
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseFragment
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.databinding.FragmentBookmarksBinding
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter

@AndroidEntryPoint
class BookmarksFragment : BaseFragment() {

    private val viewModel: BookmarkViewModel by viewModels()

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: BookmarkRecyclerViewAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        subscribeObserver()
        initViews()
        initRecyclerView()
        viewModel.getBookmarks()

        return binding.root
    }

    private fun initRecyclerView() {
        recyclerView = binding.recyclerViewBookmark
        recyclerAdapter = BookmarkRecyclerViewAdapter(
            onDeleteBookmark = {
                viewModel.deleteBookmark(it.url)
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
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)
    }

    private fun initViews() {
        binding.apply {
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
                viewModel.getBookmarks()
            }
        }
    }

    private fun hideEmptyStateLayout() {
        binding.layoutBookmarkEmpty.isVisible = false
    }

    private fun showEmptyStateLayout() {
        binding.layoutBookmarkEmpty.isVisible = true
    }

    private fun hideLoadingProgress() {
        binding.refreshLayout.isRefreshing = false
    }

    private fun showLoadingProgress() {
        binding.refreshLayout.isRefreshing = true
    }

    private fun subscribeObserver() {
        viewModel.articles.observe(
            viewLifecycleOwner,
            {
                when (it) {
                    is DataState.Success -> {
                        populateRecyclerView(it.data)
                    }

                    is DataState.Loading -> {
                        showLoadingProgress()
                    }

                    is DataState.Error -> {
                        hideLoadingProgress()
                        showEmptyStateLayout()
                    }
                }
            }
        )
    }

    private fun populateRecyclerView(articles: List<Article>) {
        if (articles.isEmpty()) {
            showEmptyStateLayout()
            recyclerView.adapter = null
            recyclerAdapter.notifyDataSetChanged()
        } else {
            recyclerAdapter.setArticleListItems(ArrayList(articles))
            hideEmptyStateLayout()
            hideLoadingProgress()
            recyclerView.scheduleLayoutAnimation()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
