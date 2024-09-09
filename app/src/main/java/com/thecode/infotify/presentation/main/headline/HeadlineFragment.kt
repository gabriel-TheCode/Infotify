package com.thecode.infotify.presentation.main.headline

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseFragment
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.databinding.FragmentHeadlineBinding
import com.thecode.infotify.presentation.main.NewsOnClickListener
import com.thecode.infotify.presentation.main.NewsRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup

@AndroidEntryPoint
class HeadlineFragment : BaseFragment(), NewsOnClickListener {

    // ViewModel
    private val viewModel: HeadlineViewModel by viewModels()

    // Listener
    private var newsOnClickListener: NewsOnClickListener = this

    // View Binding
    private var _binding: FragmentHeadlineBinding? = null
    private val binding get() = _binding!!

    private var category: String = getString(R.string.general_category)

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
        fetchApiNews()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchApiNews() {
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
        recyclerAdapter = NewsRecyclerViewAdapter(newsOnClickListener)
        binding.recyclerViewNews.layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewNews.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)
    }

    private fun initViews() {
        binding.apply {
            included.btnRetry.setOnClickListener { fetchApiNews() }
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

            themedButtonGroup.selectButton(btnGeneral)

            themedButtonGroup.setOnSelectListener {
                when (it) {
                    btnGeneral -> {
                        category = getString(R.string.general_category)
                        fetchApiNews()
                    }

                    btnScience -> {
                        category = getString(R.string.science_category)
                        fetchApiNews()
                    }

                    btnEntertainment -> {
                        category = getString(R.string.entertainment_category)
                        fetchApiNews()
                    }

                    btnTechnology -> {
                        category = getString(R.string.technology_category)
                        fetchApiNews()
                    }

                    btnSports -> {
                        category = getString(R.string.sports_category)
                        fetchApiNews()
                    }
                }
            }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun populateRecyclerView(articles: List<Article>) {
        if (articles.isEmpty()) {
            showBadStateLayout()
        } else {
            recyclerAdapter.setArticleListItems(articles)
            binding.recyclerViewNews.scheduleLayoutAnimation()
        }
    }

    override fun saveBookmark(article: Article) {
        viewModel.saveBookmark(article)
        showSuccessDialog(
            getString(R.string.success),
            getString(R.string.bookmark_saved)
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

