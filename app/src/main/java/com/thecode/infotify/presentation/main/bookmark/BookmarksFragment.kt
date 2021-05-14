package com.thecode.infotify.presentation.main.bookmark

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thecode.infotify.R
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.databinding.FragmentBookmarksBinding
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import org.json.JSONException


@AndroidEntryPoint
class BookmarksFragment : Fragment() {

    private val viewModel: BookmarkViewModel by viewModels()
    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: BookmarkRecyclerViewAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var layoutEmptyState: LinearLayout
    private lateinit var listArticles: ArrayList<ArticleEntity>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        val view = binding.root

        initViews()
        initRecyclerView()
        subscribeObserver()

        viewModel.getBookmarks()

        return view
    }



    private fun initRecyclerView() {
        recyclerView = binding.recyclerViewNewsBookmark
        recyclerAdapter = BookmarkRecyclerViewAdapter(requireContext(), viewModel)
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
            populateRecyclerView(listArticles)
        }
    }

    private fun hideEmptyStateLayout() {
        if (layoutEmptyState.visibility == View.VISIBLE)
            layoutEmptyState.visibility = View.GONE
    }

    private fun showEmptyStateLayout(){
        layoutEmptyState.visibility = View.VISIBLE
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

    private fun populateRecyclerView(articles: List<ArticleEntity>) {
        try {
            val articleArrayList: ArrayList<ArticleEntity> = ArrayList()
            for (i in articles.indices) {
                val article = articles[i]
                articleArrayList.add(article)
                recyclerAdapter.setArticleListItems(articleArrayList)
            }
            hideEmptyStateLayout()
            refreshLayout.isRefreshing = false
            recyclerView.scheduleLayoutAnimation()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
