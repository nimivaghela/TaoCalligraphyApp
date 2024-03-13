package com.app.taocalligraphy.models.response

import com.google.gson.annotations.SerializedName

data class FetchDailyStatDataReponse(
    @SerializedName("dailyMeditationStats")
    val dailyMeditationStats: DailyMeditationStats?

)

data class DailyMeditationStats(
    @SerializedName("goalPercentage")
    val goalPercentage: String?,
    @SerializedName("liveSessionCount")
    val liveSessionCount: String?,
    @SerializedName("meditationTime")
    val meditationTime: String?,
    @SerializedName("numberOfMeditations")
    val numberOfMeditations: String?
)