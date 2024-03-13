package com.app.taocalligraphy.models.response.how_to_meditate_response

import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FetchHowToMeditateDataListResponse(
    @SerializedName("watch")
    var watch: HowToMeditateDataListModel?,
    @SerializedName("read")
    var read: HowToMeditateDataListModel?,
) : Serializable

data class HowToMeditateDataListModel(
    @SerializedName("totalRecords")
    var totalRecords: String?,
    @SerializedName("list")
    var list: ArrayList<HowToMeditateDataModel?>?
)

data class HowToMeditateDataModel(
    @SerializedName("title")
    var title: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("time")
    var time: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("contentImage")
    var contentImage: String?,
    val thumbnailImage: String? = "",
    @SerializedName("contentId")
    var contentId: String?,
    @SerializedName("isSubscribed")
    var isSubscribed: Boolean? = true,
    @SerializedName("isLocked")
    var isLocked: Boolean? = true,
    @SerializedName("isPaidContent")
    var isPaidContent: Boolean? = true,
    @SerializedName("isPurchased")
    var isPurchased: Boolean? = true,
    @SerializedName("subscription")
    var subscription: ProgramDataModel.Subscription?,
    @SerializedName("isFeatured")
    var isFeatured: Boolean? = true,
    @SerializedName("unlockDays")
    var unlockDays: Int? = 0
) : Serializable