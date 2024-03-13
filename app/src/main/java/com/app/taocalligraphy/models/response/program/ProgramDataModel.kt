package com.app.taocalligraphy.models.response.program

import com.github.mikephil.charting.components.Description
import com.google.gson.annotations.SerializedName

data class ProgramDataModel(
    @SerializedName("id")
    var id: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("type")
    var type: String?,
    /*@SerializedName(value = "image", alternate = ["programImage"])
    var image: String?,*/
    val thumbnailImage: String = "",
    @SerializedName(value = "isFavorite")
    var isFavorites: Boolean = false,
    @SerializedName("joinedUserCount")
    var joinedUserCount: String?,
    @SerializedName("averageRatingCount")
    var averageRatingCount: String?,
    @SerializedName("ratingCount")
    var ratingCount: String?,
    @SerializedName("contentType")
    var contentType: String?,
    @SerializedName("isSubscribed")
    var isSubscribed: Boolean? = false,
    @SerializedName("subscription")
    var subscription: Subscription?,
    @SerializedName("isPurchased")
    var isPurchased: Boolean? = false,
    @SerializedName("isLocked")
    var isLocked: Boolean?,
    @SerializedName("isPaidContent")
    var isPaidContent: Boolean? = false,
    @SerializedName("isFeatured")
    var isFeatured: Boolean?,
    @SerializedName("unlockDays")
    var unlockDays: Int? = null
) {
    data class Subscription(
        @SerializedName("isAccessible")
        var isAccessible: Boolean? = false,
        @SerializedName("badge")
        var badge: String? = "",
    )
}

data class LinkedProgramData(
    val extraLinkedProgramList: ArrayList<ForYouProgramListResponse.ForYouProgramList.Program?> = ArrayList(),
    val feedbackTagList: List<FeedbackTag> = ArrayList()
)

data class FeedbackTag(
    val id: Int = 0,
    val name: String = ""
)