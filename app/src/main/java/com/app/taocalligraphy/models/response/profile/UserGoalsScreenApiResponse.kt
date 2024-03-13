package com.app.taocalligraphy.models.response.profile


import com.google.gson.annotations.SerializedName

class UserGoalsScreenApiResponse(
    @SerializedName("dailyMeditationTarget")
    val dailyMeditationTarget: String?, // 0
    @SerializedName("list")
    val list: ArrayList<UserGoalsScreenList?>?
) {
    data class UserGoalsScreenList(
        @SerializedName("isSelected")
        var isSelected: Boolean?,
        @SerializedName("keywordId")
        var keywordId: Int?,
        @SerializedName("name")
        var name: String?,
        var isChanged: Boolean = false,
    )
}