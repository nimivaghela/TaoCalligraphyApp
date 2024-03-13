package com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model

import com.google.gson.annotations.SerializedName

data class SearchContentDatamodel(
    @SerializedName("id")
    var id: String?,
    @SerializedName("availableFromDate")
    var availableFromDate: String?,
    @SerializedName("availableEndDate")
    var availableEndDate: String?,
    @SerializedName("languageId")
    var languageId: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("contentDuration")
    var contentDuration: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("contentImage")
    var contentImage: String?,
    val thumbnailImage: String? = "",
    @SerializedName("isFavorite")
    var isFavorites: Boolean? = false,
    @SerializedName("isSubscribed")
    var isSubscribed: Boolean?,
    @SerializedName("subscription")
    var subscription: Subscription?,
    @SerializedName("isLocked")
    var isLocked: Boolean?,
    @SerializedName("isPaidContent")
    var isPaidContent: Boolean?,
    @SerializedName("isPurchased")
    var isPurchased: Boolean?,
    @SerializedName("unlockDays")
    var unlockDays: Int? = 0){
    data class Subscription(
        @SerializedName("isAccessible")
        var isAccessible: Boolean? = false,
        @SerializedName("badge")
        var badge: String? = "",
    )
}

