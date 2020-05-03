package com.thecode.infotify.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.TextView
import com.thecode.infotify.R

class CustomProgressBar {
    var dialog: Dialog? = null
        private set

    @JvmOverloads
    fun show(
        context: Context,
        title: CharSequence? = null,
        cancelable: Boolean = true,
        cancelListener: DialogInterface.OnCancelListener? = null
    ): Dialog {
        val inflator = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflator.inflate(R.layout.progress_bar, null)
        if (title != null) {
            val tv = view.findViewById<TextView>(R.id.id_title)
            tv.text = title
        }
        dialog = Dialog(context, R.style.NewDialog)
        dialog!!.setContentView(view)
        dialog!!.setCancelable(cancelable)
        dialog!!.setOnCancelListener(cancelListener)
        dialog!!.show()
        return dialog as Dialog
    }

}