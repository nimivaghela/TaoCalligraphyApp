package com.app.taocalligraphy.models.response.stats_response


import com.google.gson.annotations.SerializedName


data class FetchMonthStatDataReponse(
    @SerializedName("dailyMeditationStatsDetails")
    val dailyMeditationStatsDetails: ArrayList<DailyMeditationStatsDetail?>?,
    @SerializedName("meditationAveragePerDay")
    val meditationAveragePerDay: MeditationAveragePerDay?,
    @SerializedName("monthlyMeditationStats")
    val monthlyMeditationStats: MonthlyMeditationStats?
) {
    data class DailyMeditationStatsDetail(
        @SerializedName("date")
        val date: String?,
        @SerializedName("meditationTime")
        val meditationTime: String?
    )

    data class MeditationAveragePerDay(
        @SerializedName("liveSessionCountPerDay")
        val liveSessionCountPerDay: String?,
        @SerializedName("meditationTimePerDay")
        val meditationTimePerDay: String?,
        @SerializedName("numberOfMeditationsPerDay")
        val numberOfMeditationsPerDay: String?
    )

    data class MonthlyMeditationStats(
        @SerializedName("liveSessionCount")
        val liveSessionCount: String?,
        @SerializedName("numberOfMeditations")
        val numberOfMeditations: String?,
        @SerializedName("totalMeditationTime")
        val totalMeditationTime: String?
    )
}
