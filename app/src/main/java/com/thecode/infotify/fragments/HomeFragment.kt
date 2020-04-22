package com.thecode.infotify.fragments


import NewsRecyclerViewAdapter
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.infotify.R
import com.thecode.infotify.entities.Article
import com.thecode.infotify.interfaces.ApiInterface
import com.thecode.infotify.responses.NewsObjectResponse
import com.thecode.infotify.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: NewsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        fetchApiNews()

        recyclerView = view.recycler_view_news
        recyclerAdapter = NewsRecyclerViewAdapter(context!!)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = recyclerAdapter



        return view

    }


    private fun fetchApiNews() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.NEWSAPI_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api: ApiInterface =
            retrofit.create(ApiInterface::class.java)
        val call: Call<NewsObjectResponse> = api.getTopHeadlinesByCountry("fr", AppConstants.NEWSAPI_TOKEN)
        call.enqueue(object : Callback<NewsObjectResponse?> {
            override fun onResponse(
                call: Call<NewsObjectResponse?>?,
                response: Response<NewsObjectResponse?>
            ) {
                Log.i("Responsestring", response.body()!!.status + " " + response.body()!!.totalResults)
                //Toast.makeText()
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString())
                        displayNews(response.body()!!.articles)
                    } else {
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                        //Toast.makeText(context,"Nothing returned",Toast.LENGTH_LONG).show()
                        AestheticDialog.showToaster(
                            context as Activity,
                            "Error",
                            "Nothing returned",
                            AestheticDialog.ERROR)
                    }
                }
            }

            override fun onFailure(call: Call<NewsObjectResponse?>?, t: Throwable?) {}
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
                AestheticDialog.showToaster(
                    context as Activity,
                    "Success",
                    "News returned successfully",
                    AestheticDialog.SUCCESS)

            //Toast.makeText(context,"News returned successfully",Toast.LENGTH_LONG).show()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}
