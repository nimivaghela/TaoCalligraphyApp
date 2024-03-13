package com.app.taocalligraphy.models.response.program

import com.google.gson.annotations.SerializedName

class UserProgressDetailApiResponse : ArrayList<UserProgressDetailApiResponse.Data>() {
    data class Data(
        @SerializedName("date")
        var date: String?,
        @SerializedName("dayNo")
        var dayNo: Int?,
        @SerializedName("minutes")
        var minutes: Int?,
        @SerializedName("totalMeditations")
        var totalMeditations: Int?,
        @SerializedName("userMeditations")
        var userMeditations: Int?,
    )
}