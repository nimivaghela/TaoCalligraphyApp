package com.app.taocalligraphy.utils.extensions

import android.text.TextUtils
import android.util.Log
import com.app.taocalligraphy.BuildConfig

const val DEFAULT_TAG = "TaoCalligraphy Logger: "
fun Any.logError(tag: String = this.javaClass.simpleName, msg: String?) {
    msg?.run {
        if (BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(tag))
                Log.e(this.javaClass.simpleName, msg)
            else
                Log.e(tag, msg)
        }
    }
}

fun Any.logDebug(tag: String = this.javaClass.simpleName, msg: String?) {
    msg?.run {
        if (BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(tag))
                Log.d(this.javaClass.simpleName, msg)
            else
                Log.d(tag, msg)
        }

    }
}

fun Any.logVerbose(tag: String = this.javaClass.simpleName, msg: String?) {
    msg?.run {
        if (BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(tag))
                Log.v(this.javaClass.simpleName, msg)
            else
                Log.v(tag, msg)
        }
    }
}

fun Any.logInfo(tag: String = this.javaClass.simpleName, msg: String?) {
    msg?.run {
        if (BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(tag))
                Log.i(this.javaClass.simpleName, msg)
            else
                Log.i(tag, msg)
        }

    }
}

fun logError(tag: String = DEFAULT_TAG, msg: String?) {
    msg?.run {
        if (BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(tag))
                Log.e(DEFAULT_TAG, msg)
            else
                Log.e(tag, msg)
        }

    }
}

fun logDebug(tag: String = DEFAULT_TAG, msg: String?) {
    msg?.run {
        if (BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(tag))
                Log.d(DEFAULT_TAG, msg)
            else
                Log.d(tag, msg)
        }

    }
}

fun logVerbose(tag: String = DEFAULT_TAG, msg: String?) {
    msg?.run {
        if (BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(tag))
                Log.v(DEFAULT_TAG, msg)
            else
                Log.v(tag, msg)
        }
    }
}

fun logInfo(tag: String = DEFAULT_TAG, msg: String?) {
    msg?.run {
        if (BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(tag))
                Log.i(DEFAULT_TAG, msg)
            else
                Log.i(tag, msg)
        }

    }
}
