package com.app.taocalligraphy.models.response.home_screen

import com.google.gson.annotations.SerializedName

data class FetchDailyWisdomDataResponse(
    @SerializedName("personName")
    var personName: String?,
    @SerializedName("text")
    var text: String?,
    @SerializedName("goalPercentage")
    var goalPercentage: String?,
    @SerializedName("numberOfMeditations")
    var numberOfMeditations: String?,
    @SerializedName("liveSessionCount")
    var liveSessionCount: String?,
    @SerializedName("meditationTime")
    var meditationTime: String?,
    @SerializedName("totalUnReadNotificationCount")
    var totalUnReadNotificationCount: String?,
    @SerializedName("totalUnReadChatCount")
    var totalUnReadChatCount: String?,
)
