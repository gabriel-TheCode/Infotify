package com.thecode.infotify.utils.extensions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

suspend fun <T> DataStore<Preferences>.setValue(key: Preferences.Key<T>, value: T) {
    this.edit { preferences ->
        preferences[key] = value
    }
}
