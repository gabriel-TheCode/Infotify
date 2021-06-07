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

    private var category: String = "General"
    private var lang: String = ""

    // Views
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: NewsRecyclerViewAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var btnRetry: AppCompatButton
    lateinit var layoutBadState: View
    lateinit var textState: TextView
    lateinit var imgState: ImageView
    private lateinit var themedButtonGroup: ThemedToggleButtonGroup
    private lateinit var btnGeneral: ThemedButton
    private lateinit var btnScience: ThemedButton
    private lateinit var btnSport: ThemedButton
    private lateinit var btnTV: ThemedButton
    private lateinit var btnTech: ThemedButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHeadlineBinding.inflate(inflater, container, false)

        val view = binding.root


        subscribeObservers()


        initViews()
        initRecyclerView()

        fetchApiNews()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchApiNews() {
        viewModel.getHeadlines(
            viewModel.getLanguage(),
            category
        )
        recyclerView.scheduleLayoutAnimation()
    }

    private fun showInternetConnectionErrorLayout() {
        if (recyclerAdapter.itemCount > 0) {
            showErrorDialog(
                getString(R.string.network_error),
                getString(R.string.check_internet)
            )
        } else {
            layoutBadState.isVisible = true
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
            layoutBadState.isVisible = true
            textState.text = getString(R.string.no_result_found)
            btnRetry.isVisible = true
        }
    }

    private fun hideBadStateLayout() {
        layoutBadState.isVisible = false
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
                        Toast.makeText(
                            context,
                            getString(R.string.internet_connection_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
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
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)
    }

    private fun initViews() {
        refreshLayout = binding.refreshLayout
        recyclerView = binding.recyclerViewNews
        btnRetry = binding.included.btnRetry
        layoutBadState = binding.included.layoutBadState
        imgState = binding.included.imgState
        textState = binding.included.textState
        themedButtonGroup = binding.themedButtonGroup
        btnGeneral = binding.btnGeneral
        btnScience = binding.btnScience
        btnSport = binding.btnSports
        btnTV = binding.btnEntertainment
        btnTech = binding.btnTechnology


        btnRetry.setOnClickListener { fetchApiNews() }

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
                    category = "General"
                    showToast(category)
                    fetchApiNews()
                }

                btnScience -> {
                    category = "Science"
                    showToast(category)
                    fetchApiNews()
                }

                btnTV -> {
                    category = "Entertainment"
                    showToast(category)
                    fetchApiNews()
                }

                btnTech -> {
                    category = "Technology"
                    showToast(category)
                    fetchApiNews()
                }

                btnSport -> {
                    category = "Sports"
                    showToast(category)
                    fetchApiNews()
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
            val articleArrayList: ArrayList<Article> = ArrayList()
            for (i in articles.indices) {
                val article = articles[i]
                articleArrayList.add(article)
                recyclerAdapter.setArticleListItems(articleArrayList)
                recyclerView.scheduleLayoutAnimation()
            }
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
