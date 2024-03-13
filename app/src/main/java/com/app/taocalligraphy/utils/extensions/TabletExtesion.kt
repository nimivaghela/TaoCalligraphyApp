package com.app.taocalligraphy.utils.extensions

import android.content.Context
import android.content.res.Configuration
import com.app.taocalligraphy.R

fun isTablet(context: Context): Boolean {
    return context.resources.getBoolean(R.bool.isTablet)
}

fun isTabletLandScape(context: Context): Boolean {
    var orientation = context.resources.configuration.orientation
    return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}