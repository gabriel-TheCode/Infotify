package com.thecode.infotify.presentation.main.bookmark

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
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
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.databinding.FragmentBookmarksBinding
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import org.json.JSONException


@AndroidEntryPoint
class BookmarksFragment : BaseFragment(), BookmarkOnClickListener {

    private val viewModel: BookmarkViewModel by viewModels()

    // Listener
    private var newsOnClickListener: BookmarkOnClickListener = this

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: BookmarkRecyclerViewAdapter
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var layoutEmptyState: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        val view = binding.root

        subscribeObserver()
        initViews()
        initRecyclerView()
        viewModel.getBookmarks()

        return view
    }


    private fun initRecyclerView() {
        recyclerView = binding.recyclerViewNewsBookmark
        recyclerAdapter = BookmarkRecyclerViewAdapter(newsOnClickListener)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)
    }

    private fun initViews() {
        refreshLayout = binding.refreshLayout
        layoutEmptyState = binding.layoutBookmarkEmpty
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

    private fun hideEmptyStateLayout() {
            layoutEmptyState.isVisible = false
    }

    private fun showEmptyStateLayout(){
        layoutEmptyState.isVisible = true
    }

    private fun hideLoadingProgress() {
        refreshLayout.isRefreshing = false
    }

    private fun showLoadingProgress() {
        refreshLayout.isRefreshing = true
    }

    private fun subscribeObserver() {
        viewModel.articles.observe(viewLifecycleOwner, {
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
        })
    }

    private fun populateRecyclerView(articles: List<Article>) {
        try {
            val articleArrayList: ArrayList<Article> = ArrayList()
            for (i in articles.indices) {
                val article = articles[i]
                articleArrayList.add(article)
                recyclerAdapter.setArticleListItems(articleArrayList)
            }
            hideEmptyStateLayout()
            hideLoadingProgress()
            recyclerView.scheduleLayoutAnimation()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun deleteBookmark(article: Article) {
        viewModel.deleteBookmark(article.url)
        AestheticDialog.showRainbow(
            requireActivity(),
            "Success",
            "Bookmark deleted successfully",
            AestheticDialog.SUCCESS
        )
    }

    override fun openNews(article: Article) {
        loadWebviewDialog(article)
    }

    override fun shareNews(article: Article) {
        openSharingIntent(article)
    }
}
