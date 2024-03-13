package com.app.taocalligraphy.models.response.meditation_timer


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MeditationListApiResponse(
    @SerializedName("list")
    var list: MutableList<MeditationDetail?>?,
    @SerializedName("total")
    var total: Int?
) : Serializable {
    data class MeditationDetail(
        @SerializedName("ambientSound")
        var ambientSound: Sound?,
        @SerializedName("backgroundImage")
        var backgroundImage: BackgroundImage?,
        @SerializedName("endSound")
        var endSound: Sound?,
        @SerializedName("meditationId")
        var meditationId: Int?,
        @SerializedName("meditationTime")
        var meditationTime: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("reminderTime")
        var reminderTime: String?,
        @SerializedName("startSound")
        var startSound: Sound?,
        var isPopupWindowVisible: Boolean = false
    ) : Serializable {
        data class AmbientSound(
            @SerializedName("isDefaultForEnd")
            val isDefaultForEnd: Boolean?,
            @SerializedName("isDefaultForStart")
            val isDefaultForStart: Boolean?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("sound")
            var sound: String?,
            @SerializedName("soundId")
            var soundId: Int?
        ) : Serializable

        data class BackgroundImage(
            @SerializedName("backgroundImageId")
            var backgroundImageId: Int?,
            @SerializedName("backgroundImageOriginal")
            var backgroundImageOriginal: String?,
            @SerializedName("backgroundImageThumb")
            var backgroundImageThumb: String?
        ) : Serializable

        data class EndSound(
            @SerializedName("isDefaultForEnd")
            val isDefaultForEnd: Boolean?,
            @SerializedName("isDefaultForStart")
            val isDefaultForStart: Boolean?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("sound")
            var sound: String?,
            @SerializedName("soundId")
            var soundId: Int?
        ) : Serializable

        data class StartSound(
            @SerializedName("isDefaultForEnd")
            val isDefaultForEnd: Boolean?,
            @SerializedName("isDefaultForStart")
            val isDefaultForStart: Boolean?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("sound")
            var sound: String?,
            @SerializedName("soundId")
            var soundId: Int?
        ) : Serializable

        data class Sound(
            @SerializedName("isDefaultForEnd")
            val isDefaultForEnd: Boolean?,
            @SerializedName("isDefaultForStart")
            val isDefaultForStart: Boolean?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("sound")
            var sound: String?,
            @SerializedName("soundId")
            var soundId: Int?
        ) : Serializable
    }
}