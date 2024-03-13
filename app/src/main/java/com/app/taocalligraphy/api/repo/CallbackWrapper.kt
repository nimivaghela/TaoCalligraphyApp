package com.app.taocalligraphy.api.repo

import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.StatusCode.STATUS_0
import com.app.taocalligraphy.other.Constants.StatusCode.STATUS_1
import com.app.taocalligraphy.other.Constants.StatusCode.STATUS_401
import com.app.taocalligraphy.other.Constants.StatusCode.STATUS_412
import com.app.taocalligraphy.other.Constants.StatusCode.STATUS_422
import com.app.taocalligraphy.other.Constants.StatusCode.STATUS_426
import com.app.taocalligraphy.other.Constants.StatusCode.STATUS_500
import com.app.taocalligraphy.utils.extensions.logError
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.reactivex.observers.DisposableObserver
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

abstract class CallbackWrapper<T : CommonResponseModel<*>>(
    private val view: BaseView?
) : DisposableObserver<T>() {

    override fun onNext(response: T) {
        when (response.status) {
            STATUS_1 -> {
                onApiSuccess(response)
            }
            STATUS_0 -> {
                onApiSuccess(response)
            }
            STATUS_500 -> {
                view?.internalServer()
            }
            else -> {
                logError(msg = "Unknown Error repo ${response.message}")
                response.message?.let {
                    view?.onUnknownError(response.message)
                    view?.forbidden(response.message)
                } ?: let {
                    view?.internalServer()
                }
                onApiError(null)
            }
        }
    }

    override fun onError(e: Throwable) {
        logError(msg = "Error in API call ${e.printStackTrace()}")
        var errorModel: ErrorModel? = null
        when (e) {
            is HttpException -> {
                val responseBody = e.response()?.errorBody()
                errorModel = getErrorMessage(responseBody)
                logError(msg = "Error in API call errorModel ${errorModel?.message}")
                errorModel?.let {
                    view?.onUnknownError(errorModel?.message ?: "")
                    when (e.code()) {
                        STATUS_401 -> {
                            view?.autoLogout()
                        }
                        STATUS_422 -> {
                            view?.onOTPExpire(errorModel?.message)
                        }
                        STATUS_412 -> {
                            view?.onVerificationError()
                        }
                        STATUS_426 -> {
                            view?.onSubscribeRequired(errorModel?.message)
                        }
                        else -> {
                            logError(msg = "onError: ${errorModel?.message}")
                        }
                    }

                } ?: let {
                    view?.onUnknownError(errorModel?.message)
                }
            }

            is SSLHandshakeException -> {
                view?.onServerDown()
                errorModel = ErrorModel(0, "Server Connection issue", ERROR)
            }
            is SocketTimeoutException -> {
                view?.onTimeout()
                errorModel = ErrorModel(0, "Server Connection issue", ERROR)
            }
            is ConnectException -> {
                view?.onConnectionError()
                errorModel = ErrorModel(0, "Network error", ERROR)
            }
            is IOException -> {
                errorModel = ErrorModel(0, "Internet issue", ERROR)
                view?.onNetworkError()
            }
            else -> {
                view?.onUnknownError(e.message)
            }
        }
        onApiError(errorModel)
    }

    override fun onComplete() {

    }

    internal abstract fun onApiSuccess(response: T)

    internal abstract fun onApiError(e: ErrorModel?)

    private fun getErrorMessage(responseBody: ResponseBody?): ErrorModel? {
        try {
            responseBody?.let {
                val json = JsonParser.parseString(it.string())
                return Gson().fromJson(json, ErrorModel::class.java)
            } ?: let {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
