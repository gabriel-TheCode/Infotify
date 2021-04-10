package com.thecode.infotify.presentation.newsdetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thecode.infotify.R
import com.thecode.infotify.databinding.ActivityNewsDetailsBinding


class NewsDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //RECEIVE OUR DATA
        val i = intent
        val title = i.extras?.getString("title")
        val description = i.extras?.getString("description")
        val source = i.extras?.getString("source")
        val imageUrl = i.extras?.getString("imageUrl")
        val content = i.extras?.getString("content")
        val date = i.extras?.getString("date")
        val url = i.extras?.getString("url")
        val formattedDate = date?.split("T")?.get(0)

        //REFERENCE VIEWS FROM XML
        val img = binding.imageNews
        val txtSource = binding.textSource
        val txtContent = binding.textContent
        val txtDate = binding.textDate

        //ASSIGN DATA TO THOSE VIEWS
        Glide.with(this).load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .apply(RequestOptions().centerCrop())
            .into(img)
        txtSource.text = source
        txtContent.text = "$description\n\n $content"
        txtDate.text = formattedDate
        setTitle(title)

    }

}
