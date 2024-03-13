package com.app.taocalligraphy.models.request


import com.google.gson.annotations.SerializedName

data class UserProfilePublicRequest(
    @SerializedName("isSearchable")
    var isSearchable: Boolean=false,
    @SerializedName("profileLink")
    var profileLink: String="",
    @SerializedName("viewAboutMe")
    var viewAboutMe: Boolean=false,
    @SerializedName("viewBirthday")
    var viewBirthday: Boolean=false,
    @SerializedName("viewGender")
    var viewGender: Boolean=false,
    @SerializedName("viewInterest")
    var viewInterest: Boolean=false
)