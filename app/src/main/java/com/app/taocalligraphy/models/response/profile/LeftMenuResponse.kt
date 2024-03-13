package com.app.taocalligraphy.models.response.profile


import com.google.gson.annotations.SerializedName


data class LeftMenuResponse(
    @SerializedName("email")
    val email: String?,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("goals")
    val goals: ArrayList<String>?,
    @SerializedName("interests")
    val interests: ArrayList<String?>?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("originalProfilePicUrl")
    val originalProfilePicUrl: String?,
    @SerializedName("subscription")
    val subscription: String?,
    @SerializedName("subscriptionPlan")
    val subscriptionPlan: String?
)
