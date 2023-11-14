package com.thecode.infotify.presentation.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mikepenz.aboutlibraries.LibsBuilder
import com.thecode.infotify.R
import com.thecode.infotify.databinding.ActivityAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private val viewModel: AboutViewModel by viewModels()
    private lateinit var binding: ActivityAboutBinding

    private lateinit var layoutGithub: RelativeLayout
    private lateinit var layoutTwitter: RelativeLayout
    private lateinit var layoutSourceCode: RelativeLayout
    private lateinit var layoutPlaystore: RelativeLayout
    private lateinit var layoutOpenSource: RelativeLayout
    private lateinit var txtVersion: TextView
    private lateinit var imgBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.isNightModeActivated()) {
            setTheme(R.style.AppTheme_Base_Night)
        } else {
            setTheme(R.style.AppTheme_Base_Light)
        }

        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root

        initViews()
        setUpListeners()

        setContentView(view)
    }

    private fun initViews() {
        txtVersion = binding.textVersion
        imgBack = binding.imgBack
        layoutGithub = binding.layoutGithub
        layoutTwitter = binding.layoutTwitter
        layoutSourceCode = binding.layoutSourceCode
        layoutPlaystore = binding.layoutPlaystore
        layoutOpenSource = binding.layoutLicenses

        val versionName: String = com.thecode.infotify.BuildConfig.VERSION_NAME
        txtVersion.text = versionName
    }

    private fun setUpListeners() {
        layoutTwitter.setOnClickListener {
            var intent: Intent
            try {
                // get the Twitter app if possible
                application.packageManager.getPackageInfo("com.twitter.android", 0)
                intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name=gabriel_thecode")
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            } catch (e: Exception) {
                // no Twitter app, revert to browser
                intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/gabriel_thecode"))
            }
            startActivity(intent)
        }

        layoutPlaystore.setOnClickListener {
            val appPackageName = packageName

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

        layoutGithub.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gabriel-TheCode"))
            startActivity(intent)
        }

        layoutSourceCode.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gabriel-TheCode/Infotify"))
            startActivity(intent)
        }

        layoutOpenSource.setOnClickListener {
            LibsBuilder()
                .withAboutIconShown(true)
                .withAboutAppName(resources.getString(R.string.app_name))
                .withAboutVersionShown(true)
                .withLicenseShown(true)
                .withVersionShown(true)
                .withAutoDetect(true)
                .start(this)
        }

        imgBack.setOnClickListener {
            finish()
            this.overridePendingTransition(
                R.anim.enter_from_left,
                R.anim.exit_from_right
            )
        }
    }
}
