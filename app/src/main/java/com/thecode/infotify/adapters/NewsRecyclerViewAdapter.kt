import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sackcentury.shinebuttonlib.ShineButton
import com.thecode.infotify.R
import com.thecode.infotify.entities.Article
import kotlinx.android.synthetic.main.adapter_news.view.*


class NewsRecyclerViewAdapter(val context: Context) : RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder>() {

    var newsList : List<Article> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_news,parent,false)
        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.tvNewsTitle.text = newsList[position].title
        holder.tvNewsdescription.text = newsList[position].description
        if(newsList[position].author.equals(null)){
            holder.tvPublisherName.text = "Infotify News"
        }else{
            holder.tvPublisherName.text = newsList[position].author
        }

        Glide.with(context).load(newsList[position].urlToImage)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .apply(RequestOptions().centerCrop())
            .into(holder.image)
        holder.btnShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                newsList[position].title + " - via Infotify News App\n" + newsList[position].url
            )
            sendIntent.type = "text/plain"
            context.startActivity(sendIntent)
        }
    }

    fun setArticleListItems(newsList: List<Article>){
        this.newsList = newsList
        notifyDataSetChanged()
    }

    class NewsViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        val tvNewsTitle: TextView = itemView!!.text_title
        val tvNewsdescription: TextView = itemView!!.text_description
        val tvPublisherName: TextView = itemView!!.text_name_publisher
        val image: ImageView = itemView!!.image_news
        val btnShare: ImageView = itemView!!.btnShare
        val btnBookmark: ImageView = itemView!!.btnBookmark

    }
}