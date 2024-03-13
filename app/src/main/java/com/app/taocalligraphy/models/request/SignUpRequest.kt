package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

class SignUpRequest(
    @SerializedName("deviceToken")
    var deviceToken: String="",
    @SerializedName("deviceType")
    var deviceType: String="",
    @SerializedName("email")
    var email: String="",
    @SerializedName("firstName")
    var firstName: String="",
    @SerializedName("lastName")
    var lastName: String="",
    @SerializedName("password")
    var password: String="",
    @SerializedName("signupType")
    var signupType: String="",
    @SerializedName("socialId")
    var socialId: String="",
    @SerializedName("isEmailIdEnteredManually")
    var isEmailIdEnteredManually: Boolean=false,
    @SerializedName("browserType")
    var browserType: String= "",
    @SerializedName("deviceModel")
    var deviceModel: String= "",
    @SerializedName("deviceVersion")
    var deviceVersion: String= "",
    @SerializedName("location")
    var location: String= "",
    @SerializedName("osType")
    var osType: String= "",
    @SerializedName("deviceId")
    var deviceId: String= "",
    @SerializedName("referralCode")
    var referralCode: String= "",
)