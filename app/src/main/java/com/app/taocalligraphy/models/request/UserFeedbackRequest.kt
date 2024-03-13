package com.app.taocalligraphy.models.request


import com.google.gson.annotations.SerializedName

data class UserFeedbackRequest(
    @SerializedName("feedbackNature")
    var feedbackNature: String="",
    @SerializedName("message")
    var message: String=""
)