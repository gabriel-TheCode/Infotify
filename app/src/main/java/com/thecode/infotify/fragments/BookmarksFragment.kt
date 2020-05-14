package com.thecode.infotify.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
    lateinit var layoutEmptyState: LinearLayout
    private lateinit var listArticles: ArrayList<Article>
    val realm: Realm = Realm.getDefaultInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bookmarks, container, false)
        refreshLayout = view.refresh_layout
        recyclerView = view.recycler_view_news_bookmark
        layoutEmptyState = view.layout_bookmark_empty
        recyclerAdapter = BookmarkRecyclerViewAdapter(context!!)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        //recyclerView.adapter = recyclerAdapter
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)

        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorPrimaryDark)
        refreshLayout.setOnRefreshListener{
            displayBookmarks(listArticles)
        }

        realm.refresh()


        listArticles = ArrayList()
        val query: RealmQuery<Article> = realm.where(Article::class.java)
        val results: RealmResults<Article> = query.findAll()
        var i: Int
        if (results.isNotEmpty()){
            layoutEmptyState.visibility = View.GONE
            i = 0
            while (i < results.size) {
                assert(results[i] != null)
                listArticles.add(i, results[i]!!)
                i++ }
        }else{
            layoutEmptyState.visibility = View.VISIBLE
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
            refreshLayout.isRefreshing = false
            recyclerView.scheduleLayoutAnimation()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }



}
