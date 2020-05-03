package com.thecode.infotify.fragments


import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.infotify.R
import com.thecode.infotify.adapters.NewsRecyclerViewAdapter
import com.thecode.infotify.entities.Article
import com.thecode.infotify.interfaces.ApiInterface
import com.thecode.infotify.responses.NewsObjectResponse
import com.thecode.infotify.utils.AppConstants
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import kotlinx.android.synthetic.main.bottom_sheet_search.view.*
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
        private lateinit var imgSearchOptions: ImageView
        lateinit var q: String
        lateinit var s: String
        lateinit var l: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        q = "news"
        s = "PublishedAt"
        l = "en"
        searchView = view.searchview
        imgSearchOptions = view.image_settings
        refreshLayout = view.refresh_layout
        recyclerView = view.recycler_view_news_everything
        recyclerAdapter = NewsRecyclerViewAdapter(context!!)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        //recyclerView.adapter = recyclerAdapter
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)

        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorPrimaryDark)
        refreshLayout.setOnRefreshListener{
            fetchApiNews(q,l,s)
        }

        // perform set on query text listener event

        // perform set on query text listener event
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                q = query
                fetchApiNews(q,l,s)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
// do something when text changes
                return false
            }
        })

        imgSearchOptions.setOnClickListener{
            showBottomSheetSearch()
        }
        fetchApiNews(q,l,s)


        return view
    }


    private fun fetchApiNews(query:String, language:String, sortBy:String) {
        Log.d("Search", "$query - $language - $sortBy")
        refreshLayout.isRefreshing = true
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.NEWSAPI_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api: ApiInterface =
            retrofit.create(ApiInterface::class.java)
        val call: Call<NewsObjectResponse> = api.getEverything(query, language, sortBy, AppConstants.NEWSAPI_TOKEN)
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
                        Toast.makeText(context,"Nothing found", Toast.LENGTH_LONG).show()

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

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showBottomSheetSearch() {
        val view: View =
            layoutInflater.inflate(R.layout.bottom_sheet_search, null)
        val textClose = view.text_close
        val btnValidateSignature = view.btn_apply
        val spinnerLang: Spinner = view.spinner_lang
        ArrayAdapter.createFromResource(
            context!!,
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
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                l = languages[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val spinnerSort: Spinner = view.spinner_sortby
        ArrayAdapter.createFromResource(
            context!!,
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
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
               s = sorts[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val dialog = BottomSheetDialog(context!!)
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
            fetchApiNews(q,l,s)
        }
    }

}
