package com.thecode.infotify.fragments


import com.thecode.infotify.adapters.NewsRecyclerViewAdapter
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.infotify.R
import com.thecode.infotify.entities.Article
import com.thecode.infotify.interfaces.ApiInterface
import com.thecode.infotify.responses.NewsObjectResponse
import com.thecode.infotify.utils.AppConstants
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import kotlinx.android.synthetic.main.fragment_search.view.*
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

        lateinit var recyclerView: RecyclerView
        lateinit var recyclerAdapter: NewsRecyclerViewAdapter
        lateinit var refreshLayout: SwipeRefreshLayout
        lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        searchView = view.searchview
        refreshLayout = view.refresh_layout
        recyclerView = view.recycler_view_news_everything
        recyclerAdapter = NewsRecyclerViewAdapter(context!!)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        //recyclerView.adapter = recyclerAdapter
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)

        refreshLayout.setColorSchemeResources(android.R.color.holo_orange_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_orange_light)
        refreshLayout.setOnRefreshListener{
            fetchApiNews("covid-19")
        }

        // perform set on query text listener event

        // perform set on query text listener event
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                fetchApiNews(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
// do something when text changes
                return false
            }
        })
        fetchApiNews("covid-19")
        recyclerView.scheduleLayoutAnimation()

        return view
    }



    private fun fetchApiNews(query:String) {
        refreshLayout.isRefreshing = true
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.NEWSAPI_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api: ApiInterface =
            retrofit.create(ApiInterface::class.java)
        val call: Call<NewsObjectResponse> = api.getEverythingByQuery(query, AppConstants.NEWSAPI_TOKEN)
        call.enqueue(object : Callback<NewsObjectResponse?> {
            override fun onResponse(
                call: Call<NewsObjectResponse?>?,
                response: Response<NewsObjectResponse?>
            ) {
                refreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if(response.body()!!.status.equals("error")){
                            AestheticDialog.showToaster(context as Activity?, "Error", "The remote service is unavalaible", AestheticDialog.ERROR)
                        }else{
                            Log.i("onSuccess", response.body().toString())
                            displayNews(response.body()!!.articles)
                        }

                    } else {
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                        Toast.makeText(context,"Nothing returned", Toast.LENGTH_LONG).show()
                        /*AestheticDialog.showToaster(
                            context as Activity,
                            "Error",
                            "Nothing returned",
                            AestheticDialog.ERROR)*/
                    }
                }
            }

            override fun onFailure(call: Call<NewsObjectResponse?>?, t: Throwable?) {
                refreshLayout.isRefreshing = false
                Toast.makeText(context,"Connection error", Toast.LENGTH_SHORT).show()
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

            /*AestheticDialog.showToaster(
                context as Activity,
                "Success",
                "News returned successfully",
                AestheticDialog.SUCCESS)*/

            Toast.makeText(context,"News returned successfully", Toast.LENGTH_LONG).show()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


}
