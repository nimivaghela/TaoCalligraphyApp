package com.app.taocalligraphy.models.response.history


import com.google.gson.annotations.SerializedName


data class FetchProgramHistoryData(
    @SerializedName("forYouProgramList")
    val forYouProgramList: ForYouProgramList?,
    @SerializedName("inProgressProgramsList")
    val inProgressProgramsList: InProgressProgramsList?
) {
    data class ForYouProgramList(
        @SerializedName("list")
        val list: ArrayList<ForYouProgramData?>?,
        @SerializedName("totalRecords")
        val totalRecords: Int?
    )

    data class InProgressProgramsList(
        @SerializedName("list")
        val list: ArrayList<ForYouProgramData?>?,
        @SerializedName("totalRecords")
        val totalRecords: Int?
    )


    data class ForYouProgramData(
        @SerializedName("completedPercentage")
        val completedPercentage: Int?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("isLocked")
        val isLocked: Boolean?,
        @SerializedName("isSubscribed")
        val isSubscribed: Boolean?,
        @SerializedName("programCompletedDate")
        val programCompletedDate: String?,
        @SerializedName("programJoinDate")
        val programJoinedDate: String?,
        val thumbnailImage: String = "",
        @SerializedName("title")
        val title: String?,
        @SerializedName("unlockDays")
        val unlockDays: Any?
    )

}
