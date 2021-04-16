package com.thecode.infotify.application

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.thecode.infotify.utils.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class InfotifySharedPref {

    companion object {
        private const val NIGHT_MODE = "NIGHT_MODE"
        private const val IS_ONBOARDING_COMPLETED = "IS_ONBOARDING_COMPLETED"
    }

    private lateinit var sp: SharedPreferences

    // Editor for Shared preferences
    private lateinit var editor: SharedPreferences.Editor

    // Context
    private lateinit var context: Context


    @SuppressLint("CommitPrefEdits")
    @Inject
    fun infotifySharedPref(@ApplicationContext mContext: Context) {
        context = mContext
        sp = context.getSharedPreferences(AppConstants.SP_APPNAME, Activity.MODE_PRIVATE)
        editor = sp.edit()
    }

    /*fun init(mContext: Context) {
        context = mContext
        sp = mContext.getSharedPreferences(AppConstants.SP_APPNAME, Activity.MODE_PRIVATE)
        editor = sp.edit()
    }*/

    fun clearSession() {
        editor.clear()
        editor.commit()
    }

    fun setOnboardingCompleted() {
        editor.putBoolean(IS_ONBOARDING_COMPLETED, true)
        editor.commit()
    }

    fun isOnboardingCompleted(): Boolean {
        return sp.getBoolean(IS_ONBOARDING_COMPLETED, false)
    }

    fun isNightModeEnabled(): Boolean {
        return sp.getBoolean(NIGHT_MODE, false)
    }

    fun setNightModeEnabled(state: Boolean) {
        editor.putBoolean(NIGHT_MODE, state)
        editor.commit()
    }



}