package com.app.taocalligraphy.models.response.profile


import com.google.gson.annotations.SerializedName

data class UserProfileEditApiResponse(
        @SerializedName("originalProfilePicUrl")
        var originalProfilePicUrl: String?,
        @SerializedName("thumbProfilePicUrl")
        var thumbProfilePicUrl: String?
    )