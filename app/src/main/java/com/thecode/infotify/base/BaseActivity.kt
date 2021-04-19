package com.thecode.infotify.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showServiceErrorDialog(){

    }
}