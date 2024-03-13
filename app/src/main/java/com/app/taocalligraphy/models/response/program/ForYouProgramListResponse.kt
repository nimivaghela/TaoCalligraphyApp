package com.app.taocalligraphy.models.response.program


import com.google.gson.annotations.SerializedName

data class ForYouProgramListResponse(
    @SerializedName("forYouProgramList")
    val forYouProgramList: ForYouProgramList?
) {
    data class ForYouProgramList(
        @SerializedName("list")
        val programList: ArrayList<ProgramDataModel>?,
        @SerializedName("totalRecords")
        val totalRecords: Int?
    ) {
        data class Program(
            @SerializedName("id")
            val id: String?,
            @SerializedName("joinedUserCount")
            val joinedUserCount: String?,
            @SerializedName("averageRatingCount")
            val averageRatingCount: Float?,
            /*@SerializedName("programImage")
            val programImage: String?,*/
            val thumbnailImage: String = "",
            @SerializedName("ratingCount")
            val ratingCount: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("isFavorite")
            var isFavorite: Boolean?,
            @SerializedName("isSubscribed")
            var isSubscribed: Boolean?,
            @SerializedName("isLocked")
            var isLocked: Boolean?,
            @SerializedName("isPaidContent")
            var isPaidContent: Boolean?,
            @SerializedName("isPurchased")
            var isPurchased: Boolean?,
            @SerializedName("isFeatured")
            var isFeatured: Boolean?,
            @SerializedName("unlockDays")
            var unlockDays: Int? = 0
        )
    }
}