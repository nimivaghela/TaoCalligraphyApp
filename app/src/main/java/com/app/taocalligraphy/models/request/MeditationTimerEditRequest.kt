package com.app.taocalligraphy.models.request


import com.google.gson.annotations.SerializedName

data class MeditationTimerEditRequest(
    @SerializedName("meditationId")
    var meditationId: Int = -1,
    @SerializedName("meditationTime")
    var meditationTime: Int = -1,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("startSound")
    var startSound: Int = -1,
    @SerializedName("endSound")
    var endSound: Int = -1,
    @SerializedName("ambientSound")
    var ambientSound: Int = -1,
    @SerializedName("reminderTime")
    var reminderTime: String = "",
    @SerializedName("background")
    var background: Int = -1
)