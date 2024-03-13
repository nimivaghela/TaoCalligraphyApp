package com.app.taocalligraphy.models.response.playList

import com.google.gson.annotations.SerializedName

data class PlaylistApiResponse(
    @SerializedName("list")
    var list: ArrayList<Playlist>,
    @SerializedName("totalRecords")
    var totalRecords: Int
) {
    data class Playlist(
        @SerializedName("id")
        var id: Int,
        @SerializedName("meditations")
        var meditations: Int,
        @SerializedName("playlistImage")
        var playlistImage: String,
        @SerializedName("timeDurationInMinutes")
        var timeDurationInMinutes: Int,
        @SerializedName("title")
        var title: String
    )
}