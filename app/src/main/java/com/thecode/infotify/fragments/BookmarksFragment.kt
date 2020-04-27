package com.thecode.infotify.fragments


import com.thecode.infotify.adapters.NewsRecyclerViewAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thecode.infotify.R
import com.thecode.infotify.adapters.BookmarkRecyclerViewAdapter
import com.thecode.infotify.entities.Article
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import kotlinx.android.synthetic.main.fragment_bookmarks.view.*
import org.json.JSONException


/**
 * A simple [Fragment] subclass.
 */
class BookmarksFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: BookmarkRecyclerViewAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var listArticles: ArrayList<Article>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bookmarks, container, false)
        refreshLayout = view.refresh_layout
        recyclerView = view.recycler_view_news_bookmark
        recyclerAdapter = BookmarkRecyclerViewAdapter(context!!)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        //recyclerView.adapter = recyclerAdapter
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)

        refreshLayout.setColorSchemeResources(android.R.color.holo_orange_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_orange_light)
        refreshLayout.setOnRefreshListener{
            displayBookmarks(listArticles)
        }
        val realm: Realm = Realm.getDefaultInstance()

        listArticles = ArrayList()
        val query: RealmQuery<Article> = realm.where(Article::class.java)
        val results: RealmResults<Article> = query.findAll()
        var i: Int
        if (results != null) {
            i = 0
            while (i < results.size) {
                assert(results[i] != null)
                listArticles.add(i, results[i]!!)
                i++
            }
        }

        displayBookmarks(listArticles)

        return view
    }

    private fun displayBookmarks(articles: ArrayList<Article>) {
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
            refreshLayout.isRefreshing = false
            Toast.makeText(context,"News returned successfully", Toast.LENGTH_LONG).show()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }



}
