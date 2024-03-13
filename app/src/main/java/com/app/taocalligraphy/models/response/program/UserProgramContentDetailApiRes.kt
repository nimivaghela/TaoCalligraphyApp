package com.app.taocalligraphy.models.response.program

import com.google.gson.annotations.SerializedName
import java.util.*

class UserProgramContentDetailApiRes : ArrayList<UserProgramContentDetailApiRes.Data>() {
    data class Data(
        @SerializedName("contentAvailabilityTime")
        var contentAvailabilityTime: String?,
        @SerializedName("contentDescription")
        var contentDescription: String?,
        @SerializedName("contentDuration")
        var contentDuration: String?,
        @SerializedName("contentId")
        var contentId: Int?,
        @SerializedName("contentImage")
        var contentImage: String?,
        val thumbnailImage: String? = "",
        val backgroundImageMobile:String? = "",
        @SerializedName("contentOrder")
        var contentOrder: Int?,
        @SerializedName("contentTitle")
        var contentTitle: String?,
        @SerializedName("isPlayed")
        var isPlayed: Boolean?,
        @SerializedName("subscription")
        var subscription: UserProgramApiResponse.Subscription? = null,
        @SerializedName("isPurchased")
        var isPurchased: Boolean? = false,
        @SerializedName("isPaidContent")
        var isPaidContent: Boolean? = false,
        @SerializedName("programContentId")
        var programContentId: Int?,
        var currentDate: String = "",
        var combinedDate: Calendar,
        var isPlayEnable: Boolean = false

    )
}