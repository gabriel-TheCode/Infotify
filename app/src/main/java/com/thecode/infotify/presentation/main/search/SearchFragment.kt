package com.thecode.infotify.presentation.main.search

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseFragment
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.databinding.BottomSheetSearchBinding
import com.thecode.infotify.databinding.FragmentSearchBinding
import com.thecode.infotify.presentation.main.NewsOnClickListener
import com.thecode.infotify.presentation.main.NewsRecyclerViewAdapter
import com.thecode.infotify.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */

@AndroidEntryPoint
class SearchFragment : BaseFragment(), NewsOnClickListener {
    private val viewModel: SearchViewModel by viewModels()

    // Listener
    private var newsOnClickListener: NewsOnClickListener = this

    private var _binding: FragmentSearchBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: NewsRecyclerViewAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    lateinit var btnRetry: AppCompatButton
    lateinit var layoutBadState: View
    lateinit var textState: TextView
    lateinit var imgState: ImageView
    private lateinit var imgSearchOptions: ImageView
    lateinit var query: String
    lateinit var sortBy: String
    lateinit var language: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val view = binding.root

        subscribeObserver()
        initViews()
        initRecyclerView()

        fetchApiNews(query, language, sortBy)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchApiNews(query: String, language: String, sortBy: String) {
        Log.d("Search", "$query - $language - $sortBy")
        viewModel.getSearchNews(query, language, sortBy)
    }

    private fun showBottomSheetSearch() {
        val binding: BottomSheetSearchBinding = BottomSheetSearchBinding.inflate(layoutInflater)
        val view = binding.root
        val textClose = binding.textClose
        val btnValidateSignature = binding.btnApply
        val spinnerLang: Spinner = binding.spinnerLang
        ArrayAdapter.createFromResource(
            view.context,
            R.array.languages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLang.adapter = adapter
            val spinnerPosition = adapter.getPosition(language.toUpperCase(Locale.ROOT))
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
        textClose.setOnClickListener { dialog.dismiss() }
        btnValidateSignature.setOnClickListener {
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
            layoutBadState.isVisible = true
            textState.text = getString(R.string.internet_connection_error)
            btnRetry.isVisible = true
        }
    }

    private fun showNoResultErrorLayout() {
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
        val articleArrayList: ArrayList<Article> = ArrayList()
        for (i in articles.indices) {
            val article = articles[i]
            articleArrayList.add(article)
            recyclerAdapter.setArticleListItems(articleArrayList)
            recyclerView.scheduleLayoutAnimation()
        }
    }

    private fun hideLoadingProgress() {
        refreshLayout.isRefreshing = false
    }

    private fun showLoadingProgress() {
        refreshLayout.isRefreshing = true
    }

    private fun initViews() {
        query = "news"
        sortBy = "PublishedAt"
        language = AppConstants.DEFAULT_LANG
        searchView = binding.searchview
        btnRetry = binding.included.btnRetry
        layoutBadState = binding.included.layoutBadState
        imgState = binding.included.imgState
        textState = binding.included.textState
        imgSearchOptions = binding.imageSettings
        refreshLayout = binding.refreshLayout

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

        imgSearchOptions.setOnClickListener {
            showBottomSheetSearch()
        }

        btnRetry.setOnClickListener {
            fetchApiNews(query, language, sortBy)
        }

        // perform set on query text listener event
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String): Boolean {
                query = q
                fetchApiNews(query, language, sortBy)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // do something when text changes
                return false
            }
        })
    }

    private fun initRecyclerView() {

        recyclerView = binding.recyclerViewNewsEverything
        recyclerAdapter = NewsRecyclerViewAdapter(newsOnClickListener)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)
    }

    override fun saveBookmark(article: Article) {
        viewModel.saveBookmark(article)
        showSuccessDialog("Success", "Bookmark saved")
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
