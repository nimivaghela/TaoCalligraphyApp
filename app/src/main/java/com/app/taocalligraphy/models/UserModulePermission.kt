package com.app.taocalligraphy.models

import com.app.taocalligraphy.models.response.stats_response.FetchMonthStatDataReponse
import com.google.gson.annotations.SerializedName

data class UserModulePermission(
    @SerializedName("permissionId")
    val permissionId: Int?,
    @SerializedName("moduleId")
    val moduleId: Int?,
    @SerializedName("moduleNumber")
    val moduleNumber: Int?,
    @SerializedName("moduleName")
    val moduleName: String?,
    @SerializedName("canAccess")
    val canAccess: Boolean?,
    @SerializedName("isHidden")
    val isHidden: Boolean?,
    @SerializedName("badge")
    val badge: String?,
)
