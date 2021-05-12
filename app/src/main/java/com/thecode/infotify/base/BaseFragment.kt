package com.thecode.infotify.base

import androidx.fragment.app.Fragment
import com.thecode.aestheticdialogs.AestheticDialog
import dagger.hilt.android.AndroidEntryPoint

open class BaseFragment : Fragment() {

    fun showErrorDialog(title: String, description: String) {
        AestheticDialog.showRainbow(
            activity,
            title,
            description,
            AestheticDialog.ERROR)
    }

    fun showSuccessDialog(title: String, description: String) {
        AestheticDialog.showRainbow(
            activity,
            title,
            description,
            AestheticDialog.SUCCESS)
    }
}