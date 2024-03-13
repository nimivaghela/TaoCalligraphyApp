package com.app.taocalligraphy.models.response.notification_model


import com.google.gson.annotations.SerializedName


data class ReadNotificationDataByIdResponse(
    @SerializedName("meditationUnreadCount")
    val meditation: Int?,
    @SerializedName("programUnreadCount")
    val programs: Int?,
    @SerializedName("dailyWisdomUnreadCount")
    val dailyWisdom: Int?,
    @SerializedName("subscriptionUnreadCount")
    val subscription: Int?
)
