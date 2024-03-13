package com.app.taocalligraphy.models.request


import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("deviceToken")
    var deviceToken: String = "",
    @SerializedName("deviceType")
    var deviceType: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("loginType")
    var loginType: String = "",
    @SerializedName("password")
    var password: String = "",
    @SerializedName("socialId")
    var socialId: String = "",
    @SerializedName("browserType")
    var browserType: String = "",
    @SerializedName("deviceModel")
    var deviceModel: String = "",
    @SerializedName("deviceVersion")
    var deviceVersion: String = "",
    @SerializedName("location")
    var location: String = "",
    @SerializedName("osType")
    var osType: String = "",
    @SerializedName("deviceId")
    var deviceId: String = "",

    )