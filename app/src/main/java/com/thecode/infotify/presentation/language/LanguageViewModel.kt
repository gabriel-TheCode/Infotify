package com.thecode.infotify.presentation.language

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.usecases.GetLanguages
import com.thecode.infotify.core.usecases.GetLanguagesApiCodes
import com.thecode.infotify.core.usecases.SaveLanguagePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val getLanguagesApiCodes: GetLanguagesApiCodes,
    private val getLanguages: GetLanguages,
    private val saveLanguagePreference: SaveLanguagePreference
) : ViewModel() {

    val languages = MutableLiveData<List<String>>()

    fun getCurrentLanguages() {
        val list = getLanguages().toMutableList()
        languages.value = list
    }

    fun saveLanguagePref(position: Int) {
        viewModelScope.launch {
            val languageCode = getLanguagesApiCodes()[position]
            saveLanguagePreference.invoke(languageCode)
        }

    }

}