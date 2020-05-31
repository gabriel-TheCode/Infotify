package com.thecode.infotify.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.thecode.infotify.R
import com.thecode.infotify.utils.SharedPreferenceUtils
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class AboutActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SharedPreferenceUtils.isNightModeEnabled()) {
            setTheme(R.style.AppTheme_Base_Night)
        } else {
            setTheme(R.style.AppTheme_Base_Light)
        }

        setContentView(R.layout.activity_about)
    }


}
