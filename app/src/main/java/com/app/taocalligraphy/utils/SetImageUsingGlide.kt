package com.app.taocalligraphy.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import com.app.taocalligraphy.TaoCalligraphy
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import jp.wasabeef.glide.transformations.BlurTransformation

@SuppressLint("CheckResult")
fun ImageView.loadImage(
    url: String?,
    placeHolder: Int,
    isPlaceholderEnabled: Boolean = false
) {
    if (url?.lowercase()?.contains(".svg") == true) {
        GlideToVectorYou
            .init()
            .with(TaoCalligraphy.instance)
            .load(Uri.parse(url), this)
    } else {
        Glide
            .with(TaoCalligraphy.instance)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply {
                if (isPlaceholderEnabled)
                    error(placeHolder)
            }
            .dontAnimate()
            .into(this)
    }
}

@SuppressLint("CheckResult")
fun ImageView.loadImageWithBlur(
    url: String?,
    placeHolder: Int,
    isPlaceholderEnabled: Boolean = false
) {
    Glide
        .with(TaoCalligraphy.instance)
        .load(url)
        .apply(bitmapTransform(BlurTransformation(90)))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply {
            if (isPlaceholderEnabled)
                error(placeHolder)
        }
        .dontAnimate()
        .into(this)

}

@SuppressLint("CheckResult")
fun ImageView.loadImageProfile(
    url: String?,
    placeHolder: Int
) {
    if (url?.lowercase()?.contains(".svg") == true) {
        GlideToVectorYou
            .init()
            .with(TaoCalligraphy.instance)
            .load(Uri.parse(url), this)
    } else {
        Glide
            .with(TaoCalligraphy.instance)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placeHolder)
            .error(placeHolder)
            .dontAnimate()
            .into(this)
    }
}

@SuppressLint("CheckResult")
fun ImageView.loadImageProfile(
    url: Uri?,
    placeHolder: Int
) {
    Glide
        .with(TaoCalligraphy.instance)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(placeHolder)
        .error(placeHolder)
        .dontAnimate()
        .into(this)
}


@SuppressLint("CheckResult")
fun ImageView.loadImageWithAnimate(
    url: String?,
    placeHolder: Int,
    isPlaceholderEnabled: Boolean = false
) {
    if (url?.lowercase()?.contains(".svg") == true) {
        GlideToVectorYou
            .init()
            .with(TaoCalligraphy.instance)
            .load(Uri.parse(url), this)
    } else {
        Glide
            .with(TaoCalligraphy.instance)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .apply {
                if (isPlaceholderEnabled)
                    error(placeHolder)
            }
            .transition(DrawableTransitionOptions.withCrossFade(1500))
            .into(this)
    }
}

@SuppressLint("CheckResult")
fun ImageView.loadImage(
    url: Uri?,
    placeHolder: Int,
    isPlaceholderEnabled: Boolean = false
) {
    if (url?.toString()?.lowercase()?.contains(".svg") == true) {
        GlideToVectorYou
            .init()
            .with(TaoCalligraphy.instance)
            .load(Uri.parse(url.toString()), this)
    } else {
        Glide
            .with(TaoCalligraphy.instance)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply {
                if (isPlaceholderEnabled) {
                    placeholder(placeHolder)
                    error(placeHolder)
                }
            }
            .dontAnimate()
            .into(this)
    }

}

@SuppressLint("CheckResult")
fun ImageView.loadImage(
    url: Int?,
    placeHolder: Int
) {
    if (url?.toString()?.lowercase()?.contains(".svg") == true) {
        GlideToVectorYou
            .init()
            .with(TaoCalligraphy.instance)
            .load(Uri.parse(url.toString()), this)
    } else {
        Glide
            .with(TaoCalligraphy.instance)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(placeHolder)
            .dontAnimate()
            .into(this)
    }
}

fun loadHtml(textView: AppCompatTextView, data: String?) {
    textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(data)
    }
    textView.movementMethod = LinkMovementMethod.getInstance()

}

fun getHtmlString(textView: AppCompatTextView, htmlString: String?) {
    textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT).toString()
    } else {
        Html.fromHtml(htmlString).toString()
    }
}

fun String.getHtmlString(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT).toString()
    } else {
        Html.fromHtml(this).toString()
    }
}

fun Context.isNetwork(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nw = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(nw)
        ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        //for other device how are able to connect with Ethernet
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        //for check internet over Bluetooth
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}