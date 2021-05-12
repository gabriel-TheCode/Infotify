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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseFragment
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.presentation.main.NewsRecyclerViewAdapter
import com.thecode.infotify.databinding.FragmentHeadlineBinding
import com.thecode.infotify.databinding.LayoutBadStateBinding
import com.thecode.infotify.utils.AppConstants
import com.thecode.infotify.utils.AppConstants.ARG_CATEGORY
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import org.json.JSONException


@AndroidEntryPoint
class HeadlineFragment : BaseFragment() {

    private val viewModel: HeadlineViewModel by viewModels()

    // View Binding
    private var _bindingHeadline: FragmentHeadlineBinding? = null
    private var _bindingLayoutBadState: LayoutBadStateBinding? = null
    private val binding get() = _bindingHeadline!!
    private val bindingLayoutBadState get() = _bindingLayoutBadState!!

    private lateinit var category: String

    // Views
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: NewsRecyclerViewAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var btnRetry: AppCompatButton
    lateinit var layoutBadState: View
    lateinit var textState: TextView
    lateinit var imgState: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            category = arguments?.getString(ARG_CATEGORY).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bindingHeadline = FragmentHeadlineBinding.inflate(inflater, container, false)
        _bindingLayoutBadState = LayoutBadStateBinding.inflate(inflater, container, false)

        val view = binding.root
        initViews()
        initRecyclerView()
        subscribeObserver()

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
        if (category == "popular") {
            viewModel.getHeadlines(AppConstants.DEFAULT_LANG, "")
        } else {
            viewModel.getHeadlines(
                AppConstants.DEFAULT_LANG,
                category
            )
        }
    }
        private fun showInternetConnectionErrorLayout() {
            if (recyclerAdapter.itemCount > 0) {
                showErrorDialog(getString(R.string.error),
                    getString(R.string.check_internet))
            } else {
                layoutBadState.visibility = View.VISIBLE
                textState.text = getString(R.string.internet_connection_error)
                btnRetry.visibility = View.VISIBLE
            }
        }

        private fun showNoResultErrorLayout() {
            if (recyclerAdapter.itemCount > 0) {
                showErrorDialog(getString(R.string.error),
                    getString(R.string.service_unavailable))
            } else {
                layoutBadState.visibility = View.VISIBLE
                textState.text = getString(R.string.no_result_found)
                btnRetry.visibility = View.GONE
            }
        }

        private fun hideBadStateLayout() {
            if (layoutBadState.visibility == View.VISIBLE)
                layoutBadState.visibility = View.GONE
        }

        companion object {
            @JvmStatic
            fun newInstance(category: String) =
                HeadlineFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_CATEGORY, category)
                    }
                }
        }

        private fun subscribeObserver() {
            viewModel.headlineState.observe(viewLifecycleOwner, {
                when (it) {
                    is DataState.Success -> {
                        hideBadStateLayout()
                        hideLoadingProgress()
                        if (it.data.articles.isEmpty()) {
                            showNoResultErrorLayout()
                        } else {
                            populateRecyclerView(it.data.articles)
                        }
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
            })
        }

        private fun hideLoadingProgress() {
            refreshLayout.isRefreshing = false
        }

        private fun showLoadingProgress() {
            refreshLayout.isRefreshing = true
        }

        private fun initRecyclerView() {
            recyclerAdapter = NewsRecyclerViewAdapter(requireContext(), viewModel)
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
                val articleArrayList: ArrayList<Article> = ArrayList()
                for (i in articles.indices) {
                    val article = articles[i]
                    articleArrayList.add(article)
                    recyclerAdapter.setArticleListItems(articleArrayList)
                    recyclerView.scheduleLayoutAnimation()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
}
