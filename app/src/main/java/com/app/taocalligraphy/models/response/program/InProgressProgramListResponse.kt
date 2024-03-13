package com.app.taocalligraphy.models.response.program


import com.google.gson.annotations.SerializedName

data class InProgressProgramListResponse(
    @SerializedName("inProgressProgramsList")
    val inProgressProgramsList: InProgressProgramsList?
) {
    data class InProgressProgramsList(
        @SerializedName("list")
        val programList: ArrayList<Program>? = arrayListOf(),
        @SerializedName("totalRecords")
        val totalRecords: Int?
    ) {
        data class Program(
            @SerializedName("completedDays")
            val completedDays: Int?,
            @SerializedName("programTotalDays")
            val programTotalDays: Int?,
            @SerializedName("id")
            val id: String?,
            /*@SerializedName("programImage")
            val programImage: String?,*/
            val thumbnailImage: String = "",
            @SerializedName("subscription")
            var subscription: ProgramDataModel.Subscription?,
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
            var unlockDays: Int? = null
        ){
            data class Subscription(
                @SerializedName("isAccessible")
                var isAccessible: Boolean? = false,
                @SerializedName("badge")
                var badge: String? = "",
            )
        }
    }
}