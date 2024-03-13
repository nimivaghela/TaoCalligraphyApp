package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

data class UserProfileRequest(
    @SerializedName("aboutMe")
    var aboutMe: String = "",
    @SerializedName("ageRange")
    var ageRange: String = "",
    @SerializedName("countryId")
    var countryId: Int?,
    @SerializedName("dateOfBirth")
    var dateOfBirth: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("firstName")
    var firstName: String = "",
    @SerializedName("gender")
    var gender: String = "",
    @SerializedName("isShowSessionRoomInOtherLanguage")
    var isShowSessionRoomInOtherLanguage: Boolean = false,
    @SerializedName("lastName")
    var lastName: String = "",
    @SerializedName("mobileNo")
    var mobileNo: String = "",
    @SerializedName("regionId")
    var regionId: Int?,
    @SerializedName("spokenLanguages")
    var spokenLanguages : List<Int> = listOf()
)