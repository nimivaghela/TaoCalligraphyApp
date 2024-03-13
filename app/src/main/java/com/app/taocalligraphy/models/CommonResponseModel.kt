package com.app.taocalligraphy.models

import com.app.taocalligraphy.other.Constants.ERROR
import com.google.gson.annotations.SerializedName

data class CommonResponseModel<T>(
    @SerializedName("status")
    var status: Int,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("type")
    var type: String? = ERROR,
    @SerializedName("data")
    var data: T? = null
)

data class RequestState<T>(
    var error: ApiError? = null,
    var progress: Boolean = false,
    var apiResponse: CommonResponseModel<T>? = null
)

/**
 * @errorState error state defined in the Config.kt class
 * you can set CUSTOM_ERROR, NETWORK_ERROR
 * In case of CUSTOM_ERROR, you have to set customMessage also
 */
data class ApiError(val errorCode: Int?, val errorState: String, val customMessage: String?,val type: String)

data class ErrorModel(
    @SerializedName("status")
    val errorCode:Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("type")
    val type: String = ERROR
)