package com.thecode.infotify.presentation.main.search

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseFragment
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.databinding.BottomSheetSearchBinding
import com.thecode.infotify.databinding.FragmentSearchBinding
import com.thecode.infotify.presentation.main.NewsRecyclerViewAdapter
import com.thecode.infotify.utils.AppConstants.DEFAULT_LANG
import com.thecode.infotify.utils.AppConstants.DEFAULT_QUERY
import com.thecode.infotify.utils.AppConstants.DEFAULT_SORT
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import java.util.Locale


@AndroidEntryPoint
class SearchFragment : BaseFragment() {
    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: NewsRecyclerViewAdapter

    lateinit var query: String
    lateinit var sortBy: String
    lateinit var language: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        subscribeObserver()
        initViews()
        initRecyclerView()
        fetchApiNews(query, language, sortBy)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchApiNews(query: String, language: String, sortBy: String) {
        viewModel.getSearchNews(query, language, sortBy)
    }

    private fun showBottomSheetSearch() {
        val binding: BottomSheetSearchBinding = BottomSheetSearchBinding.inflate(layoutInflater)
        val view = binding.root
        val spinnerLang: Spinner = binding.spinnerLang
        ArrayAdapter.createFromResource(
            view.context,
            R.array.languages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLang.adapter = adapter
            val spinnerPosition = adapter.getPosition(language.uppercase(Locale.ROOT))
            spinnerLang.setSelection(spinnerPosition)
        }

        val languages = resources.getStringArray(R.array.languages_values)
        spinnerLang.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                language = languages[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val spinnerSort: Spinner = binding.spinnerSortby
        ArrayAdapter.createFromResource(
            view.context,
            R.array.options_values,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSort.adapter = adapter
            val spinnerPosition = adapter.getPosition(sortBy)
            spinnerSort.setSelection(spinnerPosition)
        }

        val sorts = resources.getStringArray(R.array.options_values)
        spinnerSort.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                sortBy = sorts[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val dialog = BottomSheetDialog(view.context)
        dialog.setContentView(view)
        val displayMetrics = this.resources.displayMetrics
        val height = displayMetrics.heightPixels
        val maxHeight = (height * 0.88).toInt()
        val mBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        mBehavior.peekHeight = maxHeight
        dialog.show()
        binding.textClose.setOnClickListener { dialog.dismiss() }
        binding.btnApply.setOnClickListener {
            dialog.dismiss()
            fetchApiNews(query, language, sortBy)
        }
    }

    private fun showInternetConnectionErrorLayout() {
        if (recyclerAdapter.itemCount > 0) {
            showErrorDialog(
                getString(R.string.network_error),
                getString(R.string.check_internet)
            )
        } else {
            binding.apply {
                included.layoutBadState.isVisible = true
                included.textState.text = getString(R.string.internet_connection_error)
                included.btnRetry.isVisible = true
            }
        }
    }

    private fun hideBadStateLayout() {
        binding.included.layoutBadState.isVisible = false
    }

    private fun subscribeObserver() {
        viewModel.searchState.observe(
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

    private fun populateRecyclerView(articles: List<Article>) {
        recyclerAdapter.setArticleListItems(articles)
        recyclerView.scheduleLayoutAnimation()
    }

    private fun hideLoadingProgress() {
        binding.refreshLayout.isRefreshing = false
    }

    private fun showLoadingProgress() {
        binding.refreshLayout.isRefreshing = true
    }

    private fun initViews() {
        query = DEFAULT_QUERY
        sortBy = DEFAULT_SORT
        language = DEFAULT_LANG

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
                fetchApiNews(query, language, sortBy)
            }

            imageSettings.setOnClickListener {
                showBottomSheetSearch()
            }

            included.btnRetry.setOnClickListener {
                fetchApiNews(query, language, sortBy)
            }

            searchview.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(q: String): Boolean {
                    query = q
                    fetchApiNews(query, language, sortBy)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
        }

    }

    private fun initRecyclerView() {
        recyclerView = binding.recyclerViewNewsEverything
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
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)
    }

    fun saveBookmark(article: Article) {
        viewModel.saveBookmark(article)
        showSuccessDialog(getString(R.string.success), getString(R.string.bookmark_saved))
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
