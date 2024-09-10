package com.thecode.infotify.presentation.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.mikepenz.aboutlibraries.LibsBuilder
import com.thecode.infotify.R
import com.thecode.infotify.databinding.ActivityAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private val viewModel: AboutViewModel by viewModels()
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityAboutBinding.inflate(layoutInflater)

        observeNightMode()

        initViews()
        setUpListeners()
        setContentView(binding.root)
    }

    private fun observeNightMode() {
        viewModel.state.observe(this) { isNightMode ->
            val nightMode = if (isNightMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }


    private fun initViews() {
        val versionName: String = com.thecode.infotify.BuildConfig.VERSION_NAME
        binding.textVersion.text = versionName
    }

    private fun setUpListeners() {
        binding.apply {
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
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gabriel-TheCode"))
                startActivity(intent)
            }

            layoutSourceCode.setOnClickListener {
                val intent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/gabriel-TheCode/Infotify")
                    )
                startActivity(intent)
            }

            layoutLicenses.setOnClickListener {
                LibsBuilder()
                    .withAboutIconShown(true)
                    .withAboutAppName(resources.getString(R.string.app_name))
                    .withAboutVersionShown(true)
                    .withLicenseShown(true)
                    .withVersionShown(true)
                    .start(this@AboutActivity)
            }

            imgBack.setOnClickListener {
                finish()
            }
        }
    }
}
