package com.thecode.infotify.fragments


import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.infotify.R
import com.thecode.infotify.adapters.NewsRecyclerViewAdapter
import com.thecode.infotify.entities.Article
import com.thecode.infotify.interfaces.ApiInterface
import com.thecode.infotify.responses.NewsObjectResponse
import com.thecode.infotify.utils.AppConstants
import com.thecode.infotify.utils.AppConstants.ARG_CATEGORY
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import kotlinx.android.synthetic.main.fragment_headline.view.*
import kotlinx.android.synthetic.main.layout_bad_state.view.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HeadlineFragment : Fragment() {

    private lateinit var category: String

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: NewsRecyclerViewAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var btnRetry: AppCompatButton
    lateinit var layoutBadState: View
    lateinit var textState: TextView
    lateinit var imgState: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments!=null) {
            category = arguments?.getString(ARG_CATEGORY).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_headline, container, false)
        refreshLayout = view.refresh_layout
        recyclerView = view.recycler_view_news
        btnRetry = view.btn_retry
        layoutBadState = view.layout_bad_state
        imgState = view.img_state
        textState = view.text_state
        recyclerAdapter = NewsRecyclerViewAdapter(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(activity)
        //recyclerView.adapter = recyclerAdapter
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)

        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorPrimaryDark)
        val typedValue = TypedValue()
        val theme: Resources.Theme = requireContext().theme
        theme.resolveAttribute(R.attr.primaryCardBackgroundColor, typedValue, true)
        @ColorInt val color = typedValue.data
        refreshLayout.setProgressBackgroundColorSchemeColor(color)
        refreshLayout.setOnRefreshListener{
            fetchApiNews()
        }

        btnRetry.setOnClickListener{
            fetchApiNews()
        }

        fetchApiNews()
        recyclerView.scheduleLayoutAnimation()

        return view

    }


    private fun fetchApiNews() {
        refreshLayout.isRefreshing = true
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.NEWSAPI_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api: ApiInterface =
            retrofit.create(ApiInterface::class.java)
        val call: Call<NewsObjectResponse>
        call = if(category == "popular"){
            api.getTopHeadlinesByLanguage(AppConstants.DEFAULT_LANG, AppConstants.NEWSAPI_TOKEN)
        }else{
            api.getTopHeadlinesByLanguageAndCategory(AppConstants.DEFAULT_LANG, category, AppConstants.NEWSAPI_TOKEN)
        }
       call.enqueue(object : Callback<NewsObjectResponse> {
            override fun onResponse(
                call: Call<NewsObjectResponse>,
                response: Response<NewsObjectResponse>
            ) {
                refreshLayout.isRefreshing = false
                Log.i("Responsestring", (response.body()?.status ?: "No result") + " " + (response.body()?.totalResults
                    ?: 0))
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        hideBadStateLayout()
                        Log.i("onSuccess", response.body().toString())
                        val a = response.body()?.articles
                        if (a != null) displayNews(a) else showNoResultErrorLayout()
                    } else {
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                        showNoResultErrorLayout()
                    }
                }
            }

            override fun onFailure(call: Call<NewsObjectResponse>, t: Throwable?) {
                refreshLayout.isRefreshing = false
                showInternetConnectionErrorLayout()
                Toast.makeText(context,getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show()
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
                recyclerView.scheduleLayoutAnimation()
            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun showInternetConnectionErrorLayout(){
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

    fun showNoResultErrorLayout(){
        if(recyclerAdapter.itemCount > 0){
            AestheticDialog.showRainbow(activity, getString(R.string.error), getString(R.string.service_unavailable), AestheticDialog.ERROR)
        }else {
            layoutBadState.visibility = View.VISIBLE
            textState.text = getString(R.string.no_result_found)
            btnRetry.visibility = View.GONE
        }
    }

    fun hideBadStateLayout(){
        if(layoutBadState.visibility == View.VISIBLE)
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


}
