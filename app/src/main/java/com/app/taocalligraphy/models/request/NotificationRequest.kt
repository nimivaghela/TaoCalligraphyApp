package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

data class NotificationRequest(
    @SerializedName("current_page")
    var currentPage: Int = -1,
    @SerializedName("per_page")
    var perPage: Int = -1,
    @SerializedName("search")
    var search: String = "",
    @SerializedName("notificationType")
    var notificationType: String = ""
)
