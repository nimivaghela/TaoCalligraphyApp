package com.app.taocalligraphy.utils.extensions

import android.os.Build
import android.text.Html
import android.text.Spanned

fun setHtmlTextToTextView(htmlText: String?): Spanned {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return (Html.fromHtml(
            htmlText,
            Html.FROM_HTML_MODE_COMPACT
        ))
    } else {
        return (Html.fromHtml(htmlText!!))
    }

}