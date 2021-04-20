package com.thecode.infotify

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.thecode.infotify.utils.SharedPreferenceUtils
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.realm.Realm
import io.realm.RealmConfiguration

class InfotifyApp : MultiDexApplication() {
    override fun onCreate() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true) // Allow to load drawable dynamically to solve RessourceNotFoundException with drawable vectors
        super.onCreate()
        instance = this
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/SF-Regular.otf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )

        SharedPreferenceUtils.init(baseContext)
        Realm.init(applicationContext)
        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .name("infotity.realm").build()
        Realm.setDefaultConfiguration(config)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    val context: Context
        get() = applicationContext

    companion object {
        @get:Synchronized
        var instance: InfotifyApp? = null
            private set
    }
}