package com.app.taocalligraphy.utils.extensions

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View

fun String.clickEvent(
    startIndex: Int,
    endIndex: Int,
    isLink: Boolean,
    spanColor: Int,
    clickEvent: () -> Unit
): SpannableString {
    return SpannableString(this).clickEvent(startIndex, endIndex, isLink, spanColor, clickEvent)
}

fun SpannableString.clickEvent(
    startIndex: Int,
    endIndex: Int,
    isLink: Boolean,
    spanColor: Int,
    clickEvent: () -> Unit
): SpannableString {
    return this.apply {
        this.setSpan(
            WordSpan(isLink, clickEvent), startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        this.setSpan(
            ForegroundColorSpan(spanColor),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

open class WordSpan(private val isLink: Boolean, private val onClickEvent: () -> Unit) :
    ClickableSpan() {
    override fun onClick(widget: View) {
        onClickEvent()
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isLink
    }
}

fun String.appendHtml(): String {
    return "<html><head><style type=\"text/css\">@font-face {font-family:JostRegular;src:url(\"file:///android_asset/fonts/font_jost_regular.ttf\")}body {font-family:JostRegular;font-size: 18px;color: #656565;}</style></head><body>$this</body></html>"
}

fun String.appendHtmlForTablet(): String {
    return "<html><head><style type=\"text/css\">@font-face {font-family:JostRegular;src:url(\"file:///android_asset/fonts/font_jost_regular.ttf\")}body {font-family:JostRegular;font-size: 16px;color: #656565;}</style></head><body>$this</body></html>"
}

fun String.appendHtmlForTabletTextSize22(): String {
    return "<html><head><style type=\"text/css\">@font-face {font-family:JostRegular;src:url(\"file:///android_asset/fonts/font_jost_regular.ttf\")}body {font-family:JostRegular;font-size: 22px;color: #656565;}</style></head><body>$this</body></html>"
}