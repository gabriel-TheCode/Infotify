package com.thecode.infotify.activities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thecode.infotify.R
import com.thecode.infotify.databinding.ActivityAboutBinding
import com.thecode.infotify.utils.SharedPreferenceUtils
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    private lateinit var layoutGithub : RelativeLayout
    private lateinit var layoutTwitter : RelativeLayout
    private lateinit var layoutSourceCode : RelativeLayout
    private lateinit var layoutPlaystore : RelativeLayout
    private lateinit var txtVersion : TextView

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)

        if (SharedPreferenceUtils.isNightModeEnabled()) {
            setTheme(R.style.AppTheme_Base_Night)
        } else {
            setTheme(R.style.AppTheme_Base_Light)
        }

        val view = binding.root
        setContentView(view)

        txtVersion = binding.textVersion
        layoutGithub = binding.layoutGithub
        layoutTwitter = binding.layoutTwitter
        layoutSourceCode = binding.layoutSourceCode
        layoutPlaystore = binding.layoutPlaystore

        val versionName : String = com.thecode.infotify.BuildConfig.VERSION_NAME
        txtVersion.text = versionName
        layoutTwitter.setOnClickListener {
            var intent: Intent
            try {
                // get the Twitter app if possible
                application.packageManager.getPackageInfo("com.twitter.android", 0)
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=gabriel_thecode"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            } catch (e : Exception) {
                // no Twitter app, revert to browser
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/gabriel_thecode"))
            }
            startActivity(intent)

        }

        layoutPlaystore.setOnClickListener{
            val appPackageName = packageName // getPackageName() from Context or Activity object

            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }

        layoutGithub.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gabriel-TheCode"))
            startActivity(intent)
        }

        layoutSourceCode.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gabriel-TheCode/Infotify"))
            startActivity(intent)
        }

        binding.imgBack.setOnClickListener{
           finish()
        }
    }

}


