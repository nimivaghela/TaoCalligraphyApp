package com.app.taocalligraphy.models.response.notification_model


import com.google.gson.annotations.SerializedName


data class FetchNotificationListDataResponse(
    @SerializedName("meditation")
    val meditation: NotificationData?,
    @SerializedName("programs")
    val programs: NotificationData?,
    @SerializedName("subscription")
    val subscription: NotificationData?,
    @SerializedName("dailyWisdom")
    val dailyWisdom: NotificationData?,
) {
    data class NotificationData(
        @SerializedName("list")
        val list: ArrayList<NotificationDataList?>?,
        @SerializedName("totalRecords")
        val totalRecords: String?,
        @SerializedName("totalUnReadNotificationCount")
        val totalUnReadNotificationCount: String?
    ) {
        data class NotificationDataList(
            @SerializedName("body")
            val body: String?,
            @SerializedName("id")
            val id: String?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("notificationRead")
            var notificationRead: Boolean?,
            @SerializedName("notificationTime")
            val notificationTime: String?,
            @SerializedName("notificationType")
            val notificationType: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("redirectionId")
            val redirectionId: String?,
        )
    }

}
