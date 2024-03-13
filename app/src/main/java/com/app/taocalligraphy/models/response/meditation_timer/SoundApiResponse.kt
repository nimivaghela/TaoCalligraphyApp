package com.app.taocalligraphy.models.response.meditation_timer


import com.google.gson.annotations.SerializedName

data class SoundApiResponse(
    @SerializedName("list")
    var list: ArrayList<SoundList?>?,
    @SerializedName("total")
    var total: Int?
) {
    data class SoundList(
        @SerializedName("name")
        var name: String?,
        @SerializedName("sound")
        var sound: String?,
        @SerializedName("soundId")
        var soundId: Int?,
        @SerializedName("isDefaultForStart")
        var isDefaultForStart: Boolean? = false,
        @SerializedName("isDefaultForEnd")
        var isDefaultForEnd: Boolean? = false,
        @SerializedName("isDefaultForAmbient")
        var isDefaultForAmbient: Boolean? = false,

        var isSelected: Boolean = false
    )
}