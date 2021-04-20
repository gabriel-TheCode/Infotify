package com.thecode.infotify.application

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.thecode.infotify.utils.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InfotifySharedPref @SuppressLint("CommitPrefEdits") @Inject constructor(@ApplicationContext context: Context){

    companion object {
        private const val NIGHT_MODE = "NIGHT_MODE"
        private const val IS_ONBOARDING_COMPLETED = "IS_ONBOARDING_COMPLETED"
    }

    private var appSharedPrefs: SharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Activity.MODE_PRIVATE)
    private var prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()

    fun clearSession() {
        prefsEditor.clear()
        prefsEditor.commit()
    }

    fun setOnboardingCompleted() {
        prefsEditor.putBoolean(IS_ONBOARDING_COMPLETED, true)
        prefsEditor.commit()
    }

    fun isOnboardingCompleted(): Boolean {
        return appSharedPrefs.getBoolean(IS_ONBOARDING_COMPLETED, false)
    }

    fun isNightModeEnabled(): Boolean {
        return appSharedPrefs.getBoolean(NIGHT_MODE, false)
    }

    fun setNightModeEnabled(state: Boolean) {
        prefsEditor.putBoolean(NIGHT_MODE, state)
        prefsEditor.commit()
    }
}

