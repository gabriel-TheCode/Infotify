package com.thecode.infotify.presentation.main.search

import android.app.Activity
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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.infotify.R
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.presentation.main.NewsRecyclerViewAdapter
import com.thecode.infotify.databinding.BottomSheetSearchBinding
import com.thecode.infotify.databinding.FragmentSearchBinding
import com.thecode.infotify.databinding.LayoutBadStateBinding
import com.thecode.infotify.http.service.ApiInterface
import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import com.thecode.infotify.utils.AppConstants
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private var _bindingLayoutBadState: LayoutBadStateBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!
    private val bindingLayoutBadState get() = _bindingLayoutBadState!!
    private lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: NewsRecyclerViewAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    lateinit var btnRetry: AppCompatButton
    lateinit var layoutBadState: View
    lateinit var textState: TextView
    lateinit var imgState: ImageView
    private lateinit var imgSearchOptions: ImageView
    lateinit var q: String
    lateinit var s: String
    lateinit var l: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        _bindingLayoutBadState = LayoutBadStateBinding.inflate(inflater, container, false)

        val view = binding.root

        q = "news"
        s = "PublishedAt"
        l = AppConstants.DEFAULT_LANG
        searchView = binding.searchview
        btnRetry = bindingLayoutBadState.btnRetry
        layoutBadState = bindingLayoutBadState.layoutBadState
        imgState = bindingLayoutBadState.imgState
        textState = bindingLayoutBadState.textState
        imgSearchOptions = binding.imageSettings
        refreshLayout = binding.refreshLayout
        recyclerView = binding.recyclerViewNewsEverything
        recyclerAdapter = NewsRecyclerViewAdapter(view.context)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        // recyclerView.adapter = recyclerAdapter
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)

        refreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorPrimaryDark
        )
        val typedValue = TypedValue()
        val theme: Resources.Theme = view.context.theme
        theme.resolveAttribute(R.attr.primaryCardBackgroundColor, typedValue, true)
        @ColorInt val color = typedValue.data
        refreshLayout.setProgressBackgroundColorSchemeColor(color)
        refreshLayout.setOnRefreshListener {
            fetchApiNews(q, l, s)
        }

        // perform set on query text listener event
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                q = query
                fetchApiNews(q, l, s)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // do something when text changes
                return false
            }
        })

        imgSearchOptions.setOnClickListener {
            showBottomSheetSearch()
        }

        btnRetry.setOnClickListener {
            fetchApiNews(q, l, s)
        }

        fetchApiNews(q, l, s)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchApiNews(query: String, language: String, sortBy: String) {
        Log.d("Search", "$query - $language - $sortBy")
        refreshLayout.isRefreshing = true
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.NEWSAPI_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api: ApiInterface =
            retrofit.create(ApiInterface::class.java)
        val call: Call<NewsObjectResponse> =
            api.getEverything(query, language, sortBy, AppConstants.NEWSAPI_TOKEN)
        call.enqueue(object : Callback<NewsObjectResponse?> {
            override fun onResponse(
                call: Call<NewsObjectResponse?>?,
                response: Response<NewsObjectResponse?>
            ) {
                refreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if (response.body()?.status == "error") {
                            AestheticDialog.showToaster(
                                context as Activity?,
                                getString(R.string.error),
                                getString(R.string.service_unavailable),
                                AestheticDialog.ERROR
                            )
                            showInternetConnectionErrorLayout()
                        } else {
                            hideBadStateLayout()
                            Log.i("onSuccess", response.body().toString())
                            val a = response.body()?.articles
                            if (a != null) displayNews(a) else showNoResultErrorLayout()
                        }
                    } else {
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                        showNoResultErrorLayout()
                    }
                }
            }

            override fun onFailure(call: Call<NewsObjectResponse?>?, t: Throwable?) {
                refreshLayout.isRefreshing = false
                showInternetConnectionErrorLayout()
                Toast.makeText(
                    context,
                    getString(R.string.internet_connection_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun displayNews(articles: Array<Article>) {
        try {

            val articleArrayList: ArrayList<Article> = ArrayList()
            for (i in articles.indices) {
                val article = articles[i]
                articleArrayList.add(article)
                recyclerAdapter.setArticleListItems(articleArrayList)
            }

            recyclerView.scheduleLayoutAnimation()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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
            val spinnerPosition = adapter.getPosition(l.toUpperCase())
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
                l = languages[position]
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
            val spinnerPosition = adapter.getPosition(s)
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
                s = sorts[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val dialog = BottomSheetDialog(view.context)
        dialog.setContentView(view)
        val displayMetrics = this.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val maxHeight = (height * 0.88).toInt()
        val mBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        mBehavior.peekHeight = maxHeight
        dialog.show()
        textClose.setOnClickListener { dialog.dismiss() }
        btnValidateSignature.setOnClickListener {
            dialog.dismiss()
            fetchApiNews(q, l, s)
        }
    }

    fun showInternetConnectionErrorLayout() {
        if (recyclerAdapter.itemCount > 0) {
            AestheticDialog.showRainbow(
                activity,
                getString(R.string.error),
                getString(R.string.check_internet),
                AestheticDialog.ERROR
            )
        } else {
            layoutBadState.visibility = View.VISIBLE
            textState.text = getString(R.string.internet_connection_error)
            btnRetry.visibility = View.VISIBLE
        }
    }

    fun showNoResultErrorLayout() {
        if (recyclerAdapter.itemCount > 0) {
            AestheticDialog.showToaster(
                activity,
                getString(R.string.error),
                getString(R.string.service_unavailable),
                AestheticDialog.ERROR
            )
        } else {
            layoutBadState.visibility = View.VISIBLE
            textState.text = getString(R.string.no_result_found)
            btnRetry.visibility = View.GONE
        }
    }

    fun hideBadStateLayout() {
        if (layoutBadState.visibility == View.VISIBLE)
            layoutBadState.visibility = View.GONE
    }
}
