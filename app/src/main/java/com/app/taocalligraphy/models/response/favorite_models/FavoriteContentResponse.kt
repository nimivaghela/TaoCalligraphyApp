package com.app.taocalligraphy.models.response.favorite_models

import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.google.gson.annotations.SerializedName

data class FavoriteContentResponse(
    @SerializedName("list")
    val list: ArrayList<FavoriteContentDataModel?>?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
)

data class FavoriteContentDataModel(
    @SerializedName("categoryDetailsList")
    val categoryDetailsList: ArrayList<CategoryDataModel>?,
    @SerializedName("contentDuration")
    val contentDuration: String?,
    @SerializedName("contentImage")
    val contentImage: String?,
    val thumbnailImage: String? = "",
    @SerializedName("contentName")
    val contentName: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isFavorite")
    var isFavorite: Boolean? = false,
    @SerializedName("isLocked")
    val isLocked: Boolean?,
    @SerializedName("isSubscribed")
    val isSubscribed: Boolean?,
    @SerializedName("subscription")
    val subscription: Subscription?,
    @SerializedName("isPaidContent")
    var isPaidContent: Boolean?,
    @SerializedName("isFeatured")
    var isFeatured: Boolean?,
    @SerializedName("isPurchased")
    var isPurchased: Boolean?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("unlockDays")
    var unlockDays: Int?=null,
    val description: String? = "",
) {
    data class Subscription(
        @SerializedName("isAccessible")
        var isAccessible: Boolean? = false,
        @SerializedName("badge")
        var badge: String? = "",
    )
}