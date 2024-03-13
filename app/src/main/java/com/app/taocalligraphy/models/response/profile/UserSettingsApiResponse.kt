package com.app.taocalligraphy.models.response.profile


import com.google.gson.annotations.SerializedName

data class UserSettingsApiResponse(
    @SerializedName("appLanguage")
    var appLanguage: Int?,
    @SerializedName("isEmailNotificationEnabled")
    var isEmailNotificationEnabled: Boolean? = false,
    @SerializedName("isInAppNotificationEnabled")
    var isInAppNotificationEnabled: Boolean? = false,
    @SerializedName("isSilenceDuringMeditation")
    var isSilenceDuringMeditation: Boolean? = false,
    @SerializedName("isSilenceDuringOtherApps")
    var isSilenceDuringOtherApps: Boolean? = false,
    @SerializedName("is12HourFormat")
    var is12HourFormat: Boolean? = false,
    @SerializedName("isSilenceDuringSession")
    var isSilenceDuringSession: Boolean? = false,
    @SerializedName("isZenModeEnabled")
    var isZenModeEnabled: Boolean? = false,
    @SerializedName("sessionReminderTime")
    var sessionReminderTime: Int?,
    @SerializedName("userImportantInfo")
    var userImportantInfo: ArrayList<UserImportantInfo?>?,
    @SerializedName("deviceData")
    val deviceData: ArrayList<DeviceData?>?,
) {
    data class UserImportantInfo(
        @SerializedName("description")
        var description: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("title")
        var title: String?
    )

    data class DeviceData(
        @SerializedName("browserType")
        val browserType: String?, // Android App
        @SerializedName("deviceModel")
        val deviceModel: String?, // Motorola edge 20 fusion
        @SerializedName("deviceVersion")
        val deviceVersion: String?, // 1.0
        @SerializedName("isThisDevice")
        val isThisDevice: Boolean?, // true
        @SerializedName("lastUsedDateTime")
        val lastUsedDateTime: String?, // 2022-09-14 05:47:43.0
        @SerializedName("location")
        val location: String?, // Khas Rd, Khas, Gujarat 382255, India
        @SerializedName("sessionId")
        val sessionId: Int?, // 769
        @SerializedName("systemType")
        val systemType: String? // Android
    )
}