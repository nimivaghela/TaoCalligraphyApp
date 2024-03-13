package com.app.taocalligraphy.models.response.playList


import com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter.ContentData
import com.google.gson.annotations.SerializedName

data class PlaylistContentFilterApiResponse(
    @SerializedName("list")
    var list: List<ContentList>,
    @SerializedName("totalRecords")
    var totalRecords: Int,
    @SerializedName("playlistName")
    var playlistName: String?,
) {
    data class ContentList(
        @SerializedName("contentImage")
        var contentImage: String = "",
        var thumbnailImage: String = "",
        val backgroundImageMobile: String = "",
        @SerializedName("contentName")
        var contentName: String = "",
        @SerializedName("contentFile")
        var contentFile: String? = "",
        val contentFileForHls: String? = "",
        val contentFileForDownload: String? = "",
        @SerializedName("id")
        var id: String = "",
        @SerializedName("isFavorite")
        var isFavorite: Boolean = false,
        @SerializedName("type")
        var type: String = "",
        @SerializedName("categoryDetailsList")
        var categoryDetailsList: List<CategoryDetails> = mutableListOf(),
        @SerializedName("contentDuration")
        var contentDuration: String= "",
        @SerializedName("contentOrder")
        var contentOrder: Int = -1,
        @SerializedName("subscription")
        var subscription: ContentData.Subscription? = null,
        @SerializedName("isLocked")
        var isLocked: Boolean? =false,
        @SerializedName("isPaidContent")
        var isPaidContent: Boolean? =false,
        @SerializedName("isPurchased")
        var isPurchased: Boolean? = false,
        var isSelected: Boolean = false,
        var isForAddItem :Boolean = false
    ) {
        data class CategoryDetails(
            @SerializedName("id")
            var id: Int,
            @SerializedName("mainCategory")
            var mainCategory: String,
            @SerializedName("subCategory")
            var subCategory: List<String>
        )
    }
}