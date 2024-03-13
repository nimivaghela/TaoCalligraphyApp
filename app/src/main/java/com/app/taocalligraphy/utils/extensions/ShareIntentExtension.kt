package com.app.taocalligraphy.utils.extensions

import android.app.Activity
import android.content.ClipDescription
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.app.taocalligraphy.BuildConfig.APP_URL
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.IOS_APPLE_ID
import com.app.taocalligraphy.other.Constants.IOS_BUNDLE_ID
import com.app.taocalligraphy.other.Constants.IOS_MINIMUM_VERSION
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.awaitAll

const val URL = APP_URL
const val URL_PROFILE = "$APP_URL/profile"
const val URL_CONTENT = "$APP_URL/meditation-content"
const val URL_READ_MEDITATION = "$APP_URL/meditation-read"
const val URL_WATCH_MEDITATION = "$APP_URL/resources-content"

fun Activity.shareUserProfile(
    username: String,
    userId: String,
    userProfilePhoto: String,
    url: String
) {
    val title = "$username on Tao Calligraphy"
    val description = "Add me as friend on Tao Calligraphy"
    val key = "userId"
    val value = userId
    this.buildLink(key, value, title, description, userProfilePhoto, true, url,true)
}

fun Activity.shareMeditationContent(
    contentTitle: String,
    contentDescription: String,
    contentId: String,
    contentImage: String,
    url: String
) {
    val title = contentTitle
    val description = contentDescription
    val key = "contentId"
    val value = contentId
    this.buildLink(key, value, title, description, contentImage, false, url)
}

fun Activity.buildResetPasswordLink(url: String) {
    var shareDynamicLink: String
    Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
        link = Uri.parse(url)
        domainUriPrefix = getString(com.app.taocalligraphy.R.string.domain_url_prefix)

        androidParameters(BuildConfig.APPLICATION_ID) {
            minimumVersion = 1
        }

        iosParameters(IOS_BUNDLE_ID) {
            appStoreId = IOS_APPLE_ID
            minimumVersion = IOS_MINIMUM_VERSION
        }
    }.addOnSuccessListener {
        shareDynamicLink = it.shortLink.toString()
        openChooser(this, shareDynamicLink)
    }.addOnFailureListener {
    }
}

private fun Activity.buildLink(
    key: String,
    value: String,
    title: String,
    description: String,
    imageURL: String,
    isProfileShare: Boolean,
    url: String,
    isProfile : Boolean = false
) {
    var shareDynamicLink: String
    var shareContent: String
    Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
        if (url.isNullOrEmpty())
            link = Uri.parse("$URL?$key=$value")
        else
            link = Uri.parse("$url?$key=$value")
        domainUriPrefix = getString(com.app.taocalligraphy.R.string.domain_url_prefix)

        socialMetaTagParameters {
            this.title = title
            if (imageURL.isNotEmpty()) {
                this.imageUrl = Uri.parse(imageURL)
            }
            this.description = description
        }

        androidParameters(com.app.taocalligraphy.BuildConfig.APPLICATION_ID) {
            minimumVersion = 1
        }

        iosParameters(IOS_BUNDLE_ID) {
            appStoreId = IOS_APPLE_ID
            minimumVersion = IOS_MINIMUM_VERSION
        }
    }.addOnSuccessListener {
        shareDynamicLink = it.shortLink.toString()
        shareContent = if (isProfileShare) {
            shareDynamicLink
        } else {
            "$title\n\n$shareDynamicLink"
        }
        if(isProfile)
         openChooserForProfile(this, shareContent)
        else
         openChooser(this, shareContent)
    }.addOnFailureListener {
        Log.e("Error",it.message?:"error")
    }
}

fun openChooser(activity: Activity, sharingContent: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, sharingContent)
    intent.type = "text/plain"
    activity.startActivity(intent)
}

fun openChooserForProfile(activity: Activity, sharingContent: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.action = Intent.ACTION_SEND
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, sharingContent)
    intent.putExtra(Intent.EXTRA_TITLE, Constants.appName)
    activity.startActivity(Intent.createChooser(intent, Constants.appName))
}
              