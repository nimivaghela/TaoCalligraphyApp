package com.app.taocalligraphy.models.request


import com.google.gson.annotations.SerializedName

data class UserSettingsRequest(
    @SerializedName("appLanguage")
    var appLanguage: Int=-1,
    @SerializedName("isEmailNotificationEnabled")
    var isEmailNotificationEnabled: Boolean=false,
    @SerializedName("isInAppNotificationEnabled")
    var isInAppNotificationEnabled: Boolean=false,
    @SerializedName("isSilenceDuringMeditation")
    var isSilenceDuringMeditation: Boolean=false,
    @SerializedName("isSilenceDuringOtherApps")
    var isSilenceDuringOtherApps: Boolean=false,
    @SerializedName("isSilenceDuringSession")
    var isSilenceDuringSession: Boolean=false,
    @SerializedName("isZenModeEnabled")
    var isZenModeEnabled: Boolean=false,
    @SerializedName("is12HourFormat")
    var is12HourFormat: Boolean=false,
    @SerializedName("sessionReminderTimeInMinute")
    var sessionReminderTimeInMinute: Int=-1
)