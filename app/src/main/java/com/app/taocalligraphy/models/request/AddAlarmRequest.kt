package com.app.taocalligraphy.models.request


import com.google.gson.annotations.SerializedName

data class AddAlarmRequest(
    @SerializedName("time")
    var time: String = "",
    @SerializedName("repeatDays")
    var repeatDays: ArrayList<Int> = ArrayList<Int>(),
    @SerializedName("contentId")
    var contentId: String = "",
)