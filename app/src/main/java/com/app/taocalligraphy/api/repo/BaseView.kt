package com.app.taocalligraphy.api.repo

interface BaseView {

    fun internalServer()

    fun notFound(error: String?)

    fun unauthorized(error: String?)

    fun forbidden(error: String?)

    fun onUnknownError(error: String?)

    fun onTimeout()

    fun onNetworkError()

    fun onConnectionError()

    fun generalErrorAction()

    fun autoLogout()

    fun onServerDown()

    fun onOTPExpire(error: String?)

    fun onVerificationError()

    fun onSubscribeRequired(error: String?)
}