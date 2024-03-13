package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

data class DeleteNotificationRequest(
    @SerializedName("notificationId")
    var notificationId: ArrayList<Int> = arrayListOf(),
    @SerializedName("notificationType")
    var type: String? = null

)
