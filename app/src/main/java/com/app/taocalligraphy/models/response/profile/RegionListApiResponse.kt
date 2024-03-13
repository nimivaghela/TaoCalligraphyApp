package com.app.taocalligraphy.models.response.profile

import com.google.gson.annotations.SerializedName

class RegionListApiResponse : ArrayList<RegionListApiResponse.Data>() {
    data class Data(
        @SerializedName("id")
        var id: Int?,
        @SerializedName("name")
        var name: String?,
        var isSelected: Boolean = false
    )
}