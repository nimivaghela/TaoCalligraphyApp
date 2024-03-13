package com.app.taocalligraphy.models.response.history


import com.google.gson.annotations.SerializedName


data class FetchMeditationHistoryData(
    @SerializedName("list")
    val list: ArrayList<MeditationData?>?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
) {
    data class MeditationData(
        @SerializedName("backgroundImageMobile")
        val backgroundImageMobile: String?,
        @SerializedName("backgroundImageWeb")
        val backgroundImageWeb: String?,
        @SerializedName("categoryDetailsList")
        val categoryDetailsList: ArrayList<CategoryDetails?>?,
        @SerializedName("contentDuration")
        val contentDuration: String?,
        @SerializedName("contentImage")
        val contentImage: String?,
        val thumbnailImage: String? = "",
        @SerializedName("contentLastPlayDate")
        val contentLastPlayDate: String?,
        @SerializedName("contentLastPlayTime")
        val contentLastPlayTime: String?,
        @SerializedName("contentName")
        val contentName: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("isLocked")
        val isLocked: Boolean?,
        @SerializedName("isSubscribed")
        val isSubscribed: Boolean?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("unlockDays")
        val unlockDays: Any?
    ) {
        data class CategoryDetails(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("mainCategory")
            val mainCategory: String?,
            @SerializedName("subCategory")
            val subCategory: ArrayList<String?>?
        )
    }
}
