package com.app.taocalligraphy.utils.extensions

import android.content.Context
import android.widget.Toast

fun showToast(context: Context, message: String?) {
    if (!message.isNullOrEmpty()) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}