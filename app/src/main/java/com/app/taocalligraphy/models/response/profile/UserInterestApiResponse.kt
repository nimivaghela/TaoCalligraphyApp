package com.app.taocalligraphy.models.response.profile


import com.google.gson.annotations.SerializedName

data class UserInterestApiResponse(
    @SerializedName("isViewInterest")
    var isViewInterest: Boolean? = false,
    @SerializedName("list")
    var list: List<InterestList?>?
) {
    data class InterestList(
        @SerializedName("id")
        var id: Int?,
        @SerializedName("isSelected")
        var isSelected: Boolean?,
        @SerializedName("name")
        var name: String?,
        var isChanged: Boolean = false
    )
}