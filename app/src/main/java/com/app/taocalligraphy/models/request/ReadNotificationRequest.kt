package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

data class ReadNotificationRequest(
    @SerializedName("notificationId")
    var notificationId: ArrayList<Int> = arrayListOf()

)
